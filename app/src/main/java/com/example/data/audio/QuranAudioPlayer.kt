package com.example.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import com.example.data.model.*
import com.example.data.repository.QuranRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSurah: SurahInfo = QuranRepository.surahs[16], // Default Al-Isra (17)
    val currentAyahNumber: Int = 1,
    val currentReciter: ReciterInfo = QuranRepository.reciters[0], // Alafasy
    val positionMs: Long = 0L,
    val durationMs: Long = 1000L,
    val playbackSpeed: Float = 1.0f,
    val audioEffect: AudioEffectPreset = AudioEffectPreset.NORMAL,
    val audioQuality: AudioQualityPreset = AudioQualityPreset.HQ_320,
    val isLoudnessBoostEnabled: Boolean = false,
    val loudnessGainMb: Int = 300,
    val isSleepTimerActive: Boolean = false,
    val sleepTimerRemainingSec: Int = 0,
    val isSmartStopAtSurahEnd: Boolean = true,
    val isFadeOutEnabled: Boolean = true,
    val isDeepSleepDimming: Boolean = false,
    val errorMessage: String? = null
)

object QuranAudioPlayer {
    private const val TAG = "QuranAudioPlayer"
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // AudioFX DSP processors
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null

    private val _state = MutableStateFlow(AudioPlayerState())
    val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

    private var progressJob: Job? = null
    private var sleepCountDownTimer: CountDownTimer? = null
    private var context: Context? = null
    private var currentPlayingUrl: String? = null

    fun initialize(appContext: Context) {
        this.context = appContext.applicationContext
    }

    fun playSurah(
        surah: SurahInfo,
        reciter: ReciterInfo = _state.value.currentReciter,
        quality: AudioQualityPreset = _state.value.audioQuality
    ) {
        val url = QuranRepository.getSurahAudioUrl(reciter, surah.number, quality)
        _state.value = _state.value.copy(
            currentSurah = surah,
            currentReciter = reciter,
            audioQuality = quality,
            currentAyahNumber = 1,
            isLoading = true,
            errorMessage = null
        )
        playStreamUrl(url)
    }

    fun playAyah(
        surah: SurahInfo,
        ayahNumber: Int,
        reciter: ReciterInfo = _state.value.currentReciter,
        quality: AudioQualityPreset = _state.value.audioQuality
    ) {
        val url = QuranRepository.getAyahAudioUrl(reciter, surah.number, ayahNumber, quality)
        _state.value = _state.value.copy(
            currentSurah = surah,
            currentReciter = reciter,
            audioQuality = quality,
            currentAyahNumber = ayahNumber,
            isLoading = true,
            errorMessage = null
        )
        playStreamUrl(url)
    }

