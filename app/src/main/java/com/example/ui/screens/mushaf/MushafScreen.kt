package com.example.ui.screens.mushaf

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.audio.QuranAudioPlayer
import com.example.data.local.BookmarkEntity
import com.example.data.local.DatabaseProvider
import com.example.data.local.NoteEntity
import com.example.data.model.AyahDetail
import com.example.data.model.SurahInfo
import com.example.data.repository.QuranRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MushafScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { DatabaseProvider.getDatabase(context) }
    val playerState by QuranAudioPlayer.state.collectAsState()

    var currentSurah by remember { mutableStateOf(QuranRepository.surahs[16]) } // Surah 17 (Al-Isra) by default
    val ayahs = remember(currentSurah) { QuranRepository.getAyahsForSurah(currentSurah.number) }

    var selectedAyah by remember { mutableStateOf<AyahDetail?>(null) }
    var activeTabMode by remember { mutableStateOf(0) } // 0: تلاوة مفسرة, 1: مصحوبة, 2: ليلي
    var showSurahSelectorDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var noteInputText by remember { mutableStateOf("") }
    var activeTafsirTab by remember { mutableStateOf(0) } // 0: التفسير الميسر, 1: الإعراب واللغة, 2: الترجمة الإنجليزية

    Scaffold(
        topBar = {
            MushafTopBar(
                surah = currentSurah,
                onOpenSurahSelector = { showSurahSelectorDialog = true },
                onOpenSearch = { showSearchDialog = true }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkSurface)
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sub-header bar (الجزء 15 | الحزب 29 | صفحة 282)
            item {
                MushafSubHeader(
                    surah = currentSurah,
                    activeMode = activeTabMode,
                    onModeSelect = { activeTabMode = it }
                )
            }

            // Mushaf 3D Gilded Frame Container
            item {
                MushafPaperFrame(
                    surah = currentSurah,
                    ayahs = ayahs,
                    selectedAyah = selectedAyah,
                    currentPlayingAyah = if (playerState.currentSurah.number == currentSurah.number && playerState.isPlaying) playerState.currentAyahNumber else null,
                    onAyahClick = { ayah ->
                        selectedAyah = if (selectedAyah?.numberInSurah == ayah.numberInSurah) null else ayah
                    }
                )
            }

            // Bottom Ayah Detail & Tafsir Card (Matches Image 3)
            item {
                selectedAyah?.let { ayah ->
                    AyahDetailAndTafsirCard(
                        ayah = ayah,
                        surah = currentSurah,
                        activeTab = activeTafsirTab,
                        onTabChange = { activeTafsirTab = it },
                        onPlayAyah = {
                            QuranAudioPlayer.playAyah(currentSurah, ayah.numberInSurah)
                        },
                        onCopy = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("Ayah", "${ayah.textArabic} [سورة ${currentSurah.nameArabic}: ${ayah.numberInSurah}]"))
                            Toast.makeText(context, "تم نسخ الآية الكريمة", Toast.LENGTH_SHORT).show()
                        },
                        onShare = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "﴿${ayah.textArabic}﴾\n[سورة ${currentSurah.nameArabic} - آية ${ayah.numberInSurah}]\n\nعبر تطبيق نور الفرقان")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "مشاركة الآية"))
                        },
                        onBookmark = {
                            scope.launch {
                                db.quranDao().insertBookmark(
                                    BookmarkEntity(
                                        surahNumber = currentSurah.number,
                                        surahName = currentSurah.nameArabic,
                                        ayahNumber = ayah.numberInSurah,
                                        ayahText = ayah.textArabic
                                    )
                                )
                                Toast.makeText(context, "تمت إضافة الآية إلى المحفوظات", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onAddNote = {
                            noteInputText = ""
                            showAddNoteDialog = true
                        }
                    )
                }
            }

            // Bottom Spacing for PlaybackDock
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Surah Selector Dialog (All 114 Surahs)
    if (showSurahSelectorDialog) {
        SurahSelectorDialog(
            currentSurah = currentSurah,
            onSurahSelected = { selected ->
                currentSurah = selected
                selectedAyah = null
                showSurahSelectorDialog = false
            },
            onDismiss = { showSurahSelectorDialog = false }
        )
    }

    // Quick Search Dialog
    if (showSearchDialog) {
        MushafSearchDialog(
            onSurahSelected = { selected ->
                currentSurah = selected
                selectedAyah = null
                showSearchDialog = false
            },
            onDismiss = { showSearchDialog = false }
        )
    }

    // Add Reflection Note Dialog
    if (showAddNoteDialog && selectedAyah != null) {
        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            containerColor = DarkSurfaceContainer,
            title = {
                Text(
                    "إضافة خاطرة وتدبر",
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "سورة ${currentSurah.nameArabic} - آية ${selectedAyah?.numberInSurah}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Secondary
                    )
                    OutlinedTextField(
                        value = noteInputText,
                        onValueChange = { noteInputText = it },
                        placeholder = { Text("اكتب تدبرك الشخصي حول هذه الآية...", color = OnSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = DarkSurfaceContainerHighest,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteInputText.isNotBlank()) {
                            scope.launch {
                                db.quranDao().insertNote(
                                    NoteEntity(
                                        surahNumber = currentSurah.number,
                                        surahName = currentSurah.nameArabic,
                                        ayahNumber = selectedAyah!!.numberInSurah,
                                        noteText = noteInputText
                                    )
                                )
                                Toast.makeText(context, "تم حفظ التدبر بنجاح", Toast.LENGTH_SHORT).show()
                                showAddNoteDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("حفظ", color = OnPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoteDialog = false }) {
                    Text("إلغاء", color = OnSurfaceVariant)
                }
            }
        )
    }
}

// -------------------------------------------------------------
// TOP BAR & SUB-HEADER (IMAGE 3 MATCH)
// -------------------------------------------------------------
@Composable
fun MushafTopBar(
    surah: SurahInfo,
    onOpenSurahSelector: () -> Unit,
    onOpenSearch: () -> Unit
) {
    Surface(
        color = DarkSurfaceContainerHigh.copy(alpha = 0.95f),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onOpenSearch,
                modifier = Modifier
                    .background(DarkSurfaceContainerLow, CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "بحث", tint = Primary)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenSurahSelector() }
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "القرآن الكريم - سورة ${surah.nameArabic} (${surah.revelationType})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Primary)
                }
                Text(
                    "الحزب ${surah.hizbNumber} • الجزء ${surah.juzNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Secondary
                )
            }

            IconButton(
                onClick = onOpenSurahSelector,
                modifier = Modifier
                    .background(DarkSurfaceContainerLow, CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.ListAlt, contentDescription = "قائمة السور", tint = Secondary)
            }
        }
    }
}

