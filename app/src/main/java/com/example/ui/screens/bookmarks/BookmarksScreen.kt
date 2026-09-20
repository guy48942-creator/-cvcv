package com.example.ui.screens.bookmarks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.audio.QuranAudioPlayer
import com.example.data.audio.SalawatNotificationManager
import com.example.data.local.BookmarkEntity
import com.example.data.local.DatabaseProvider
import com.example.data.local.NoteEntity
import com.example.data.model.AudioQualityPreset
import com.example.data.repository.QuranRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("مؤقت السكينة والتحميلات", "المحفوظات والملاحظات", "الصلاة على النبي ﷺ")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
    ) {
        // App Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(DarkSurfaceContainerHighest, DarkSurface)
                    )
                )
                .padding(top = 16.dp, bottom = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "مستودع السكينة والتهجد",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Primary
                )
                Text(
                    text = "مؤقت النوم • التلاوات المحملة • المحفوظات",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }
        }

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurfaceContainerHigh,
            contentColor = Primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedTab == index) Primary else OnSurfaceVariant
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> ZenTimerAndStorageContent()
            1 -> BookmarksAndNotesContent()
            2 -> SalawatNotificationContent()
        }
    }
}

// -------------------------------------------------------------
// IMAGE 1 RECREATION: ZEN SLEEP TIMER & STORAGE SANCTUARY
// -------------------------------------------------------------
@Composable
fun ZenTimerAndStorageContent() {
    val playerState by QuranAudioPlayer.state.collectAsState()
    var fullQuranDownloading by remember { mutableStateOf(false) }
    var fullQuranProgress by remember { mutableStateOf(0.42f) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Zen Sleep Timer Card with Dial
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Bedtime, contentDescription = null, tint = Secondary)
                            Text(
                                "مؤقت السكينة والتهجد",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = OnSurface
                            )
                        }
                        if (playerState.isSleepTimerActive) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryContainer.copy(alpha = 0.3f),
                                contentColor = Primary
                            ) {
                                Text(
                                    "نشط الآن",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

                    // Circular Zen Timer Dial
                    Box(
                        modifier = Modifier.size(190.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Track background circle
                            drawCircle(
                                color = DarkSurfaceContainerHighest,
                                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Progress arc
                            val sweepAngle = if (playerState.isSleepTimerActive) {
                                val remaining = playerState.sleepTimerRemainingSec.coerceAtLeast(0)
                                (remaining % 3600f) / 3600f * 360f
                            } else 270f

                            drawArc(
                                brush = Brush.sweepGradient(listOf(Secondary, Primary, Secondary)),
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val timeText = if (playerState.isSleepTimerActive) {
                                val m = playerState.sleepTimerRemainingSec / 60
                                val s = playerState.sleepTimerRemainingSec % 60
                                String.format("%02d:%02d", m, s)
                            } else "28:35"

                            Text(
                                text = timeText,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Primary
                            )
                            Text(
                                text = if (playerState.isSleepTimerActive) "متبقٍ حتى السكون" else "اضغط للبدء",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }

                    // Controls under timer dial
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledIconButton(
                            onClick = {
                                if (playerState.isSleepTimerActive) {
                                    QuranAudioPlayer.cancelSleepTimer()
                                } else {
                                    QuranAudioPlayer.startSleepTimer(30)
                                }
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Primary),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Icon(
                                if (playerState.isSleepTimerActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = OnPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        OutlinedButton(
                            onClick = { QuranAudioPlayer.addMinutesToSleepTimer(5) },
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.4f))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+5 دقائق", color = Primary)
                        }
                    }

                    // Preset intervals pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val presets = listOf(15, 30, 45)
                        presets.forEach { min ->
                            FilterChip(
                                selected = playerState.isSleepTimerActive && (playerState.sleepTimerRemainingSec / 60) in (min - 2..min),
                                onClick = { QuranAudioPlayer.startSleepTimer(min) },
                                label = { Text("$min دقيقة") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryContainer.copy(alpha = 0.4f),
                                    selectedLabelColor = Primary
                                )
                            )
                        }
                        FilterChip(
                            selected = playerState.isSmartStopAtSurahEnd,
                            onClick = {
                                QuranAudioPlayer.toggleSmartStopAtSurahEnd(!playerState.isSmartStopAtSurahEnd)
                            },
                            label = { Text("نهاية السورة") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SecondaryContainer.copy(alpha = 0.4f),
                                selectedLabelColor = Secondary
                            )
                        )
                    }

                    HorizontalDivider(color = DarkSurfaceContainerHighest)

                    // Smart Sleep toggles
                    SleepSettingToggle(
                        title = "التوقف الذكي بانتهاء السورة",
                        subtitle = "ينهي التلاوة مع ختام آخر آية دون قطع مفاجئ للخشوع",
                        checked = playerState.isSmartStopAtSurahEnd,
                        onCheckedChange = { QuranAudioPlayer.toggleSmartStopAtSurahEnd(it) }
                    )

                    SleepSettingToggle(
                        title = "التلاشي الهادئ (Fade-Out)",
                        subtitle = "انخفاض تدريجي لطيف لمستوى الصوت في آخر 5 دقائق",
                        checked = playerState.isFadeOutEnabled,
                        onCheckedChange = { QuranAudioPlayer.toggleFadeOut(it) }
                    )

                    SleepSettingToggle(
                        title = "شاشة السكون المعتمة",
                        subtitle = "تحويل الواجهة لنمط الحبر الليلي لتوفير الطاقة وتقليل وهج الإضاءة",
                        checked = playerState.isDeepSleepDimming,
                        onCheckedChange = { QuranAudioPlayer.toggleDeepSleepDimming(it) }
                    )
                }
            }
        }

        // 2. Offline Sanctuary Storage Dashboard
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Primary)
                            Text(
                                "مستودع السكينة دون إنترنت",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = OnSurface
                            )
                        }
                    }

                    // Storage text & bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("المساحة المشغولة للمصحف: 2.4 GB", style = MaterialTheme.typography.labelSmall, color = Primary)
                        Text("المتبقية: 48.6 GB", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }

                    // Multi-segmented bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                    ) {
                        Box(modifier = Modifier.weight(0.65f).fillMaxHeight().background(Primary))
                        Box(modifier = Modifier.weight(0.15f).fillMaxHeight().background(Secondary))
                        Box(modifier = Modifier.weight(0.10f).fillMaxHeight().background(Color(0xFF64B5F6)))
                        Box(modifier = Modifier.weight(0.10f).fillMaxHeight().background(DarkSurfaceContainerHighest))
                    }

                    // Audio Quality Selector
                    Text("جودة الصوت المفضل للتحميل والاستماع:", style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AudioQualityPreset.values().forEach { quality ->
                            val isSelected = playerState.audioQuality == quality
                            OutlinedButton(
                                onClick = { QuranAudioPlayer.setAudioQuality(quality) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) PrimaryContainer.copy(alpha = 0.3f) else Color.Transparent
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) Primary else DarkSurfaceContainerHighest
                                )
                            ) {
                                Text(
                                    quality.titleArabic,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Primary else OnSurfaceVariant
                                )
                            }
                        }
                    }

                    // Full Reciter Pack Card (Al-Husary 114 Surahs)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainerHigh),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Secondary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(SecondaryContainer.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DownloadForOffline, contentDescription = null, tint = Secondary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "مصحف الشيخ الحصري كاملاً",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = OnSurface
                                )
                                Text(
                                    "114 سورة • رواية حفص • 1.8 جيجابايت",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant
                                )
                                if (fullQuranDownloading) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { fullQuranProgress },
                                        modifier = Modifier.fillMaxWidth().height(4.dp),
                                        color = Secondary,
                                        trackColor = DarkSurfaceContainerHighest
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    fullQuranDownloading = !fullQuranDownloading
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryContainer),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    if (fullQuranDownloading) "جار التحميل" else "تحميل المصحف",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSecondaryContainer
                                )
                            }
                        }
                    }

                    // Active & Downloaded Surahs List
                    Text("السور المحملة على الهاتف:", style = MaterialTheme.typography.titleSmall, color = OnSurface)
                    
                    DownloadedSurahItem(
                        surahName = "سورة الكهف",
                        reciterName = "الشيخ مشاري راشد العفاسي",
                        fileSize = "24 ميجابايت",
                        statusText = "جاهزة للاستماع دون إنترنت",
                        isCompleted = true,
                        onPlay = {
                            QuranAudioPlayer.playSurah(QuranRepository.surahs[17]) // Surah 18
                        }
                    )

                    DownloadedSurahItem(
                        surahName = "سورة مريم",
                        reciterName = "الشيخ عبد الباسط عبد الصمد",
                        fileSize = "18 ميجابايت",
                        statusText = "جار التحميل 68%",
                        isCompleted = false,
                        progress = 0.68f
                    )

                    DownloadedSurahItem(
                        surahName = "سورة طه",
                        reciterName = "الشيخ محمود خليل الحصري",
                        fileSize = "21 ميجابايت",
                        statusText = "جاهزة للاستماع دون إنترنت",
                        isCompleted = true,
                        onPlay = {
                            QuranAudioPlayer.playSurah(QuranRepository.surahs[19]) // Surah 20
                        }
                    )
                }
            }
        }

        // 3. Spiritual Night Quote Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainerLow),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "أُنْسُ اللَّيْلِ وَالْقُرْآنِ",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Secondary
                    )
                    Text(
                        "«أفضل الصلاة بعد الفريضة صلاة الليل، وأفضل الصيام بعد رمضان صيام شهر الله المحرم»",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = OnSurface
                    )
                    Text(
                        "- رواه مسلم -",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SleepSettingToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = OnSurface)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OnPrimary,
                checkedTrackColor = Primary,
                uncheckedTrackColor = DarkSurfaceContainerHighest
            )
        )
    }
}

