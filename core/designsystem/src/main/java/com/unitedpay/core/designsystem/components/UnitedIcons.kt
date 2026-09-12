package com.unitedpay.core.designsystem.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Enterprise-grade custom vector icons strictly conforming to the Zero-Emoji standard.
 * Mathematically constructed vectors matching the visual language in Imaged/ screenshots.
 */
object UnitedIcons {

    // ==========================================
    // Quick Actions (Row 1)
    // ==========================================

    /**
     * Crypto / Digital Rupee: Stack of cylindrical currency tokens
     */
    val Crypto: ImageVector by lazy {
        ImageVector.Builder(
            name = "Crypto",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Top cylinder ellipse
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 3f)
                curveTo(16.4f, 3f, 20f, 4.34f, 20f, 6f)
                curveTo(20f, 7.66f, 16.4f, 9f, 12f, 9f)
                curveTo(7.6f, 9f, 4f, 7.66f, 4f, 6f)
                curveTo(4f, 4.34f, 7.6f, 3f, 12f, 3f)
                close()
            }
            // Middle rim
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 11f)
                curveTo(4f, 12.66f, 7.6f, 14f, 12f, 14f)
                curveTo(16.4f, 14f, 20f, 12.66f, 20f, 11f)
            }
            // Bottom cylinder rim & side walls
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 6f)
                lineTo(4f, 17f)
                curveTo(4f, 18.66f, 7.6f, 20f, 12f, 20f)
                curveTo(16.4f, 20f, 20f, 18.66f, 20f, 17f)
                lineTo(20f, 6f)
            }
            // Currency Rupee horizontal bar
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(10f, 10f)
                lineTo(14f, 10f)
                moveTo(10.5f, 12f)
                lineTo(13.5f, 12f)
            }
        }.build()
    }

    /**
     * Recharge: Smartphone outline with screen line & speaker
     */
    val Recharge: ImageVector by lazy {
        ImageVector.Builder(
            name = "Recharge",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(7f, 3f)
                lineTo(17f, 3f)
                curveTo(18.1f, 3f, 19f, 3.9f, 19f, 5f)
                lineTo(19f, 19f)
                curveTo(19f, 20.1f, 18.1f, 21f, 17f, 21f)
                lineTo(7f, 21f)
                curveTo(5.9f, 21f, 5f, 20.1f, 5f, 19f)
                lineTo(5f, 5f)
                curveTo(5f, 3.9f, 5.9f, 3f, 7f, 3f)
                close()
            }
            // Speaker slit
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(11f, 6f)
                lineTo(13f, 6f)
            }
            // Bottom button indicator
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(11f, 18f)
                lineTo(13f, 18f)
            }
        }.build()
    }

    /**
     * TV Cable: High definition monitor screen with base stand
     */
    val TvCable: ImageVector by lazy {
        ImageVector.Builder(
            name = "TvCable",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Screen outline
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 5f)
                lineTo(20f, 5f)
                curveTo(21.1f, 5f, 22f, 5.9f, 22f, 7f)
                lineTo(22f, 15f)
                curveTo(22f, 16.1f, 21.1f, 17f, 20f, 17f)
                lineTo(4f, 17f)
                curveTo(2.9f, 17f, 2f, 16.1f, 2f, 15f)
                lineTo(2f, 7f)
                curveTo(2f, 5.9f, 2.9f, 5f, 4f, 5f)
                close()
            }
            // Stand stem
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(12f, 17f)
                lineTo(12f, 20f)
            }
            // Stand base
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(8f, 20f)
                lineTo(16f, 20f)
            }
        }.build()
    }

    /**
     * Electricity: Lightbulb with lightning filament
     */
    val Electricity: ImageVector by lazy {
        ImageVector.Builder(
            name = "Electricity",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Bulb outline
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(9f, 18f)
                lineTo(15f, 18f)
                moveTo(10f, 21f)
                lineTo(14f, 21f)
                moveTo(6.5f, 9.5f)
                curveTo(6.5f, 6.46f, 8.96f, 4f, 12f, 4f)
                curveTo(15.04f, 4f, 17.5f, 6.46f, 17.5f, 9.5f)
                curveTo(17.5f, 11.5f, 16.3f, 13.2f, 15f, 14.5f)
                lineTo(15f, 16.5f)
                lineTo(9f, 16.5f)
                lineTo(9f, 14.5f)
                curveTo(7.7f, 13.2f, 6.5f, 11.5f, 6.5f, 9.5f)
                close()
            }
            // Lightning filament
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12.5f, 7.5f)
                lineTo(10.5f, 10.5f)
                lineTo(13.5f, 10.5f)
                lineTo(11.5f, 13.5f)
            }
        }.build()
    }

    // ==========================================
    // Quick Actions (Row 2)
    // ==========================================

    /**
     * Offers: Scalloped discount badge / voucher
     */
    val Offers: ImageVector by lazy {
        ImageVector.Builder(
            name = "Offers",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 3f)
                lineTo(14.5f, 4.5f)
                lineTo(17.5f, 4f)
                lineTo(18.5f, 6.8f)
                lineTo(21f, 8.5f)
                lineTo(20.5f, 11.5f)
                lineTo(22f, 14.2f)
                lineTo(20f, 16.5f)
                lineTo(19.5f, 19.5f)
                lineTo(16.5f, 20f)
                lineTo(14.5f, 22f)
                lineTo(12f, 21f)
                lineTo(9.5f, 22f)
                lineTo(7.5f, 20f)
                lineTo(4.5f, 19.5f)
                lineTo(4f, 16.5f)
                lineTo(2f, 14.2f)
                lineTo(3.5f, 11.5f)
                lineTo(3f, 8.5f)
                lineTo(5.5f, 6.8f)
                lineTo(6.5f, 4f)
                lineTo(9.5f, 4.5f)
                close()
            }
            // Percent stroke
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(9f, 15f)
                lineTo(15f, 9f)
                moveTo(9.5f, 9.5f)
                lineTo(9.5f, 9.5f)
                moveTo(14.5f, 14.5f)
                lineTo(14.5f, 14.5f)
            }
        }.build()
    }

    /**
     * Gift Cards: Voucher card with ribbon cross
     */
    val GiftCards: ImageVector by lazy {
        ImageVector.Builder(
            name = "GiftCards",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Card base
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 6f)
                lineTo(21f, 6f)
                curveTo(21.55f, 6f, 22f, 6.45f, 22f, 7f)
                lineTo(22f, 17f)
                curveTo(22f, 17.55f, 21.55f, 18f, 21f, 18f)
                lineTo(3f, 18f)
                curveTo(2.45f, 18f, 2f, 17.55f, 2f, 17f)
                lineTo(2f, 7f)
                curveTo(2f, 6.45f, 2.45f, 6f, 3f, 6f)
                close()
            }
            // Vertical ribbon
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(9f, 6f)
                lineTo(9f, 18f)
            }
            // Horizontal ribbon
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(2f, 12f)
                lineTo(22f, 12f)
            }
        }.build()
    }

    /**
     * Rewards: Gift box with ribbon bow on top
     */
    val Rewards: ImageVector by lazy {
        ImageVector.Builder(
            name = "Rewards",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Box lower body
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 10f)
                lineTo(20f, 10f)
                lineTo(19f, 20f)
                lineTo(5f, 20f)
                close()
            }
            // Box lid
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 7f)
                lineTo(21f, 7f)
                lineTo(21f, 10f)
                lineTo(3f, 10f)
                close()
            }
            // Center vertical ribbon
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(12f, 7f)
                lineTo(12f, 20f)
            }
            // Bow loops
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 7f)
                curveTo(10f, 4f, 7f, 4f, 8.5f, 6.5f)
                curveTo(9.5f, 7.5f, 11f, 7f, 12f, 7f)
                curveTo(13f, 7f, 14.5f, 7.5f, 15.5f, 6.5f)
                curveTo(17f, 4f, 14f, 4f, 12f, 7f)
            }
        }.build()
    }

    /**
     * More: 4 rounded squares in a 2x2 grid
     */
    val More: ImageVector by lazy {
        ImageVector.Builder(
            name = "More",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            val stroke = SolidColor(Color.Black)
            val strokeWidth = 1.75f

            // Top-left square
            path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(5f, 4f)
                lineTo(8.5f, 4f)
                curveTo(9.3f, 4f, 10f, 4.7f, 10f, 5.5f)
                lineTo(10f, 9f)
                curveTo(10f, 9.8f, 9.3f, 10.5f, 8.5f, 10.5f)
                lineTo(5f, 10.5f)
                curveTo(4.2f, 10.5f, 3.5f, 9.8f, 3.5f, 9f)
                lineTo(3.5f, 5.5f)
                curveTo(3.5f, 4.7f, 4.2f, 4f, 5f, 4f)
                close()
            }
            // Top-right square
            path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(15f, 4f)
                lineTo(19f, 4f)
                curveTo(19.55f, 4f, 20f, 4.45f, 20f, 5f)
                lineTo(20f, 9f)
                curveTo(20f, 9.8f, 19.8f, 10.5f, 19f, 10.5f)
                lineTo(15f, 10f)
                curveTo(14.45f, 10f, 14f, 9.55f, 14f, 9f)
                lineTo(14f, 5f)
                curveTo(14f, 4.45f, 14.45f, 4f, 15f, 4f)
                close()
            }
            // Bottom-left square
            path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(5f, 14f)
                lineTo(9f, 14f)
                curveTo(9.55f, 4f, 10f, 14.45f, 10f, 15f)
                lineTo(10f, 19f)
                curveTo(10f, 19.55f, 9.55f, 20f, 9f, 20f)
                lineTo(5f, 20f)
                curveTo(4.45f, 20f, 4f, 19.55f, 4f, 19f)
                lineTo(4f, 15f)
                curveTo(4f, 14.45f, 4.45f, 14f, 5f, 14f)
                close()
            }
            // Bottom-right square
            path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(15f, 14f)
                lineTo(19f, 14f)
                curveTo(19.55f, 14f, 20f, 14.45f, 20f, 15f)
                lineTo(20f, 19f)
                curveTo(20f, 19.55f, 19.55f, 20f, 19f, 20f)
                lineTo(15f, 20f)
                curveTo(14.45f, 20f, 14f, 19.55f, 14f, 19f)
                lineTo(14f, 15f)
                curveTo(14f, 14.45f, 14.45f, 14f, 15f, 14f)
                close()
            }
        }.build()
    }

    // ==========================================
    // Money Control Icons
    // ==========================================

    /**
     * Diagonal Arrow Top-Right (Pay Money)
     */
    val ArrowTopRight: ImageVector by lazy {
        ImageVector.Builder(
            name = "ArrowTopRight",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(7f, 17f)
                lineTo(17f, 7f)
                moveTo(9f, 7f)
                lineTo(17f, 7f)
                lineTo(17f, 15f)
            }
        }.build()
    }

    /**
     * Diagonal Arrow Bottom-Left (Request Money)
     */
    val ArrowBottomLeft: ImageVector by lazy {
        ImageVector.Builder(
            name = "ArrowBottomLeft",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(17f, 7f)
                lineTo(7f, 17f)
                moveTo(15f, 17f)
                lineTo(7f, 17f)
                lineTo(7f, 9f)
            }
        }.build()
    }

    /**
     * Plus Sign (Add Money)
     */
    val Plus: ImageVector by lazy {
        ImageVector.Builder(
            name = "Plus",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(12f, 6f)
                lineTo(12f, 18f)
                moveTo(6f, 12f)
                lineTo(18f, 12f)
            }
        }.build()
    }

    /**
     * Bank / Temple Portico (Check Balance)
     */
    val BankTemple: ImageVector by lazy {
        ImageVector.Builder(
            name = "BankTemple",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            val stroke = SolidColor(Color.White)
            // Pediment roof triangle
            path(
                stroke = stroke,
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 8f)
                lineTo(12f, 3.5f)
                lineTo(20f, 8f)
                close()
            }
            // Entablature bar
            path(stroke = stroke, strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round) {
                moveTo(3f, 9f)
                lineTo(21f, 9f)
            }
            // 3 Columns / Pillars
            path(stroke = stroke, strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round) {
                moveTo(6.5f, 10f)
                lineTo(6.5f, 17.5f)
                moveTo(12f, 10f)
                lineTo(12f, 17.5f)
                moveTo(17.5f, 10f)
                lineTo(17.5f, 17.5f)
            }
            // Podium base
            path(stroke = stroke, strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round) {
                moveTo(3f, 18.5f)
                lineTo(21f, 18.5f)
                moveTo(2f, 21f)
                lineTo(22f, 21f)
            }
        }.build()
    }

    // ==========================================
    // Navigation & Header Icons
    // ==========================================

    /**
     * 4-Square App Menu Icon (Header Top Left)
     */
    val GridMenu: ImageVector by lazy {
        ImageVector.Builder(
            name = "GridMenu",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            val stroke = SolidColor(Color.White)
            val strokeWidth = 1.8f
            path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(5f, 4f)
                lineTo(8.5f, 4f)
                curveTo(9.3f, 4f, 10f, 4.7f, 10f, 5.5f)
                lineTo(10f, 9f)
                curveTo(10f, 9.8f, 9.3f, 10.5f, 8.5f, 10.5f)
                lineTo(5f, 10.5f)
                curveTo(4.2f, 10.5f, 3.5f, 9.8f, 3.5f, 9f)
                lineTo(3.5f, 5.5f)
                curveTo(3.5f, 4.7f, 4.2f, 4f, 5f, 4f)
                close()
            }
            path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(15.5f, 4f)
                lineTo(19f, 4f)
                curveTo(19.8f, 4f, 20.5f, 4.7f, 20.5f, 5.5f)
                lineTo(20.5f, 9f)
                curveTo(20.5f, 9.8f, 19.8f, 10.5f, 19f, 10.5f)
                lineTo(15.5f, 10.5f)
                curveTo(14.7f, 10.5f, 14f, 9.8f, 14f, 9f)
                lineTo(14f, 5.5f)
                curveTo(14f, 4.7f, 14.7f, 4f, 15.5f, 4f)
                close()
            }
            path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(5f, 14.5f)
                lineTo(8.5f, 14.5f)
                curveTo(9.3f, 14.5f, 10f, 15.2f, 10f, 16f)
                lineTo(10f, 19.5f)
                curveTo(10f, 20.3f, 9.3f, 21f, 8.5f, 21f)
                lineTo(5f, 21f)
                curveTo(4.2f, 21f, 3.5f, 20.3f, 3.5f, 19.5f)
                lineTo(3.5f, 16f)
                curveTo(3.5f, 15.2f, 4.2f, 14.5f, 5f, 14.5f)
                close()
            }
            path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(15.5f, 14.5f)
                lineTo(19f, 14.5f)
                curveTo(19.8f, 14.5f, 20.5f, 15.2f, 20.5f, 16f)
                lineTo(20.5f, 19.5f)
                curveTo(20.5f, 20.3f, 19.8f, 21f, 19f, 21f)
                lineTo(15.5f, 21f)
                curveTo(14.7f, 21f, 14f, 20.3f, 14f, 19.5f)
                lineTo(14f, 16f)
                curveTo(14f, 15.2f, 14.7f, 14.5f, 15.5f, 14.5f)
                close()
            }
        }.build()
    }

    /**
     * Chat Message Bubble with Unread Indicator (Header Top Right)
     */
    val ChatBubble: ImageVector by lazy {
        ImageVector.Builder(
            name = "ChatBubble",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19f, 4f)
                lineTo(5f, 4f)
                curveTo(3.9f, 4f, 3f, 4.9f, 3f, 6f)
                lineTo(3f, 15f)
                curveTo(3f, 16.1f, 3.9f, 17f, 5f, 17f)
                lineTo(8f, 17f)
                lineTo(10.5f, 20.5f)
                curveTo(10.8f, 20.9f, 11.5f, 20.8f, 11.7f, 20.3f)
                lineTo(13f, 17f)
                lineTo(19f, 17f)
                curveTo(20.1f, 17f, 21f, 16.1f, 21f, 15f)
                lineTo(21f, 6f)
                curveTo(21f, 4.9f, 20.1f, 4f, 19f, 4f)
                close()
            }
            // Unread dot inside
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(8f, 10.5f)
                lineTo(8f, 10.5f)
                moveTo(12f, 10.5f)
                lineTo(12f, 10.5f)
                moveTo(16f, 10.5f)
                lineTo(16f, 10.5f)
            }
        }.build()
    }

    /**
     * Center Floating QR Scanner Reticle Viewfinder `[ - ]`
     */
    val ScanReticle: ImageVector by lazy {
        ImageVector.Builder(
            name = "ScanReticle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            val stroke = SolidColor(Color.White)
            val strokeWidth = 2.2f

            path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(4f, 9f)
                lineTo(4f, 5f)
                lineTo(8f, 5f)
            }
            path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(16f, 5f)
                lineTo(20f, 5f)
                lineTo(20f, 9f)
            }
            path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(4f, 15f)
                lineTo(4f, 19f)
                lineTo(8f, 19f)
            }
            path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(16f, 19f)
                lineTo(20f, 19f)
                lineTo(20f, 15f)
            }
            path(stroke = SolidColor(Color(0xFF00A3FF)), strokeLineWidth = 2.2f, strokeLineCap = StrokeCap.Round) {
                moveTo(7f, 12f)
                lineTo(17f, 12f)
            }
        }.build()
    }

    /**
     * Credit/Debit Card Icon (Bottom Nav)
     */
    val Cards: ImageVector by lazy {
        ImageVector.Builder(
            name = "Cards",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 5f)
                lineTo(21f, 5f)
                curveTo(22.1f, 5f, 23f, 5.9f, 23f, 7f)
                lineTo(23f, 17f)
                curveTo(23f, 18.1f, 22.1f, 19f, 21f, 19f)
                lineTo(3f, 19f)
                curveTo(1.9f, 19f, 1f, 18.1f, 1f, 17f)
                lineTo(1f, 7f)
                curveTo(1f, 5.9f, 1.9f, 5f, 3f, 5f)
                close()
            }
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.5f) {
                moveTo(1f, 9.5f)
                lineTo(23f, 9.5f)
            }
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Round) {
                moveTo(4f, 14f)
                lineTo(7f, 14f)
            }
        }.build()
    }

    /**
     * Transaction History / Passbook Ledger Icon (Bottom Nav)
     */
    val History: ImageVector by lazy {
        ImageVector.Builder(
            name = "History",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(5f, 3f)
                lineTo(19f, 3f)
                curveTo(20.1f, 3f, 21f, 3.9f, 21f, 5f)
                lineTo(21f, 19f)
                curveTo(21f, 20.1f, 20.1f, 21f, 19f, 21f)
                lineTo(5f, 21f)
                curveTo(3.9f, 21f, 3f, 20.1f, 3f, 19f)
                lineTo(3f, 5f)
                curveTo(3f, 3.9f, 3.9f, 3f, 5f, 3f)
                close()
            }
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Round) {
                moveTo(7f, 7.5f)
                lineTo(17f, 7.5f)
                moveTo(7f, 11.5f)
                lineTo(15f, 11.5f)
                moveTo(7f, 15.5f)
                lineTo(12f, 15.5f)
            }
        }.build()
    }

    /**
     * Self Transfer: Two interlocking circular transfer arrows
     */
    val SelfTransfer: ImageVector by lazy {
        ImageVector.Builder(
            name = "SelfTransfer",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Top arrow pointing right
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 8f)
                lineTo(18f, 8f)
                moveTo(14f, 4f)
                lineTo(18f, 8f)
                lineTo(14f, 12f)
            }
            // Bottom arrow pointing left
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20f, 16f)
                lineTo(6f, 16f)
                moveTo(10f, 12f)
                lineTo(6f, 16f)
                lineTo(10f, 20f)
            }
        }.build()
    }

    /**
     * Bank Transfer: Direct account transfer (Bank temple with forward arrow)
     */
    val BankTransfer: ImageVector by lazy {
        ImageVector.Builder(
            name = "BankTransfer",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Roof pediment
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.7f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(2f, 9f)
                lineTo(12f, 3f)
                lineTo(22f, 9f)
                close()
            }
            // Pillars
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.7f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(5f, 11f); lineTo(5f, 18f)
                moveTo(10f, 11f); lineTo(10f, 18f)
                moveTo(15f, 11f); lineTo(15f, 18f)
                moveTo(19f, 11f); lineTo(19f, 18f)
            }
            // Base plinth
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(2f, 21f)
                lineTo(22f, 21f)
            }
        }.build()
    }

    /**
     * Autopay / UPI Mandates: Circular recurrence loop with checkmark
     */
    val Autopay: ImageVector by lazy {
        ImageVector.Builder(
            name = "Autopay",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Circular arc
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(20f, 12f)
                curveTo(20f, 16.4f, 16.4f, 20f, 12f, 20f)
                curveTo(7.6f, 20f, 4f, 16.4f, 4f, 12f)
                curveTo(4f, 7.6f, 7.6f, 4f, 12f, 4f)
                curveTo(15f, 4f, 17.6f, 5.7f, 19f, 8f)
            }
            // Arrow head
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(15f, 8f)
                lineTo(19f, 8f)
                lineTo(19f, 4f)
            }
            // Center checkmark
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(9f, 12f)
                lineTo(11f, 14f)
                lineTo(15f, 10f)
            }
        }.build()
    }

    /**
     * FASTag Toll: Toll booth barrier
     */
    val Fastag: ImageVector by lazy {
        ImageVector.Builder(name = "Fastag", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(4f, 21f); lineTo(4f, 9f); lineTo(8f, 9f); lineTo(8f, 21f)
                moveTo(4f, 13f); lineTo(20f, 6f)
                moveTo(14f, 16f); lineTo(20f, 16f); lineTo(20f, 21f); lineTo(14f, 21f); close()
            }
        }.build()
    }

    /**
     * Piped Gas: Gas burner flame
     */
    val Gas: ImageVector by lazy {
        ImageVector.Builder(name = "Gas", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(12f, 3f)
                curveTo(12f, 3f, 7f, 8.5f, 7f, 14f)
                curveTo(7f, 17.5f, 9.2f, 21f, 12f, 21f)
                curveTo(14.8f, 21f, 17f, 17.5f, 17f, 14f)
                curveTo(17f, 8.5f, 12f, 3f, 12f, 3f)
                close()
                moveTo(12f, 13f)
                curveTo(11f, 14f, 10.5f, 15.5f, 10.5f, 17f)
                curveTo(10.5f, 18.5f, 11.2f, 19.5f, 12f, 19.5f)
                curveTo(12.8f, 19.5f, 13.5f, 18.5f, 13.5f, 17f)
                curveTo(13.5f, 15.5f, 13f, 14f, 12f, 13f)
            }
        }.build()
    }

    /**
     * Water: Clean teardrop
     */
    val Water: ImageVector by lazy {
        ImageVector.Builder(name = "Water", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(12f, 3f)
                curveTo(12f, 3f, 6f, 10f, 6f, 15f)
                curveTo(6f, 18.3f, 8.7f, 21f, 12f, 21f)
                curveTo(15.3f, 21f, 18f, 18.3f, 18f, 15f)
                curveTo(18f, 10f, 12f, 3f, 12f, 3f)
                close()
            }
        }.build()
    }

    /**
     * Broadband: Wi-Fi signal arcs
     */
    val Broadband: ImageVector by lazy {
        ImageVector.Builder(name = "Broadband", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round) {
                moveTo(5f, 7f); curveTo(9f, 3.5f, 15f, 3.5f, 19f, 7f)
                moveTo(8f, 10.5f); curveTo(10.5f, 8f, 13.5f, 8f, 16f, 10.5f)
                moveTo(10.5f, 14f); curveTo(11.5f, 13f, 12.5f, 13f, 13.5f, 14f)
                moveTo(12f, 18f); lineTo(12.01f, 18f)
            }
        }.build()
    }

    /**
     * Insurance: Protective umbrella
     */
    val Insurance: ImageVector by lazy {
        ImageVector.Builder(name = "Insurance", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(12f, 3f); lineTo(12f, 5f)
                moveTo(4f, 13f); curveTo(4f, 7.5f, 7.5f, 5f, 12f, 5f); curveTo(16.5f, 5f, 20f, 7.5f, 20f, 13f); lineTo(4f, 13f); close()
                moveTo(12f, 13f); lineTo(12f, 19f); curveTo(12f, 20.5f, 13.5f, 21f, 14.5f, 20f)
            }
        }.build()
    }

    /**
     * Loan EMI: Percentage calculation & notes
     */
    val LoanEmi: ImageVector by lazy {
        ImageVector.Builder(name = "LoanEmi", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(3f, 6f); lineTo(21f, 6f); lineTo(21f, 18f); lineTo(3f, 18f); close()
                moveTo(8f, 10f); lineTo(8.01f, 10f)
                moveTo(16f, 14f); lineTo(16.01f, 14f)
                moveTo(7f, 14f); lineTo(17f, 10f)
            }
        }.build()
    }

    /**
     * Digital Gold: Stacked Gold Ingot Bar
     */
    val DigitalGold: ImageVector by lazy {
        ImageVector.Builder(name = "DigitalGold", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(5f, 15f); lineTo(8f, 7f); lineTo(16f, 7f); lineTo(19f, 15f); close()
                moveTo(3f, 19f); lineTo(5f, 15f); lineTo(19f, 15f); lineTo(21f, 19f); close()
            }
        }.build()
    }

    /**
     * Mutual Funds: Upward trend growth chart
     */
    val MutualFunds: ImageVector by lazy {
        ImageVector.Builder(name = "MutualFunds", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(4f, 20f); lineTo(20f, 20f)
                moveTo(4f, 16f); lineTo(10f, 10f); lineTo(14f, 14f); lineTo(20f, 6f)
                moveTo(15f, 6f); lineTo(20f, 6f); lineTo(20f, 11f)
            }
        }.build()
    }

    /**
     * Metro: Modern express train
     */
    val Metro: ImageVector by lazy {
        ImageVector.Builder(name = "Metro", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(6f, 4f); lineTo(18f, 4f); curveTo(19.5f, 4f, 20f, 5.5f, 20f, 7f); lineTo(20f, 16f); curveTo(20f, 17.5f, 18.5f, 18f, 17f, 18f); lineTo(7f, 18f); curveTo(5.5f, 18f, 4f, 17.5f, 4f, 16f); lineTo(4f, 7f); curveTo(4f, 5.5f, 4.5f, 4f, 6f, 4f); close()
                moveTo(4f, 11f); lineTo(20f, 11f)
                moveTo(7f, 15f); lineTo(7.01f, 15f)
                moveTo(17f, 15f); lineTo(17.01f, 15f)
                moveTo(7f, 18f); lineTo(5f, 21f)
                moveTo(17f, 18f); lineTo(19f, 21f)
            }
        }.build()
    }

    /**
     * Biometric / Fingerprint: Secure biometric ridges
     */
    val Biometric: ImageVector by lazy {
        ImageVector.Builder(name = "Biometric", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round) {
                moveTo(12f, 3f); curveTo(8.5f, 3f, 6f, 5.5f, 6f, 9f); curveTo(6f, 14f, 8f, 17f, 8f, 21f)
                moveTo(12f, 7f); curveTo(10f, 7f, 9f, 8.5f, 9f, 10.5f); curveTo(9f, 13.5f, 10f, 16.5f, 10f, 19.5f)
                moveTo(12f, 11f); curveTo(13f, 11f, 14f, 12f, 14f, 13.5f); curveTo(14f, 16f, 13f, 18f, 13f, 21f)
                moveTo(15f, 7.5f); curveTo(17f, 9f, 18f, 11.5f, 18f, 15f); curveTo(18f, 17f, 17f, 19.5f, 16f, 21f)
            }
        }.build()
    }

    /**
     * Soundbox / Audio Speaker Horn
     */
    val SoundboxSpeaker: ImageVector by lazy {
        ImageVector.Builder(name = "SoundboxSpeaker", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(4f, 9f); lineTo(8f, 9f); lineTo(14f, 4f); lineTo(14f, 20f); lineTo(8f, 15f); lineTo(4f, 15f); close()
                moveTo(18f, 8f); curveTo(19.5f, 10f, 19.5f, 14f, 18f, 16f)
                moveTo(20.5f, 6f); curveTo(23f, 9.5f, 23f, 14.5f, 20.5f, 18f)
            }
        }.build()
    }

    /**
     * Dispute / 24x7 Help Headset
     */
    val HelpHeadset: ImageVector by lazy {
        ImageVector.Builder(name = "HelpHeadset", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(4f, 12f); curveTo(4f, 7.5f, 7.5f, 4f, 12f, 4f); curveTo(16.5f, 4f, 20f, 7.5f, 20f, 12f)
                moveTo(4f, 12f); lineTo(4f, 17f); curveTo(4f, 18f, 5f, 19f, 6f, 19f); lineTo(7f, 19f); lineTo(7f, 12f); lineTo(4f, 12f)
                moveTo(20f, 12f); lineTo(20f, 17f); curveTo(20f, 18f, 19f, 19f, 18f, 19f); lineTo(17f, 19f); lineTo(17f, 12f); lineTo(20f, 12f)
                moveTo(7f, 19f); lineTo(10f, 21f); lineTo(14f, 21f)
            }
        }.build()
    }

    /**
     * Movie Tickets: Clapperboard icon
     */
    val MovieTicket: ImageVector by lazy {
        ImageVector.Builder(name = "MovieTicket", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(4f, 8f); lineTo(20f, 8f); lineTo(20f, 19f); curveTo(20f, 20f, 19f, 21f, 18f, 21f); lineTo(6f, 21f); curveTo(5f, 21f, 4f, 20f, 4f, 19f); close()
                moveTo(4f, 8f); lineTo(6f, 4f); lineTo(20f, 4f); lineTo(18f, 8f); close()
                moveTo(9f, 4f); lineTo(7.5f, 8f)
                moveTo(14f, 4f); lineTo(12.5f, 8f)
                moveTo(10f, 12f); lineTo(15f, 14.5f); lineTo(10f, 17f); close()
            }
        }.build()
    }

    /**
     * Train / Railway: Train front silhouette
     */
    val TrainTicket: ImageVector by lazy {
        ImageVector.Builder(name = "TrainTicket", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(5f, 5f); curveTo(5f, 3.5f, 6.5f, 3f, 12f, 3f); curveTo(17.5f, 3f, 19f, 3.5f, 19f, 5f); lineTo(19f, 17f); curveTo(19f, 18.5f, 17.5f, 19f, 12f, 19f); curveTo(6.5f, 19f, 5f, 18.5f, 5f, 17f); close()
                moveTo(5f, 10f); lineTo(19f, 10f)
                moveTo(8f, 15f); lineTo(8.01f, 15f)
                moveTo(16f, 15f); lineTo(16.01f, 15f)
                moveTo(6f, 21f); lineTo(8f, 19f)
                moveTo(18f, 21f); lineTo(16f, 19f)
            }
        }.build()
    }

    /**
     * Flight / Airplane
     */
    val FlightTicket: ImageVector by lazy {
        ImageVector.Builder(name = "FlightTicket", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(12f, 3f); lineTo(14f, 9f); lineTo(21f, 12f); lineTo(14f, 14f); lineTo(13f, 19f); lineTo(16f, 21f); lineTo(12f, 20f); lineTo(8f, 21f); lineTo(9f, 19f); lineTo(8f, 14f); lineTo(1f, 12f); lineTo(8f, 9f); close()
            }
        }.build()
    }

    /**
     * Bus: Passenger Bus front
     */
    val BusTicket: ImageVector by lazy {
        ImageVector.Builder(name = "BusTicket", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(5f, 4f); curveTo(5f, 3f, 6f, 2f, 8f, 2f); lineTo(16f, 2f); curveTo(18f, 2f, 19f, 3f, 19f, 4f); lineTo(19f, 18f); curveTo(19f, 19f, 18f, 20f, 16f, 20f); lineTo(8f, 20f); curveTo(6f, 20f, 5f, 19f, 5f, 18f); close()
                moveTo(5f, 10f); lineTo(19f, 10f)
                moveTo(8f, 15f); lineTo(8.01f, 15f)
                moveTo(16f, 15f); lineTo(16.01f, 15f)
                moveTo(6f, 20f); lineTo(6f, 22f)
                moveTo(18f, 20f); lineTo(18f, 22f)
            }
        }.build()
    }

    /**
     * Hotel: Building with pin
     */
    val HotelBooking: ImageVector by lazy {
        ImageVector.Builder(name = "HotelBooking", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(4f, 21f); lineTo(4f, 5f); curveTo(4f, 4f, 5f, 3f, 6f, 3f); lineTo(14f, 3f); curveTo(15f, 3f, 16f, 4f, 16f, 5f); lineTo(16f, 21f)
                moveTo(16f, 9f); lineTo(20f, 9f); curveTo(21f, 9f, 21f, 10f, 21f, 11f); lineTo(21f, 21f)
                moveTo(8f, 7f); lineTo(10f, 7f)
                moveTo(8f, 11f); lineTo(10f, 11f)
                moveTo(8f, 15f); lineTo(10f, 15f)
                moveTo(2f, 21f); lineTo(22f, 21f)
            }
        }.build()
    }

    /**
     * Stocks / Bull trend chart
     */
    val StocksBull: ImageVector by lazy {
        ImageVector.Builder(name = "StocksBull", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(3f, 20f); lineTo(21f, 20f)
                moveTo(4f, 15f); lineTo(9f, 10f); lineTo(13f, 14f); lineTo(20f, 6f)
                moveTo(15f, 6f); lineTo(20f, 6f); lineTo(20f, 11f)
            }
        }.build()
    }

    /**
     * Cashback & Offers Tag
     */
    val CashbackTag: ImageVector by lazy {
        ImageVector.Builder(name = "CashbackTag", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(10f, 3f); lineTo(20f, 3f); curveTo(20.5f, 3f, 21f, 3.5f, 21f, 4f); lineTo(21f, 14f); lineTo(12f, 21f); lineTo(3f, 12f); close()
                moveTo(16.5f, 7.5f); lineTo(16.51f, 7.5f)
                moveTo(8f, 11f); lineTo(12f, 11f)
            }
        }.build()
    }

    /**
     * Refer & Win / Two people shaking hands or connecting
     */
    val ReferEarn: ImageVector by lazy {
        ImageVector.Builder(name = "ReferEarn", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(8f, 7f); curveTo(8f, 5.5f, 6.5f, 4f, 5f, 4f); curveTo(3.5f, 4f, 2f, 5.5f, 2f, 7f); curveTo(2f, 8.5f, 3.5f, 10f, 5f, 10f); curveTo(6.5f, 10f, 8f, 8.5f, 8f, 7f); close()
                moveTo(22f, 7f); curveTo(22f, 5.5f, 20.5f, 4f, 19f, 4f); curveTo(17.5f, 4f, 16f, 5.5f, 16f, 7f); curveTo(16f, 8.5f, 17.5f, 10f, 19f, 10f); curveTo(20.5f, 10f, 22f, 8.5f, 22f, 7f); close()
                moveTo(1f, 18f); curveTo(1f, 14.5f, 3f, 13f, 6f, 13f); lineTo(9f, 16f); lineTo(15f, 16f); lineTo(18f, 13f); curveTo(21f, 13f, 23f, 14.5f, 23f, 18f)
            }
        }.build()
    }

    /**
     * LPG Gas Cylinder
     */
    val GasCylinder: ImageVector by lazy {
        ImageVector.Builder(name = "GasCylinder", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(8f, 3f); lineTo(16f, 3f); lineTo(16f, 6f); lineTo(8f, 6f); close()
                moveTo(10f, 3f); lineTo(10f, 6f)
                moveTo(14f, 3f); lineTo(14f, 6f)
                moveTo(6f, 8f); curveTo(6f, 7f, 7f, 6f, 9f, 6f); lineTo(15f, 6f); curveTo(17f, 6f, 18f, 7f, 18f, 8f); lineTo(18f, 20f); curveTo(18f, 21f, 17f, 22f, 15f, 22f); lineTo(9f, 22f); curveTo(7f, 22f, 6f, 21f, 6f, 20f); close()
                moveTo(6f, 14f); lineTo(18f, 14f)
            }
        }.build()
    }

    /**
     * Pay Later / Paytm Postpaid / Credit Card on UPI
     */
    val PayLater: ImageVector by lazy {
        ImageVector.Builder(name = "PayLater", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.75f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(3f, 5f); curveTo(3f, 4f, 4f, 3f, 5f, 3f); lineTo(19f, 3f); curveTo(20f, 3f, 21f, 4f, 21f, 5f); lineTo(21f, 17f); curveTo(21f, 18f, 20f, 19f, 19f, 19f); lineTo(5f, 19f); curveTo(4f, 19f, 3f, 18f, 3f, 17f); close()
                moveTo(3f, 9f); lineTo(21f, 9f)
                moveTo(7f, 14f); lineTo(11f, 14f)
                moveTo(9f, 12.5f); lineTo(9f, 15.5f)
            }
        }.build()
    }

    /**
     * Services Hub: 4-Tile Grid Icon for Bottom Navigation
     */
    val ServicesHub: ImageVector by lazy {
        ImageVector.Builder(
            name = "ServicesHub",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            val stroke = SolidColor(Color.Black)
            val strokeWidth = 1.75f
            path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(4f, 4f); lineTo(9f, 4f); lineTo(9f, 9f); lineTo(4f, 9f); close()
                moveTo(15f, 4f); lineTo(20f, 4f); lineTo(20f, 9f); lineTo(15f, 9f); close()
                moveTo(4f, 15f); lineTo(9f, 15f); lineTo(9f, 20f); lineTo(4f, 20f); close()
                moveTo(15f, 15f); lineTo(20f, 15f); lineTo(20f, 20f); lineTo(15f, 20f); close()
            }
        }.build()
    }
}



