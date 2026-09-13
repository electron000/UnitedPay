package com.unitedpay.feature.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.components.UnitedGlassCard
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.session.UserSessionManager

/**
 * Consolidated Hookolu RuPay Platinum Card & Prepaid Wallet Showcase:
 * - Authoritative single card component unifying 3D visual, live Active/Freeze toggle,
 *   centered prepaid wallet balance, and direct Add Money / Card Details actions.
 * - Eliminates redundant second card sections across the Homepage.
 */
@Composable
fun CardPromotionSection(
    onAddCardClick: () -> Unit,
    onCardDetailsClick: () -> Unit,
    onAddMoneyClick: () -> Unit = onAddCardClick,
    modifier: Modifier = Modifier
) {
    val session by UserSessionManager.currentSession.collectAsState()
    val walletBal = session?.formattedWalletBalance ?: if (UserSessionManager.isSim1Active) "₹14,250.00" else "₹0.00"
    val isFrozen = session?.isCardFrozen ?: false
    val cardHolderName = session?.userProfile?.fullName?.ifBlank { "ARUNJYOTI CHANGKAKOTY" }
        ?: if (UserSessionManager.isSim1Active) "ARUNJYOTI CHANGKAKOTY" else "NEW USER"

    UnitedGlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Card Identity + Interactive Active/Frozen Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = UnitedMoneyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Hookolu RuPay Prepaid Card",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Platinum Contactless • •••• 9024",
                            fontSize = 11.sp,
                            color = UnitedTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Freeze / Unfreeze Button
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .clickable { UserSessionManager.toggleCardFreeze() },
                    shape = RoundedCornerShape(100.dp),
                    color = if (isFrozen) Color(0xFFFEF2F2) else Color(0xFFDCFCE7),
                    border = BorderStroke(0.5.dp, if (isFrozen) Color(0xFFEF4444) else Color(0xFF10B981))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isFrozen) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = if (isFrozen) Color(0xFFEF4444) else Color(0xFF15803D),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isFrozen) "Frozen" else "Active",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFrozen) Color(0xFFEF4444) else Color(0xFF15803D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3D Perspective Virtual Card (Tap to View Details)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(onClick = onCardDetailsClick)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(18.dp), spotColor = UnitedHeaderBlueDark),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFF1F5F9),
                                    Color(0xFFE2E8F0),
                                    Color(0xFF8BA6DF),
                                    Color(0xFF2357CB)
                                )
                            )
                        )
                        .border(1.dp, UnitedBorderLight, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BrandShield(size = 30.dp, asCardBadge = true)
                            Text(
                                text = ")))",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Masked Card number
                        Text(
                            text = "••••  ••••  ••••  9024",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace,
                            color = UnitedTextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

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
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569)
                                )
                                Text(
                                    text = cardHolderName,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UnitedTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "EXPIRES",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569)
                                )
                                Text(
                                    text = "08/28",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UnitedTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Overlapping Circles
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                )
                                Box(
                                    modifier = Modifier
                                        .offset(x = (-7).dp)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF59E0B).copy(alpha = 0.85f))
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prepaid Wallet Balance (Centered)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Prepaid Wallet Balance",
                    fontSize = 11.5.sp,
                    color = UnitedTextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = walletBal,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = UnitedMoneyBlue,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Balanced 50/50 Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAddMoneyClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("+ Add Money", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = UnitedWhite)
                }

                OutlinedButton(
                    onClick = onCardDetailsClick,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, UnitedMoneyBlue),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Card Details", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = UnitedMoneyBlue)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = UnitedBorderLight)
            Spacer(modifier = Modifier.height(8.dp))

            // Sleek secondary link to add/link another bank card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onAddCardClick)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+ Link Bank Account / RuPay Card",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = UnitedMoneyBlue
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = UnitedMoneyBlue,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
