package com.example.ui.screens.qibla

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.ui.theme.*
import com.google.android.gms.location.LocationServices
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(navController: NavController) {
    val context = LocalContext.current
    var azimuth by remember { mutableFloatStateOf(0f) }
    var userLat by remember { mutableDoubleStateOf(24.7136) } // Default Riyadh / fallback
    var userLng by remember { mutableDoubleStateOf(46.6753) }
    var qiblaAngle by remember { mutableFloatStateOf(136f) }
    var locationStatusText by remember { mutableStateOf("جاري تحديد موقعك الجغرافي عبر الأقمار الصناعية...") }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        userLat = location.latitude
                        userLng = location.longitude
                        // Calculate Great-circle bearing to Kaaba (21.4225, 39.8262)
                        val kaabaLat = Math.toRadians(21.4225)
                        val kaabaLng = Math.toRadians(39.8262)
                        val latRad = Math.toRadians(userLat)
                        val lngRad = Math.toRadians(userLng)

                        val dLng = kaabaLng - lngRad
                        val y = sin(dLng) * cos(kaabaLat)
                        val x = cos(latRad) * sin(kaabaLat) - sin(latRad) * cos(kaabaLat) * cos(dLng)
                        var bearing = Math.toDegrees(atan2(y, x)).toFloat()
                        bearing = (bearing + 360) % 360
                        qiblaAngle = bearing
                        locationStatusText = "تم تحديث الموقع بنجاح (خط العرض: ${String.format("%.2f", userLat)}, خط الطول: ${String.format("%.2f", userLng)})"
                    }
                }
            } catch (e: SecurityException) {
                locationStatusText = "تعذر الوصول للموقع الحالي"
            }
        } else {
            locationStatusText = "تم استخدام الإحداثيات الافتراضية لعدم تفعيل إذن الموقع"
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        userLat = location.latitude
                        userLng = location.longitude
                        val kaabaLat = Math.toRadians(21.4225)
                        val kaabaLng = Math.toRadians(39.8262)
                        val latRad = Math.toRadians(userLat)
                        val lngRad = Math.toRadians(userLng)
                        val dLng = kaabaLng - lngRad
                        val y = sin(dLng) * cos(kaabaLat)
                        val x = cos(latRad) * sin(kaabaLat) - sin(latRad) * cos(kaabaLat) * cos(dLng)
                        var bearing = Math.toDegrees(atan2(y, x)).toFloat()
                        bearing = (bearing + 360) % 360
                        qiblaAngle = bearing
                        locationStatusText = "الموقع الحالي: (${String.format("%.2f", userLat)}°, ${String.format("%.2f", userLng)}°)"
                    }
                }
            } catch (e: SecurityException) {}
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val listener = object : SensorEventListener {
            private val rotationMatrix = FloatArray(9)
            private val orientation = FloatArray(3)

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val azimuthInDegrees = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    azimuth = (azimuthInDegrees + 360) % 360
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationVectorSensor != null) {
            sensorManager.registerListener(listener, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val animatedRotation by animateFloatAsState(targetValue = -azimuth, label = "compassRotation")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تحديد اتجاه القبلة بدقة", color = Primary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = OnSurface)
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "الكعبة المشرفة - مكة المكرمة (القبلة الحية)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Secondary
            )

            Text(
                locationStatusText,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Compass Dial Container
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceContainer)
                    .border(2.dp, Brush.sweepGradient(listOf(Secondary, Primary, Secondary)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize().rotate(animatedRotation)) {
                    drawCircle(
                        color = DarkSurfaceContainerHighest,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )

                    for (i in 0 until 360 step 30) {
                        val angleRad = Math.toRadians(i.toDouble())
                        val r1 = size.width / 2 - 8.dp.toPx()
                        val r2 = size.width / 2 - 20.dp.toPx()
                        val x1 = center.x + (r1 * kotlin.math.sin(angleRad)).toFloat()
                        val y1 = center.y - (r1 * kotlin.math.cos(angleRad)).toFloat()
                        val x2 = center.x + (r2 * kotlin.math.sin(angleRad)).toFloat()
                        val y2 = center.y - (r2 * kotlin.math.cos(angleRad)).toFloat()

                        drawLine(
                            color = if (i % 90 == 0) Primary else OnSurfaceVariant.copy(alpha = 0.5f),
                            start = androidx.compose.ui.geometry.Offset(x1, y1),
                            end = androidx.compose.ui.geometry.Offset(x2, y2),
                            strokeWidth = if (i % 90 == 0) 3.dp.toPx() else 1.5.dp.toPx()
                        )
                    }
                }

                // Rotating Kaaba pointer needle
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .rotate(animatedRotation + qiblaAngle),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "موقع القبلة",
                            tint = Secondary,
                            modifier = Modifier.size(42.dp)
                        )
                        Text(
                            "القبلة",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Secondary
                        )
                    }
                }

                // Center Degree Text
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${azimuth.toInt()}°",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = Primary
                    )
                    Text(
                        "زاوية القبلة: ${qiblaAngle.toInt()}°",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            // Guidance Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier.size(44.dp).background(PrimaryContainer.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CompassCalibration, contentDescription = null, tint = Primary)
                    }
                    Column {
                        Text("معايرة بوصلة القبلة", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Primary)
                        Text(
                            "يتم حساب اتجاه القبلة استناداً إلى موقعك الجغرافي الحالي وحساسات الجهاز بدقة تامة.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