@Composable
fun MushafSubHeader(
    surah: SurahInfo,
    activeMode: Int,
    onModeSelect: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("الجزء ${surah.juzNumber}", style = MaterialTheme.typography.labelMedium, color = Primary)
                Text("الحزب ${surah.hizbNumber}", style = MaterialTheme.typography.labelMedium, color = Secondary)
                Text("صفحة ${surah.pageNumber}", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
            }

            // Mode Selector Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val modes = listOf("تلاوة مفسرة", "مصحوبة بالترجمة", "قراءة ليلية")
                modes.forEachIndexed { idx, label ->
                    val isSelected = activeMode == idx
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onModeSelect(idx) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) PrimaryContainer.copy(alpha = 0.35f) else DarkSurfaceContainerLow,
                        border = BorderStroke(1.dp, if (isSelected) Primary else Color.Transparent)
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) Primary else OnSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3D GILDED MUSHAF FRAME WITH DECORATIVE BORDERS
// -------------------------------------------------------------
@Composable
fun MushafPaperFrame(
    surah: SurahInfo,
    ayahs: List<AyahDetail>,
    selectedAyah: AyahDetail?,
    currentPlayingAyah: Int?,
    onAyahClick: (AyahDetail) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
        border = BorderStroke(2.dp, Brush.linearGradient(listOf(Secondary, Primary, Secondary)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Surah Title Cartouche (Placard)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                DarkSurfaceContainerLow,
                                DarkSurfaceContainerHighest,
                                DarkSurfaceContainerLow
                            )
                        )
                    )
                    .border(1.dp, Secondary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "سُورَةُ ${surah.nameArabic}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = Secondary
                    )
                    Text(
                        text = "آياتها ${surah.totalVerses} • ترتيبها ${surah.number} • نزلت بـ${if (surah.revelationType == "مكية") "مكة المكرمة" else "المدينة المنورة"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Basmalah (unless Surah At-Tawbah)
            if (surah.number != 9) {
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp
                    ),
                    color = Primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Flowing Quran Verses
            ayahs.forEach { ayah ->
                val isSelected = selectedAyah?.numberInSurah == ayah.numberInSurah
                val isPlaying = currentPlayingAyah == ayah.numberInSurah

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                isPlaying -> PrimaryContainer.copy(alpha = 0.35f)
                                isSelected -> SecondaryContainer.copy(alpha = 0.2f)
                                else -> Color.Transparent
                            }
                        )
                        .clickable { onAyahClick(ayah) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Number Cartouche Icon
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (isPlaying || isSelected) Primary.copy(alpha = 0.2f) else DarkSurfaceContainerLow,
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    if (isPlaying || isSelected) Primary else Secondary.copy(alpha = 0.3f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${ayah.numberInSurah}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isPlaying || isSelected) Primary else Secondary
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Uthmanic Verse Text
                        Text(
                            text = ayah.textArabic,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 21.sp,
                                lineHeight = 38.sp,
                                textAlign = TextAlign.Right
                            ),
                            color = when {
                                isPlaying -> Primary
                                isSelected -> Secondary
                                else -> OnSurface
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// AYAH DETAIL, TAFSIR & ACTION BAR (IMAGE 3 MATCH)
// -------------------------------------------------------------
@Composable
fun AyahDetailAndTafsirCard(
    ayah: AyahDetail,
    surah: SurahInfo,
    activeTab: Int,
    onTabChange: (Int) -> Unit,
    onPlayAyah: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onBookmark: () -> Unit,
    onAddNote: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Action Bar (Buttons: Play, Tafsir, Copy, Share, Bookmark, Note)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AyahActionButton(
                    icon = Icons.Default.PlayArrow,
                    label = "استماع",
                    tint = Primary,
                    onClick = onPlayAyah
                )
                AyahActionButton(
                    icon = Icons.Default.BookmarkBorder,
                    label = "حفظ",
                    tint = Secondary,
                    onClick = onBookmark
                )
                AyahActionButton(
                    icon = Icons.Default.EditNote,
                    label = "تدبر",
                    tint = Color(0xFF64B5F6),
                    onClick = onAddNote
                )
                AyahActionButton(
                    icon = Icons.Default.ContentCopy,
                    label = "نسخ",
                    tint = OnSurface,
                    onClick = onCopy
                )
                AyahActionButton(
                    icon = Icons.Default.Share,
                    label = "مشاركة",
                    tint = OnSurface,
                    onClick = onShare
                )
            }

            HorizontalDivider(color = DarkSurfaceContainerHighest)

            // Tabs: التفسير الميسر | إعراب ولغة | ترجمة إنجليزية
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf("التفسير الميسر", "الإعراب واللغة", "English Translation")
                tabs.forEachIndexed { i, title ->
                    val isSel = activeTab == i
                    FilterChip(
                        selected = isSel,
                        onClick = { onTabChange(i) },
                        label = { Text(title, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryContainer.copy(alpha = 0.4f),
                            selectedLabelColor = Primary
                        )
                    )
                }
            }

            // Content according to active tab
            when (activeTab) {
                0 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "التفسير الميسر (سورة ${surah.nameArabic} - آية ${ayah.numberInSurah}):",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Primary
                        )
                        Text(
                            ayah.tafsirMuyassar,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 26.sp),
                            color = OnSurface
                        )
                    }
                }
                1 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "الإعراب والفوائد البيانية:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Secondary
                        )
                        Text(
                            ayah.irabSummary,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 26.sp),
                            color = OnSurface
                        )
                    }
                }
                2 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "Sahih International Translation:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF64B5F6)
                        )
                        Text(
                            ayah.translationEn,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                            color = OnSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AyahActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(tint.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurface)
    }
}