@Composable
fun DownloadedSurahItem(
    surahName: String,
    reciterName: String,
    fileSize: String,
    statusText: String,
    isCompleted: Boolean,
    progress: Float = 1.0f,
    onPlay: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainerHigh),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(surahName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = OnSurface)
                    Text("$reciterName • $fileSize", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    Text(statusText, style = MaterialTheme.typography.labelSmall, color = if (isCompleted) Primary else Secondary)
                }
                if (isCompleted) {
                    IconButton(
                        onClick = onPlay,
                        modifier = Modifier.background(Primary.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "تشغيل", tint = Primary)
                    }
                } else {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(28.dp),
                        color = Secondary,
                        strokeWidth = 3.dp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: BOOKMARKS AND NOTES CONTENT
// -------------------------------------------------------------
@Composable
fun BookmarksAndNotesContent() {
    val context = LocalContext.current
    val db = remember { DatabaseProvider.getDatabase(context) }
    val scope = rememberCoroutineScope()

    val bookmarks by db.quranDao().getAllBookmarks().collectAsState(initial = emptyList())
    val notes by db.quranDao().getAllNotes().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "الآيات المحفوظة (${bookmarks.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Primary
            )
        }

        if (bookmarks.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("لا توجد آيات محفوظة حالياً", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                        Text("يمكنك حفظ أي آية أثناء القراءة في المصحف الشريف", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                }
            }
        } else {
            items(bookmarks) { b ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "سورة ${b.surahName} • آية ${b.ayahNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Primary
                            )
                            IconButton(onClick = {
                                scope.launch { db.quranDao().deleteBookmark(b) }
                            }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = OnSurfaceVariant)
                            }
                        }
                        Text(b.ayahText, style = MaterialTheme.typography.bodyLarge, color = OnSurface)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "ملاحظاتي وتدبراتي (${notes.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Secondary
            )
        }

        if (notes.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("لا توجد خواطر أو ملاحظات مسجلة", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                        Text("أضف خواطرك حول الآيات لتسجيل تدبراتك اليومية", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                }
            }
        } else {
            items(notes) { note ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "خاطرة على سورة ${note.surahName} (آية ${note.ayahNumber})",
                                style = MaterialTheme.typography.labelMedium,
                                color = Secondary
                            )
                            IconButton(onClick = {
                                scope.launch { db.quranDao().deleteNote(note) }
                            }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = OnSurfaceVariant)
                            }
                        }
                        Text(note.noteText, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: SALAWAT NOTIFICATION SYSTEM ("صل على محمد")
// -------------------------------------------------------------
@Composable
fun SalawatNotificationContent() {
    val settings by SalawatNotificationManager.settings.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(PrimaryContainer.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Primary)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "التنبيه الصوتي: صلّ على محمد ﷺ",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = OnSurface
                            )
                            Text(
                                "تذكير صوتي دوري لتعطير لسانك بالصلاة على النبي",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(color = DarkSurfaceContainerHighest)

                    // Enable / Disable Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("تفعيل التذكير الدوري", style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                        Switch(
                            checked = settings.isEnabled,
                            onCheckedChange = {
                                SalawatNotificationManager.updateSettings(
                                    isEnabled = it,
                                    intervalMinutes = settings.intervalMinutes,
                                    isVoiceEnabled = settings.isVoiceEnabled
                                )
                            }
                        )
                    }

                    // Voice Audio Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("النطق الصوتي بالعبارة", style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                        Switch(
                            checked = settings.isVoiceEnabled,
                            onCheckedChange = {
                                SalawatNotificationManager.updateSettings(
                                    isEnabled = settings.isEnabled,
                                    intervalMinutes = settings.intervalMinutes,
                                    isVoiceEnabled = it
                                )
                            }
                        )
                    }

                    // Interval Selection
                    Text("تكرار التنبيه كل:", style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val intervals = listOf(15, 30, 45, 60)
                        intervals.forEach { mins ->
                            FilterChip(
                                selected = settings.intervalMinutes == mins,
                                onClick = {
                                    SalawatNotificationManager.updateSettings(
                                        isEnabled = settings.isEnabled,
                                        intervalMinutes = mins,
                                        isVoiceEnabled = settings.isVoiceEnabled
                                    )
                                },
                                label = { Text("$mins دقيقة") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryContainer.copy(alpha = 0.4f),
                                    selectedLabelColor = Primary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Voice Selection
                    Text("اختر الصوت المفضل للتنبيه:", style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val voiceNames = listOf("صوت ١", "صوت ٢", "صوت ٣", "صوت ٤")
                        voiceNames.forEachIndexed { index, name ->
                            val isSelected = settings.selectedVoiceIndex == index
                            OutlinedButton(
                                onClick = {
                                    SalawatNotificationManager.updateSettings(
                                        isEnabled = settings.isEnabled,
                                        intervalMinutes = settings.intervalMinutes,
                                        isVoiceEnabled = settings.isVoiceEnabled,
                                        voiceIndex = index
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) PrimaryContainer.copy(alpha = 0.3f) else Color.Transparent
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) Primary else DarkSurfaceContainerHighest
                                )
                            ) {
                                Text(
                                    name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Primary else OnSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Test Audio Button
                    Button(
                        onClick = {
                            SalawatNotificationManager.triggerSalawatAudio(showMessage = true)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = OnPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "تجربة التنبيه الصوتي الآن (صلى على محمد)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = OnPrimary
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainerLow),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("فضل الصلاة على النبي ﷺ", style = MaterialTheme.typography.titleSmall, color = Secondary)
                    Text(
                        "قال رسول الله ﷺ: «مَنْ صَلَّى عَلَيَّ صَلَاةً صَلَّى اللَّهُ عَلَيْهِ بِهَا عَشْرًا» - رواه مسلم.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurface
                    )
                }
            }
        }
    }
}
