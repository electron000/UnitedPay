package com.unitedpay.feature.home.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.UnitedIcons
import com.unitedpay.core.designsystem.theme.UnitedLimeAccent
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedSagePill
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import kotlinx.coroutines.launch

/**
 * 2-Page Swipeable Money Control Carousel with 8 Pure UPI & Banking Actions:
 * - Manual sliding (swipe left/right) with smooth gestures
 * - 2 interactive pagination dots with animated active indicator
 * - 4 Cobalt circular action buttons per page
 *
 * Page 1: To Mobile, To Bank A/C, To Self A/C, Check Balance
 * Page 2: Request Money, UPI Lite, Credit Card, UPI Autopay
 */
@Composable
fun MoneyControlSection(
    // Page 1: Core Transfers & Balance
    onPayMoneyClick: () -> Unit,
    onBankTransferClick: () -> Unit,
    onSelfTransferClick: () -> Unit,
    onCheckBalanceClick: () -> Unit,
    // Page 2: UPI Utilities & Mandates
    onRequestMoneyClick: () -> Unit,
    onAddMoneyClick: () -> Unit,
    onCreditCardClick: () -> Unit,
    onAutopayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Money Control",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = UnitedTextPrimary,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        // 2-Page Swipeable Action Matrix (Manual Swipe / Gesture-driven)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> {
                    // Page 1: Primary Transfers & Balance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        MoneyActionButton(
                            icon = UnitedIcons.ArrowTopRight,
                            label = "To Mobile",
                            onClick = onPayMoneyClick,
                            iconTint = UnitedSagePill,
                            modifier = Modifier.weight(1f)
                        )
                        MoneyActionButton(
                            icon = UnitedIcons.BankTransfer,
                            label = "To Bank A/C",
                            onClick = onBankTransferClick,
                            iconTint = UnitedLimeAccent,
                            modifier = Modifier.weight(1f)
                        )
                        MoneyActionButton(
                            icon = UnitedIcons.SelfTransfer,
                            label = "To Self A/C",
                            onClick = onSelfTransferClick,
                            iconTint = UnitedLimeAccent,
                            modifier = Modifier.weight(1f)
                        )
                        MoneyActionButton(
                            icon = UnitedIcons.BankTemple,
                            label = "Check Balance",
                            onClick = onCheckBalanceClick,
                            iconTint = UnitedLimeAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                1 -> {
                    // Page 2: UPI Utilities & Mandates
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        MoneyActionButton(
                            icon = UnitedIcons.ArrowBottomLeft,
                            label = "Request Money",
                            onClick = onRequestMoneyClick,
                            iconTint = UnitedLimeAccent,
                            modifier = Modifier.weight(1f)
                        )
                        MoneyActionButton(
                            icon = UnitedIcons.Plus,
                            label = "UPI Lite",
                            onClick = onAddMoneyClick,
                            iconTint = UnitedLimeAccent,
                            modifier = Modifier.weight(1f)
                        )
                        MoneyActionButton(
                            icon = UnitedIcons.Cards,
                            label = "Credit Card",
                            onClick = onCreditCardClick,
                            iconTint = UnitedLimeAccent,
                            modifier = Modifier.weight(1f)
                        )
                        MoneyActionButton(
                            icon = UnitedIcons.Autopay,
                            label = "UPI Autopay",
                            onClick = onAutopayClick,
                            iconTint = UnitedLimeAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive 2-Dot Indicator (Manual swipe synchronization + Click to jump)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(2) { index ->
                val isSelected = pagerState.currentPage == index
                val dotWidth by animateDpAsState(
                    targetValue = if (isSelected) 18.dp else 6.dp,
                    label = "dotWidth"
                )

                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(dotWidth)
                        .clip(CircleShape)
                        .background(if (isSelected) UnitedMoneyBlue else Color(0xFFCBD5E1))
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                )
                if (index < 1) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}

@Composable
private fun MoneyActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = UnitedLimeAccent
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 1.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    spotColor = UnitedMoneyBlue.copy(alpha = 0.35f),
                    ambientColor = UnitedMoneyBlue.copy(alpha = 0.20f)
                )
                .clip(CircleShape)
                .background(UnitedMoneyBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(25.dp)
            )
        }
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827),
            textAlign = TextAlign.Center,
            letterSpacing = (-0.2).sp,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
