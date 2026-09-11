package com.unitedpay.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.theme.UnitedAccentGold
import com.unitedpay.core.designsystem.theme.UnitedPosterGradient
import com.unitedpay.core.designsystem.theme.UnitedShieldCyan
import com.unitedpay.core.designsystem.theme.UnitedWhite

/**
 * Celebratory North-East Regional Identity Banner matching the poster in Imaged/images.jpg.
 */
@Composable
fun RegionalPromoBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(UnitedPosterGradient)
                .border(1.dp, Color(0xFF1B3575), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "NORTH-EAST INDIA'S FIRST",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedShieldCyan,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "UPI Payments App",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UnitedWhite
                        )
                    }
                    BrandShield(size = 38.dp, asCardBadge = true)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tagline pills: Simple | Secure | Instant with modern reduced radius
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF07183D).copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF28488D), RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FeaturePill(icon = Icons.Default.Shield, text = "SIMPLE")
                    Text(text = "|", color = Color(0xFF28488D), fontSize = 12.sp)
                    FeaturePill(icon = Icons.Default.Lock, text = "SECURE")
                    Text(text = "|", color = Color(0xFF28488D), fontSize = 12.sp)
                    FeaturePill(icon = Icons.Default.ElectricBolt, text = "INSTANT", tint = UnitedAccentGold)
                }
            }
        }
    }
}

@Composable
private fun FeaturePill(
    icon: ImageVector,
    text: String,
    tint: Color = UnitedShieldCyan
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = UnitedWhite
        )
    }
}
