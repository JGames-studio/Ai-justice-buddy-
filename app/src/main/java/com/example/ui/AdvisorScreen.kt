package com.example.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvisorScreen(viewModel: LawViewModel) {
    val context = LocalContext.current
    val advisorInput by viewModel.advisorInput.collectAsState()
    val selectedState by viewModel.selectedState.collectAsState()
    val isAdvisorLoading by viewModel.isAdvisorLoading.collectAsState()
    val advisorResponse by viewModel.advisorResponse.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val avatarConfig by viewModel.avatarConfig.collectAsState()

    // Multimodal image state
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedSampleDocument by remember { mutableStateOf<String?>(null) } // "Citation", "Lease", "NoTrespass"
    val isAnalyzingImage by viewModel.isAnalyzingImage.collectAsState()
    val imageAnalysisResponse by viewModel.imageAnalysisResponse.collectAsState()
    var imageDescription by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    // Activity launcher for choosing an image
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            selectedSampleDocument = null
        }
    }

    val statesList = listOf("Federal", "California", "New York", "Texas", "Florida", "Illinois")
    var statesDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // AI Avatar Greet Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(24.dp),
            border = CardStrokeHelper.cardStroke()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    CartoonLawyerAvatar(
                        faceStyle = avatarConfig.faceStyle,
                        outfitStyle = avatarConfig.outfitStyle,
                        backgroundStyle = avatarConfig.backgroundStyle,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "AI Legal Advisor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        JGamesStudioLogo()
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "State your situation below. I will search relevant laws and point out loopholes and enforcer errors.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // GOLDEN RULES CHECKLIST CARD (KNOW YOUR RIGHTS ONBOARDING)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Golden Rules",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Golden Rules: Dealing with Law Enforcers",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Keep these 5 non-negotiable constitutional rules in mind during any physical encounter:",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(12.dp))

                val goldenRules = listOf(
                    "Remain Calm & Polite" to "Do not run, physically resist, or lie. Keep your hands visible at all times.",
                    "Right to Silence" to "You have the constitutional right to remain silent under the 5th Amendment. Clearly state: 'I am invoking my right to remain silent.'",
                    "Do Not Consent to Searches" to "Never consent to searches of your person, bag, car, or home. Explicitly state: 'I do not consent to any searches.'",
                    "Ask If You are Free to Go" to "Ask enforcers: 'Am I being detained, or am I free to go?' If free, calmly walk away.",
                    "Request Your Counsel" to "If arrested, immediately say: 'I want to speak with my lawyer.' Do not answer any questions without counsel present."
                )

                goldenRules.forEachIndexed { index, rule ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = rule.first,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = rule.second,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            border = CardStrokeHelper.cardStroke(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Evaluate a Situation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // State Selector Dropdown
                ExposedDropdownMenuBox(
                    expanded = statesDropdownExpanded,
                    onExpandedChange = { statesDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedState,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jurisdiction / State") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statesDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = statesDropdownExpanded,
                        onDismissRequest = { statesDropdownExpanded = false }
                    ) {
                        statesList.forEach { state ->
                            DropdownMenuItem(
                                text = { Text(state) },
                                onClick = {
                                    viewModel.updateSelectedState(state)
                                    statesDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Situation Prompt
                OutlinedTextField(
                    value = advisorInput,
                    onValueChange = { viewModel.updateAdvisorInput(it) },
                    label = { Text("What happened? (e.g. pulled over, security deposit dispute...)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("advisor_input"),
                    maxLines = 5,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Voice Presets (Fast Dictation Simulation)
                Text(
                    text = "Quick Voice Presets (Simulated Mic):",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(
                        "Traffic Stop" to "Police pulled me over, claimed I sped, then searched my trunk without consent.",
                        "Eviction threat" to "Landlord texted me to pack up and get out by Friday or she will change the locks."
                    )
                    presets.forEach { (label, text) ->
                        SuggestionChip(
                            onClick = {
                                viewModel.updateAdvisorInput(text)
                                if (label.contains("Traffic")) {
                                    viewModel.updateSelectedState("Federal")
                                } else {
                                    viewModel.updateSelectedState("California")
                                }
                            },
                            label = { Text(label, fontSize = 11.sp) },
                            icon = { Icon(Icons.Default.Mic, contentDescription = "Preset Mic", modifier = Modifier.size(14.dp)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ACTION BUTTON
                Button(
                    onClick = { viewModel.evaluateSituation() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_advisor"),
                    enabled = !isAdvisorLoading && advisorInput.trim().isNotEmpty()
                ) {
                    if (isAdvisorLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Searching Laws & Loopholes...")
                    } else {
                        Icon(Icons.Default.Gavel, contentDescription = "Evaluate")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Evaluate Legality & Find Loopholes")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // EVALUATION RESULT CARD
        AnimatedVisibility(
            visible = advisorResponse.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = CardStrokeHelper.cardStroke()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI Legal Counsel Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row {
                            IconButton(onClick = {
                                if (isSpeaking) viewModel.stopSpeaking() else viewModel.speak(advisorResponse.replace(Regex("[#*]"), ""))
                            }) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                    contentDescription = "Speak Aloud",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = {
                                viewModel.addCase(
                                    title = "Case: ${advisorInput.take(25)}...",
                                    state = selectedState,
                                    description = advisorInput,
                                    aiFeedback = advisorResponse
                                )
                                viewModel.speak("Case saved to local folders!")
                            }) {
                                Icon(Icons.Default.Save, contentDescription = "Save Case", tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = advisorResponse,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Disclaimer: This feedback is for informational purposes only and does not constitute official legal representation.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // DOCUMENT & IMAGE ANALYZER SECTION
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            border = CardStrokeHelper.cardStroke(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Image, contentDescription = "Camera", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Upload Video / Document / Scene Photo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Analyze lease agreements, tickets, or evidence photos for legal violations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Sample document fast options (Critical for Emulator!)
                Text(
                    text = "Select Sample Legal Asset (For instant testing):",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val samples = listOf(
                        "Speeding Ticket" to "Citation",
                        "Rental Lease" to "Lease",
                        "Warning Sign" to "NoTrespass"
                    )
                    samples.forEach { (label, value) ->
                        FilterChip(
                            selected = selectedSampleDocument == value,
                            onClick = {
                                selectedSampleDocument = value
                                selectedImageUri = null
                                imageDescription = "Analyzing legality of $label"
                            },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Gallery Picker Button
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = "Gallery")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select File from Device Gallery")
                }

                // Selected visual preview
                if (selectedImageUri != null || selectedSampleDocument != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Render a beautiful stylized mockup of the document using vector styling!
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFF1F5F9))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = when (selectedSampleDocument) {
                                            "Citation" -> Icons.Default.ReceiptLong
                                            "Lease" -> Icons.Default.Description
                                            else -> Icons.Default.Warning
                                        },
                                        contentDescription = "Doc Icon",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = when (selectedSampleDocument) {
                                            "Citation" -> "MOCK_CITATION_TICKET_CA_2026.PNG"
                                            "Lease" -> "MOCK_RESIDENTIAL_LEASE_NY.PDF"
                                            else -> "EVIDENCE_NO_TRESPASS_SIGN.JPG"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "High-fidelity simulated file ready for Gemini AI analysis",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = imageDescription,
                    onValueChange = { imageDescription = it },
                    label = { Text("What is this file? (optional context)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Trigger AI Multimodal analysis
                Button(
                    onClick = {
                        val base64: String
                        val mime: String
                        
                        if (selectedSampleDocument != null) {
                            // Use dummy base64 representation of our mocks
                            base64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
                            mime = "image/png"
                            val promptContext = when (selectedSampleDocument) {
                                "Citation" -> "Analyze California Speeding Citation Ticket. The officer did not sign the front, radar calibration date is omitted, and road conditions were dry. Point out enforcer mistakes."
                                "Lease" -> "Analyze New York residential apartment lease. Look for landlord deposit withholding loopholes and late payment grace period legality under NY Real Property Law."
                                else -> "Analyze evidence photo of a private property warning sign. The sign is hidden behind a tree branch and lacks statutory citation codes."
                            }
                            viewModel.analyzeDocumentImage(base64, mime, "$promptContext. User Notes: $imageDescription")
                        } else if (selectedImageUri != null) {
                            val stream: InputStream? = context.contentResolver.openInputStream(selectedImageUri!!)
                            val bitmap = BitmapFactory.decodeStream(stream)
                            stream?.close()
                            
                            if (bitmap != null) {
                                val outStream = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outStream)
                                val byteArray = outStream.toByteArray()
                                base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                                mime = context.contentResolver.getType(selectedImageUri!!) ?: "image/jpeg"
                                viewModel.analyzeDocumentImage(base64, mime, imageDescription)
                            } else {
                                viewModel.speak("Failed to process image file.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isAnalyzingImage && (selectedImageUri != null || selectedSampleDocument != null),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    if (isAnalyzingImage) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing File Legality...")
                    } else {
                        Icon(Icons.Default.Psychology, contentDescription = "Analyze")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Legality Inspection")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // FILE ANALYSIS RESULT
        AnimatedVisibility(
            visible = imageAnalysisResponse.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardStrokeHelper.cardStroke(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AI File Inspection Result",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = imageAnalysisResponse,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

object CardStrokeHelper {
    @Composable
    fun cardStroke() = androidx.compose.foundation.BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    )
}
