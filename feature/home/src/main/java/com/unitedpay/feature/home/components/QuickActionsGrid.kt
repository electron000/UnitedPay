package com.unitedpay.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.UnitedIcons
import com.unitedpay.core.designsystem.theme.UnitedBorderLight
import com.unitedpay.core.designsystem.theme.UnitedHeaderBlueDark
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedWhite

import com.unitedpay.core.designsystem.components.UnitedGlassCard

/**
 * Quick Actions Floating Card matching the reference image exactly:
 * - Authentic iOS-style Frosted Glass Card with 28dp corner radius
 * - Specular hairline highlight border and ambient blue-tinted drop shadow
 * - 2 rows x 4 columns with minimalist royal blue line vectors:
 *   Row 1: Crypto, Recharge, TV Cable, Electricity
 *   Row 2: Offers, Gift Cards, Rewards, More
 * - Zero emojis - 100% Vector geometry.
 */
@Composable
fun QuickActionsGrid(
    onCryptoClick: () -> Unit = {},
    onRechargeClick: () -> Unit = {},
    onTvCableClick: () -> Unit = {},
    onElectricityClick: () -> Unit = {},
    onOffersClick: () -> Unit = {},
    onGiftCardsClick: () -> Unit = {},
    onRewardsClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    UnitedGlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Quick Actions",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(
                    icon = UnitedIcons.Crypto,
                    label = "Crypto",
                    onClick = onCryptoClick,
                    modifier = Modifier.weight(1f)
                )
                QuickActionItem(
                    icon = UnitedIcons.Recharge,
                    label = "Recharge",
                    onClick = onRechargeClick,
                    modifier = Modifier.weight(1f)
                )
                QuickActionItem(
                    icon = UnitedIcons.TvCable,
                    label = "TV Cable",
                    onClick = onTvCableClick,
                    modifier = Modifier.weight(1f)
                )
                QuickActionItem(
                    icon = UnitedIcons.Electricity,
                    label = "Electricity",
                    onClick = onElectricityClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(
                    icon = UnitedIcons.Offers,
                    label = "Offers",
                    onClick = onOffersClick,
                    modifier = Modifier.weight(1f)
                )
                QuickActionItem(
                    icon = UnitedIcons.GiftCards,
                    label = "Gift Cards",
                    onClick = onGiftCardsClick,
                    modifier = Modifier.weight(1f)
                )
                QuickActionItem(
                    icon = UnitedIcons.Rewards,
                    label = "Rewards",
                    onClick = onRewardsClick,
                    modifier = Modifier.weight(1f)
                )
                QuickActionItem(
                    icon = UnitedIcons.More,
                    label = "More",
                    onClick = onMoreClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = UnitedMoneyBlue,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1F2937),
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}
