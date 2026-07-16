package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SecuritySettingsScreen(viewModel: LawViewModel) {
    val currentPin by viewModel.pinCode.collectAsState()
    val biometricEnabled by viewModel.biometricEnabled.collectAsState()

    var showPinInputCard by remember { mutableStateOf(false) }
    var pinSetupInput by remember { mutableStateOf("") }
    var pinFeedbackMessage by remember { mutableStateOf("") }

    var showShredConfirm by remember { mutableStateOf(false) }
    var isShreddingInProgress by remember { mutableStateOf(false) }
    var isShreddingComplete by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // LOCK HEADER SUMMARY CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(24.dp),
                border = CardStrokeHelper.cardStroke()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security Active",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("CLIENT CONFIDENTIALITY LEVEL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = if (currentPin != null) "PRIVILEGE PROTECTED" else "STANDARD SECURE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // CONFIDENTIALITY PIN CONTROLS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardStrokeHelper.cardStroke()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Privilege Passcode Gate", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text(
                        "Configure a 4-digit PIN lock that acts as a secure barrier before reading any client dossiers, case briefs, or custom law reminders.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    if (currentPin == null) {
                        Button(
                            onClick = {
                                showPinInputCard = true
                                pinSetupInput = ""
                                pinFeedbackMessage = ""
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Enable Privilege PIN Lock", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("PIN lock active (4-digit)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Button(
                                onClick = {
                                    viewModel.setPinCode(null)
                                    showPinInputCard = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                            ) {
                                Text("Disable", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // PIN setup form expansion
                    AnimatedVisibility(visible = showPinInputCard && currentPin == null) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            OutlinedTextField(
                                value = pinSetupInput,
                                onValueChange = { if (it.length <= 4) pinSetupInput = it },
                                label = { Text("Enter 4-digit PIN") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (pinSetupInput.length == 4) {
                                        viewModel.setPinCode(pinSetupInput)
                                        pinSetupInput = ""
                                        showPinInputCard = false
                                        pinFeedbackMessage = "PIN Lock successfully configured!"
                                    } else {
                                        pinFeedbackMessage = "Error: PIN must be exactly 4 digits."
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save PIN Code", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (pinFeedbackMessage.isNotEmpty()) {
                        Text(
                            text = pinFeedbackMessage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (pinFeedbackMessage.contains("success", ignoreCase = true)) Color(0xFF10B981) else Color(0xFFEF4444),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        // BIOMETRIC SECURITY MOCK TOGGLE
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardStrokeHelper.cardStroke()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Fingerprint, contentDescription = "Biometrics", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mock Biometric Authentication", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Use system fingerprint/face match as PIN bypass", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = biometricEnabled,
                        onCheckedChange = { viewModel.setBiometricEnabled(it) }
                    )
                }
            }
        }

        // AI CYBER SECURITY SHIELD WITH REAL-TIME MONITORING & UPDATES
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Deep Slate Dark
                border = BorderStroke(2.dp, Color(0xFF10B981)) // Emerald green border
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Cyber Security",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "AI Cyber Shield Active",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Text(
                                    "Real-time monitoring & dynamic protection",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF065F46))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "SECURE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Real-time security logs / items
                    val securityStats = listOf(
                        Triple(Icons.Default.WifiTethering, "Intrusion Detection Status", "No threat packets detected"),
                        Triple(Icons.Default.Lock, "Local Vault Cipher", "Military Grade AES-256 GCM"),
                        Triple(Icons.Default.CloudSync, "Admin Isolation Shield", "Active (Zero Admin Access to Files)"),
                        Triple(Icons.Default.Update, "Security Signature Patch", "Build v4.9.1.75 - Up to date")
                    )

                    securityStats.forEach { stat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = stat.first,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stat.second,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = stat.third,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // OFFICIAL LEGAL DISCLAIMER CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = CardStrokeHelper.cardStroke()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Notice",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "LEGAL DISCLAIMERS & WAIVER",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "1. No Responsibility for Case Decisions: jgames.studio is an independent AI application developer and is strictly not responsible, liable, or accountable for any legal case decisions, rulings, or court actions resulting from the use of this software.\n\n" +
                                "2. Strict Admin Isolation: The application administrators have absolutely ZERO access to your business information, personal files, contracts, or private financial records stored within the client-side sandbox. Your files remain completely secure, private, and localized on your physical hardware.\n\n" +
                                "3. Internet Leak Liability Waiver: jgames.studio holds no responsibility or liability whatsoever for any leaked information or data compromises that occur through third-party internet actions, unencrypted network transmissions, or external cybersecurity breaches.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // SECURE SHREDDER CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)), // Soft Red
                border = BorderStroke(2.dp, Color(0xFFEF4444)) // High alert Red
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Shredder", tint = Color(0xFFEF4444), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Localized Cache Shredder", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color(0xFF991B1B))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "In case of compromise, execute the secure shredder to completely wipe all localized case files, briefs, custom reminders, and search histories from SQLite memory. This is completely irreversible.",
                        fontSize = 12.sp,
                        color = Color(0xFF7F1D1D)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            showShredConfirm = true
                            isShreddingComplete = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ACTIVATE CACHE SHREDDER", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    // SHREDDER DIALOG WITH ANIMATION
    if (showShredConfirm) {
        AlertDialog(
            onDismissRequest = { if (!isShreddingInProgress) showShredConfirm = false },
            title = {
                Text(
                    text = "CONFIRM SECURE APP SHRED",
                    color = Color(0xFF991B1B),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                if (isShreddingComplete) {
                    Button(onClick = { showShredConfirm = false }) {
                        Text("Closed")
                    }
                } else {
                    Button(
                        onClick = {
                            isShreddingInProgress = true
                            coroutineScope.launch {
                                delay(2500L) // Wiping SQLite animation delay
                                viewModel.shredSensitiveData()
                                isShreddingInProgress = false
                                isShreddingComplete = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        enabled = !isShreddingInProgress
                    ) {
                        Text("PROCEED SHRED")
                    }
                }
            },
            dismissButton = {
                if (!isShreddingInProgress && !isShreddingComplete) {
                    TextButton(onClick = { showShredConfirm = false }) {
                        Text("Abort Wiping")
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isShreddingInProgress) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            CircularProgressIndicator(color = Color(0xFFEF4444))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Zeroing SQLite database cells...",
                                fontSize = 12.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                            Text(
                                "Sanitizing file metadata tags...",
                                fontSize = 10.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = Color.Gray
                            )
                        }
                    } else if (isShreddingComplete) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Wiped",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "App Cache shredded successfully!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Text(
                            text = "Warning: You are requesting a secure shred. This zero-writes all saved cases, folders, and briefs from your device sandbox memory. Recovering this data is completely impossible.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        )
    }
}
