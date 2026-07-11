package com.example.swiftride.ui.fare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swiftride.data.fare.FareBreakdown
import com.example.swiftride.data.fare.RideCategory
import com.example.swiftride.data.translate

@Composable
fun RideSelectionBottomSheet(
    categories: List<RideCategory>,
    fares: Map<String, FareBreakdown>,
    selectedCategoryId: String?,
    distanceMeters: Double,
    durationSeconds: Double,
    selectedLanguage: String,
    isDarkMode: Boolean,
    onCategorySelected: (String) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Route metrics formatting
    val distanceKm = distanceMeters / 1000.0
    val distanceStr = if (distanceKm < 0.1) {
        "< 0.1 km"
    } else {
        String.format(java.util.Locale.US, "%.1f km", distanceKm)
    }

    val minutes = (durationSeconds / 60.0).toInt()
    val durationStr = when {
        minutes < 1 -> "1 min"
        minutes < 60 -> "$minutes min"
        else -> {
            val hours = minutes / 60
            val remMin = minutes % 60
            if (remMin > 0) "${hours}h ${remMin}m" else "${hours}h"
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF151515) else Color(0xFFFFFFFF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, start = 20.dp, end = 20.dp, top = 16.dp)
        ) {
            // Drag indicator (pill shape)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = if (isDarkMode) Color(0xFF333333) else Color(0xFFE0E0E0),
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Trip Summary metrics (Distance & Duration)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Choose a Ride".translate(selectedLanguage),
                    color = if (isDarkMode) Color.White else Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "$distanceStr • $durationStr",
                    color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable list of Ride Categories
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categories.forEach { category ->
                    val breakdown = fares[category.id]
                    val price = breakdown?.totalFare ?: 0.0
                    RideCategoryCard(
                        category = category,
                        price = price,
                        isSelected = category.id == selectedCategoryId,
                        selectedLanguage = selectedLanguage,
                        isDarkMode = isDarkMode,
                        onClick = { onCategorySelected(category.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show breakdown for selected category
            selectedCategoryId?.let { categoryId ->
                fares[categoryId]?.let { breakdown ->
                    FareBreakdownCard(
                        breakdown = breakdown,
                        selectedLanguage = selectedLanguage,
                        isDarkMode = isDarkMode
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Confirm & Cancel Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isDarkMode) Color.White else Color.Black
                    )
                ) {
                    Text(
                        text = "Cancel".translate(selectedLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = onConfirm,
                    enabled = selectedCategoryId != null,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3A86FF),
                        contentColor = if (isDarkMode) Color.Black else Color.White,
                        disabledContainerColor = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFE5E5EA),
                        disabledContentColor = if (isDarkMode) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.3f)
                    )
                ) {
                    val confirmText = if (selectedCategoryId != null) {
                        val categoryName = categories.find { it.id == selectedCategoryId }?.name ?: ""
                        "Confirm $categoryName"
                    } else {
                        "Confirm Ride"
                    }
                    val translatedConfirm = confirmText.translate(selectedLanguage)
                    Text(
                        text = translatedConfirm,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FareUpdatingCard(
    selectedLanguage: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF151515) else Color(0xFFFFFFFF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = if (isDarkMode) Color(0xFF333333) else Color(0xFFE0E0E0),
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.height(24.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color(0xFF3A86FF),
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Updating Fare...".translate(selectedLanguage),
                color = if (isDarkMode) Color.White else Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun FareErrorCard(
    message: String,
    selectedLanguage: String,
    isDarkMode: Boolean,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF151515) else Color(0xFFFFFFFF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = if (isDarkMode) Color(0xFF333333) else Color(0xFFE0E0E0),
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error".translate(selectedLanguage),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Fare Estimation Failed".translate(selectedLanguage),
                color = if (isDarkMode) Color.White else Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message.translate(selectedLanguage),
                color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isDarkMode) Color.White else Color.Black
                    )
                ) {
                    Text(
                        text = "Cancel".translate(selectedLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Retry".translate(selectedLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
