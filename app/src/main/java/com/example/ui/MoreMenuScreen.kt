package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AvatarConfig
import com.example.data.LawClass
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MoreMenuScreen(viewModel: LawViewModel) {
    var activeSubModule by remember { mutableStateOf<String?>(null) } // null, "AVATAR", "CALENDAR", "QUIZ", "CLASSES"

    if (activeSubModule == null) {
        // Main Grid Dashboard for More Tools
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Legal Suite & Customizer",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Manage your calendar, customize your virtual cartoon attorney, and learn the rules of law.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Option 1: Avatar Studio
            DashboardModuleCard(
                title = "Cartoon Lawyer Customizer",
                desc = "Customize face styles, ties, clothing, and backgrounds for your virtual counsel avatar.",
                icon = Icons.Default.Face,
                color = MaterialTheme.colorScheme.primary,
                onClick = { activeSubModule = "AVATAR" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 2: Calendar & Reminders
            DashboardModuleCard(
                title = "Court Calendar & Reminders",
                desc = "Schedule hearings, trial dates, and set local reminders of federal/state code changes.",
                icon = Icons.Default.CalendarToday,
                color = MaterialTheme.colorScheme.secondary,
                onClick = { activeSubModule = "CALENDAR" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 3: Fun Legal Quiz
            DashboardModuleCard(
                title = "Citizens Law Quiz",
                desc = "Test your legal literacy. Master your 4th, 5th, and 1st Amendment rights with our interactive quiz.",
                icon = Icons.Default.Quiz,
                color = Color(0xFFD35400),
                onClick = { activeSubModule = "QUIZ" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 4: AI Law Classes
            DashboardModuleCard(
                title = "AI Law Classes & Professor",
                desc = "Interactive legal training courses. Simulate class lectures and ask the Law Professor specific QA.",
                icon = Icons.Default.School,
                color = Color(0xFF27AE60),
                onClick = { activeSubModule = "CLASSES" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 5: GPS Safety & Law Firm Maps
            DashboardModuleCard(
                title = "GPS Safety & Firm Maps",
                desc = "Analyze real-time location, safety bulletins, local crime logs, pedophile alert registers, and nearby verified firms.",
                icon = Icons.Default.Map,
                color = Color(0xFF2563EB),
                onClick = { activeSubModule = "GPS" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 6: Emergency Scanner Radio
            DashboardModuleCard(
                title = "Emergency Scanner Radio",
                desc = "Monitor live radio frequencies, log dispatch feeds, and customize squelch with our interactive visualizer.",
                icon = Icons.Default.SettingsInputAntenna,
                color = Color(0xFF059669),
                onClick = { activeSubModule = "SCANNER" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 7: Subscriptions & Donation Cause
            DashboardModuleCard(
                title = "Premium & Defense Charity",
                desc = "Select premium packages and support our legal defense assistance fund to help families in need.",
                icon = Icons.Default.VolunteerActivism,
                color = Color(0xFFD97706),
                onClick = { activeSubModule = "SUBSCRIPTION" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 8: Private Law Firm Suite
            DashboardModuleCard(
                title = "Private Law Firm Suite",
                desc = "Unlock enterprise cloud lockers, e-file cases, track billable ledgers, and query private accountants or executive assistants.",
                icon = Icons.Default.BusinessCenter,
                color = Color(0xFF1E293B),
                onClick = { activeSubModule = "PRIVATE_SUITE" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 9: Pin Gate & Shredder Settings
            DashboardModuleCard(
                title = "Confidential PIN & Shredder",
                desc = "Configure pin access locks to safeguard sensitive folders and execute safe database cache shredding.",
                icon = Icons.Default.Security,
                color = Color(0xFF4F46E5),
                onClick = { activeSubModule = "SECURITY" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 9: Admin Overrides Panel
            DashboardModuleCard(
                title = "Admin Override Console",
                desc = "Developer coordinate overrides, Gemini AI prompt tuning, and dynamic audited bookkeeping ledger.",
                icon = Icons.Default.AdminPanelSettings,
                color = Color(0xFF7C3AED),
                onClick = { activeSubModule = "ADMIN" }
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Disclaimers, Privacy & Copyright Footer Block
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LEGAL DISCLAIMERS & PRIVACY POLICY",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This application provides artificial intelligence generated legal evaluations and informational code directories. It is strictly intended as a research assistant and does not constitute formal legal representation, official legal advice, or establish an attorney-client privilege contract. Always consult with a licensed, credentialed counselor for official court representation.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "PRIVACY ASSURANCE: In compliance with standard defense confidentiality protocols, all documents, cases, searches, and location streams remain entirely in your local system sandbox. We do not sync, store, or transmit sensitive client privilege information to external third parties.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "© JGames.studio. All Rights Reserved.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Created by JGames.studio. JGames.studio owns copyright to idea.",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    } else {
        // Render Back Navigation Scaffold for active sub module
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    activeSubModule = null
                    viewModel.stopSpeaking()
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (activeSubModule) {
                        "AVATAR" -> "Attorney Customizer Studio"
                        "CALENDAR" -> "Court Dates & Reminders"
                        "QUIZ" -> "Constitutional Rights Quiz"
                        "CLASSES" -> "AI Law School Lectures"
                        "GPS" -> "GPS Safety & Firm Maps"
                        "SCANNER" -> "Emergency Dispatch Scanner"
                        "SUBSCRIPTION" -> "Membership & Defense Charity"
                        "PRIVATE_SUITE" -> "Private Law Firm Suite"
                        "SECURITY" -> "Confidential PIN & Shredder"
                        else -> "Admin Override Console"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            Box(modifier = Modifier.weight(1f)) {
                when (activeSubModule) {
                    "AVATAR" -> AvatarCustomizerSubScreen(viewModel)
                    "CALENDAR" -> CourtCalendarSubScreen(viewModel)
                    "QUIZ" -> LegalQuizSubScreen(viewModel)
                    "CLASSES" -> LawClassesSubScreen(viewModel)
                    "GPS" -> GPSSafetyScreen(viewModel)
                    "SCANNER" -> ScannerScreen(viewModel)
                    "SUBSCRIPTION" -> SubscriptionsScreen(viewModel)
                    "PRIVATE_SUITE" -> PrivateFirmSuiteScreen(viewModel)
                    "SECURITY" -> SecuritySettingsScreen(viewModel)
                    "ADMIN" -> AdminControlScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun DashboardModuleCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardStrokeHelper.cardStroke()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// --- SUB-SCREEN 1: AVATAR CUSTOMIZER ---
@Composable
fun AvatarCustomizerSubScreen(viewModel: LawViewModel) {
    val config by viewModel.avatarConfig.collectAsState()

    val faces = listOf("The Stern Judge", "The Slick Litigator", "The Tech Techie", "The Wise Counsel")
    val outfits = listOf("The Classic Navy Suit", "The Royal Purple Tux", "The Casual Blazer", "The Detective Trenchcoat")
    val backgrounds = listOf("Courtroom Podium", "Classic Library", "Modern Office", "City View Night")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Large Live Preview Card
        Card(
            modifier = Modifier
                .size(220.dp)
                .testTag("avatar_preview"),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = CardStrokeHelper.cardStroke()
        ) {
            CartoonLawyerAvatar(
                faceStyle = config.faceStyle,
                outfitStyle = config.outfitStyle,
                backgroundStyle = config.backgroundStyle,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Customizer Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select Facial / Hair Style", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    faces.take(2).forEach { face ->
                        FilterChip(
                            selected = config.faceStyle == face,
                            onClick = { viewModel.updateAvatarFace(face) },
                            label = { Text(face, fontSize = 11.sp) }
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    faces.drop(2).forEach { face ->
                        FilterChip(
                            selected = config.faceStyle == face,
                            onClick = { viewModel.updateAvatarFace(face) },
                            label = { Text(face, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Legal Suit / Attire", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    outfits.take(2).forEach { outfit ->
                        FilterChip(
                            selected = config.outfitStyle == outfit,
                            onClick = { viewModel.updateAvatarOutfit(outfit) },
                            label = { Text(outfit.replace("The ", ""), fontSize = 11.sp) }
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    outfits.drop(2).forEach { outfit ->
                        FilterChip(
                            selected = config.outfitStyle == outfit,
                            onClick = { viewModel.updateAvatarOutfit(outfit) },
                            label = { Text(outfit.replace("The ", ""), fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Background Studio", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    backgrounds.take(2).forEach { bg ->
                        FilterChip(
                            selected = config.backgroundStyle == bg,
                            onClick = { viewModel.updateAvatarBackground(bg) },
                            label = { Text(bg, fontSize = 11.sp) }
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    backgrounds.drop(2).forEach { bg ->
                        FilterChip(
                            selected = config.backgroundStyle == bg,
                            onClick = { viewModel.updateAvatarBackground(bg) },
                            label = { Text(bg, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }
    }
}

// --- SUB-SCREEN 2: COURT CALENDAR ---
@Composable
fun CourtCalendarSubScreen(viewModel: LawViewModel) {
    val remindersList by viewModel.reminders.collectAsState()
    val alertsList by viewModel.alerts.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Upcoming Deadlines & Hearings", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(28.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (remindersList.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Text(
                    "No court reminders scheduled. Tap the plus icon to schedule a court date or personal statutory calendar entry.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 240.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(remindersList) { reminder ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = CardStrokeHelper.cardStroke()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(reminder.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("📅 ${reminder.dateText}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                    Text("🕒 ${reminder.timeText}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                }
                                if (reminder.location.isNotEmpty()) {
                                    Text("📍 ${reminder.location}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            IconButton(onClick = { viewModel.removeReminder(reminder.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statutory Change Alerts
        Text("Statutory & Law Change Bulletins", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(alertsList) { alert ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (alert.isRead) MaterialTheme.colorScheme.surface
                        else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
                    ),
                    border = CardStrokeHelper.cardStroke()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!alert.isRead) {
                                    Icon(Icons.Default.NewReleases, contentDescription = "New", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(alert.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            SuggestionChip(
                                onClick = {},
                                label = { Text(alert.state, fontSize = 9.sp) }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(alert.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (!alert.isRead) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Mark Read",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .clickable { viewModel.markLawAlertRead(alert.id) }
                                    .align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var courtTitle by remember { mutableStateOf("") }
        var courtDate by remember { mutableStateOf("2026-07-28") }
        var courtTime by remember { mutableStateOf("10:00 AM") }
        var courtLocation by remember { mutableStateOf("") }
        var courtNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addReminder(courtTitle, courtDate, courtTime, courtLocation, courtNotes)
                        showAddDialog = false
                    },
                    enabled = courtTitle.isNotEmpty()
                ) {
                    Text("Schedule Entry")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Schedule Court Date/Reminder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = courtTitle, onValueChange = { courtTitle = it }, label = { Text("Title (e.g. Traffic Hearing)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = courtDate, onValueChange = { courtDate = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = courtTime, onValueChange = { courtTime = it }, label = { Text("Time (HH:MM AM/PM)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = courtLocation, onValueChange = { courtLocation = it }, label = { Text("Courtroom / Location (optional)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = courtNotes, onValueChange = { courtNotes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
                }
            }
        )
    }
}

// --- SUB-SCREEN 3: LEGAL QUIZ ---
@Composable
fun LegalQuizSubScreen(viewModel: LawViewModel) {
    val quizState by viewModel.quizState.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val index by viewModel.quizQuestionIndex.collectAsState()
    val selectedOption by viewModel.selectedOption.collectAsState()
    val highScores by viewModel.highScores.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (quizState) {
            "NOT_STARTED" -> {
                Icon(Icons.Default.Quiz, contentDescription = "Quiz logo", modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Citizens Legal Literacy Challenge", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Test your fundamental knowledge of United States constitutional protections, landlord-tenant codes, and self-defense statutes.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = { viewModel.startQuiz() }) {
                    Text("Start Knowledge Challenge")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Local Leaderboard High Scores", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                if (highScores.isEmpty()) {
                    Text("No records yet. Be the first!", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    highScores.forEach { record ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🛡️ ${record.player}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Score: ${record.score}/${record.total}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            "IN_PROGRESS" -> {
                val question = viewModel.quizQuestions[index]

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Question ${index + 1}/${viewModel.quizQuestions.size}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text("Score: $score", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Question Text
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = question.question,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options list
                question.options.forEachIndexed { optIdx, option ->
                    val isSelected = selectedOption == optIdx
                    val isCorrect = optIdx == question.correctOptionIndex
                    val buttonColor = when {
                        selectedOption == null -> MaterialTheme.colorScheme.surface
                        isSelected && isCorrect -> Color(0xFFD4EFDF) // Light Green
                        isSelected && !isCorrect -> Color(0xFFFADBD8) // Light Red
                        isCorrect -> Color(0xFFD4EFDF) // Highlight correct
                        else -> MaterialTheme.colorScheme.surface
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { viewModel.selectQuizOption(optIdx) }
                            .testTag("quiz_option_$optIdx"),
                        colors = CardDefaults.cardColors(containerColor = buttonColor),
                        border = CardStrokeHelper.cardStroke()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.selectQuizOption(optIdx) },
                                enabled = selectedOption == null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(option, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Explanation
                AnimatedVisibility(visible = selectedOption != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Legal Guidance:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
                            Text(question.explanation, fontSize = 12.sp, lineHeight = 18.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.nextQuizQuestion() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Next Question")
                            }
                        }
                    }
                }
            }
            "FINISHED" -> {
                Icon(Icons.Default.CheckCircle, contentDescription = "Finish", tint = Color(0xFF2ECC71), modifier = Modifier.size(72.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Challenge Completed!", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Your Final Score: $score out of ${viewModel.quizQuestions.size}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = { viewModel.startQuiz() }) {
                    Text("Play Again")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { viewModel.startQuiz() }) {
                    Text("Close Results")
                }
            }
        }
    }
}

// --- SUB-SCREEN 4: LAW CLASSES ---
@Composable
fun LawClassesSubScreen(viewModel: LawViewModel) {
    val selectedClass by viewModel.selectedClass.collectAsState()
    val classQuestion by viewModel.classQuestion.collectAsState()
    val isClassLoading by viewModel.isClassLoading.collectAsState()
    val classAnswer by viewModel.classAnswer.collectAsState()

    if (selectedClass == null) {
        val syllabusText by viewModel.customSyllabusText.collectAsState()
        val isSyllabusLoading by viewModel.isSyllabusLoading.collectAsState()
        var customTopicInput by remember { mutableStateOf("") }

        // Classes browser list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Enterprise syllabus builder block
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF10B981)) // Emerald border
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFD1FAE5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.School, contentDescription = "Business Package", tint = Color(0xFF047857))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Business Class: Syllabus & Mock Trial Customizer", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color(0xFF065F46))
                                Text("Create tailored law class curriculums and trial scripts with Gemini", fontSize = 10.sp, color = Color(0xFF047857))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = customTopicInput,
                            onValueChange = { customTopicInput = it },
                            label = { Text("Enter Law Course / Lecture Topic...") },
                            placeholder = { Text("e.g. Constitutional Freedom, Antitrust Loophole...") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.generateCustomClassSyllabus(customTopicInput)
                                customTopicInput = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSyllabusLoading && customTopicInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            if (isSyllabusLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                            } else {
                                Text("Generate Course & Trial Blueprint")
                            }
                        }

                        AnimatedVisibility(visible = syllabusText.isNotEmpty()) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Custom Generated Syllabus Outline:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF047857))
                                    IconButton(onClick = { viewModel.speak(syllabusText.replace(Regex("[#*]"), "")) }) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = "Speak", tint = Color(0xFF047857), modifier = Modifier.size(18.dp))
                                    }
                                }
                                Text(
                                    text = syllabusText,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            // Heading for general classes
            item {
                Text(
                    "Seeded Law Lectures",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(viewModel.classesList) { cl ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectClass(cl) }
                        .testTag("class_card_${cl.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardStrokeHelper.cardStroke(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(cl.category, fontSize = 10.sp) }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(cl.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(cl.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Attend Class", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                            Icon(Icons.Default.PlayArrow, contentDescription = "Enter", tint = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }
    } else {
        val cl = selectedClass!!
        var showMiniQuiz by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Close Lecture link
            TextButton(
                onClick = { viewModel.selectClass(null) },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Back")
                Text("Back to Class List")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(cl.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text(cl.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.height(16.dp))

            // Video player simulation box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.School, contentDescription = "Lecture", tint = Color.White, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Interactive Lecture Streaming Simulation", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Playing pedagogical review under strict US law standards", color = Color.Gray, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Handbook
            Text("Interactive Lecture Notes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text(cl.detailedText, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // Class Mini quiz trigger
            Button(
                onClick = { showMiniQuiz = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = "Quiz")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test your Understanding (Lecture Quiz)")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ask the Law Professor QA powered by Gemini
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = CardStrokeHelper.cardStroke()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ask Professor (Interactive AI Question)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Ask the Professor any legal question related to this course, and receive immediate authoritative feedback.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = classQuestion,
                        onValueChange = { viewModel.updateClassQuestion(it) },
                        label = { Text("Ask the Law Professor...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.askProfessor() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isClassLoading && classQuestion.isNotBlank()
                    ) {
                        if (isClassLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                        } else {
                            Text("Ask Professor")
                        }
                    }

                    // Answer
                    AnimatedVisibility(visible = classAnswer.isNotEmpty()) {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Professor's Ruling:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                            Text(classAnswer, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
                        }
                    }
                }
            }
        }

        // Mini Quiz popup
        if (showMiniQuiz && cl.interactiveQuiz.isNotEmpty()) {
            val q = cl.interactiveQuiz.first()
            var chosenOpt by remember { mutableStateOf<Int?>(null) }

            AlertDialog(
                onDismissRequest = { showMiniQuiz = false },
                confirmButton = {
                    Button(onClick = { showMiniQuiz = false }) {
                        Text("Finish")
                    }
                },
                title = { Text("Lecture Comprehension Question") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(q.question, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        q.options.forEachIndexed { index, option ->
                            val color = when {
                                chosenOpt == null -> MaterialTheme.colorScheme.surface
                                index == q.correctOptionIndex -> Color(0xFFD4EFDF)
                                index == chosenOpt -> Color(0xFFFADBD8)
                                else -> MaterialTheme.colorScheme.surface
                            }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { chosenOpt = index },
                                colors = CardDefaults.cardColors(containerColor = color),
                                border = CardStrokeHelper.cardStroke()
                            ) {
                                Text(option, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        if (chosenOpt != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Professor's Explanation: ${q.explanation}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            )
        }
    }
}
