package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ScannerScreen(viewModel: LawViewModel) {
    val scannerActive by viewModel.scannerActive.collectAsState()
    val activeChannel by viewModel.scannerChannelIndex.collectAsState()
    val squelch by viewModel.scannerSquelch.collectAsState()
    val volume by viewModel.scannerVolume.collectAsState()
    val transcripts by viewModel.scannerTranscript.collectAsState()

    val scrollState = rememberLazyListState()

    // Auto-scroll transcripts when active
    LaunchedEffect(transcripts.size) {
        if (transcripts.isNotEmpty()) {
            scrollState.animateScrollToItem(transcripts.size - 1)
        }
    }

    // Interactive waveform phase animator
    var wavePhase by remember { mutableStateOf(0f) }
    LaunchedEffect(scannerActive) {
        if (scannerActive) {
            while (true) {
                wavePhase += 0.2f
                delay(50L)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // RETRO MONITOR SCANNER UNIT
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Deep Slate
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(3.dp, if (scannerActive) Color(0xFF22C55E) else Color(0xFF64748B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title/Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (scannerActive) Color(0xFF22C55E) else Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (scannerActive) "LIVE FREQUENCY FEED" else "SCANNER RECEIVER OFFLINE",
                                color = if (scannerActive) Color(0xFF22C55E) else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        
                        Text(
                            text = if (scannerActive) viewModel.scannerChannels[activeChannel].substringBefore(" -") else "---.-- MHz",
                            color = if (scannerActive) Color(0xFF22C55E) else Color(0xFF64748B),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // WAVEFORM CANVAS VISUALIZER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF020617)) // Pitch Black
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val centerY = size.height / 2f
                            val points = size.width.toInt()
                            
                            if (scannerActive) {
                                // Draw retro tactical grid
                                for (i in 0..size.width.toInt() step 40) {
                                    drawLine(
                                        color = Color(0xFF166534).copy(alpha = 0.3f),
                                        start = Offset(i.toFloat(), 0f),
                                        end = Offset(i.toFloat(), size.height),
                                        strokeWidth = 1f
                                    )
                                }
                                for (j in 0..size.height.toInt() step 20) {
                                    drawLine(
                                        color = Color(0xFF166534).copy(alpha = 0.3f),
                                        start = Offset(0f, j.toFloat()),
                                        end = Offset(size.width, j.toFloat()),
                                        strokeWidth = 1f
                                    )
                                }

                                // Sine-wave draw
                                var prevX = 0f
                                var prevY = centerY
                                for (x in 0..points step 4) {
                                    val angle = (x.toFloat() * 0.05f) + wavePhase
                                    val amplitude = centerY * 0.6f * squelch * (1f - (Math.random().toFloat() * 0.15f))
                                    val y = centerY + (Math.sin(angle.toDouble()).toFloat() * amplitude)
                                    
                                    drawLine(
                                        color = Color(0xFF22C55E),
                                        start = Offset(prevX, prevY),
                                        end = Offset(x.toFloat(), y),
                                        strokeWidth = 3f
                                    )
                                    prevX = x.toFloat()
                                    prevY = y
                                }
                            } else {
                                // Straight dead-line with static dots
                                drawLine(
                                    color = Color(0xFF475569),
                                    start = Offset(0f, centerY),
                                    end = Offset(size.width, centerY),
                                    strokeWidth = 2f
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SQUELCH / VOLUME controllers
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Squelch Sensitivity", fontSize = 11.sp, color = Color(0xFF94A3B8), fontFamily = FontFamily.Monospace)
                                Text(String.format("%.1f dB", squelch * 10f), fontSize = 11.sp, color = Color(0xFF22C55E), fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = squelch,
                                onValueChange = { viewModel.setScannerSquelch(it) },
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF22C55E),
                                    activeTrackColor = Color(0xFF22C55E)
                                )
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Volume Power", fontSize = 11.sp, color = Color(0xFF94A3B8), fontFamily = FontFamily.Monospace)
                                Text(String.format("%d %%", (volume * 100).toInt()), fontSize = 11.sp, color = Color(0xFF22C55E), fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = volume,
                                onValueChange = { viewModel.setScannerVolume(it) },
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF22C55E),
                                    activeTrackColor = Color(0xFF22C55E)
                                )
                            )
                        }
                    }
                }
            }
        }

        // CONTROL BUTTONS ROW
        item {
            Button(
                onClick = { viewModel.toggleScanner() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (scannerActive) Color(0xFFEF4444) else Color(0xFF22C55E),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (scannerActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (scannerActive) "Stop" else "Listen",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (scannerActive) "SHUTDOWN SCANNER FEED" else "LISTEN LIVE POLICE SCANNER",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // CHANNELS DIAL ROW
        item {
            Text(
                text = "Preset Dispatch Bands",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                viewModel.scannerChannels.forEachIndexed { index, ch ->
                    val isSelected = activeChannel == index
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setScannerChannel(index) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "BAND 0${index + 1}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF64748B)
                            )
                            Text(
                                text = ch.substringBefore(" MHz"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // CHANNEL LABEL CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Active Band Detail", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Active Presets Detail", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(viewModel.scannerChannels[activeChannel], fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // LOG TRANSCRIPTS
        item {
            Text(
                text = "Dispatch Terminal Logs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                if (transcripts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Awaiting connection stream...\nEnable receiver to download bulletins.",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(transcripts) { log ->
                            Text(
                                text = log,
                                color = if (log.contains("[TUNED]") || log.contains("[SCANNER ON]")) Color(0xFF60A5FA) else Color(0xFF34D399),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
