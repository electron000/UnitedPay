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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.UnitedIcons
import com.unitedpay.core.designsystem.components.UnitedGlassCard
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.session.UserSessionManager

/**
 * Modern Micro-Badge Types for high-aesthetic fintech visual hierarchy
 */
enum class BadgeStyle {
    RED_ROSE_GRADIENT,
    BLUE_TINT,
    EMERALD_TINT,
    AMBER_TINT,
    PURPLE_TINT
}

@Composable
fun FintechMicroBadge(
    text: String,
    style: BadgeStyle = BadgeStyle.RED_ROSE_GRADIENT,
    modifier: Modifier = Modifier
) {
    val backgroundModifier = when (style) {
        BadgeStyle.RED_ROSE_GRADIENT -> Modifier.background(
            Brush.horizontalGradient(listOf(Color(0xFFFF334B), Color(0xFFFF5E62)))
        )
        BadgeStyle.BLUE_TINT -> Modifier
            .background(Color(0xFFEFF6FF))
            .border(0.5.dp, UnitedMoneyBlue.copy(alpha = 0.35f), RoundedCornerShape(100.dp))
        BadgeStyle.EMERALD_TINT -> Modifier
            .background(Color(0xFFDCFCE7))
            .border(0.5.dp, Color(0xFF10B981).copy(alpha = 0.35f), RoundedCornerShape(100.dp))
        BadgeStyle.AMBER_TINT -> Modifier
            .background(Color(0xFFFEF3C7))
            .border(0.5.dp, Color(0xFFF59E0B).copy(alpha = 0.35f), RoundedCornerShape(100.dp))
        BadgeStyle.PURPLE_TINT -> Modifier
            .background(Color(0xFFF3E8FF))
            .border(0.5.dp, Color(0xFF9333EA).copy(alpha = 0.35f), RoundedCornerShape(100.dp))
    }

    val textColor = when (style) {
        BadgeStyle.RED_ROSE_GRADIENT -> UnitedWhite
        BadgeStyle.BLUE_TINT -> UnitedMoneyBlue
        BadgeStyle.EMERALD_TINT -> Color(0xFF15803D)
        BadgeStyle.AMBER_TINT -> Color(0xFFB45309)
        BadgeStyle.PURPLE_TINT -> Color(0xFF7E22CE)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .then(backgroundModifier)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 0.2.sp,
            maxLines = 1,
            softWrap = false
        )
    }
}

/**
 * Responsive Action Tile with 2-line auto-wrapped labels and floating micro-badges
 */
