package com.unitedpay.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.unitedpay.core.designsystem.theme.UnitedGlassBorderBrush
import com.unitedpay.core.designsystem.theme.UnitedGlassSurfaceBrush
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue

/**
 * Standardized iOS-style Frosted Glass Card:
 * - Semi-translucent frosted acrylic surface (88% -> 72% white opacity)
 * - Specular hairline highlight border (95% -> 40% white opacity)
 * - Ambient diffused blue-tinted drop shadow
 * - Customizable corner radius (default 24.dp, 28.dp for hero Quick Actions)
 */
@Composable
fun UnitedGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    surfaceBrush: Brush = UnitedGlassSurfaceBrush,
    borderBrush: Brush = UnitedGlassBorderBrush,
    elevation: Dp = 10.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = shape,
                spotColor = UnitedMoneyBlue.copy(alpha = 0.12f),
                ambientColor = UnitedMoneyBlue.copy(alpha = 0.06f)
            )
            .clip(shape)
            .background(surfaceBrush)
            .border(BorderStroke(1.2.dp, borderBrush), shape)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    }
}
