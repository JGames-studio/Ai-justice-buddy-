package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun GPSSafetyScreen(viewModel: LawViewModel) {
    val context = LocalContext.current
    val latitude by viewModel.latitude.collectAsState()
    val longitude by viewModel.longitude.collectAsState()
    val gpsState by viewModel.gpsState.collectAsState()
    val city by viewModel.currentCity.collectAsState()
    val state by viewModel.currentState.collectAsState()
    val temp by viewModel.temperature.collectAsState()
    val weatherCond by viewModel.weatherCondition.collectAsState()

    val nearbyFirms by viewModel.nearbyLawFirms.collectAsState()
    val crimeAlerts by viewModel.localCrimeAlerts.collectAsState()
    val pedophileAlerts by viewModel.localPedophileAlerts.collectAsState()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            viewModel.startGpsUpdates()
        }
    }

    // Trigger update on start if allowed
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            viewModel.startGpsUpdates()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // GPS Status Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(24.dp),
                border = CardStrokeHelper.cardStroke()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GpsFixed,
                                contentDescription = "GPS status",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = gpsState,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "$city, $state",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        // Permission Trigger or Active Dot
                        if (!hasLocationPermission) {
                            Button(
                                onClick = { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Allow GPS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E)) // Green 500
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Live Stream", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF15803D))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("Latitude", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            Text(String.format("%.6f", latitude), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Longitude", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            Text(String.format("%.6f", longitude), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Local Weather", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            Text("$temp - $weatherCond", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Safety Radar Graphic Map
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = CardStrokeHelper.cardStroke()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Custom Draw Safety Radar Map
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        
                        // Concentric security radar rings
                        drawCircle(
                            color = Color(0xFF818CF8).copy(alpha = 0.1f),
                            radius = size.minDimension / 2.5f,
                            center = center
                        )
                        drawCircle(
                            color = Color(0xFF818CF8).copy(alpha = 0.2f),
                            radius = size.minDimension / 4f,
                            center = center
                        )
                        drawCircle(
                            color = Color(0xFF818CF8).copy(alpha = 0.3f),
                            radius = size.minDimension / 7f,
                            center = center
                        )

                        // Crosshairs
                        drawLine(
                            color = Color(0xFF818CF8).copy(alpha = 0.3f),
                            start = Offset(0f, center.y),
                            end = Offset(size.width, center.y),
                            strokeWidth = 2f
                        )
                        drawLine(
                            color = Color(0xFF818CF8).copy(alpha = 0.3f),
                            start = Offset(center.x, 0f),
                            end = Offset(center.x, size.height),
                            strokeWidth = 2f
                        )

                        // Pins (Green = Law Firms, Red = Crime Alert, Yellow = Pedophile registry)
                        // Center user pin
                        drawCircle(color = Color(0xFF4F46E5), radius = 8f, center = center)
                        
                        // Law firm pins
                        drawCircle(color = Color(0xFF10B981), radius = 6f, center = Offset(center.x - 100f, center.y + 40f))
                        drawCircle(color = Color(0xFF10B981), radius = 6f, center = Offset(center.x + 120f, center.y - 60f))
                        
                        // Crime pins
                        drawCircle(color = Color(0xFFEF4444), radius = 7f, center = Offset(center.x + 50f, center.y + 80f))
                        
                        // Registry pins
                        drawCircle(color = Color(0xFFF59E0B), radius = 6f, center = Offset(center.x - 130f, center.y - 70f))
                    }

                    // Floating Card labels
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Interactive Sector Map (Canvas Visualizer)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem(color = Color(0xFF4F46E5), text = "You")
                        LegendItem(color = Color(0xFF10B981), text = "Law Firms")
                        LegendItem(color = Color(0xFFEF4444), text = "Crimes")
                        LegendItem(color = Color(0xFFF59E0B), text = "Registry")
                    }
                }
            }
        }

        // Local News & Weather Alert Bulletin Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // Light warning Amber
                border = BorderStroke(1.dp, Color(0xFFFDE68A))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Alert",
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Location News Flash", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                        Text(
                            "Municipal court announces digitizing all county criminal trial filings effective today. Defense attorneys advised.",
                            fontSize = 12.sp,
                            color = Color(0xFF78350F),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Active Crime Alerts Header
        item {
            Text(
                text = "Dynamic Community Crime Logs",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Crime Alert Logs
        if (crimeAlerts.isEmpty()) {
            item {
                Text("No active reports in this sector.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(crimeAlerts) { alert ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardStrokeHelper.cardStroke()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (alert.severity == "HIGH") Color(0xFFFEE2E2) else Color(0xFFFEF3C7)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = alert.severity,
                                tint = if (alert.severity == "HIGH") Color(0xFFEF4444) else Color(0xFFF59E0B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(alert.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (alert.severity == "HIGH") Color(0xFFEF4444) else Color(0xFFF59E0B)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        alert.severity,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text("Area: ${alert.area} • ${alert.timeAgo}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(alert.description, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }

        // Active Offender Bulletins
        item {
            Text(
                text = "Community Safety Registry Alerts",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        if (pedophileAlerts.isEmpty()) {
            item {
                Text("No registry listings detected in the immediate area.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(pedophileAlerts) { off ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(off.initials, fontWeight = FontWeight.Bold, color = Color(0xFFD97706), fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Offender Initials: ${off.initials} (Age ${off.age})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Charge: ${off.charge}", fontSize = 11.sp, color = Color(0xFFB45309))
                            Text("Distance: ${off.distance} away • Status: ${off.status}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = "Registered Offender",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Nearby Law Firms
        item {
            Text(
                text = "Nearby Verified Defense Firms",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (nearbyFirms.isEmpty()) {
            item {
                Text("Searching nearby licensed firms...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(nearbyFirms) { firm ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardStrokeHelper.cardStroke()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(firm.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            if (firm.verified) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Practice",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text("Specialty: ${firm.specialty}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        Text("Address: ${firm.address} (${firm.distance} away)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${firm.rating} (${firm.reviewsCount} reviews)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(firm.phone, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
    }
}
