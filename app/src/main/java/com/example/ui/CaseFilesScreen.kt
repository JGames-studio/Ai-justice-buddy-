package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CaseFile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CaseFilesScreen(viewModel: LawViewModel) {
    val casesList by viewModel.cases.collectAsState()
    var expandedCaseId by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val zipExists by viewModel.zipFileExists.collectAsState()
    val isCreatingZip by viewModel.isCreatingZip.collectAsState()
    val zipCreationError by viewModel.zipCreationError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.checkZipExists(context)
    }

    LaunchedEffect(zipCreationError) {
        zipCreationError?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(innerPadding)
    ) {
        Text(
            text = "Case Folders",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Your legal folders containing saved analyses, evidence files, and case strategies. Persisted locally and secure.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ZIP Export Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            border = CardStrokeHelper.cardStroke()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.FolderZip,
                        contentDescription = "ZIP Export",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Case Files ZIP Export",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (zipExists) {
                        // Status chip is display-only; onClick is intentionally empty
                        SuggestionChip(
                            onClick = {},
                            label = { Text("ZIP Ready", fontSize = 10.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Create ZIP
                    Button(
                        onClick = { viewModel.createCasesZip(context) },
                        enabled = !isCreatingZip,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                    ) {
                        if (isCreatingZip) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Archive, contentDescription = "Create ZIP", modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isCreatingZip) "Creating…" else "Create ZIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Download / Share ZIP (visible when zip exists)
                    if (zipExists) {
                        Button(
                            onClick = { viewModel.shareCasesZip(context) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Download ZIP", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Download ZIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Erase ZIP
                        OutlinedButton(
                            onClick = { viewModel.eraseCasesZip(context) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = "Erase ZIP", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Erase ZIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (casesList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FolderOpen,
                        contentDescription = "Empty Folders",
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Your Case Folders are empty",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Run an evaluation in the AI Advisor and tap Save.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(casesList) { case ->
                    val isExpanded = expandedCaseId == case.id
                    val formattedDate = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
                        .format(Date(case.timestamp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedCaseId = if (isExpanded) null else case.id }
                            .testTag("case_card_${case.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp),
                        border = CardStrokeHelper.cardStroke(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Primary Info Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(case.state, fontSize = 10.sp) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(case.status, fontSize = 10.sp) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = case.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Created: $formattedDate",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { viewModel.removeCase(case.id) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            // Expanded feedback
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = expandVertically(animationSpec = tween(200)),
                                exit = shrinkVertically(animationSpec = tween(200))
                            ) {
                                Column(modifier = Modifier.padding(top = 16.dp)) {
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Description / Situation
                                    Text(
                                        text = "Your Stated Facts:",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = case.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    // Feedback
                                    Text(
                                        text = "AI Legal Counsel Feedback & Loopholes:",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = case.aiFeedback,
                                            style = MaterialTheme.typography.bodyMedium,
                                            lineHeight = 22.sp,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(onClick = { viewModel.speak(case.aiFeedback.replace(Regex("[#*]"), "")) }) {
                                            Icon(Icons.Default.VolumeUp, contentDescription = "Listen")
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Listen to Counsel")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    } // end Scaffold
}
