package com.unitedpay.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue

/**
 * Production-grade Isometric 3D Bank Building & Digital Shield illustration
 * directly matching the "+ Add Your Card" promotional card in Screenshot 2026-09-10 154737.png.
 * 100% Vector Canvas geometry, GPU accelerated, anti-aliased, zero emojis.
 */
@Composable
fun IsometricBankIllustration(
    size: Dp = 72.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Isometric Platform Base (Diamond / Rhombus)
        val platformPath = Path().apply {
            moveTo(w * 0.5f, h * 0.55f)
            lineTo(w * 0.95f, h * 0.72f)
            lineTo(w * 0.5f, h * 0.92f)
            lineTo(w * 0.05f, h * 0.72f)
            close()
        }
        drawPath(
            path = platformPath,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFE0ECFF), Color(0xFFC7DCFE)),
                start = Offset(0f, h * 0.55f),
                end = Offset(w, h * 0.92f)
            )
        )
        // Platform Rim
        val platformRim = Path().apply {
            moveTo(w * 0.05f, h * 0.72f)
            lineTo(w * 0.5f, h * 0.92f)
            lineTo(w * 0.5f, h * 0.98f)
            lineTo(w * 0.05f, h * 0.78f)
            close()
        }
        drawPath(
            path = platformRim,
            color = Color(0xFF94B8F7)
        )
        val platformRimRight = Path().apply {
            moveTo(w * 0.5f, h * 0.92f)
            lineTo(w * 0.95f, h * 0.72f)
            lineTo(w * 0.95f, h * 0.78f)
            lineTo(w * 0.5f, h * 0.98f)
            close()
        }
        drawPath(
            path = platformRimRight,
            color = Color(0xFF7BA5F5)
        )

        // Platform Circuit Grid Lines
        drawPath(
            path = platformPath,
            color = Color(0xFF6895ED).copy(alpha = 0.5f),
            style = Stroke(width = 1.5f)
        )

        // Isometric Bank Building Pediment Roof
        val roofFront = Path().apply {
            moveTo(w * 0.5f, h * 0.22f)
            lineTo(w * 0.75f, h * 0.32f)
            lineTo(w * 0.5f, h * 0.42f)
            lineTo(w * 0.25f, h * 0.32f)
            close()
        }
        drawPath(
            path = roofFront,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFFFFFF), Color(0xFFD6E4FF))
            )
        )
        drawPath(
            path = roofFront,
            color = Color(0xFF3B82F6),
            style = Stroke(width = 1.75f)
        )

        // Bank Columns (Pillars)
        val pillarColor = Color(0xFF2563EB)
        val pillarHighlight = Color(0xFF93C5FD)
        val pillarsX = listOf(0.32f, 0.44f, 0.56f, 0.68f)

        for (px in pillarsX) {
            val pillarTop = h * 0.35f + (px - 0.5f) * 0.1f * h
            val pillarBottom = h * 0.62f + (px - 0.5f) * 0.1f * h
            drawLine(
                color = pillarColor,
                start = Offset(w * px, pillarTop),
                end = Offset(w * px, pillarBottom),
                strokeWidth = 4f
            )
            drawLine(
                color = pillarHighlight,
                start = Offset(w * px - 1f, pillarTop),
                end = Offset(w * px - 1f, pillarBottom),
                strokeWidth = 1.5f
            )
        }

        // Bank Base Step
        val bankStep = Path().apply {
            moveTo(w * 0.5f, h * 0.58f)
            lineTo(w * 0.8f, h * 0.68f)
            lineTo(w * 0.5f, h * 0.76f)
            lineTo(w * 0.2f, h * 0.68f)
            close()
        }
        drawPath(
            path = bankStep,
            color = UnitedMoneyBlue
        )
        drawPath(
            path = bankStep,
            color = Color(0xFF60A5FA),
            style = Stroke(width = 1.5f)
        )

        // Floating Shield Emblem on Left
        val shieldPath = Path().apply {
            moveTo(w * 0.16f, h * 0.42f)
            lineTo(w * 0.26f, h * 0.42f)
            lineTo(w * 0.26f, h * 0.52f)
            cubicTo(w * 0.26f, h * 0.58f, w * 0.21f, h * 0.62f, w * 0.21f, h * 0.62f)
            cubicTo(w * 0.21f, h * 0.62f, w * 0.16f, h * 0.58f, w * 0.16f, h * 0.52f)
            close()
        }
        drawPath(
            path = shieldPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF00A3FF), Color(0xFF0052FF))
            )
        )
        drawPath(
            path = shieldPath,
            color = Color.White,
            style = Stroke(width = 1.2f)
        )

        // Floating Digital Currency Coin on Right
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFDF7D), Color(0xFFFFB800)),
                center = Offset(w * 0.82f, h * 0.42f),
                radius = w * 0.08f
            ),
            radius = w * 0.08f,
            center = Offset(w * 0.82f, h * 0.42f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = w * 0.08f,
            center = Offset(w * 0.82f, h * 0.42f),
            style = Stroke(width = 1.2f)
        )
    }
}