@Composable
private fun HomeActionTile(
    title: String,
    icon: ImageVector,
    badge: String?,
    badgeStyle: BadgeStyle = BadgeStyle.RED_ROSE_GRADIENT,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.padding(top = 4.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
                    .border(0.5.dp, Color(0xFFE2E8F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = UnitedMoneyBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            if (!badge.isNullOrBlank()) {
                FintechMicroBadge(
                    text = badge,
                    style = badgeStyle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 10.dp, y = (-6).dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = UnitedTextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            minLines = 2,
            lineHeight = 13.5.sp
        )
    }
}

/**
 * Section 6: Hookolu Core BC Banking & Financial Inclusion Hub
 */
@Composable
fun AepsInclusionSection(
    onNavigateToAeps: (String) -> Unit,
    onNavigateToMicroAtm: () -> Unit,
    onNavigateToDmt: () -> Unit
) {
    UnitedGlassCard(
        cornerRadius = 20.dp,
        elevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Banking & Financial Inclusion",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Aadhaar Enabled Banking & Micro-ATM",
                        fontSize = 11.sp,
                        color = UnitedTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                FintechMicroBadge(
                    text = "NPCI AEPS",
                    style = BadgeStyle.BLUE_TINT
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HomeActionTile(
                    title = "AEPS Cash\nWithdrawal",
                    icon = UnitedIcons.AepsFingerprint,
                    badge = "Max ₹10k",
                    badgeStyle = BadgeStyle.RED_ROSE_GRADIENT,
                    onClick = { onNavigateToAeps("CASH_WITHDRAWAL") },
                    modifier = Modifier.weight(1f)
                )
                HomeActionTile(
                    title = "Aadhaar\nBalance",
                    icon = Icons.Default.AccountBalance,
                    badge = null,
                    onClick = { onNavigateToAeps("BALANCE_ENQUIRY") },
                    modifier = Modifier.weight(1f)
                )
                HomeActionTile(
                    title = "Mini\nStatement",
                    icon = UnitedIcons.History,
                    badge = "Mini 5",
                    badgeStyle = BadgeStyle.BLUE_TINT,
                    onClick = { onNavigateToAeps("MINI_STATEMENT") },
                    modifier = Modifier.weight(1f)
                )
                HomeActionTile(
                    title = "Micro-ATM\nmPOS",
                    icon = UnitedIcons.MicroAtm,
                    badge = "mPOS",
                    badgeStyle = BadgeStyle.AMBER_TINT,
                    onClick = onNavigateToMicroAtm,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Section 7: HookoluPay RuPay Prepaid Card & Wallet Banner
 */
@Composable
fun PrepaidWalletSection(
    onAddMoneyClick: () -> Unit,
    onCardDetailsClick: () -> Unit
) {
    val session by UserSessionManager.currentSession.collectAsState()
    val walletBal = session?.formattedWalletBalance ?: if (UserSessionManager.isSim1Active) "₹14,250.00" else "₹0.00"
    val isFrozen = session?.isCardFrozen ?: false

    UnitedGlassCard(
        cornerRadius = 20.dp,
        elevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Card Details + Active/Frozen Toggle
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
                            .size(40.dp)
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
                            color = UnitedTextPrimary
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Platinum Contactless • •••• 9024",
                            fontSize = 11.sp,
                            color = UnitedTextSecondary
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
            HorizontalDivider(color = UnitedBorderLight)
            Spacer(modifier = Modifier.height(14.dp))

            // Wallet Balance Info (Center Aligned)
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
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = walletBal,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = UnitedMoneyBlue,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Responsive Action Buttons (50-50 width distribution)
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
        }
    }
}

/**
 * Section 8: Recharge & BBPS Bill Payments Grid (16+ Services)
 */
@Composable
fun BbpsRechargeGridSection(
    onRechargeClick: () -> Unit,
    onElectricityClick: () -> Unit,
    onDthClick: () -> Unit,
    onGasClick: () -> Unit,
    onWaterClick: () -> Unit,
    onFastagClick: () -> Unit,
    onEducationClick: () -> Unit,
    onSubscriptionsClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    UnitedGlassCard(
        cornerRadius = 20.dp,
        elevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Recharge & Bill Payments",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Bharat BillPay • Instant Confirmation",
                        fontSize = 11.sp,
                        color = UnitedTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                FintechMicroBadge(
                    text = "BBPS Assured",
                    style = BadgeStyle.EMERALD_TINT
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HomeActionTile("Mobile\nRecharge", UnitedIcons.Recharge, null, onClick = onRechargeClick, modifier = Modifier.weight(1f))
                HomeActionTile("Electricity\nBill", UnitedIcons.Electricity, "APDCL", badgeStyle = BadgeStyle.BLUE_TINT, onClick = onElectricityClick, modifier = Modifier.weight(1f))
                HomeActionTile("DTH\nCable", UnitedIcons.TvCable, null, onClick = onDthClick, modifier = Modifier.weight(1f))
                HomeActionTile("Book LPG\nCylinder", UnitedIcons.GasCylinder, "Indane", badgeStyle = BadgeStyle.AMBER_TINT, onClick = onGasClick, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HomeActionTile("FASTag\nRecharge", UnitedIcons.Fastag, null, onClick = onFastagClick, modifier = Modifier.weight(1f))
                HomeActionTile("Water\nBill", UnitedIcons.Water, null, onClick = onWaterClick, modifier = Modifier.weight(1f))
                HomeActionTile("Education\nFees", UnitedIcons.EducationFees, null, onClick = onEducationClick, modifier = Modifier.weight(1f))
                HomeActionTile("OTT &\nMedia", UnitedIcons.SubscriptionsOtt, "Hotstar", badgeStyle = BadgeStyle.PURPLE_TINT, onClick = onSubscriptionsClick, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = UnitedBorderLight)
            Spacer(modifier = Modifier.height(10.dp))

            // Modern view all categories link
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onMoreClick),
                color = Color(0xFFF8FAFC)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View All 16+ Utility Billers & Categories",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedMoneyBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = UnitedMoneyBlue,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

/**
 * Section 9: Financial Services & Embedded Lending
 */
@Composable
fun FinancialLendingSection(
    onApplyCreditClick: () -> Unit,
    onOpenBankAccountClick: () -> Unit,
    onDigitalGoldClick: () -> Unit,
    onMutualFundsClick: () -> Unit,
    onInsuranceClick: () -> Unit
) {
    UnitedGlassCard(
        cornerRadius = 20.dp,
        elevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Vyapar Credit Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onApplyCreditClick),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F172A)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = UnitedIcons.LendingHand,
                                contentDescription = null,
                                tint = UnitedLimeAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HOOKOLU VYAPAR CREDIT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = UnitedLimeAccent,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Get up to ₹2,00,000 instant credit line",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Instant disbursal • 0% collateral",
                            fontSize = 10.5.sp,
                            color = UnitedWhite.copy(alpha = 0.75f)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = UnitedLimeAccent
                    ) {
                        Text(
                            text = "Apply Now",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UnitedMidnightNavy,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Services Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HomeActionTile(
                    title = "Open Bank\nA/c",
                    icon = UnitedIcons.BankOpening,
                    badge = "AU SFB",
                    badgeStyle = BadgeStyle.BLUE_TINT,
                    onClick = onOpenBankAccountClick,
                    modifier = Modifier.weight(1f)
                )
                HomeActionTile(
                    title = "Digital\nGold",
                    icon = UnitedIcons.DigitalGold,
                    badge = "24K",
                    badgeStyle = BadgeStyle.AMBER_TINT,
                    onClick = onDigitalGoldClick,
                    modifier = Modifier.weight(1f)
                )
                HomeActionTile(
                    title = "Mutual\nFunds",
                    icon = UnitedIcons.MutualFunds,
                    badge = null,
                    onClick = onMutualFundsClick,
                    modifier = Modifier.weight(1f)
                )
                HomeActionTile(
                    title = "Insurance\nLIC",
                    icon = UnitedIcons.Insurance,
                    badge = null,
                    onClick = onInsuranceClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Section 10: Travel & Transit Suite
 */
@Composable
fun TravelTransitSection(
    onBusClick: () -> Unit,
    onFlightClick: () -> Unit,
    onTrainClick: () -> Unit,
    onHotelClick: () -> Unit
) {
    UnitedGlassCard(
        cornerRadius = 20.dp,
        elevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f, fill = true)) {
                    Text(
                        text = "Travel & Transit Hub",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Northeast Express Bus, IRCTC Trains, Flights & Hotels",
                        fontSize = 11.sp,
                        color = UnitedTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                FintechMicroBadge(
                    text = "Zero Fee",
                    style = BadgeStyle.BLUE_TINT
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HomeActionTile(
                    title = "Bus\nBooking",
                    icon = UnitedIcons.BusTicket,
                    badge = "ASTC",
                    badgeStyle = BadgeStyle.EMERALD_TINT,
                    onClick = onBusClick,
                    modifier = Modifier.weight(1f)
                )
                HomeActionTile(
                    title = "Flight\nTickets",
                    icon = UnitedIcons.FlightTicket,
                    badge = "GAU",
                    badgeStyle = BadgeStyle.BLUE_TINT,
                    onClick = onFlightClick,
                    modifier = Modifier.weight(1f)
                )
                HomeActionTile(
                    title = "Train\nIRCTC",
                    icon = UnitedIcons.TrainTicket,
                    badge = "IRCTC",
                    badgeStyle = BadgeStyle.AMBER_TINT,
                    onClick = onTrainClick,
                    modifier = Modifier.weight(1f)
                )
                HomeActionTile(
                    title = "Hotel\nStay",
                    icon = UnitedIcons.HotelBooking,
                    badge = null,
                    onClick = onHotelClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Section 11: Retailer & BC Merchant Operations Hub
 */
@Composable
fun RetailerMerchantSection(
    onDashboardClick: () -> Unit
) {
    val session by UserSessionManager.currentSession.collectAsState()
    val metrics = session?.retailerMetrics

    UnitedGlassCard(
        cornerRadius = 20.dp,
        elevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Store identity + Verified Pill
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
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = UnitedMoneyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Merchant & BC Agent Operations",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Arunjyoti Enterprise • HKL-RET-AS089",
                            fontSize = 11.sp,
                            color = UnitedTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                FintechMicroBadge(
                    text = "BC Active",
                    style = BadgeStyle.EMERALD_TINT
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two distinct metric mini-cards side-by-side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Today's Volume
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Today's Volume", fontSize = 11.sp, color = UnitedTextSecondary)
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = metrics?.formattedVolume ?: if (UserSessionManager.isSim1Active) "₹42,850.00" else "₹0.00",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UnitedTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${metrics?.todayTxnCount ?: if (UserSessionManager.isSim1Active) 18 else 0} Transactions",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF15803D)
                        )
                    }
                }

                // Earned Commission
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF0FDF4),
                    border = BorderStroke(0.5.dp, Color(0xFFBBF7D0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Earned Commission", fontSize = 11.sp, color = Color(0xFF166534))
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = metrics?.formattedCommission ?: if (UserSessionManager.isSim1Active) "₹340.50" else "₹0.00",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF15803D)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Instant Payout Ready",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = UnitedMoneyBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dedicated Open Dashboard Button
            Button(
                onClick = onDashboardClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(
                    imageVector = UnitedIcons.CommissionChart,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = UnitedWhite
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Open BC Dashboard & Settlement",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp
                )
            }
        }
    }
}

/**
 * Section 12: Trust, Grievance & Regulatory Compliance Footer
 */
@Composable
fun TrustRegulatoryFooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "NPCI Certified",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedTextSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = UnitedMoneyBlue,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "RBI 256-Bit SSL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedTextSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "24x7 Grievance",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "HookoluPay / United Pay • Empowering Northeast Bharat with Digital Inclusion",
            fontSize = 10.sp,
            color = UnitedTextSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}
