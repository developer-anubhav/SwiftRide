package com.example.swiftride.ui.fare

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swiftride.data.fare.RideCategory
import com.example.swiftride.data.translate

@Composable
fun RideCategoryCard(
    category: RideCategory,
    price: Double,
    isSelected: Boolean,
    selectedLanguage: String,
    isDarkMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFEFEFEF)
    } else {
        if (isDarkMode) Color(0xFF151515) else Color(0xFFFFFFFF)
    }

    val borderColor = if (isSelected) {
        Color(0xFF3A86FF)
    } else {
        if (isDarkMode) Color(0xFF333333) else Color(0xFFE0E0E0)
    }

    val icon = when (category.id) {
        "bike" -> Icons.Default.Moped
        "auto" -> Icons.Default.ElectricRickshaw
        else -> Icons.Default.DirectionsCar
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                imageVector = icon,
                contentDescription = category.name.translate(selectedLanguage),
                tint = if (isSelected) {
                    Color(0xFF3A86FF)
                } else {
                    if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
                },
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category.name.translate(selectedLanguage),
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${category.maxPassengers}",
                            color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = category.description.translate(selectedLanguage),
                    color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif
                )
                
                Text(
                    text = "Arrives in ${category.simulatedEtaMinutes} min".translate(selectedLanguage),
                    color = Color(0xFF3A86FF).copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.SansSerif
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Price
            Text(
                text = String.format(java.util.Locale.US, "₹%.0f", price),
                color = if (isDarkMode) Color.White else Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}
