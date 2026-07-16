package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SubscriptionsScreen(viewModel: LawViewModel) {
    val activeSub by viewModel.activeSubscription.collectAsState()
    val isCheckoutVisible by viewModel.isPaymentDialogVisible.collectAsState()
    val donationBal by viewModel.donationBalance.collectAsState()
    val donationGl by viewModel.donationGoal.collectAsState()
    val donationPct by viewModel.donationPercentage.collectAsState()

    var selectedTierToBuy by remember { mutableStateOf<String?>(null) }
    var selectedTierPrice by remember { mutableStateOf(0.0) }

    // Direct donation states
    var customDonationAmount by remember { mutableStateOf("") }
    var showDirectDonationSuccess by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // CURRENT TIER BADGE HERO
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(24.dp),
                border = CardStrokeHelper.cardStroke()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("ACTIVE MEMBERSHIP TIER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activeSub.uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Thank you for supporting JGames.studio legal defense assistance project. $donationPct% of subscription proceed fees are directly deposited into family defense funds.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // CHARITY DEFENSE FUND CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(2.dp, Color(0xFF10B981)) // Emerald 500
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFD1FAE5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = "Charity Donation",
                                tint = Color(0xFF059669)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("JGames Defense Assistance Fund", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Text("Helping low-income families secure public defense", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress bar
                    val progress = (donationBal / donationGl).toFloat().coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = String.format("Raised: $%.2f", donationBal),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857)
                        )
                        Text(
                            text = String.format("Goal: $%.0f", donationGl),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Direct Donation Form
                    Text("Make a Direct Donation", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customDonationAmount,
                            onValueChange = { customDonationAmount = it },
                            label = { Text("Amount ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                val amt = customDonationAmount.toDoubleOrNull()
                                if (amt != null && amt > 0) {
                                    viewModel.addDirectDonation(amt)
                                    customDonationAmount = ""
                                    showDirectDonationSuccess = true
                                    coroutineScope.launch {
                                        delay(3000L)
                                        showDirectDonationSuccess = false
                                    }
                                }
                            },
                            enabled = customDonationAmount.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Donate", fontWeight = FontWeight.Bold)
                        }
                    }

                    AnimatedVisibility(visible = showDirectDonationSuccess) {
                        Text(
                            text = "Thank you for your generous donation to help families in defense!",
                            color = Color(0xFF047857),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        // TIER LISTING HEADER
        item {
            Text(
                text = "Premium Litigation Packages",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // TIER 1: Silver Counselor
        item {
            SubscriptionCard(
                tier = "Silver Counselor",
                price = "$9.99 / mo",
                features = listOf(
                    "Face/Attire Attorney customization",
                    "Unlimited AI evaluation situation logs",
                    "Basic real-time GPS law firm radar",
                    "15% proceeding fee helps defense charity"
                ),
                gradient = Brush.linearGradient(colors = listOf(Color(0xFF94A3B8), Color(0xFF475569))),
                onSelect = {
                    selectedTierToBuy = "Silver Counselor"
                    selectedTierPrice = 9.99
                    viewModel.setPaymentDialogVisible(true)
                }
            )
        }

        // TIER 2: Gold Platinum Advocate
        item {
            SubscriptionCard(
                tier = "Gold Platinum Advocate",
                price = "$24.99 / mo",
                features = listOf(
                    "All Custom Attorney options unlocked",
                    "Priority voice response TTS syntheses",
                    "Police Scanner frequency dial & waveform",
                    "Updated Pedophile alert safety maps",
                    "Enhanced 15% direct charity contribution"
                ),
                gradient = Brush.linearGradient(colors = listOf(Color(0xFFFBBF24), Color(0xFFD97706))),
                onSelect = {
                    selectedTierToBuy = "Gold Platinum Advocate"
                    selectedTierPrice = 24.99
                    viewModel.setPaymentDialogVisible(true)
                }
            )
        }

        // TIER 3: Diamond Barrister
        item {
            SubscriptionCard(
                tier = "Diamond Barrister",
                price = "$49.99 / mo",
                features = listOf(
                    "Direct emergency hotline support logs",
                    "JGames admin ledger & audited finances",
                    "GPS override custom coordinate feeds",
                    "Complete local climate/news safety bulletin",
                    "Prestige avatar medal frame badge"
                ),
                gradient = Brush.linearGradient(colors = listOf(Color(0xFF818CF8), Color(0xFF4F46E5))),
                onSelect = {
                    selectedTierToBuy = "Diamond Barrister"
                    selectedTierPrice = 49.99
                    viewModel.setPaymentDialogVisible(true)
                }
            )
        }

        // TIER 4: Business Law Classes
        item {
            SubscriptionCard(
                tier = "Business Class Package",
                price = "$99.99 / mo",
                features = listOf(
                    "High-capacity class syllabus customizer",
                    "Custom Law Lectures syllabus builder",
                    "Mock Trial cases & blueprints generator",
                    "Interactive Class discussion guides",
                    "Full professor QA feedback priority"
                ),
                gradient = Brush.linearGradient(colors = listOf(Color(0xFF10B981), Color(0xFF047857))),
                onSelect = {
                    selectedTierToBuy = "Business Class Package"
                    selectedTierPrice = 99.99
                    viewModel.setPaymentDialogVisible(true)
                }
            )
        }

        // TIER 5: Private Law Firm Suite
        item {
            SubscriptionCard(
                tier = "Private Law Firm Suite",
                price = "$299.99 / mo",
                features = listOf(
                    "AES-256 Secured AI Cloud Storage (250GB)",
                    "Direct state & federal Electronic Court Filing",
                    "Private AI Accountant (Forensic & IOLTA CPA)",
                    "Dedicated Legal Associate AI Administrative Assistant",
                    "Complete local confidentiality privilege compliance"
                ),
                gradient = Brush.linearGradient(colors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))),
                onSelect = {
                    selectedTierToBuy = "Private Law Firm Suite"
                    selectedTierPrice = 299.99
                    viewModel.setPaymentDialogVisible(true)
                }
            )
        }
    }

    // CHECKOUT DIALOG WITH CARD FLIP FORM
    if (isCheckoutVisible && selectedTierToBuy != null) {
        var cardNumber by remember { mutableStateOf("") }
        var expDate by remember { mutableStateOf("") }
        var cvv by remember { mutableStateOf("") }
        var cardHolder by remember { mutableStateOf("") }

        var isProcessing by remember { mutableStateOf(false) }
        var isCompleted by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { viewModel.setPaymentDialogVisible(false) },
            title = {
                Text(
                    "Secure Checkout - SSL Encrypted",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                if (isCompleted) {
                    Button(onClick = {
                        viewModel.setPaymentDialogVisible(false)
                        selectedTierToBuy = null
                    }) {
                        Text("Finish")
                    }
                } else {
                    Button(
                        onClick = {
                            isProcessing = true
                            coroutineScope.launch {
                                delay(3000L) // Simulate secure bank processing
                                isProcessing = false
                                isCompleted = true
                                viewModel.setSubscriptionTier(selectedTierToBuy!!)
                            }
                        },
                        enabled = !isProcessing && cardNumber.length >= 16 && cvv.length >= 3,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Authorize $${selectedTierPrice}")
                    }
                }
            },
            dismissButton = {
                if (!isProcessing && !isCompleted) {
                    TextButton(onClick = { viewModel.setPaymentDialogVisible(false) }) {
                        Text("Cancel")
                    }
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isProcessing) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Authenticating secure chip keys...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Contacting central credit network ... JGames.studio SSL active",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else if (isCompleted) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Payment Fully Authorized!",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color(0xFF047857)
                            )
                            Text(
                                "Your access is upgraded. Thank you for helping low-income families secure public defense.",
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "Upgrading to: ${selectedTierToBuy!!}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Visual Credit Card Graphic Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    when (selectedTierToBuy) {
                                        "Silver Counselor" -> Brush.linearGradient(colors = listOf(Color(0xFF94A3B8), Color(0xFF475569)))
                                        "Gold Platinum Advocate" -> Brush.linearGradient(colors = listOf(Color(0xFFFBBF24), Color(0xFFD97706)))
                                        "Business Class Package" -> Brush.linearGradient(colors = listOf(Color(0xFF10B981), Color(0xFF047857)))
                                        "Private Law Firm Suite" -> Brush.linearGradient(colors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)))
                                        else -> Brush.linearGradient(colors = listOf(Color(0xFF818CF8), Color(0xFF4F46E5)))
                                    }
                                )
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("JGames Secure card", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                    Icon(imageVector = Icons.Default.CreditCard, contentDescription = "Chip", tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                                Text(
                                    text = if (cardNumber.isBlank()) "•••• •••• •••• ••••" else cardNumber.chunked(4).joinToString(" "),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("HOLDER", fontSize = 9.sp, color = Color.White.copy(alpha = 0.6f))
                                        Text(if (cardHolder.isBlank()) "YOUR NAME" else cardHolder.uppercase(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("EXP", fontSize = 9.sp, color = Color.White.copy(alpha = 0.6f))
                                        Text(if (expDate.isBlank()) "MM/YY" else expDate, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Card Input Fields
                        OutlinedTextField(
                            value = cardHolder,
                            onValueChange = { cardHolder = it },
                            label = { Text("Cardholder Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { if (it.length <= 16) cardNumber = it },
                            label = { Text("Card Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = expDate,
                                onValueChange = { if (it.length <= 5) expDate = it },
                                label = { Text("Exp (MM/YY)") },
                                modifier = Modifier.weight(1.3f)
                            )
                            OutlinedTextField(
                                value = cvv,
                                onValueChange = { if (it.length <= 3) cvv = it },
                                label = { Text("CVV") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun SubscriptionCard(
    tier: String,
    price: String,
    features: List<String>,
    gradient: Brush,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardStrokeHelper.cardStroke()
    ) {
        Column {
            // Header Color Block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradient)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(tier.uppercase(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text(price, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }

            // Features Column
            Column(modifier = Modifier.padding(16.dp)) {
                features.forEach { ft ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Included",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(ft, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onSelect() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Select Package", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
