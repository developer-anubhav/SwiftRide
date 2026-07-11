package com.example.swiftride.ui.fare

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swiftride.data.fare.FareBreakdown
import com.example.swiftride.data.translate

@Composable
fun FareBreakdownCard(
    breakdown: FareBreakdown,
    selectedLanguage: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkMode) Color(0xFF1C1C1E) else Color(0xFFF9F9F9),
        border = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Total Fare & Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Estimated Total".translate(selectedLanguage),
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "Tap to view breakdown".translate(selectedLanguage),
                        color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format(java.util.Locale.US, "₹%.2f", breakdown.totalFare),
                        color = Color(0xFF3A86FF),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle breakdown".translate(selectedLanguage),
                        tint = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expanded Breakdown list
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(
                        color = if (isDarkMode) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
                    )

                    BreakdownRow(
                        label = "Base Fare".translate(selectedLanguage),
                        amount = breakdown.baseFare,
                        isDarkMode = isDarkMode
                    )
                    BreakdownRow(
                        label = "Distance Charge".translate(selectedLanguage),
                        amount = breakdown.distanceCharge,
                        isDarkMode = isDarkMode
                    )
                    BreakdownRow(
                        label = "Time Charge".translate(selectedLanguage),
                        amount = breakdown.timeCharge,
                        isDarkMode = isDarkMode
                    )

                    // Placeholders for future rules
                    if (breakdown.surgeCharge > 0.0) {
                        BreakdownRow(
                            label = "Surge Pricing".translate(selectedLanguage),
                            amount = breakdown.surgeCharge,
                            isDarkMode = isDarkMode,
                            color = Color(0xFFFF5252)
                        )
                    }
                    if (breakdown.tollFees > 0.0) {
                        BreakdownRow(
                            label = "Toll Fees".translate(selectedLanguage),
                            amount = breakdown.tollFees,
                            isDarkMode = isDarkMode
                        )
                    }
                    if (breakdown.couponDiscount > 0.0) {
                        BreakdownRow(
                            label = "Coupon Discount".translate(selectedLanguage),
                            amount = -breakdown.couponDiscount,
                            isDarkMode = isDarkMode,
                            color = Color(0xFF4CAF50)
                        )
                    }
                    if (breakdown.platformFee > 0.0) {
                        BreakdownRow(
                            label = "Platform Fee".translate(selectedLanguage),
                            amount = breakdown.platformFee,
                            isDarkMode = isDarkMode
                        )
                    }
                    if (breakdown.taxes > 0.0) {
                        BreakdownRow(
                            label = "Taxes & Levies".translate(selectedLanguage),
                            amount = breakdown.taxes,
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BreakdownRow(
    label: String,
    amount: Double,
    isDarkMode: Boolean,
    color: Color? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
            fontSize = 13.sp,
            fontFamily = FontFamily.SansSerif
        )
        Text(
            text = String.format(java.util.Locale.US, if (amount < 0.0) "-₹%.2f" else "₹%.2f", Math.abs(amount)),
            color = color ?: (if (isDarkMode) Color.White else Color.Black),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif
        )
    }
}
