package com.unitedpay.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.components.UnitedBottomBar
import com.unitedpay.core.designsystem.components.UnitedGlassCard
import com.unitedpay.core.designsystem.components.grainyGradientBackground
import com.unitedpay.core.designsystem.theme.UnitedBorderLight
import com.unitedpay.core.designsystem.theme.UnitedCanvasLight
import com.unitedpay.core.designsystem.theme.UnitedHeaderBlueDark
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedRoyalBlue
import com.unitedpay.core.designsystem.theme.UnitedSuccess
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.mock.UnitedMockData

/**
 * Dedicated Cards Screen corresponding to the "Cards" tab in the bottom navigation bar.
 * Directly implements "YOUR CARD YOUR CONTROL" with card controls, contactless toggle,
 * linked bank accounts, and persistent bottom navigation bar.
 */
@Composable
fun CardsScreen(
    onNavigateHome: () -> Unit,
    onNavigateScan: () -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateProfile: () -> Unit,
    onAddNewCard: () -> Unit = {},
    onCardLimitsClick: () -> Unit = {},
    onResetPinClick: () -> Unit = {}
) {
    var cardDetails by remember { mutableStateOf(com.unitedpay.core.model.session.UserSessionManager.getCurrentCardDetails()) }
    var linkedAccounts by remember { mutableStateOf(com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts()) }
    var userProfile by remember { mutableStateOf(com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile()) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.banking.getCardDetails().collect {
            if (it is Resource.Success) cardDetails = it.data
        }
    }
    LaunchedEffect(Unit) {
        UnitedPayApi.client.banking.getLinkedBankAccounts().collect {
            if (it is Resource.Success) linkedAccounts = it.data
        }
    }
    LaunchedEffect(Unit) {
        UnitedPayApi.client.banking.getUserProfile().collect {
            if (it is Resource.Success) userProfile = it.data
        }
    }

    var isCardFrozen by remember { mutableStateOf(false) }
    var isOnlineEnabled by remember { mutableStateOf(cardDetails.isOnlineActive) }
    var isContactlessEnabled by remember { mutableStateOf(cardDetails.isContactlessActive) }
    var isCvvVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = UnitedCanvasLight,
        bottomBar = {
            UnitedBottomBar(
                currentRoute = "cards",
                onNavigateHome = onNavigateHome,
                onNavigateCards = {},
                onNavigateScan = onNavigateScan,
                onNavigateHistory = onNavigateHistory,
                onNavigateProfile = onNavigateProfile
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "YOUR CARD YOUR CONTROL",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UnitedTextPrimary
                        )
                        Text(
                            text = "Manage your RuPay debit & credit cards",
                            fontSize = 12.sp,
                            color = UnitedTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEBF2FF))
                            .clickable(onClick = onAddNewCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Card",
                            tint = UnitedMoneyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 3D Virtual Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(22.dp), spotColor = UnitedHeaderBlueDark),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = if (isCardFrozen) {
                                        listOf(Color(0xFF94A3B8), UnitedTextSecondary, Color(0xFF475569))
                                    } else {
                                        listOf(
                                            Color(0xFFFFFFFF),
                                            Color(0xFFF0F4FF),
                                            Color(0xFF93B4F8),
                                            Color(0xFF1E4AB2)
                                        )
                                    }
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isCardFrozen) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFDC2626), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "FROZEN",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = UnitedWhite
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text(
                                        text = ")))",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = cardDetails.cardNumberFormatted,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.8.sp,
                                fontFamily = FontFamily.Monospace,
                                color = UnitedTextPrimary,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Credentials Row: VALID THRU & CVV (Cleanly Spaced)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "VALID THRU",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF334155)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cardDetails.validThru,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.width(28.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "CVV",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF334155)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isCvvVisible) cardDetails.cvv else "•••",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Bottom Row: CARD HOLDER & Mastercard Network Badge
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
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF334155)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = userProfile.userName.uppercase(),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Overlapping Circles (Mastercard Logo)
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

            // Quick Card Actions (4 Action Pills)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CardActionButton(
                        icon = if (isCardFrozen) Icons.Default.Lock else Icons.Default.Security,
                        label = if (isCardFrozen) "Unfreeze" else "Freeze Card",
                        onClick = { isCardFrozen = !isCardFrozen },
                        modifier = Modifier.weight(1f)
                    )
                    CardActionButton(
                        icon = Icons.Default.Visibility,
                        label = if (isCvvVisible) "Hide CVV" else "Show CVV",
                        onClick = { isCvvVisible = !isCvvVisible },
                        modifier = Modifier.weight(1f)
                    )
                    CardActionButton(
                        icon = Icons.Default.Tune,
                        label = "Card Limits",
                        onClick = onCardLimitsClick,
                        modifier = Modifier.weight(1f)
                    )
                    CardActionButton(
                        icon = Icons.Default.CreditCard,
                        label = "Reset PIN",
                        onClick = onResetPinClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Security Controls Card
            item {
                UnitedGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    elevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Card Security & Preferences",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Online Payments Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Online E-Commerce Transactions",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = UnitedTextPrimary
                                )
                                Text(
                                    text = "Domestic shopping and UPI payment apps",
                                    fontSize = 11.sp,
                                    color = UnitedTextSecondary
                                )
                            }
                            Switch(
                                checked = isOnlineEnabled,
                                onCheckedChange = { isOnlineEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = UnitedWhite,
                                    checkedTrackColor = UnitedMoneyBlue,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFD1D5DB),
                                    uncheckedBorderColor = Color(0xFF9CA3AF).copy(alpha = 0.5f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Contactless Tap & Pay Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Contactless Tap & Pay",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = UnitedTextPrimary
                                )
                                Text(
                                    text = "No PIN required for payments up to ₹5,000",
                                    fontSize = 11.sp,
                                    color = UnitedTextSecondary
                                )
                            }
                            Switch(
                                checked = isContactlessEnabled,
                                onCheckedChange = { isContactlessEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = UnitedWhite,
                                    checkedTrackColor = UnitedMoneyBlue,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFD1D5DB),
                                    uncheckedBorderColor = Color(0xFF9CA3AF).copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }
            }

            // Linked Bank Accounts Section
            item {
                Text(
                    text = "LINKED BANK ACCOUNTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedTextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                linkedAccounts.forEachIndexed { index, account ->
                    LinkedBankCard(
                        bankName = account.bankName,
                        accountMask = account.accountNumberMasked,
                        isPrimary = account.isPrimary,
                        balance = "₹ ${String.format("%.2f", account.balance ?: 0.0)}"
                    )
                    if (index < linkedAccounts.size - 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun CardActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFFEBF2FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = UnitedMoneyBlue,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium,
            color = UnitedTextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LinkedBankCard(
    bankName: String,
    accountMask: String,
    isPrimary: Boolean,
    balance: String
) {
    UnitedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 14.dp,
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEBF2FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = UnitedMoneyBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = bankName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary
                        )
                        if (isPrimary) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFDCFCE7), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "PRIMARY",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UnitedSuccess
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "A/C $accountMask",
                        fontSize = 11.sp,
                        color = UnitedTextSecondary
                    )
                }
            }

            Text(
                text = balance,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = UnitedMoneyBlue
            )
        }
    }
}
