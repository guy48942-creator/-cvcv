package com.example.data.audio

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class SalawatSettings(
    val isEnabled: Boolean = true,
    val intervalMinutes: Int = 30,
    val isVoiceEnabled: Boolean = true,
    val selectedVoiceIndex: Int = 0,
    val lastTriggeredTime: Long = 0L
)

object SalawatNotificationManager {
    private const val TAG = "SalawatNotification"
    private const val CHANNEL_ID = "salawat_reminder_channel"

    private var appContext: Context? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false
    private var mediaPlayer: MediaPlayer? = null

    private val salawatVoices = listOf(
        "https://audio.com/api/audio/1792446774618788/download", // Salawat 1
        "https://www.islamcan.com/audio/salawat/salawat1.mp3",
        "https://www.islamcan.com/audio/salawat/salawat2.mp3",
        "https://www.islamcan.com/audio/salawat/salawat3.mp3",
        "https://www.islamcan.com/audio/salawat/salawat4.mp3"
    )

    private val _settings = MutableStateFlow(SalawatSettings())
    val settings: StateFlow<SalawatSettings> = _settings.asStateFlow()

    private val handler = Handler(Looper.getMainLooper())
    private val reminderRunnable = object : Runnable {
        override fun run() {
            if (_settings.value.isEnabled) {
                triggerSalawatAudio(showMessage = true)
                val delayMs = _settings.value.intervalMinutes * 60 * 1000L
                handler.postDelayed(this, delayMs)
            }
        }
    }

    fun initialize(context: Context) {
        this.appContext = context.applicationContext

        // Initialize Android TextToSpeech for reciting Salawat in Arabic
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("ar"))
                isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
                Log.d(TAG, "TTS initialized with Arabic status: $isTtsReady")
            }
        }

        createNotificationChannel()
        scheduleReminder()
    }

    fun updateSettings(isEnabled: Boolean, intervalMinutes: Int, isVoiceEnabled: Boolean, voiceIndex: Int = 0) {
        _settings.value = SalawatSettings(isEnabled, intervalMinutes, isVoiceEnabled, voiceIndex)
        scheduleReminder()
    }

    private fun scheduleReminder() {
        handler.removeCallbacks(reminderRunnable)
        if (_settings.value.isEnabled) {
            val delayMs = _settings.value.intervalMinutes * 60 * 1000L
            handler.postDelayed(reminderRunnable, delayMs)
        }
    }

    fun triggerSalawatAudio(showMessage: Boolean = true) {
        val ctx = appContext ?: return
        val phrase = "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَىٰ نَبِيِّنَا مُحَمَّدٍ ﷺ"

        if (_settings.value.isVoiceEnabled) {
            playRealSalawatVoice(_settings.value.selectedVoiceIndex)
        }

        if (showMessage) {
            Toast.makeText(ctx, phrase, Toast.LENGTH_LONG).show()
            showNotification(phrase)
        }
    }

    private fun playRealSalawatVoice(index: Int) {
        val ctx = appContext ?: return
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                // Add User-Agent header
                val headers = mapOf("User-Agent" to "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.181 Mobile Safari/537.36")
                val url = salawatVoices[index % salawatVoices.size]
                val uri = android.net.Uri.parse(url)
                setDataSource(ctx, uri, headers)
                
                setOnPreparedListener { start() }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "Salawat MediaPlayer error: what=$what, extra=$extra for URL: $url")
                    fallbackToTts()
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Salawat playback exception", e)
            fallbackToTts()
        }
    }

    private fun fallbackToTts() {
        val phrase = "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَىٰ نَبِيِّنَا مُحَمَّدٍ"
        if (isTtsReady && textToSpeech != null) {
            textToSpeech?.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, "salawat_id")
        }
    }

    private fun createNotificationChannel() {
        val ctx = appContext ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "تذكير بالصلاة على النبي ﷺ"
            val descriptionText = "إشعار صوتي دوري للصلاة والسلام على رسول الله"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(phrase: String) {
        val ctx = appContext ?: return
        val builder = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("الصلاة على النبي ﷺ")
            .setContentText(phrase)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1001, builder.build())
    }
}
