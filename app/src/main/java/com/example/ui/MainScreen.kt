package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme

enum class LegalTab {
    ADVISOR,
    LIBRARY,
    CASES,
    DIRECTORY,
    MORE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: LawViewModel) {
    var activeTab by remember { mutableStateOf(LegalTab.ADVISOR) }
    
    val pinCode by viewModel.pinCode.collectAsState()
    val isAppUnlocked by viewModel.isAppUnlocked.collectAsState()
    val satelliteRadioActive by viewModel.satelliteRadioActive.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    MyApplicationTheme {
        if (pinCode != null && !isAppUnlocked) {
            // Security PIN Lock Screen
            PinLockScreen(viewModel = viewModel)
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        tonalElevation = 0.dp,
                        modifier = Modifier.testTag("app_top_bar")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // User Avatar (JD Gradient exactly like Sleek Interface Design)
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(Color(0xFF818CF8), Color(0xFFC084FC)) // Indigo 400 to Purple 400
                                            )
                                        )
                                        .border(2.dp, Color(0xFF4F46E5), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "JD",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // App/Brand Title (Ai justice buddy ( powerd by Gemini))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Ai justice buddy",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF64748B), // Slate 500
                                        letterSpacing = 1.2.sp
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "powered by Gemini",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            lineHeight = 18.sp
                                        )
                                        JGamesStudioLogo()
                                    }
                                }

                                // Top Action Icons
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Lock App option (if PIN is configured)
                                    if (pinCode != null) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surface)
                                                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                                                .clickable { viewModel.lockApp() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Lock Confidential Gate",
                                                tint = Color(0xFF475569),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surface)
                                            .border(1.dp, Color(0xFFE2E8F0), CircleShape) // border-slate-100
                                            .clickable { viewModel.toggleSatelliteRadio() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Radio,
                                            contentDescription = "Satellite Radio",
                                            tint = if (satelliteRadioActive) Color(0xFF4F46E5) else Color(0xFF475569),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.testTag("app_bottom_bar")
                    ) {
                        val itemColors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            unselectedIconColor = Color(0xFF94A3B8),
                            unselectedTextColor = Color(0xFF94A3B8)
                        )

                        NavigationBarItem(
                            selected = activeTab == LegalTab.ADVISOR,
                            onClick = { activeTab = LegalTab.ADVISOR },
                            icon = { Icon(Icons.Default.Scale, contentDescription = "AI Advisor") },
                            label = { Text("Hub", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = itemColors,
                            modifier = Modifier.testTag("tab_advisor")
                        )
                        NavigationBarItem(
                            selected = activeTab == LegalTab.LIBRARY,
                            onClick = { activeTab = LegalTab.LIBRARY },
                            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Law Books") },
                            label = { Text("Law Books", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = itemColors,
                            modifier = Modifier.testTag("tab_library")
                        )
                        NavigationBarItem(
                            selected = activeTab == LegalTab.CASES,
                            onClick = { activeTab = LegalTab.CASES },
                            icon = { Icon(Icons.Default.Folder, contentDescription = "Cases") },
                            label = { Text("Cases", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = itemColors,
                            modifier = Modifier.testTag("tab_cases")
                        )
                        NavigationBarItem(
                            selected = activeTab == LegalTab.DIRECTORY,
                            onClick = { activeTab = LegalTab.DIRECTORY },
                            icon = { Icon(Icons.Default.SupervisorAccount, contentDescription = "Find Lawyer") },
                            label = { Text("Lawyers", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = itemColors,
                            modifier = Modifier.testTag("tab_directory")
                        )
                        NavigationBarItem(
                            selected = activeTab == LegalTab.MORE,
                            onClick = { activeTab = LegalTab.MORE },
                            icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More Suite") },
                            label = { Text("Suite", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = itemColors,
                            modifier = Modifier.testTag("tab_more")
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Floating/Collapsible Satellite Radio Dashboard Ribbon
                        if (satelliteRadioActive) {
                            SatelliteRadioFloatingWidget(viewModel = viewModel)
                        }

                        // AI VOICE ACTIVE PLAYBACK FEEDBACK INDICATOR
                        AnimatedVisibility(visible = isSpeaking) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.95f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Voice playing",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "AI COUNSEL VOICE TRANSMITTING",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            "Playing dynamic legal analysis and case evaluations...",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.stopSpeaking() },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Stop Speech",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                        
                        Box(modifier = Modifier.weight(1f)) {
                            Crossfade(targetState = activeTab, label = "TabTransition") { tab ->
                                when (tab) {
                                    LegalTab.ADVISOR -> AdvisorScreen(viewModel = viewModel)
                                    LegalTab.LIBRARY -> LibraryScreen(
                                        viewModel = viewModel,
                                        onNavigateToAdvisor = { activeTab = LegalTab.ADVISOR }
                                    )
                                    LegalTab.CASES -> CaseFilesScreen(viewModel = viewModel)
                                    LegalTab.DIRECTORY -> DirectoryScreen(viewModel = viewModel)
                                    LegalTab.MORE -> MoreMenuScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PinLockScreen(viewModel: LawViewModel) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val failedAttempts by viewModel.failedPinAttempts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Slate 900
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Confidentiality Lock",
            tint = Color(0xFF818CF8), // Indigo 400
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Attorney-Client Privilege Gate",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Text(
            text = "Enter 4-digit security PIN to access files",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )
        
        // Dot indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            for (i in 0 until 4) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (i < enteredPin.length) Color(0xFF818CF8)
                            else Color(0xFF334155)
                        )
                )
            }
        }
        
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color(0xFFEF4444),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        
        if (failedAttempts > 0) {
            Text(
                text = "Failed attempts: $failedAttempts/5. (Auto-destruct shredding active)",
                color = Color(0xFFFBBF24),
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Keypad grid
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("C", "0", "OK")
            )
            for (row in keys) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (key in row) {
                        Button(
                            onClick = {
                                when (key) {
                                    "C" -> if (enteredPin.isNotEmpty()) {
                                        enteredPin = enteredPin.dropLast(1)
                                        errorMessage = ""
                                    }
                                    "OK" -> {
                                        if (enteredPin.length == 4) {
                                            if (viewModel.verifyPinCode(enteredPin)) {
                                                errorMessage = ""
                                            } else {
                                                errorMessage = "Invalid PIN"
                                                enteredPin = ""
                                            }
                                        } else {
                                            errorMessage = "PIN must be 4 digits"
                                        }
                                    }
                                    else -> {
                                        if (enteredPin.length < 4) {
                                            enteredPin += key
                                            errorMessage = ""
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.5f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (key == "OK") Color(0xFF4F46E5) else Color(0xFF1E293B),
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = key, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SatelliteRadioFloatingWidget(viewModel: LawViewModel) {
    val activeChannel by viewModel.satelliteChannelIndex.collectAsState()
    val activeVolume by viewModel.satelliteVolume.collectAsState()
    val isFloating by viewModel.isSatelliteFloating.collectAsState()

    if (isFloating) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.SettingsInputAntenna,
                        contentDescription = "Satellite Radio",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SATELLITE ENTERTAINMENT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = viewModel.satelliteRadioChannels[activeChannel],
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(onClick = { viewModel.toggleSatelliteRadio() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Satellite Radio",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Channel Buttons Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    viewModel.satelliteRadioChannels.forEachIndexed { idx, _ ->
                        val isSelected = activeChannel == idx
                        Button(
                            onClick = { viewModel.setSatelliteChannel(idx) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "CH ${idx + 1}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Volume controller & track visualization
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volume",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = activeVolume,
                        onValueChange = { viewModel.setSatelliteVolume(it) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Track: Live Air",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
