package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LawyerFirm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(viewModel: LawViewModel) {
    val context = LocalContext.current
    val searchQuery by viewModel.lawyerSearchQuery.collectAsState()
    val firmsList by viewModel.lawyerFirms.collectAsState()

    var selectedFirmForReviews by remember { mutableStateOf<String?>(null) }
    var showConsultationDialog by remember { mutableStateOf<LawyerFirm?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Welcome Header
        Text(
            text = "Connect with Real Lawyers",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Search professional attorneys by city, zip, or legal specialty. Top-rated firms are shown first.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchLawyers(it) },
            placeholder = { Text("Search by City, State or Specialty (e.g. Austin, California...)") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("lawyer_search_input"),
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchLawyers("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Results
        if (firmsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.SearchOff,
                        contentDescription = "Empty Firms",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No lawyers found in this area",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Try searching 'CA', 'NY', 'Texas', 'Miami' or leave blank to show all.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(firmsList) { firm ->
                    val isReviewsExpanded = selectedFirmForReviews == firm.name

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("firm_card_${firm.name}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp),
                        border = CardStrokeHelper.cardStroke(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header: Name, Specialty
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = firm.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = firm.specialty,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = "Rating",
                                            tint = Color(0xFFF1C40F),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${firm.rating} (${firm.reviewsCount} reviews)",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                // Location badge
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(firm.location, fontSize = 10.sp) }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Contact info
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, contentDescription = "Address", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(firm.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                Icon(Icons.Default.Phone, contentDescription = "Phone", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(firm.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Review toggle
                            Text(
                                text = if (isReviewsExpanded) "Hide Client Testimonials" else "Read Client Testimonials (${firm.reviews.size})",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clickable {
                                        selectedFirmForReviews = if (isReviewsExpanded) null else firm.name
                                    }
                                    .padding(vertical = 4.dp)
                            )

                            AnimatedVisibility(visible = isReviewsExpanded) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    firm.reviews.forEach { review ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(review.reviewerName, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                    Row {
                                                        repeat(review.rating) {
                                                            Icon(Icons.Default.Star, contentDescription = "*", tint = Color(0xFFF1C40F), modifier = Modifier.size(10.dp))
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text("\"${review.comment}\"", style = MaterialTheme.typography.bodySmall, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${firm.phone}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = "Call")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Call Phone", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { showConsultationDialog = firm },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Icon(Icons.Default.Email, contentDescription = "Consult")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Free Consultation", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Free consultation popup
    if (showConsultationDialog != null) {
        val firm = showConsultationDialog!!
        var nameInput by remember { mutableStateOf("") }
        var phoneInput by remember { mutableStateOf("") }
        var detailsInput by remember { mutableStateOf("I would like to consult about my legal issue...") }
        var requestStatus by remember { mutableStateOf("IDLE") } // IDLE, SENDING, SENT

        AlertDialog(
            onDismissRequest = { showConsultationDialog = null },
            confirmButton = {
                if (requestStatus == "SENT") {
                    Button(onClick = { showConsultationDialog = null }) {
                        Text("Close")
                    }
                } else {
                    Button(
                        onClick = {
                            requestStatus = "SENDING"
                            viewModel.speak("Sending consulting dossier to ${firm.name}.")
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                requestStatus = "SENT"
                                viewModel.speak("Dossier transmitted securely. They will reach out in 2 hours.")
                            }, 1500)
                        },
                        enabled = nameInput.isNotBlank() && phoneInput.isNotBlank()
                    ) {
                        Text("Submit Secured Case")
                    }
                }
            },
            dismissButton = {
                if (requestStatus != "SENT") {
                    TextButton(onClick = { showConsultationDialog = null }) {
                        Text("Cancel")
                    }
                }
            },
            title = {
                Text(
                    text = "Request Consultation\nwith ${firm.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                if (requestStatus == "SENT") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF2ECC71),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Consultation Dossier Sent!",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${firm.name} has received your encrypted dossier and will contact you shortly at $phoneInput.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Fill in your secure details. We encrypt your legal dossier before transmitting it directly to the firm.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Your Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Callback Phone Number") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = detailsInput,
                            onValueChange = { detailsInput = it },
                            label = { Text("Case Brief Overview") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            maxLines = 3
                        )
                    }
                }
            }
        )
    }
}
