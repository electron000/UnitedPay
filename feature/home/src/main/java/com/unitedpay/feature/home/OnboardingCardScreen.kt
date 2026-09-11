package com.unitedpay.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.theme.UnitedCanvasLight
import com.unitedpay.core.designsystem.theme.UnitedHeaderBlueDark
import com.unitedpay.core.designsystem.theme.UnitedLimeAccent
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite

/**
 * Onboarding & Card Showcase Screen matching Screen 1 (Left) of Screenshot 2026-09-10 154737.png:
 * - Brand logo header "UP UNITED PAY"
 * - Headline: "YOUR CARD\nYOUR CONTROL"
 * - Subtitle: "Anytime, Anywhere"
 * - Floating 3D perspective card with contactless symbol, chip, cardholder name, and RuPay emblem
 * - Bottom "GET STARTED" pill action button
 */
@Composable
fun OnboardingCardScreen(
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = UnitedCanvasLight
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Brand Header
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrandShield(size = 32.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "UNITED PAY",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp,
                        color = UnitedMoneyBlue
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Hero Headline
                Text(
                    text = "YOUR CARD\nYOUR CONTROL",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 38.sp,
                    color = UnitedTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Anytime, Anywhere",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = UnitedTextSecondary
                )
            }

            // 3D Perspective Floating Card in Center
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background subtle ambient glow
                Box(
                    modifier = Modifier
                        .size(280.dp, 160.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF93C5FD).copy(alpha = 0.4f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                // 3D Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .rotate(-8f)
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(22.dp),
                            spotColor = UnitedHeaderBlueDark
                        ),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFFFFFF),
                                        Color(0xFFF0F4FF),
                                        Color(0xFF93B4F8),
                                        Color(0xFF1E4AB2)
                                    )
                                )
                            )
                            .border(1.dp, Color(0xFFFFFFFF).copy(alpha = 0.8f), RoundedCornerShape(22.dp))
                            .padding(22.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BrandShield(size = 36.dp, asCardBadge = true)
                                Text(
                                    text = ")))",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            Spacer(modifier = Modifier.height(36.dp))

                            Text(
                                text = "1234  5678  9000  0000",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF0F172A)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = "CARD HOLDER",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF334155)
                                    )
                                    Text(
                                        text = "ARUNJYOTI CHANGKAKOTY",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "VALID THRU",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF334155)
                                    )
                                    Text(
                                        text = "08/26",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                }

                                // Overlapping Circles
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEF4444))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .offset(x = (-10).dp)
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF59E0B).copy(alpha = 0.85f))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom "GET STARTED" Button
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .defaultMinSize(minHeight = 52.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp), spotColor = UnitedMoneyBlue),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue)
            ) {
                Text(
                    text = "GET STARTED",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = UnitedWhite
                )
            }
        }
    }
}
