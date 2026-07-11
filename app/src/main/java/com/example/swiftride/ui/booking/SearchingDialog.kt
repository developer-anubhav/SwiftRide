package com.example.swiftride.ui.booking

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swiftride.data.translate

@Composable
fun SearchingDialog(
    selectedLanguage: String,
    isDarkMode: Boolean,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for searching state
    val infiniteTransition = rememberInfiniteTransition(label = "SearchingPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF151515) else Color(0xFFFFFFFF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp, top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pill drag indicator
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = if (isDarkMode) Color(0xFF333333) else Color(0xFFE0E0E0),
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Pulse searching loader
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFF3A86FF).copy(alpha = 0.15f * pulseScale)
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(56.dp),
                    color = Color(0xFF3A86FF),
                    strokeWidth = 4.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Searching for nearby drivers...".translate(selectedLanguage),
                color = if (isDarkMode) Color.White else Color.Black,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Connecting you to the nearest vehicle".translate(selectedLanguage),
                color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                fontSize = 13.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color(0xFF3A86FF),
                trackColor = if (isDarkMode) Color(0xFF222222) else Color(0xFFE5E5EA)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkMode) Color(0xFF222222) else Color(0xFFF2F2F7),
                    contentColor = if (isDarkMode) Color.White else Color.Black
                )
            ) {
                Text(
                    text = "Cancel Request".translate(selectedLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
