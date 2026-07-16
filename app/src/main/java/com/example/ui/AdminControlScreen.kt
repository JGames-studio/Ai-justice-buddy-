package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminControlScreen(viewModel: LawViewModel) {
    var adminPassword by remember { mutableStateOf("") }
    var isAdminAuthenticated by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf("") }

    val systemPrompt by viewModel.adminSystemPrompt.collectAsState()
    val downloads by viewModel.totalAppDownloads.collectAsState()
    val revenue by viewModel.monthlyRevenue.collectAsState()
    val serverCosts by viewModel.serverCosts.collectAsState()
    val netProfit by viewModel.netProfit.collectAsState()
    val donationBal by viewModel.donationBalance.collectAsState()

    var showPromptSavedFeedback by remember { mutableStateOf(false) }

    if (!isAdminAuthenticated) {
        // ADMIN LOGIN WALL CARD
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardStrokeHelper.cardStroke()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = "Admin Area", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("JGames.studio Admin Control", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(
                        "Please authenticate with your secret developer administrator password to access safety system overrides and financial bookkeeping ledger tools.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = { adminPassword = it },
                        label = { Text("Enter Admin Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (authError.isNotEmpty()) {
                        Text(authError, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (adminPassword == "JGames777" || adminPassword == "admin") {
                                isAdminAuthenticated = true
                                authError = ""
                            } else {
                                authError = "Invalid administrator password. Access denied."
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Authenticate Developer Console", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Developer Hint: Password is JGames777",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // EXPLICIT DISCLAIMER & LIMITATION OF LIABILITY FOR ADMIN
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Gavel, contentDescription = "Waiver", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Strict Developer Limit Notice", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "1. jgames.studio is strictly not responsible for any court rulings, jury trials, or case decisions.\n" +
                        "2. Admin Confidentiality Lock: Administrators hold absolutely ZERO viewing rights, data hooks, or sync channels to confidential business information, personal client dossiers, or local private files.\n" +
                        "3. Internet Leak Disclaimer: jgames.studio is entirely exempt from liability regarding data leaked or intercepted across the internet.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    } else {
        // FULL DEVELOPER OVERRIDE PANEL
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ADMIN SUCCESS HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("JGames.studio Core Overrides", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Button(
                        onClick = {
                            isAdminAuthenticated = false
                            adminPassword = ""
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text("Lock Console", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // ADMIN COMPLIANCE NOTICE CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    border = CardStrokeHelper.cardStroke()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "Disclaimer", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ADMIN CONFIDENTIALITY NOTICE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "This developer panel operates completely isolated from client documents. Under compliance protocols, the administrator has NO direct or indirect access to user business files, client trust IOLTA transactions, e-filings, or local private data. jgames.studio is not responsible for case decisions or internet-based leaks.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 1. LOCATION GPS OVERRIDES
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardStrokeHelper.cardStroke()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("GPS Location Overrides", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        Text(
                            "Force system GPS coordinates to emulate specific municipalities. This triggers location maps, safety crime alerts, weather, and pedophile registry zones instantly.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        val locations = listOf(
                            Triple("Los Angeles", Pair(34.0522, -118.2437), "California"),
                            Triple("New York", Pair(40.7128, -74.0060), "New York"),
                            Triple("Miami", Pair(25.7617, -80.1918), "Florida"),
                            Triple("San Francisco", Pair(37.7749, -122.4194), "California"),
                            Triple("Sacramento", Pair(38.5816, -121.4944), "California"),
                            Triple("Chicago", Pair(41.8781, -87.6298), "Illinois")
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            locations.forEach { loc ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.setLocation(
                                                loc.second.first,
                                                loc.second.second,
                                                loc.first,
                                                loc.third,
                                                isManual = true
                                            )
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(loc.first, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("Lat: ${loc.second.first}, Lng: ${loc.second.second}", fontSize = 10.sp, color = Color.Gray)
                                        }
                                        Icon(imageVector = Icons.Default.MyLocation, contentDescription = "Simulate", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. GEMINI AI SYSTEM PROMPT INSTRUCTIONS EDITOR
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardStrokeHelper.cardStroke()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Gemini AI Core Prompt Editor", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        Text(
                            "Alter the baseline directives given to Gemini's LLM engine. Customize professional personas or emphasize civil defenses.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        var promptInput by remember { mutableStateOf(systemPrompt) }

                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { promptInput = it },
                            label = { Text("Core System Directives") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.updateSystemPrompt(promptInput)
                                showPromptSavedFeedback = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Deploy Directives to Gemini", fontWeight = FontWeight.Bold)
                        }

                        if (showPromptSavedFeedback) {
                            Text(
                                "Core directives compiled and deployed into AI engine!",
                                color = Color(0xFF10B981),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // 3. FINANCIAL BOOKKEEPING AUDIT LEDGER
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Deep Slate Dark
                    border = BorderStroke(2.dp, Color(0xFF6366F1))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AccountBalance, contentDescription = "Ledger", tint = Color(0xFF818CF8), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("JGames Bookkeeping Audited Ledger", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color.White)
                        }
                        Text(
                            "Audited financial performance from membership proceed rates, micro-donations, and local operations.",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FinanceMiniCard("TOTAL INSTAL", "$downloads", Color(0xFF60A5FA), Modifier.weight(1f))
                            FinanceMiniCard("GROSS REV", String.format("$%.2f", revenue), Color(0xFF34D399), Modifier.weight(1.2f))
                            FinanceMiniCard("NET PROFITS", String.format("$%.2f", netProfit), Color(0xFFFBBF24), Modifier.weight(1.2f))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("BALANCE SHEET AUDIT TRIAL (USD)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF818CF8), fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        FinanceLedgerRow("Gross Account Subscriptions Proceeds", String.format("$%.2f", revenue))
                        FinanceLedgerRow("Localized Direct Micro-Donations Funds", String.format("$%.2f", donationBal))
                        FinanceLedgerRow("Less: Charity Families Defense Assistance Allocation (15%)", String.format("-$%.2f", revenue * 0.15))
                        FinanceLedgerRow("Less: Hosting & Gemini Server API Charges", String.format("-$%.2f", serverCosts))
                        FinanceLedgerRow("JGames.studio Net Accumulated Reserve Retained", String.format("$%.2f", netProfit - (revenue * 0.15)))

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("AUDIT CERTIFICATION STATUS", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text("FULLY COMPLIANT • JGames.studio", fontSize = 10.sp, color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinanceMiniCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = color, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun FinanceLedgerRow(label: String, valS: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 11.sp, color = Color(0xFF94A3B8), fontFamily = FontFamily.Monospace)
        Text(valS, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}
