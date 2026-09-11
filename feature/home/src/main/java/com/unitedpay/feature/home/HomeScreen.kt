package com.unitedpay.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.UnitedBottomBar
import com.unitedpay.core.designsystem.components.UnitedIcons
import com.unitedpay.core.designsystem.components.UnitedPayLogo
import com.unitedpay.core.designsystem.components.grainyGradientBackground
import com.unitedpay.core.designsystem.theme.UnitedCanvasLight
import com.unitedpay.core.designsystem.theme.UnitedHeaderGradient
import com.unitedpay.core.designsystem.theme.UnitedLimeAccent
import com.unitedpay.core.designsystem.theme.UnitedLimePill
import com.unitedpay.core.designsystem.theme.UnitedSagePill
import com.unitedpay.core.designsystem.theme.UnitedWhite
import com.unitedpay.feature.home.components.CardPromotionSection
import com.unitedpay.feature.home.components.MoneyControlSection
import com.unitedpay.feature.home.components.QuickActionsGrid
import com.unitedpay.feature.home.components.RegionalPromoBanner

/**
 * Main United Pay Dashboard faithfully conforming to Screenshot 2026-09-10 154655.png
 * and Screenshot 2026-09-10 154737.png:
 * - Production-grade vector SVGs and canvas illustrations (zero emojis)
 * - Complete mock-working fintech transactional flows:
 *   • Check Balance with 6-digit MPIN verification
 *   • Pay Money & PAY TO CONTACT UPI transfers
 *   • Request Money collect requests
 *   • Add Money to UPI Lite / Wallet
 *   • Mobile Recharge, Electricity (APDCL), TV Cable utility payments
 *   • RBI Digital Rupee (e₹) CBDC tokens
 * - 5-item Bottom Navigation Bar with persistent state.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToScan: () -> Unit,
    onNavigateToPayment: (String) -> Unit,
    onNavigateToPassbook: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCards: () -> Unit,
    onNavigateToAllServices: () -> Unit = {},
    onNavigateToRecharge: () -> Unit = {},
    onNavigateToElectricity: () -> Unit = {},
    onNavigateToDth: () -> Unit = {},
    onNavigateToCreditCard: () -> Unit = {},
    onNavigateToBankTransfer: () -> Unit = {},
    onNavigateToSelfTransfer: () -> Unit = {},
    onNavigateToAddMoney: () -> Unit = {},
    onNavigateToCheckBalance: () -> Unit = {},
    onNavigateToAutopay: () -> Unit = {},
    onNavigateToDigitalRupee: () -> Unit = {},
    onNavigateToRequestMoney: () -> Unit = {},
    onNavigateToOffers: () -> Unit = {},
    onNavigateToRewards: () -> Unit = {},
    onNavigateToGiftCards: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .grainyGradientBackground()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                UnitedBottomBar(
                    currentRoute = "home",
                    onNavigateHome = {},
                    onNavigateCards = onNavigateToCards,
                    onNavigateScan = onNavigateToScan,
                    onNavigateHistory = onNavigateToPassbook,
                    onNavigateProfile = onNavigateToProfile
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = innerPadding.calculateBottomPadding() + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Atmospheric Gradient Header + Greeting
                item {
                    HeaderGradientSection(
                        userName = uiState.userName.ifBlank { com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile().userName },
                        onPayToContactClick = { onNavigateToPayment("") },
                        onMenuClick = onNavigateToProfile,
                        onMessagesClick = onNavigateToMessages,
                        onNotificationsClick = onNavigateToNotifications
                    )
                }

                // Section 2: Floating iOS Frosted Glass Quick Actions Card (2x4 Grid)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        QuickActionsGrid(
                            onCryptoClick = onNavigateToDigitalRupee,
                            onRechargeClick = onNavigateToRecharge,
                            onTvCableClick = onNavigateToDth,
                            onElectricityClick = onNavigateToElectricity,
                            onOffersClick = onNavigateToOffers,
                            onGiftCardsClick = onNavigateToGiftCards,
                            onRewardsClick = onNavigateToRewards,
                            onMoreClick = onNavigateToAllServices
                        )
                    }
                }

                // Section 3: Money Control Action Matrix (12 Actions across 3 Swipeable Pages)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        MoneyControlSection(
                            // Page 1: Primary Transfers
                            onPayMoneyClick = { onNavigateToPayment("") },
                            onRequestMoneyClick = onNavigateToRequestMoney,
                            onAddMoneyClick = onNavigateToAddMoney,
                            onCheckBalanceClick = onNavigateToCheckBalance,
                            // Page 2: Banking & Key Utilities
                            onSelfTransferClick = onNavigateToSelfTransfer,
                            onBankTransferClick = onNavigateToBankTransfer,
                            onRechargeClick = onNavigateToRecharge,
                            onElectricityClick = onNavigateToElectricity,
                            // Page 3: Bills & Digital Currency
                            onTvCableClick = onNavigateToDth,
                            onCreditCardClick = onNavigateToCreditCard,
                            onDigitalRupeeClick = onNavigateToDigitalRupee,
                            onAutopayClick = onNavigateToAutopay
                        )
                    }
                }

                // Section 4: Bank Account & 3D Virtual Card Showcase
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        CardPromotionSection(
                            onAddCardClick = onNavigateToCards,
                            onCardDetailsClick = onNavigateToCards
                        )
                    }
                }

                // Section 5: North-East Regional Identity Strip
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        RegionalPromoBanner()
                    }
                }

                // Bottom Spacing
                item {
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

/**
 * Top atmospheric royal blue header with grid menu, badges, and user greeting matching reference image.
 */
@Composable
private fun HeaderGradientSection(
    userName: String,
    onPayToContactClick: () -> Unit,
    onMenuClick: () -> Unit,
    onMessagesClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 12.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top App Bar Icons (4-Square Grid on left, Chat & Notification on right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left 4-Square Grid Icon (navigates to Profile/Settings)
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "Menu / Profile",
                        tint = UnitedWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Right Chat & Notification Icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMessagesClick) {
                        Box {
                            Icon(
                                imageVector = UnitedIcons.ChatBubble,
                                contentDescription = "Messages",
                                tint = UnitedWhite,
                                modifier = Modifier.size(24.dp)
                            )
                            // Unread Dot
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = onNotificationsClick) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "Notifications",
                                tint = UnitedWhite,
                                modifier = Modifier.size(26.dp)
                            )
                            // Unread Dot
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // User Greeting Row with "PAY TO CONTACT" Pill (Reduced 8dp border radius)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(end = 10.dp)
                ) {
                    Text(
                        text = "Welcome Back!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = UnitedLimeAccent
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = userName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // "PAY TO CONTACT" Action Button with 8dp reduced radius and centered text
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(UnitedSagePill)
                        .clickable(onClick = onPayToContactClick)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PAY TO CONTACT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        maxLines = 1,
                        softWrap = false,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }
    }
}
