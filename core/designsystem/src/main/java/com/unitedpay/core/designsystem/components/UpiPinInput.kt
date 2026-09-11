package com.unitedpay.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.unitedpay.core.designsystem.theme.UnitedBorderLight
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedWhite

/**
 * Secure dot indicator for NPCI UPI MPIN inputs.
 */
@Composable
fun UpiPinDotsIndicator(
    pinLength: Int = 6,
    enteredCount: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp = 16.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (i in 0 until pinLength) {
            val isFilled = i < enteredCount
            val dotColor by animateColorAsState(
                targetValue = if (isFilled) UnitedMoneyBlue else UnitedWhite,
                animationSpec = tween(150),
                label = "PinDotColor"
            )

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(dotColor)
                    .border(
                        width = 1.5.dp,
                        color = if (isFilled) UnitedMoneyBlue else UnitedBorderLight,
                        shape = CircleShape
                    )
            )
        }
    }
}