// -------------------------------------------------------------
// SURAH SELECTOR DIALOG (114 SURAHS)
// -------------------------------------------------------------
@Composable
fun SurahSelectorDialog(
    currentSurah: SurahInfo,
    onSurahSelected: (SurahInfo) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) QuranRepository.surahs
        else QuranRepository.surahs.filter {
            it.nameArabic.contains(searchQuery) || it.nameEnglish.contains(searchQuery, ignoreCase = true) || it.number.toString() == searchQuery
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceContainer,
        title = {
            Text("اختر سورة كريمة (114 سورة)", color = Primary, style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث برقم أو اسم السورة...", color = OnSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Primary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = DarkSurfaceContainerHighest
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filteredSurahs) { s ->
                        Card(
                            onClick = { onSurahSelected(s) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (s.number == currentSurah.number) PrimaryContainer.copy(alpha = 0.35f) else DarkSurfaceContainerLow
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(28.dp).background(Secondary.copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${s.number}", style = MaterialTheme.typography.labelSmall, color = Secondary)
                                    }
                                    Column {
                                        Text("سورة ${s.nameArabic}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = OnSurface)
                                        Text("${s.nameEnglish} • ${s.revelationType}", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                }
                                Text("${s.totalVerses} آية", style = MaterialTheme.typography.labelSmall, color = Primary)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = Primary)
            }
        }
    )
}

// -------------------------------------------------------------
// SEARCH DIALOG FOR SURAHS & WORDS
// -------------------------------------------------------------
@Composable
fun MushafSearchDialog(
    onSurahSelected: (SurahInfo) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) {
        if (query.isBlank()) emptyList()
        else QuranRepository.surahs.filter {
            it.nameArabic.contains(query) || it.nameEnglish.contains(query, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceContainer,
        title = {
            Text("البحث السريع في القرآن الكريم", color = Primary, style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("اكتب اسم السورة أو الآية...", color = OnSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Primary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = DarkSurfaceContainerHighest
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (results.isEmpty() && query.isNotBlank()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("لم يتم العثور على نتائج", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(results) { s ->
                            Card(
                                onClick = { onSurahSelected(s) },
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainerLow),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("سورة ${s.nameArabic}", style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                                    Text("صفحة ${s.pageNumber}", style = MaterialTheme.typography.labelSmall, color = Secondary)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Primary)
            }
        }
    )
}
