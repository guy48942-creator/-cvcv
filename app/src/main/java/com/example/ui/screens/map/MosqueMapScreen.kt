package com.example.ui.screens.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.ui.theme.*
import com.google.android.gms.location.LocationServices

data class Mosque(val id: Int, val name: String, val baseLat: Double, val baseLng: Double, val rating: Float, val address: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MosqueMapScreen(navController: NavController) {
    val context = LocalContext.current
    var userLat by remember { mutableDoubleStateOf(24.7136) }
    var userLng by remember { mutableDoubleStateOf(46.6753) }
    var locationStatus by remember { mutableStateOf("جاري رصد المساجد القريبة لموقعك الحالي...") }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        userLat = loc.latitude
                        userLng = loc.longitude
                        locationStatus = "تم تحديد موقعك بدقة • رصد بيوت الله القريبة"
                    }
                }
            } catch (e: SecurityException) {}
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        userLat = loc.latitude
                        userLng = loc.longitude
                        locationStatus = "الموقع الحالي مفعل • رصد المساجد المجاورة"
                    }
                }
            } catch (e: SecurityException) {}
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val mosques = remember {
        listOf(
            Mosque(1, "مسجد الهدى والتقوى", 24.7150, 46.6700, 4.9f, "حي السلام، شارع النور"),
            Mosque(2, "جامع الرحمن الكبير", 24.7200, 46.6800, 4.8f, "الميدان العام، قرب السوق"),
            Mosque(3, "مسجد الفاروق عمر", 24.7000, 46.6600, 4.7f, "طريق المدينة المنورة"),
            Mosque(4, "جامع الإمام البخاري", 24.7300, 46.6900, 4.9f, "حي الأندلس، شارع الروضة"),
            Mosque(5, "مسجد التقوى", 24.7100, 46.6750, 4.6f, "شارع العليا العام"),
            Mosque(6, "جامع الراجحي", 24.7400, 46.7000, 5.0f, "حي النسيم، الدائري الشرقي")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("رادار المساجد القريبة", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Primary)
                        Text("بيوت الله العامرة لحظة بلحظة", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = OnSurface)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Refresh logic */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "تحديث", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkSurface)
                .padding(innerPadding)
        ) {
            // Live Status Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PrimaryContainer.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                    Text(locationStatus, style = MaterialTheme.typography.labelSmall, color = OnSurface)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        "أقرب المساجد المتاحة الآن:",
                        style = MaterialTheme.typography.titleSmall,
                        color = Secondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                items(mosques) { mosque ->
                    val results = FloatArray(1)
                    android.location.Location.distanceBetween(userLat, userLng, mosque.baseLat, mosque.baseLng, results)
                    val distanceMeters = results[0].toInt()
                    val distStr = if (distanceMeters < 1000) "$distanceMeters متر" else String.format("%.1f كم", distanceMeters / 1000.0)

                    MosqueItem(
                        mosqueName = mosque.name,
                        address = mosque.address,
                        rating = mosque.rating,
                        distance = distStr,
                        nextPrayer = "العصر (١٥:٤٥)", // Mocked but relevant to "time"
                        onOpenMap = {
                            val mapUri = Uri.parse("geo:${mosque.baseLat},${mosque.baseLng}?q=${Uri.encode(mosque.name)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                            context.startActivity(mapIntent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MosqueItem(mosqueName: String, address: String, rating: Float, distance: String, nextPrayer: String, onOpenMap: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryContainer.copy(alpha = 0.2f),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Mosque, contentDescription = null, tint = Primary, modifier = Modifier.size(28.dp))
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(mosqueName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = OnSurface)
                    Text(address, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Secondary, modifier = Modifier.size(12.dp))
                            Text("$rating", style = MaterialTheme.typography.labelSmall, color = Secondary)
                        }
                        Text("•", color = OnSurfaceVariant)
                        Text("تبعد $distance", style = MaterialTheme.typography.labelSmall, color = Primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DarkSurfaceContainerHighest, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                    Text("الصلاة القادمة: $nextPrayer", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
                
                Button(
                    onClick = onOpenMap,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer.copy(alpha = 0.3f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("الاتجاهات", style = MaterialTheme.typography.labelSmall, color = Primary)
                }
            }
        }
    }
}
