package com.example.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun PrivateFirmSuiteScreen(viewModel: LawViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("AI Cloud Storage", "Court e-Filing", "Financial Accountant", "Dedicated Assistant")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Brand Header Row with jgames.studio logo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Firm Business Services",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            JGamesStudioLogo()
        }

        // Tab indicator row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 8.dp,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = when (index) {
                                0 -> Icons.Default.CloudQueue
                                1 -> Icons.Default.Publish
                                2 -> Icons.Default.AccountBalanceWallet
                                else -> Icons.Default.Assistant
                            },
                            contentDescription = title,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedTab) {
                0 -> CloudStorageTab(viewModel)
                1 -> CourtFilingTab(viewModel)
                2 -> FinancialAccountantTab(viewModel)
                3 -> DedicatedAssistantTab(viewModel)
            }
        }

        // CONFIDENTIAL COMPLIANCE INFO STRIP
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Local Locker",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Absolute Admin Isolation Shield Active | jgames.studio is not liable for case outcomes or internet transmission leaks.",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

// --- TAB 1: AI CLOUD STORAGE ---
@Composable
fun CloudStorageTab(viewModel: LawViewModel) {
    val documents by viewModel.firmDocuments.collectAsState()
    var showUploadDialog by remember { mutableStateOf(false) }

    var docName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Contract") }
    val categories = listOf("Contract", "Pleading", "Evidence", "Invoice", "Corporate")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
            shape = RoundedCornerShape(16.dp),
            border = CardStrokeHelper.cardStroke()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CloudDone,
                        contentDescription = "Cloud",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Secured Private AI Cloud",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Military-grade client privilege AES-256 local document locker.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress indicator
                val totalGb = 250.0
                val usedMb = documents.size * 2.4 + 121.2 // dynamic mockup
                val usedGb = usedMb / 1024.0
                val progress = (usedGb / totalGb).toFloat().coerceIn(0.01f, 1f)

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        String.format("%.2f MB of %.0f GB Used", usedMb, totalGb),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("99.9% Reliable AI Backup", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Document Locker (${documents.size} files)",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Button(
                onClick = { showUploadDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Document", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (documents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CloudOff,
                        contentDescription = "Empty",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No documents uploaded yet.", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Store contracts or filings securely in the firm sandbox.", fontSize = 11.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(documents) { doc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = CardStrokeHelper.cardStroke()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (doc.category) {
                                            "Contract" -> Icons.Default.Assignment
                                            "Pleading" -> Icons.Default.Gavel
                                            "Evidence" -> Icons.Default.Visibility
                                            else -> Icons.Default.Description
                                        },
                                        contentDescription = doc.category,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        doc.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${doc.category} • ${doc.size}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFE0F2FE))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text("AES-256", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                                        }
                                    }
                                }
                            }
                            Row {
                                IconButton(onClick = { /* simulate download */ }) {
                                    Icon(Icons.Default.Download, contentDescription = "Download", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = { viewModel.deleteFirmDocument(doc.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showUploadDialog) {
        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            title = { Text("Upload Secure Firm Document") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = docName,
                        onValueChange = { docName = it },
                        label = { Text("Document Name") },
                        placeholder = { Text("e.g. Master_Agreement") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Document Category", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { selectedCategory = cat }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    cat,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (docName.isNotBlank()) {
                            viewModel.addFirmDocument(docName, selectedCategory, "2.4 MB")
                            docName = ""
                            showUploadDialog = false
                        }
                    },
                    enabled = docName.isNotBlank()
                ) {
                    Text("Secure & Upload")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// --- TAB 2: COURT ELECTRONIC CASE FILING ---
@Composable
fun CourtFilingTab(viewModel: LawViewModel) {
    val filedCases by viewModel.filedCases.collectAsState()

    var showFilingForm by remember { mutableStateOf(false) }
    var caseTitle by remember { mutableStateOf("") }
    var selectedJurisdiction by remember { mutableStateOf("US District Court - Central CA") }
    var caseType by remember { mutableStateOf("Civil Breach of Contract") }
    var signature by remember { mutableStateOf("") }

    val jurisdictions = listOf(
        "US District Court - Central CA",
        "US District Court - Southern NY",
        "CA Superior Court - Los Angeles",
        "NY Supreme Court - Manhattan"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), // Slate 800
            shape = RoundedCornerShape(16.dp),
            border = CardStrokeHelper.cardStroke()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Publish,
                    contentDescription = "Filing",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "AI Electronic Court e-Filing",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        "Draft, validate, sign, and electronically file case pleadings directly into public court ledgers.",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Electronic Pleadings File Ledger",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Button(
                onClick = { showFilingForm = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.DriveFileRenameOutline, contentDescription = "File", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New e-Filing", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filedCases) { cs ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    border = CardStrokeHelper.cardStroke()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(cs.filingId, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            val badgeColor = when (cs.status) {
                                "Filing Certified" -> Color(0xFFD1FAE5) to Color(0xFF065F46)
                                "Clerk Reviewing" -> Color(0xFFFEF3C7) to Color(0xFF92400E)
                                else -> Color(0xFFF1F5F9) to Color(0xFF475569)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badgeColor.first)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(cs.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeColor.second)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(cs.title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        Text(cs.jurisdiction, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Type: ${cs.caseType} • Signed: ${cs.signatureName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Filed on: ${cs.filingDate}", fontSize = 11.sp, color = Color.Gray)
                            TextButton(onClick = { /* simulate seal receipt */ }) {
                                Icon(Icons.Default.Verified, contentDescription = "Receipt", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Court Receipt", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilingForm) {
        AlertDialog(
            onDismissRequest = { showFilingForm = false },
            title = { Text("Draft e-Filing Court Pleadings") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = caseTitle,
                        onValueChange = { caseTitle = it },
                        label = { Text("Case Title (Plaintiff v. Defendant)") },
                        placeholder = { Text("e.g. John Doe v. Acme Corporation") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Jurisdiction", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    jurisdictions.forEach { jur ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedJurisdiction = jur }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = selectedJurisdiction == jur, onClick = { selectedJurisdiction = jur })
                            Text(jur, fontSize = 12.sp)
                        }
                    }

                    OutlinedTextField(
                        value = caseType,
                        onValueChange = { caseType = it },
                        label = { Text("Filing Type / Pleading Category") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = signature,
                        onValueChange = { signature = it },
                        label = { Text("Electronic Digital Signature (Esq.)") },
                        placeholder = { Text("e.g. Jane Doe, Esq.") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (caseTitle.isNotBlank() && signature.isNotBlank()) {
                            viewModel.submitCaseFiling(caseTitle, selectedJurisdiction, caseType, signature)
                            caseTitle = ""
                            signature = ""
                            showFilingForm = false
                        }
                    },
                    enabled = caseTitle.isNotBlank() && signature.isNotBlank()
                ) {
                    Text("Digital Sign & File")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFilingForm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// --- TAB 3: PRIVATE AI FINANCIAL ACCOUNTANT ---
@Composable
fun FinancialAccountantTab(viewModel: LawViewModel) {
    val invoices by viewModel.financialInvoices.collectAsState()
    val trustBalance by viewModel.ioltaBalance.collectAsState()

    var showInvoiceForm by remember { mutableStateOf(false) }
    var clientName by remember { mutableStateOf("") }
    var invoiceAmount by remember { mutableStateOf("") }

    var accountingInput by remember { mutableStateOf("") }
    val accountantResponse by viewModel.accountingResponse.collectAsState()
    val isAccountantLoading by viewModel.isAccountingLoading.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // trust account and general balance block
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1.1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Slate 900
                shape = RoundedCornerShape(16.dp),
                border = CardStrokeHelper.cardStroke()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("CLIENT TRUST ACCOUNT", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("IOLTA Compliant", fontSize = 9.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(String.format("$%,.2f", trustBalance), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }

            Card(
                modifier = Modifier.weight(0.9f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = CardStrokeHelper.cardStroke()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("PENDING RECEIVABLES", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(14.dp))
                    val pendingSum = invoices.filter { it.status != "Paid" }.sumOf { it.amount }
                    Text(String.format("$%,.2f", pendingSum), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ask Financial Accountant CPA Chat
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(2.dp, Color(0xFF3B82F6)) // Dynamic Blue 500
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SupportAgent, contentDescription = "Advisor", tint = Color(0xFF2563EB))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Private AI CPA Accountant", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        Text("Forensic trust audit & tax compliance assistant", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = accountingInput,
                    onValueChange = { accountingInput = it },
                    label = { Text("Ask Private AI Accountant...") },
                    placeholder = { Text("e.g. How to log client retainer to IOLTA?") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.askFinancialAccountant(accountingInput)
                        accountingInput = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isAccountantLoading && accountingInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    if (isAccountantLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                    } else {
                        Text("Analyze Trust Ledger")
                    }
                }

                AnimatedVisibility(visible = accountantResponse.isNotEmpty()) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Accountant's Compliance Ruling:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1D4ED8))
                        Text(accountantResponse, fontSize = 12.sp, lineHeight = 18.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Invoices list title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Client Billing Invoices", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = { showInvoiceForm = true },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create Invoice", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Render Invoices
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            invoices.forEach { inv ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    border = CardStrokeHelper.cardStroke()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(inv.clientName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Invoice: ${inv.id} • ${inv.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(String.format("$%,.2f", inv.amount), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                val color = when (inv.status) {
                                    "Paid" -> Color(0xFF059669)
                                    "Pending" -> Color(0xFFD97706)
                                    else -> Color(0xFFDC2626)
                                }
                                Text(inv.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
                            }
                            if (inv.status != "Paid") {
                                Button(
                                    onClick = { viewModel.markInvoicePaid(inv.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Log Pay", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showInvoiceForm) {
        AlertDialog(
            onDismissRequest = { showInvoiceForm = false },
            title = { Text("Generate Retainer Invoice") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Client/Case Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = invoiceAmount,
                        onValueChange = { invoiceAmount = it },
                        label = { Text("Billable Retainer Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = invoiceAmount.toDoubleOrNull()
                        if (clientName.isNotBlank() && amt != null) {
                            viewModel.addInvoice(clientName, amt)
                            clientName = ""
                            invoiceAmount = ""
                            showInvoiceForm = false
                        }
                    },
                    enabled = clientName.isNotBlank() && invoiceAmount.isNotBlank()
                ) {
                    Text("Save & Dispatch")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInvoiceForm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// --- TAB 4: PRIVATE EXECUTIVE AI ASSISTANT ---
@Composable
fun DedicatedAssistantTab(viewModel: LawViewModel) {
    var queryInput by remember { mutableStateOf("") }
    val assistantResponse by viewModel.privateAssistantResponse.collectAsState()
    val isAssistantLoading by viewModel.isPrivateAssistantLoading.collectAsState()

    val shortcuts = listOf(
        "Draft Demand Letter" to "Draft a formal Breach of Contract Demand Letter to AlphaCorp demanding $50k outstanding for services rendered.",
        "ND Agreement" to "Generate a strict, state-compliant Non-Disclosure Agreement (NDA) between JGames and prospective developer partners.",
        "Draft Client Memo" to "Write a structured legal client memo summarizing constitutional search rights on private vehicle items during stops."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            border = CardStrokeHelper.cardStroke()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Support,
                        contentDescription = "Assistant",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Elite AI Counsel Assistant",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            "Dedicated associate for custom briefs, contract drafting, and letter formulation.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Shortcuts Title
        Text("Quick Executive Draft Shortcuts", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            shortcuts.forEach { pair ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { queryInput = pair.second }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        pair.first,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Query input
        OutlinedTextField(
            value = queryInput,
            onValueChange = { queryInput = it },
            label = { Text("What draft or summary do you need?") },
            placeholder = { Text("Describe the briefing or letter parameters...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                viewModel.askDedicatedAssistant(queryInput)
                queryInput = ""
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isAssistantLoading && queryInput.isNotBlank()
        ) {
            if (isAssistantLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
            } else {
                Text("Draft Legal Material")
            }
        }

        // Response Render block
        AnimatedVisibility(visible = assistantResponse.isNotEmpty()) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardStrokeHelper.cardStroke()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("DRAFT PREPARATION READY", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            Row {
                                IconButton(onClick = { viewModel.speak(assistantResponse.replace(Regex("[#*]"), "")) }) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen", modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = assistantResponse,
                            fontSize = 12.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
