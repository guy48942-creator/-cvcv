package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.audio.QuranAudioPlayer
import com.example.data.model.AudioEffectPreset
import com.example.data.model.AudioQualityPreset
import com.example.ui.theme.*

@Composable
fun PlaybackDock(
    modifier: Modifier = Modifier,
    onOpenMushaf: () -> Unit = {}
) {
    val playerState by QuranAudioPlayer.state.collectAsState()
    var showAudioSettingsDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurfaceContainerHigh.copy(alpha = 0.95f),
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Top Mini-Scrubber line
            val progress = if (playerState.durationMs > 0) {
                (playerState.positionMs.toFloat() / playerState.durationMs.toFloat()).coerceIn(0f, 1f)
            } else 0f

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Primary,
                trackColor = DarkSurfaceContainerHighest
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Wave/Speaker icon + Reciter & Surah info
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenMushaf() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(PrimaryContainer.copy(alpha = 0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "سورة ${playerState.currentSurah.nameArabic} • الآية ${playerState.currentAyahNumber}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = OnSurface,
                                maxLines = 1
                            )
                            // High Quality Badge
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (playerState.audioQuality == AudioQualityPreset.HQ_320) Primary.copy(alpha = 0.2f) else DarkSurfaceContainerHighest,
                                modifier = Modifier.clickable { showAudioSettingsDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        Icons.Default.HighQuality,
                                        contentDescription = null,
                                        tint = if (playerState.audioQuality == AudioQualityPreset.HQ_320) Primary else OnSurfaceVariant,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = if (playerState.audioQuality == AudioQualityPreset.HQ_320) "HQ 320k" else "128k",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                        color = if (playerState.audioQuality == AudioQualityPreset.HQ_320) Primary else OnSurfaceVariant
                                    )
                                }
                            }
                        }
                        Text(
                            text = playerState.currentReciter.nameArabic,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = OnSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                // Audio Controls (Prev, Play/Pause, Next, Effects/Speed)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Audio Effects & Speed button
                    IconButton(
                        onClick = { showAudioSettingsDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = "مؤثرات الصوت",
                            tint = Secondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { QuranAudioPlayer.skipPrevious() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            contentDescription = "السابق",
                            tint = OnSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    FilledIconButton(
                        onClick = { QuranAudioPlayer.togglePlayPause() },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Primary),
                        modifier = Modifier.size(40.dp)
                    ) {
                        if (playerState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = OnPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playerState.isPlaying) "إيقاف مؤقت" else "تشغيل",
                                tint = OnPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { QuranAudioPlayer.skipNext() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = "التالي",
                            tint = OnSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    if (showAudioSettingsDialog) {
        AudioSettingsDialog(onDismiss = { showAudioSettingsDialog = false })
    }
}

@Composable
fun AudioSettingsDialog(onDismiss: () -> Unit) {
    val playerState by QuranAudioPlayer.state.collectAsState()
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceContainer,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = Primary)
                Text("هندسة وتحسين جودة الصوت", color = Primary, style = MaterialTheme.typography.titleMedium)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Audio Quality Section (HQ vs Saver)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.HighQuality, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                    Text(
                        "جودة البث والصوت للقرّاء:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AudioQualityPreset.values().forEach { quality ->
                        val isSelected = playerState.audioQuality == quality
                        Card(
                            onClick = { QuranAudioPlayer.setAudioQuality(quality) },
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PrimaryContainer.copy(alpha = 0.35f) else DarkSurfaceContainerLow
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Primary) else null,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = quality.bitrate,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Primary else OnSurfaceVariant
                                    )
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Text(
                                    text = if (quality == AudioQualityPreset.HQ_320) "استوديو فائق" else "اقتصادي",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Primary else OnSurface
                                )
                                Text(
                                    text = quality.subtitleArabic.ifEmpty { quality.bitrate },
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = OnSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = DarkSurfaceContainerHighest)

                // 2. DSP Acoustic Filters and Presets
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                    Text(
                        "المعالجة الرقمية ونقاء الاستماع (DSP):",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                }

                AudioEffectPreset.values().forEach { effect ->
                    val isSelected = playerState.audioEffect == effect
                    Card(
                        onClick = { QuranAudioPlayer.setAudioEffect(effect) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) PrimaryContainer.copy(alpha = 0.3f) else DarkSurfaceContainerLow
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.6f)) else null,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    effect.titleArabic,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Primary else OnSurface
                                )
                                Text(
                                    effect.descriptionArabic,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Primary)
                            }
                        }
                    }
                }

                HorizontalDivider(color = DarkSurfaceContainerHighest)

                // 3. Smart Loudness Enhancer for quiet / vintage recitations
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (playerState.isLoudnessBoostEnabled) PrimaryContainer.copy(alpha = 0.25f) else DarkSurfaceContainerLow
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "معزز الصوت وموازنة التسجيلات",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (playerState.isLoudnessBoostEnabled) Primary else OnSurface
                            )
                            Text(
                                "رفع مستوى الصوت للتسجيلات النادرة والقديمة دون تشويش",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                        }
                        Switch(
                            checked = playerState.isLoudnessBoostEnabled,
                            onCheckedChange = { QuranAudioPlayer.setLoudnessBoost(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Primary,
                                checkedTrackColor = PrimaryContainer
                            )
                        )
                    }
                }

                HorizontalDivider(color = DarkSurfaceContainerHighest)

                // 4. Playback Speed
                Text("سرعة التلاوة:", style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f)
                    speeds.forEach { spd ->
                        FilterChip(
                            selected = playerState.playbackSpeed == spd,
                            onClick = { QuranAudioPlayer.setPlaybackSpeed(spd) },
                            label = { Text("${spd}x") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("تم وتطبيق", color = Primary, fontWeight = FontWeight.Bold)
            }
        }
    )
}
