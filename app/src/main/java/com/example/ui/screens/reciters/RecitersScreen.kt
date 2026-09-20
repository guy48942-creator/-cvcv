package com.example.ui.screens.reciters

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.audio.QuranAudioPlayer
import com.example.data.model.AudioEffectPreset
import com.example.data.model.AudioQualityPreset
import com.example.data.model.ReciterInfo
import com.example.data.repository.QuranRepository
import com.example.ui.components.AudioSettingsDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecitersScreen(navController: NavController) {
    val context = LocalContext.current
    val playerState by QuranAudioPlayer.state.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedRewayaFilter by remember { mutableStateOf("الكل") }
    var showAudioSettingsDialog by remember { mutableStateOf(false) }
    var selectedReciterForModal by remember { mutableStateOf<ReciterInfo?>(null) }

    val filteredReciters = remember(searchQuery, selectedRewayaFilter) {
        QuranRepository.reciters.filter { reciter ->
            val matchesQuery = searchQuery.isBlank() ||
                    reciter.nameArabic.contains(searchQuery) ||
                    reciter.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                    reciter.rewaya.contains(searchQuery)

            val matchesFilter = when (selectedRewayaFilter) {
                "حفص" -> reciter.rewaya.contains("حفص")
                "ورش" -> reciter.rewaya.contains("ورش")
                "فائقة HQ" -> reciter.qualityBadge.contains("320") || reciter.serverUrlHq != null || reciter.qualityBadge.contains("Master")
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
    ) {
        // Header
        RecitersHeader(
            onOpenAudioSettings = { showAudioSettingsDialog = true }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث عن قارئ أو رواية...", color = OnSurfaceVariant) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Primary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح", tint = OnSurfaceVariant)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceContainer,
                        unfocusedContainerColor = DarkSurfaceContainer,
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // Filter Chips (الكل, حفص عن عاصم, ورش عن نافع, فائقة HQ)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("الكل", "فائقة HQ", "حفص", "ورش").forEach { filter ->
                        val isSelected = selectedRewayaFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedRewayaFilter = filter },
                            leadingIcon = if (filter == "فائقة HQ") {
                                { Icon(Icons.Default.HighQuality, contentDescription = null, tint = if (isSelected) Primary else OnSurfaceVariant, modifier = Modifier.size(16.dp)) }
                            } else null,
                            label = {
                                Text(
                                    when (filter) {
                                        "الكل" -> "الكل (${QuranRepository.reciters.size})"
                                        "فائقة HQ" -> "استوديو 320k"
                                        "حفص" -> "حفص"
                                        "ورش" -> "ورش"
                                        else -> filter
                                    }
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryContainer.copy(alpha = 0.4f),
                                selectedLabelColor = Primary
                            )
                        )
                    }
                }
            }

            // Reciters List
            items(filteredReciters) { reciter ->
                val isCurrentlyPlayingThisReciter = playerState.currentReciter.id == reciter.id && playerState.isPlaying

                ReciterCard(
                    reciter = reciter,
                    isPlaying = isCurrentlyPlayingThisReciter,
                    onPlay = {
                        QuranAudioPlayer.setReciter(reciter)
                        QuranAudioPlayer.playSurah(playerState.currentSurah, reciter)
                        Toast.makeText(context, "بدء الاستماع لتلاوة ${reciter.nameArabic}", Toast.LENGTH_SHORT).show()
                    },
                    onOpenDetails = {
                        selectedReciterForModal = reciter
                    }
                )
            }

            // Bottom spacer for PlaybackDock
            item {
                Spacer(modifier = Modifier.height(75.dp))
            }
        }
    }

    if (showAudioSettingsDialog) {
        AudioSettingsDialog(onDismiss = { showAudioSettingsDialog = false })
    }

    selectedReciterForModal?.let { reciter ->
        ReciterSurahsSheet(
            reciter = reciter,
            onDismiss = { selectedReciterForModal = null },
            onSelectSurah = { surah ->
                QuranAudioPlayer.setReciter(reciter)
                QuranAudioPlayer.playSurah(surah, reciter)
                selectedReciterForModal = null
                Toast.makeText(context, "تشغيل سورة ${surah.nameArabic} بصوت ${reciter.nameArabic}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun RecitersHeader(onOpenAudioSettings: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(DarkSurfaceContainerHighest, DarkSurface)
                )
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "مكتبة القراء والمشاهير",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Primary
                )
                Text(
                    "استمع لأعذب التلاوات القرآنية في العالم الإسلامي",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }

            IconButton(
                onClick = onOpenAudioSettings,
                modifier = Modifier
                    .background(DarkSurfaceContainerLow, CircleShape)
                    .size(42.dp)
            ) {
                Icon(Icons.Default.Tune, contentDescription = "تحسين الصوت والمؤثرات", tint = Secondary)
            }
        }
    }
}

@Composable
fun ReciterCard(
    reciter: ReciterInfo,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onOpenDetails: () -> Unit
) {
    Card(
        onClick = onOpenDetails,
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) PrimaryContainer.copy(alpha = 0.2f) else DarkSurfaceContainer
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isPlaying) Primary else DarkSurfaceContainerHighest)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Reciter Avatar Icon
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        if (isPlaying) Primary.copy(alpha = 0.25f) else DarkSurfaceContainerLow,
                        CircleShape
                    )
                    .border(
                        1.5.dp,
                        if (isPlaying) Primary else Secondary.copy(alpha = 0.3f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isPlaying) Icons.Default.GraphicEq else Icons.Default.Person,
                    contentDescription = null,
                    tint = if (isPlaying) Primary else Secondary,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Reciter Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        reciter.nameArabic,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (isPlaying) Primary else OnSurface,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Primary.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                Icons.Default.HighQuality,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = reciter.qualityBadge,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = Primary
                            )
                        }
                    }
                    Text(
                        reciter.rewaya,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            // Play / Listen button
            FilledIconButton(
                onClick = onPlay,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isPlaying) Primary else PrimaryContainer.copy(alpha = 0.4f)
                ),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "إيقاف مؤقت" else "استماع",
                    tint = if (isPlaying) OnPrimary else Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// RECITER SURAHS BOTTOM SHEET / DIALOG
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciterSurahsSheet(
    reciter: ReciterInfo,
    onDismiss: () -> Unit,
    onSelectSurah: (com.example.data.model.SurahInfo) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(46.dp).background(PrimaryContainer.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Primary)
                }
                Column {
                    Text(
                        reciter.nameArabic,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Primary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = reciter.qualityBadge,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = Primary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Text(
                            "${reciter.rewaya} • نقاء استوديو عالي",
                            style = MaterialTheme.typography.labelSmall,
                            color = Secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = DarkSurfaceContainerHighest)
            Spacer(modifier = Modifier.height(8.dp))

            Text("اختر سورة للاستماع المباشر:", style = MaterialTheme.typography.bodyMedium, color = OnSurface)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(QuranRepository.surahs) { surah ->
                    Card(
                        onClick = { onSelectSurah(surah) },
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainerLow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(28.dp).background(DarkSurfaceContainerHigh, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${surah.number}", style = MaterialTheme.typography.labelSmall, color = Primary)
                                }
                                Text(
                                    "سورة ${surah.nameArabic}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = OnSurface
                                )
                            }
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Primary)
                        }
                    }
                }
            }
        }
    }
}
