package com.unitedpay.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.R
import com.unitedpay.core.designsystem.theme.UnitedBorderLight
import com.unitedpay.core.designsystem.theme.UnitedObsidian
import com.unitedpay.core.designsystem.theme.UnitedWhite

/**
 * Official United Pay "Shield & Track" brand logo component, derived directly from Imaged/Logo.jpg.
 */
@Composable
fun BrandShield(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showWordmark: Boolean = false,
    textColor: Color = UnitedObsidian,
    asCardBadge: Boolean = false
) {
    if (asCardBadge) {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp))
                .background(UnitedWhite)
                .border(0.5.dp, UnitedBorderLight, RoundedCornerShape(8.dp))
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.brand_logo),
                contentDescription = "United Pay Official Logo",
                modifier = Modifier.size(size - 4.dp),
                contentScale = ContentScale.Fit
            )
        }
    } else if (showWordmark) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.brand_emblem),
                contentDescription = "United Pay Emblem",
                modifier = Modifier.size(size),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "United Pay",
                fontSize = (size.value * 0.42).sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    } else {
        Image(
            painter = painterResource(id = R.drawable.brand_emblem),
            contentDescription = "United Pay Emblem",
            modifier = modifier.size(size),
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * Full official United Pay logo with wordmark and optional badge backing.
 */
@Composable
fun UnitedPayLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showWordmark: Boolean = true,
    asCardBadge: Boolean = false,
    textColor: Color = UnitedObsidian
) {
    if (asCardBadge) {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp))
                .background(UnitedWhite)
                .border(0.5.dp, UnitedBorderLight, RoundedCornerShape(8.dp))
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.brand_logo),
                contentDescription = "United Pay Official Logo",
                modifier = Modifier.size(size - 8.dp),
                contentScale = ContentScale.Fit
            )
        }
    } else if (showWordmark) {
        Image(
            painter = painterResource(id = R.drawable.brand_logo_transparent),
            contentDescription = "United Pay Official Logo",
            modifier = modifier.size(size),
            contentScale = ContentScale.Fit
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.brand_emblem),
            contentDescription = "United Pay Emblem",
            modifier = modifier.size(size),
            contentScale = ContentScale.Fit
        )
    }
}

