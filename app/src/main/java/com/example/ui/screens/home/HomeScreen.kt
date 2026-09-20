package com.example.ui.screens.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.audio.QuranAudioPlayer
import com.example.data.audio.SalawatNotificationManager
import com.example.data.repository.QuranRepository
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    var showTasbeehDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            HomeHeader(
                onNotificationClick = {
                    SalawatNotificationManager.triggerSalawatAudio(showMessage = true)
                }
            )
        }
        item { PrayerTimesCard() }
        item {
            QuickActions(
                navController = navController,
                onOpenTasbeeh = { showTasbeehDialog = true },
                onTriggerSalawat = {
                    SalawatNotificationManager.triggerSalawatAudio(showMessage = true)
                }
            )
        }
        item { LastReadCard(navController) }
        item { SpiritualNightSanctuaryBanner(navController) }
        item { DailyInspirationCard() }
        item { Spacer(modifier = Modifier.height(70.dp)) }
    }

    if (showTasbeehDialog) {
        ElectronicTasbeehDialog(onDismiss = { showTasbeehDialog = false })
    }
}

@Composable
fun HomeHeader(onNotificationClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(DarkSurfaceContainerHighest, DarkSurfaceContainer)
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "السلام عليكم ورحمة الله",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant
                )
                Text(
                    text = "نور الفرقان",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Primary
                )
            }
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .background(DarkSurfaceContainerLow, CircleShape)
                    .size(44.dp)
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = "الصلاة على النبي", tint = Secondary)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "﴿ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ ﴾",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 24.sp),
            color = Secondary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "سورة الرعد • الآية ٢٨",
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
fun PrayerTimesCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Primary)
                    Text("مواقيت الصلاة اليوم", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = OnSurface)
                }
                Surface(
                    shape = CircleShape,
                    color = PrimaryContainer.copy(alpha = 0.3f)
                ) {
                    Text(
                        "الصلاة القادمة: العصر",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PrayerTimeItem("الفجر", "04:32", false)
                PrayerTimeItem("الشروق", "05:56", false)
                PrayerTimeItem("الظهر", "12:15", false)
                PrayerTimeItem("العصر", "15:45", true)
                PrayerTimeItem("المغرب", "18:22", false)
                PrayerTimeItem("العشاء", "19:50", false)
            }
        }
    }
}

@Composable
fun PrayerTimeItem(name: String, time: String, isActive: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) PrimaryContainer.copy(alpha = 0.25f) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(name, style = MaterialTheme.typography.labelSmall, color = if (isActive) Primary else OnSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            time,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal),
            color = if (isActive) Primary else OnSurface
        )
        if (isActive) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(6.dp)
                    .background(Primary, CircleShape)
            )
        }
    }
}

@Composable
fun QuickActions(
    navController: NavController,
    onOpenTasbeeh: () -> Unit,
    onTriggerSalawat: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                label = "اتجاه القبلة",
                icon = Icons.Default.Explore,
                color = Secondary,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.Qibla.route) }
            )
            QuickActionButton(
                label = "المساجد القريبة",
                icon = Icons.Default.Mosque,
                color = Color(0xFF64B5F6),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.MosqueMap.route) }
            )
            QuickActionButton(
                label = "السبحة والأذكار",
                icon = Icons.Default.SelfImprovement,
                color = Primary,
                modifier = Modifier.weight(1f),
                onClick = onOpenTasbeeh
            )
        }

        // Special Quick Banner: "صلّ على النبي ﷺ"
        Card(
            onClick = onTriggerSalawat,
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainerHigh),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PrimaryContainer.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                    }
                    Text(
                        "اضغط هنا لسماع: اللَّهُمَّ صَلِّ عَلَى نَبِيِّنَا مُحَمَّدٍ ﷺ",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                }
                Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Primary)
            }
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceContainer),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurface)
        }
    }
}

@Composable
fun LastReadCard(navController: NavController) {
    Card(
        onClick = { navController.navigate(Screen.Mushaf.route) },
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainerHigh),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Secondary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(PrimaryContainer.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = Primary, modifier = Modifier.size(28.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("متابعة القراءة والورد اليومي", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                Text("سورة الإسراء", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = OnSurface)
                Text("آية ١ • الجزء ١٥ • صفحة ٢٨٢", style = MaterialTheme.typography.labelSmall, color = Secondary)
            }
            FilledIconButton(
                onClick = { navController.navigate(Screen.Mushaf.route) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = PrimaryContainer.copy(alpha = 0.4f))
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Primary)
            }
        }
    }
}

@Composable
fun SpiritualNightSanctuaryBanner(navController: NavController) {
    Card(
        onClick = { navController.navigate(Screen.Bookmarks.route) },
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
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
                    .background(SecondaryContainer.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Bedtime, contentDescription = null, tint = Secondary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "مؤقت السكينة والتهجد الليلي",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )
                Text(
                    "استمع للقرآن قبل النوم مع ميزة التلاشي الهادئ وإيقاف الشاشة",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }
            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Secondary)
        }
    }
}

@Composable
fun DailyInspirationCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainerLow),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(Icons.Default.Spa, contentDescription = null, tint = Secondary, modifier = Modifier.size(30.dp))
            Column {
                Text("درر الهدى والسكينة", style = MaterialTheme.typography.titleSmall, color = Secondary)
                Text(
                    "«خيركم من تعلم القرآن وعلمه» - صحيح البخاري",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

// -------------------------------------------------------------
// ELECTRONIC TASBEEH & AZKAR DIALOG
// -------------------------------------------------------------
@Composable
fun ElectronicTasbeehDialog(onDismiss: () -> Unit) {
    var count by remember { mutableIntStateOf(0) }
    var currentZikrIndex by remember { mutableIntStateOf(0) }
    val azkar = listOf(
        "سُبْحَانَ اللَّهِ",
        "الْحَمْدُ لِلَّهِ",
        "لَا إِلَهَ إِلَّا اللَّهُ",
        "اللَّهُ أَكْبَرُ",
        "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
        "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
        "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceContainer,
        title = {
            Text("السبحة الإلكترونية والأذكار", color = Primary, style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Current Zikr phrase
                Text(
                    text = azkar[currentZikrIndex],
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Secondary,
                    textAlign = TextAlign.Center
                )

                // Large Interactive Counter Button
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(PrimaryContainer.copy(alpha = 0.35f), CircleShape)
                        .clip(CircleShape)
                        .clickable { count++ }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Primary
                        )
                        Text("اضغط للتسبيح", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                }

                // Selector for next Zikr and Reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { count = 0 },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = OnSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تصفير", color = OnSurfaceVariant)
                    }

                    Button(
                        onClick = {
                            currentZikrIndex = (currentZikrIndex + 1) % azkar.size
                            count = 0
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("الذكر التالي", color = OnPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = OnPrimary)
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
