package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JGamesStudioLogo(
    modifier: Modifier = Modifier,
    useLightBg: Boolean = false
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (useLightBg) Color(0xFFF1F5F9) else Color(0xFF0F172A) // Slate 100 or Slate 900
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    colors = listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6)) // Blue to Purple gradient border
                ),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SportsEsports,
            contentDescription = "jgames.studio",
            tint = Color(0xFF3B82F6), // Game Studio Blue
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "jgames",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (useLightBg) Color(0xFF0F172A) else Color.White,
            letterSpacing = 0.5.sp
        )
        Text(
            text = ".studio",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B5CF6), // Purple accent
            letterSpacing = 0.5.sp
        )
    }
}