    private fun playStreamUrl(url: String, isRetryFallback: Boolean = false) {
        val ctx = context ?: return
        currentPlayingUrl = url
        scope.launch {
            try {
                releaseEffects()
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    
                    val headers = mapOf("User-Agent" to "Mozilla/5.0 (Linux; Android 12; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Mobile Safari/537.36")
                    val uri = android.net.Uri.parse(url)
                    setDataSource(ctx, uri, headers)

                    setOnPreparedListener { mp ->
                        setupAudioEffects(mp.audioSessionId)
                        applyAudioEffectsAndSpeed()
                        mp.start()
                        _state.value = _state.value.copy(
                            isPlaying = true,
                            isLoading = false,
                            durationMs = mp.duration.toLong().coerceAtLeast(1000L),
                            errorMessage = null
                        )
                        startProgressTracker()
                    }
                    setOnCompletionListener {
                        handleTrackCompletion()
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra for URL: $url")
                        
                        // Automatic fallback from HQ to standard stream if network fails
                        if (!isRetryFallback && _state.value.audioQuality == AudioQualityPreset.HQ_320) {
                            val fallbackUrl = QuranRepository.getSurahAudioUrl(
                                _state.value.currentReciter,
                                _state.value.currentSurah.number,
                                AudioQualityPreset.SAVER_128
                            )
                            if (fallbackUrl != url) {
                                Log.i(TAG, "Retrying with standard stream fallback: $fallbackUrl")
                                playStreamUrl(fallbackUrl, isRetryFallback = true)
                                return@setOnErrorListener true
                            }
                        }

                        val errorMsg = when (extra) {
                            -2147483648 -> "خطأ في خادم البث أو الاتصال، جارٍ فحص الجودة"
                            else -> "تعذر تشغيل تلاوة القارئ من الخادم (خطأ $what)"
                        }
                        
                        _state.value = _state.value.copy(
                            isPlaying = false,
                            isLoading = false,
                            errorMessage = errorMsg
                        )
                        true
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception playing stream", e)
                _state.value = _state.value.copy(
                    isPlaying = false,
                    isLoading = false,
                    errorMessage = "خطأ تقني: ${e.localizedMessage}"
                )
            }
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer
        if (mp != null) {
            if (mp.isPlaying) {
                mp.pause()
                _state.value = _state.value.copy(isPlaying = false)
            } else {
                mp.start()
                _state.value = _state.value.copy(isPlaying = true)
                startProgressTracker()
            }
        } else {
            playSurah(_state.value.currentSurah, _state.value.currentReciter, _state.value.audioQuality)
        }
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.seekTo(positionMs.toInt())
        _state.value = _state.value.copy(positionMs = positionMs)
    }

    fun skipNext() {
        val currentAyahs = QuranRepository.getAyahsForSurah(_state.value.currentSurah.number)
        val nextAyah = _state.value.currentAyahNumber + 1
        if (nextAyah <= currentAyahs.size) {
            playAyah(_state.value.currentSurah, nextAyah, _state.value.currentReciter, _state.value.audioQuality)
        } else {
            val nextSurahIndex = (QuranRepository.surahs.indexOfFirst { it.number == _state.value.currentSurah.number } + 1)
                .coerceAtMost(QuranRepository.surahs.size - 1)
            val nextSurah = QuranRepository.surahs[nextSurahIndex]
            playSurah(nextSurah, _state.value.currentReciter, _state.value.audioQuality)
        }
    }

    fun skipPrevious() {
        val prevAyah = _state.value.currentAyahNumber - 1
        if (prevAyah >= 1) {
            playAyah(_state.value.currentSurah, prevAyah, _state.value.currentReciter, _state.value.audioQuality)
        } else {
            val prevSurahIndex = (QuranRepository.surahs.indexOfFirst { it.number == _state.value.currentSurah.number } - 1)
                .coerceAtLeast(0)
            val prevSurah = QuranRepository.surahs[prevSurahIndex]
            playSurah(prevSurah, _state.value.currentReciter, _state.value.audioQuality)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _state.value = _state.value.copy(playbackSpeed = speed)
        applyAudioEffectsAndSpeed()
    }

    fun setAudioEffect(effect: AudioEffectPreset) {
        _state.value = _state.value.copy(audioEffect = effect)
        applyAudioEffects()
    }

    fun setAudioQuality(quality: AudioQualityPreset) {
        _state.value = _state.value.copy(audioQuality = quality)
        if (_state.value.isPlaying) {
            playSurah(_state.value.currentSurah, _state.value.currentReciter, quality)
        }
    }

    fun setLoudnessBoost(enabled: Boolean, gainMb: Int = 300) {
        _state.value = _state.value.copy(isLoudnessBoostEnabled = enabled, loudnessGainMb = gainMb)
        applyAudioEffects()
    }

    fun setReciter(reciter: ReciterInfo) {
        _state.value = _state.value.copy(currentReciter = reciter)
        if (_state.value.isPlaying) {
            playSurah(_state.value.currentSurah, reciter, _state.value.audioQuality)
        }
    }

    // Initialize Audio Effects on the active audioSessionId
    private fun setupAudioEffects(sessionId: Int) {
        releaseEffects()
        try {
            equalizer = Equalizer(0, sessionId).apply {
                enabled = true
            }
        } catch (e: Exception) {
            Log.w(TAG, "Equalizer initialization error: ${e.message}")
            equalizer = null
        }

        try {
            bassBoost = BassBoost(0, sessionId).apply {
                enabled = true
            }
        } catch (e: Exception) {
            Log.w(TAG, "BassBoost initialization error: ${e.message}")
            bassBoost = null
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                loudnessEnhancer = LoudnessEnhancer(sessionId).apply {
                    enabled = true
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "LoudnessEnhancer initialization error: ${e.message}")
            loudnessEnhancer = null
        }

        applyAudioEffects()
    }

    private fun releaseEffects() {
        try {
            equalizer?.release()
        } catch (_: Exception) {}
        equalizer = null

        try {
            bassBoost?.release()
        } catch (_: Exception) {}
        bassBoost = null

        try {
            loudnessEnhancer?.release()
        } catch (_: Exception) {}
        loudnessEnhancer = null
    }

    // Apply real-time DSP acoustic filters without distorting pitch
    fun applyAudioEffects() {
        val currentState = _state.value

        // 1. Hardware Equalizer tuning
        equalizer?.let { eq ->
            try {
                val numBands = eq.numberOfBands
                val minLevel = eq.bandLevelRange[0]
                val maxLevel = eq.bandLevelRange[1]
                fun clamp(level: Short): Short = level.coerceIn(minLevel, maxLevel)

                for (i in 0 until numBands) {
                    val bandIdx = i.toShort()
                    val centerFreqHz = eq.getCenterFreq(bandIdx) / 1000

                    val targetLevel: Short = when (currentState.audioEffect) {
                        AudioEffectPreset.NORMAL -> {
                            // Pristine studio flat reference
                            0.toShort()
                        }
                        AudioEffectPreset.VOCAL_CLARITY -> {
                            // Clear Tajweed and vocal presence: boost 1kHz-4kHz and 12kHz+
                            when {
                                centerFreqHz < 250 -> (-150).toShort() // gentle sub-bass cut
                                centerFreqHz in 250..1000 -> 100.toShort()
                                centerFreqHz in 1001..4000 -> 450.toShort() // vocal presence
                                else -> 500.toShort() // crystal sparkle
                            }
                        }
                        AudioEffectPreset.KHUSHU_REVERB -> {
                            // Warm resonance and sacred ambiance
                            when {
                                centerFreqHz < 500 -> 350.toShort()
                                centerFreqHz in 500..2500 -> 150.toShort()
                                else -> (-50).toShort()
                            }
                        }
                        AudioEffectPreset.BASS_RESONANCE -> {
                            // Warm, authoritative low-end depth
                            when {
                                centerFreqHz < 350 -> 600.toShort()
                                centerFreqHz in 350..1200 -> 250.toShort()
                                else -> 0.toShort()
                            }
                        }
                        AudioEffectPreset.LOUDSPEAKER_BOOST -> {
                            // High audibility for phone speakers / Bluetooth
                            when {
                                centerFreqHz < 200 -> (-250).toShort() // prevent speaker distortion
                                centerFreqHz in 200..3000 -> 350.toShort()
                                else -> 400.toShort()
                            }
                        }
                    }
                    eq.setBandLevel(bandIdx, clamp(targetLevel))
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error configuring equalizer bands: ${e.message}")
            }
        }

        // 2. BassBoost processor
        bassBoost?.let { bb ->
            try {
                val strength: Short = when (currentState.audioEffect) {
                    AudioEffectPreset.BASS_RESONANCE -> 600.toShort()
                    AudioEffectPreset.KHUSHU_REVERB -> 350.toShort()
                    else -> 0.toShort()
                }
                if (bb.strengthSupported) {
                    bb.setStrength(strength)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error setting bass boost: ${e.message}")
            }
        }

        // 3. LoudnessEnhancer processor (boosts lower-volume reciters cleanly)
        loudnessEnhancer?.let { le ->
            try {
                val targetGainMb = when {
                    currentState.isLoudnessBoostEnabled -> currentState.loudnessGainMb
                    currentState.audioEffect == AudioEffectPreset.LOUDSPEAKER_BOOST -> 450
                    currentState.audioEffect == AudioEffectPreset.VOCAL_CLARITY -> 200
                    else -> 0
                }
                le.setTargetGain(targetGainMb)
            } catch (e: Exception) {
                Log.w(TAG, "Error setting loudness enhancer: ${e.message}")
            }
        }
    }

    private fun applyAudioEffectsAndSpeed() {
        val mp = mediaPlayer ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val params = PlaybackParams()
                params.speed = _state.value.playbackSpeed
                params.pitch = 1.0f // Preserve pure natural human voice pitch
                mp.playbackParams = params
            } catch (e: Exception) {
                Log.w(TAG, "Failed setting playback params", e)
            }
        }
        applyAudioEffects()
    }

    private fun handleTrackCompletion() {
        if (_state.value.isSmartStopAtSurahEnd && _state.value.isSleepTimerActive) {
            stopPlayback()
            cancelSleepTimer()
            return
        }

        // Continuous recitation: auto-advance
        val currentAyahs = QuranRepository.getAyahsForSurah(_state.value.currentSurah.number)
        if (_state.value.currentAyahNumber < currentAyahs.size) {
            playAyah(_state.value.currentSurah, _state.value.currentAyahNumber + 1, _state.value.currentReciter, _state.value.audioQuality)
        } else {
            val currentIndex = QuranRepository.surahs.indexOfFirst { it.number == _state.value.currentSurah.number }
            if (currentIndex in 0 until QuranRepository.surahs.size - 1) {
                val nextSurah = QuranRepository.surahs[currentIndex + 1]
                playSurah(nextSurah, _state.value.currentReciter, _state.value.audioQuality)
            } else {
                _state.value = _state.value.copy(isPlaying = false, positionMs = 0)
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val mp = mediaPlayer
                if (mp != null && mp.isPlaying) {
                    _state.value = _state.value.copy(
                        positionMs = mp.currentPosition.toLong(),
                        durationMs = mp.duration.toLong().coerceAtLeast(1000L)
                    )
                }
                delay(500)
            }
        }
    }

    fun stopPlayback() {
        releaseEffects()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        progressJob?.cancel()
        _state.value = _state.value.copy(isPlaying = false, positionMs = 0)
    }

    // Zen Sleep Timer logic
    fun startSleepTimer(minutes: Int) {
        cancelSleepTimer()
        val totalSec = minutes * 60
        _state.value = _state.value.copy(
            isSleepTimerActive = true,
            sleepTimerRemainingSec = totalSec
        )

        sleepCountDownTimer = object : CountDownTimer(totalSec * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secRemaining = (millisUntilFinished / 1000).toInt()
                _state.value = _state.value.copy(sleepTimerRemainingSec = secRemaining)

                // Fade-out logic in last 5 minutes
                if (_state.value.isFadeOutEnabled && secRemaining <= 300 && secRemaining > 0) {
                    val volumeFactor = (secRemaining.toFloat() / 300f).coerceIn(0.1f, 1.0f)
                    mediaPlayer?.setVolume(volumeFactor, volumeFactor)
                }
            }

            override fun onFinish() {
                _state.value = _state.value.copy(
                    isSleepTimerActive = false,
                    sleepTimerRemainingSec = 0
                )
                stopPlayback()
            }
        }.start()
    }

    fun addMinutesToSleepTimer(minutes: Int) {
        val currentRemaining = _state.value.sleepTimerRemainingSec
        val newDuration = (currentRemaining + (minutes * 60)) / 60
        startSleepTimer(newDuration.coerceAtLeast(1))
    }

    fun cancelSleepTimer() {
        sleepCountDownTimer?.cancel()
        sleepCountDownTimer = null
        mediaPlayer?.setVolume(1.0f, 1.0f)
        _state.value = _state.value.copy(
            isSleepTimerActive = false,
            sleepTimerRemainingSec = 0
        )
    }

    fun toggleSmartStopAtSurahEnd(enabled: Boolean) {
        _state.value = _state.value.copy(isSmartStopAtSurahEnd = enabled)
    }

    fun toggleFadeOut(enabled: Boolean) {
        _state.value = _state.value.copy(isFadeOutEnabled = enabled)
    }

    fun toggleDeepSleepDimming(enabled: Boolean) {
        _state.value = _state.value.copy(isDeepSleepDimming = enabled)
    }
}
