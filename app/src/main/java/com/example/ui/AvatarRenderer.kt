package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun CartoonLawyerAvatar(
    faceStyle: String,
    outfitStyle: String,
    backgroundStyle: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.background(Color.Transparent)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. DRAW BACKGROUND
            when (backgroundStyle) {
                "Courtroom Podium" -> {
                    // Wood gradients + Scales of justice shadow
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2C1B18), Color(0xFF4A2E2B))
                        )
                    )
                    // Scales shadow
                    drawCircle(Color(0x1F000000), radius = width * 0.35f, center = Offset(width * 0.5f, height * 0.4f))
                    // Stand pole
                    drawRect(Color(0x1AFFFFFF), topLeft = Offset(width * 0.48f, height * 0.15f), size = Size(width * 0.04f, height * 0.4f))
                    // Cross bar
                    drawRect(Color(0x1AFFFFFF), topLeft = Offset(width * 0.3f, height * 0.22f), size = Size(width * 0.4f, height * 0.03f))
                }
                "Classic Library" -> {
                    // Shelf style background
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF3E2723), Color(0xFF1B0000))
                        )
                    )
                    // Draw books (vertical bars)
                    val bookColors = listOf(Color(0xFF8B0000), Color(0xFF00008B), Color(0xFF006400), Color(0xFFD4AF37), Color(0xFF4B0082))
                    for (i in 0..12) {
                        val bookWidth = width / 14
                        val bookHeight = height * 0.25f + (i % 3) * 15f
                        val bColor = bookColors[i % bookColors.size]
                        drawRoundRect(
                            color = bColor,
                            topLeft = Offset(i * bookWidth + 10f, height * 0.1f),
                            size = Size(bookWidth - 8f, bookHeight),
                            cornerRadius = CornerRadius(5f, 5f)
                        )
                        // Book spines
                        drawRect(
                            color = Color(0x33FFFFFF),
                            topLeft = Offset(i * bookWidth + 15f, height * 0.12f),
                            size = Size(2f, bookHeight - 20f)
                        )
                    }
                }
                "Modern Office" -> {
                    // Sleek gradient + glass window frame lines
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF3A6073), Color(0xFF16222F))
                        )
                    )
                    // Diagonal light highlights
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(width * 0.4f, 0f)
                        lineTo(0f, height * 0.7f)
                        close()
                    }
                    drawPath(path, color = Color(0x0DFFFFFF))
                    // Window frame
                    drawLine(Color(0x2BFFFFFF), start = Offset(width * 0.5f, 0f), end = Offset(width * 0.5f, height), strokeWidth = 8f)
                    drawLine(Color(0x2BFFFFFF), start = Offset(0f, height * 0.4f), end = Offset(width, height * 0.4f), strokeWidth = 8f)
                }
                else -> { // "City View Night"
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF000428), Color(0xFF004E92))
                        )
                    )
                    // Star dots
                    drawCircle(Color.White, radius = 3f, center = Offset(width * 0.15f, height * 0.15f))
                    drawCircle(Color.White, radius = 4f, center = Offset(width * 0.85f, height * 0.22f))
                    drawCircle(Color.White, radius = 2f, center = Offset(width * 0.45f, height * 0.08f))
                    // Skyscraper shadows
                    drawRect(Color(0xFF02091A), topLeft = Offset(width * 0.1f, height * 0.35f), size = Size(width * 0.25f, height * 0.65f))
                    drawRect(Color(0xFF051026), topLeft = Offset(width * 0.65f, height * 0.45f), size = Size(width * 0.3f, height * 0.55f))
                    // Lit windows in buildings
                    drawRect(Color(0xFFF9D423), topLeft = Offset(width * 0.15f, height * 0.45f), size = Size(15f, 20f))
                    drawRect(Color(0xFFF9D423), topLeft = Offset(width * 0.25f, height * 0.55f), size = Size(15f, 20f))
                    drawRect(Color(0xFFF9D423), topLeft = Offset(width * 0.75f, height * 0.52f), size = Size(15f, 20f))
                }
            }

            // 2. DRAW FACE (BASE HEAD)
            val headRadius = width * 0.22f
            val headCenter = Offset(width * 0.5f, height * 0.42f)
            
            // Neck
            drawRect(
                color = Color(0xFFFCD3A1), // Warm healthy tone
                topLeft = Offset(width * 0.44f, headCenter.y + headRadius - 15f),
                size = Size(width * 0.12f, height * 0.15f)
            )
            // Neck shadow
            drawRect(
                color = Color(0x1F000000),
                topLeft = Offset(width * 0.44f, headCenter.y + headRadius - 15f),
                size = Size(width * 0.12f, 20f)
            )

            // Head circle
            drawCircle(color = Color(0xFFFCD3A1), radius = headRadius, center = headCenter)

            // 3. DRAW HAIR & GLASSES (BASED ON FACE STYLE)
            when (faceStyle) {
                "The Stern Judge" -> {
                    // Classic silver parted judge/legal hair
                    drawCircle(Color(0xFFE2E8F0), radius = headRadius * 1.05f, center = headCenter.copy(y = headCenter.y - 10f))
                    // Hollow out the face
                    drawCircle(Color(0xFFFCD3A1), radius = headRadius * 0.98f, center = headCenter)
                    // Parted fringe
                    val leftFringe = Path().apply {
                        moveTo(headCenter.x - headRadius, headCenter.y - headRadius + 10f)
                        quadraticTo(headCenter.x - headRadius * 0.4f, headCenter.y - headRadius * 0.5f, headCenter.x, headCenter.y - headRadius * 0.2f)
                        lineTo(headCenter.x - headRadius, headCenter.y - headRadius * 0.2f)
                        close()
                    }
                    val rightFringe = Path().apply {
                        moveTo(headCenter.x + headRadius, headCenter.y - headRadius + 10f)
                        quadraticTo(headCenter.x + headRadius * 0.4f, headCenter.y - headRadius * 0.5f, headCenter.x, headCenter.y - headRadius * 0.2f)
                        lineTo(headCenter.x + headRadius, headCenter.y - headRadius * 0.2f)
                        close()
                    }
                    drawPath(leftFringe, Color(0xFFCBD5E1))
                    drawPath(rightFringe, Color(0xFFCBD5E1))
                    // Silver eyebrows
                    drawRoundRect(Color(0xFF94A3B8), topLeft = Offset(headCenter.x - 55f, headCenter.y - 45f), size = Size(40f, 10f), cornerRadius = CornerRadius(4f, 4f))
                    drawRoundRect(Color(0xFF94A3B8), topLeft = Offset(headCenter.x + 15f, headCenter.y - 45f), size = Size(40f, 10f), cornerRadius = CornerRadius(4f, 4f))
                }
                "The Slick Litigator" -> {
                    // Sharp black slicked back hair with sideburns
                    val hairPath = Path().apply {
                        moveTo(headCenter.x - headRadius - 5f, headCenter.y)
                        quadraticTo(headCenter.x - headRadius * 0.9f, headCenter.y - headRadius - 20f, headCenter.x, headCenter.y - headRadius - 15f)
                        quadraticTo(headCenter.x + headRadius * 0.9f, headCenter.y - headRadius - 20f, headCenter.x + headRadius + 5f, headCenter.y)
                        lineTo(headCenter.x + headRadius, headCenter.y - headRadius)
                        lineTo(headCenter.x - headRadius, headCenter.y - headRadius)
                        close()
                    }
                    drawPath(hairPath, Color(0xFF212529))
                    // Gold rimmed glasses
                    drawCircle(Color(0xFFD4AF37), radius = 30f, center = Offset(headCenter.x - 45f, headCenter.y - 10f), style = Stroke(width = 4f))
                    drawCircle(Color(0xFFD4AF37), radius = 30f, center = Offset(headCenter.x + 45f, headCenter.y - 10f), style = Stroke(width = 4f))
                    drawLine(Color(0xFFD4AF37), start = Offset(headCenter.x - 15f, headCenter.y - 10f), end = Offset(headCenter.x + 15f, headCenter.y - 10f), strokeWidth = 5f)
                    drawLine(Color(0xFFD4AF37), start = Offset(headCenter.x - 75f, headCenter.y - 10f), end = Offset(headCenter.x - headRadius, headCenter.y - 5f), strokeWidth = 4f)
                    drawLine(Color(0xFFD4AF37), start = Offset(headCenter.x + 75f, headCenter.y - 10f), end = Offset(headCenter.x + headRadius, headCenter.y - 5f), strokeWidth = 4f)
                }
                "The Tech Techie" -> {
                    // Trendy curly blond hair
                    drawCircle(Color(0xFFECC880), radius = headRadius * 0.35f, center = Offset(headCenter.x - headRadius * 0.7f, headCenter.y - headRadius * 0.7f))
                    drawCircle(Color(0xFFECC880), radius = headRadius * 0.35f, center = Offset(headCenter.x + headRadius * 0.7f, headCenter.y - headRadius * 0.7f))
                    drawCircle(Color(0xFFECC880), radius = headRadius * 0.45f, center = Offset(headCenter.x, headCenter.y - headRadius * 0.9f))
                    drawCircle(Color(0xFFECC880), radius = headRadius * 0.35f, center = Offset(headCenter.x - headRadius * 0.3f, headCenter.y - headRadius * 0.85f))
                    drawCircle(Color(0xFFECC880), radius = headRadius * 0.35f, center = Offset(headCenter.x + headRadius * 0.3f, headCenter.y - headRadius * 0.85f))
                    
                    // Hipster black frame glasses
                    drawRoundRect(Color(0xFF1A1A1A), topLeft = Offset(headCenter.x - 65f, headCenter.y - 25f), size = Size(50f, 35f), cornerRadius = CornerRadius(8f, 8f), style = Stroke(width = 6f))
                    drawRoundRect(Color(0xFF1A1A1A), topLeft = Offset(headCenter.x + 15f, headCenter.y - 25f), size = Size(50f, 35f), cornerRadius = CornerRadius(8f, 8f), style = Stroke(width = 6f))
                    drawLine(Color(0xFF1A1A1A), start = Offset(headCenter.x - 15f, headCenter.y - 10f), end = Offset(headCenter.x + 15f, headCenter.y - 10f), strokeWidth = 8f)
                }
                else -> { // "The Wise Counsel"
                    // Bald top, silver side hair puffs
                    drawCircle(Color(0xFFF1F5F9), radius = headRadius * 0.32f, center = Offset(headCenter.x - headRadius * 0.95f, headCenter.y - 10f))
                    drawCircle(Color(0xFFF1F5F9), radius = headRadius * 0.32f, center = Offset(headCenter.x + headRadius * 0.95f, headCenter.y - 10f))
                    drawCircle(Color(0xFFF1F5F9), radius = headRadius * 0.28f, center = Offset(headCenter.x - headRadius * 1.05f, headCenter.y + 15f))
                    drawCircle(Color(0xFFF1F5F9), radius = headRadius * 0.28f, center = Offset(headCenter.x + headRadius * 1.05f, headCenter.y + 15f))
                    // Friendly small round silver-rimmed glasses
                    drawCircle(Color(0xFF94A3B8), radius = 25f, center = Offset(headCenter.x - 38f, headCenter.y - 12f), style = Stroke(width = 4f))
                    drawCircle(Color(0xFF94A3B8), radius = 25f, center = Offset(headCenter.x + 38f, headCenter.y - 12f), style = Stroke(width = 4f))
                    drawLine(Color(0xFF94A3B8), start = Offset(headCenter.x - 13f, headCenter.y - 12f), end = Offset(headCenter.x + 13f, headCenter.y - 12f), strokeWidth = 4f)
                }
            }

            // Eyes (simple elegant cartoon eyes)
            drawCircle(Color(0xFF334155), radius = 7f, center = Offset(headCenter.x - 40f, headCenter.y - 8f))
            drawCircle(Color(0xFF334155), radius = 7f, center = Offset(headCenter.x + 40f, headCenter.y - 8f))
            // Eye sparkles
            drawCircle(Color.White, radius = 2.5f, center = Offset(headCenter.x - 42f, headCenter.y - 10f))
            drawCircle(Color.White, radius = 2.5f, center = Offset(headCenter.x + 38f, headCenter.y - 10f))

            // Cheeks (blush)
            drawCircle(Color(0x33FF8A8A), radius = 20f, center = Offset(headCenter.x - 60f, headCenter.y + 20f))
            drawCircle(Color(0x33FF8A8A), radius = 20f, center = Offset(headCenter.x + 60f, headCenter.y + 20f))

            // Nose
            val nosePath = Path().apply {
                moveTo(headCenter.x, headCenter.y - 5f)
                quadraticTo(headCenter.x + 12f, headCenter.y + 10f, headCenter.x, headCenter.y + 15f)
            }
            drawPath(nosePath, color = Color(0xFFE2A970), style = Stroke(width = 4f))

            // Mouth (smiling confidently)
            val mouthPath = Path().apply {
                moveTo(headCenter.x - 28f, headCenter.y + 42f)
                quadraticTo(headCenter.x, headCenter.y + 60f, headCenter.x + 28f, headCenter.y + 42f)
            }
            drawPath(mouthPath, color = Color(0xFFC0392B), style = Stroke(width = 4.5f))

            // 4. DRAW OUTFIT/SUITS (SHOULDERS AND COLLARS)
            val leftShoulder = Offset(width * 0.12f, height)
            val rightShoulder = Offset(width * 0.88f, height)
            val chestV = Offset(width * 0.5f, headCenter.y + headRadius + 110f)

            when (outfitStyle) {
                "The Classic Navy Suit" -> {
                    // Shoulders and coat
                    val suitPath = Path().apply {
                        moveTo(width * 0.36f, headCenter.y + headRadius - 5f)
                        lineTo(leftShoulder.x, leftShoulder.y)
                        lineTo(rightShoulder.x, rightShoulder.y)
                        lineTo(width * 0.64f, headCenter.y + headRadius - 5f)
                        lineTo(chestV.x, chestV.y)
                        close()
                    }
                    drawPath(suitPath, Color(0xFF1E3A8A)) // Navy Blue

                    // White inner shirt triangle
                    val shirtPath = Path().apply {
                        moveTo(width * 0.44f, headCenter.y + headRadius - 10f)
                        lineTo(width * 0.56f, headCenter.y + headRadius - 10f)
                        lineTo(width * 0.5f, headCenter.y + headRadius + 90f)
                        close()
                    }
                    drawPath(shirtPath, Color.White)

                    // Red Tie
                    val tiePath = Path().apply {
                        moveTo(width * 0.48f, headCenter.y + headRadius + 20f)
                        lineTo(width * 0.52f, headCenter.y + headRadius + 20f)
                        lineTo(width * 0.54f, headCenter.y + headRadius + 130f)
                        lineTo(width * 0.5f, headCenter.y + headRadius + 160f)
                        lineTo(width * 0.46f, headCenter.y + headRadius + 130f)
                        close()
                    }
                    drawPath(tiePath, Color(0xFFDC2626)) // Red

                    // Lapels
                    drawLine(Color(0xFF172554), start = Offset(width * 0.38f, headCenter.y + headRadius + 5f), end = Offset(width * 0.48f, headCenter.y + headRadius + 95f), strokeWidth = 10f)
                    drawLine(Color(0xFF172554), start = Offset(width * 0.62f, headCenter.y + headRadius + 5f), end = Offset(width * 0.52f, headCenter.y + headRadius + 95f), strokeWidth = 10f)
                }
                "The Royal Purple Tux" -> {
                    // Elegant purple velvet suit
                    val suitPath = Path().apply {
                        moveTo(width * 0.36f, headCenter.y + headRadius - 5f)
                        lineTo(leftShoulder.x, leftShoulder.y)
                        lineTo(rightShoulder.x, rightShoulder.y)
                        lineTo(width * 0.64f, headCenter.y + headRadius - 5f)
                        lineTo(chestV.x, chestV.y)
                        close()
                    }
                    drawPath(suitPath, Color(0xFF581C87)) // Purple

                    // White shirt
                    val shirtPath = Path().apply {
                        moveTo(width * 0.44f, headCenter.y + headRadius - 10f)
                        lineTo(width * 0.56f, headCenter.y + headRadius - 10f)
                        lineTo(width * 0.5f, headCenter.y + headRadius + 80f)
                        close()
                    }
                    drawPath(shirtPath, Color.White)

                    // Gold bow tie
                    val leftBow = Path().apply {
                        moveTo(width * 0.5f, headCenter.y + headRadius + 25f)
                        lineTo(width * 0.44f, headCenter.y + headRadius + 10f)
                        lineTo(width * 0.44f, headCenter.y + headRadius + 40f)
                        close()
                    }
                    val rightBow = Path().apply {
                        moveTo(width * 0.5f, headCenter.y + headRadius + 25f)
                        lineTo(width * 0.56f, headCenter.y + headRadius + 10f)
                        lineTo(width * 0.56f, headCenter.y + headRadius + 40f)
                        close()
                    }
                    drawPath(leftBow, Color(0xFFD4AF37))
                    drawPath(rightBow, Color(0xFFD4AF37))
                    drawCircle(Color(0xFFB49020), radius = 6f, center = Offset(width * 0.5f, headCenter.y + headRadius + 25f))

                    // Satin black lapels
                    drawLine(Color(0xFF120024), start = Offset(width * 0.38f, headCenter.y + headRadius + 5f), end = Offset(width * 0.48f, headCenter.y + headRadius + 85f), strokeWidth = 12f)
                    drawLine(Color(0xFF120024), start = Offset(width * 0.62f, headCenter.y + headRadius + 5f), end = Offset(width * 0.52f, headCenter.y + headRadius + 85f), strokeWidth = 12f)
                }
                "The Casual Blazer" -> {
                    // Grey and Teal
                    val suitPath = Path().apply {
                        moveTo(width * 0.36f, headCenter.y + headRadius - 5f)
                        lineTo(leftShoulder.x, leftShoulder.y)
                        lineTo(rightShoulder.x, rightShoulder.y)
                        lineTo(width * 0.64f, headCenter.y + headRadius - 5f)
                        lineTo(chestV.x, chestV.y)
                        close()
                    }
                    drawPath(suitPath, Color(0xFF0F766E)) // Teal

                    // Grey inner shirt
                    val shirtPath = Path().apply {
                        moveTo(width * 0.43f, headCenter.y + headRadius - 10f)
                        lineTo(width * 0.57f, headCenter.y + headRadius - 10f)
                        lineTo(width * 0.5f, headCenter.y + headRadius + 100f)
                        close()
                    }
                    drawPath(shirtPath, Color(0xFFE2E8F0)) // Light Slate Grey
                }
                else -> { // "The Detective Trenchcoat"
                    // Tan brown double-breasted trenchcoat
                    val suitPath = Path().apply {
                        moveTo(width * 0.36f, headCenter.y + headRadius - 5f)
                        lineTo(leftShoulder.x, leftShoulder.y)
                        lineTo(rightShoulder.x, rightShoulder.y)
                        lineTo(width * 0.64f, headCenter.y + headRadius - 5f)
                        lineTo(chestV.x, chestV.y)
                        close()
                    }
                    drawPath(suitPath, Color(0xFFC2410C)) // Rust Tan/Brown

                    // Inner white collar
                    val shirtPath = Path().apply {
                        moveTo(width * 0.44f, headCenter.y + headRadius - 10f)
                        lineTo(width * 0.56f, headCenter.y + headRadius - 10f)
                        lineTo(width * 0.5f, headCenter.y + headRadius + 85f)
                        close()
                    }
                    drawPath(shirtPath, Color.White)

                    // Black Tie
                    val tiePath = Path().apply {
                        moveTo(width * 0.48f, headCenter.y + headRadius + 15f)
                        lineTo(width * 0.52f, headCenter.y + headRadius + 15f)
                        lineTo(width * 0.53f, headCenter.y + headRadius + 110f)
                        lineTo(width * 0.5f, headCenter.y + headRadius + 130f)
                        lineTo(width * 0.47f, headCenter.y + headRadius + 110f)
                        close()
                    }
                    drawPath(tiePath, Color(0xFF0F172A)) // Slate Black

                    // Collar overlaps (drawing broad trenchcoat lapels)
                    val leftCollar = Path().apply {
                        moveTo(width * 0.34f, headCenter.y + headRadius + 5f)
                        lineTo(width * 0.47f, headCenter.y + headRadius + 85f)
                        lineTo(width * 0.42f, headCenter.y + headRadius + 95f)
                        close()
                    }
                    val rightCollar = Path().apply {
                        moveTo(width * 0.66f, headCenter.y + headRadius + 5f)
                        lineTo(width * 0.53f, headCenter.y + headRadius + 85f)
                        lineTo(width * 0.58f, headCenter.y + headRadius + 95f)
                        close()
                    }
                    drawPath(leftCollar, Color(0xFF9A3412))
                    drawPath(rightCollar, Color(0xFF9A3412))

                    // Double-breasted buttons (gold)
                    drawCircle(Color(0xFFF1C40F), radius = 6f, center = Offset(width * 0.4f, headCenter.y + headRadius + 110f))
                    drawCircle(Color(0xFFF1C40F), radius = 6f, center = Offset(width * 0.6f, headCenter.y + headRadius + 110f))
                    drawCircle(Color(0xFFF1C40F), radius = 6f, center = Offset(width * 0.4f, headCenter.y + headRadius + 140f))
                    drawCircle(Color(0xFFF1C40F), radius = 6f, center = Offset(width * 0.6f, headCenter.y + headRadius + 140f))
                }
            }
        }
    }
}
