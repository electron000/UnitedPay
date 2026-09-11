package com.unitedpay.core.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Brand Colors (Strictly matched to the official United Pay logo's 'U' - Imaged/Logo.jpg)
val UnitedMoneyBlue = Color(0xFF0078DF) // Signature brand blue of logo's 'U' (#0078DF)
val UnitedRoyalBlue = Color(0xFF0078DF) // Aligned uniformly across the entire platform
val UnitedDeepBlue = Color(0xFF0242D6)  // Lower gradient base of logo's 'U'
val UnitedShieldCyan = Color(0xFF00A0E2) // Upper crest tone of logo's 'U'
val UnitedMidnightNavy = Color(0xFF030D26)
val UnitedObsidian = Color(0xFF121620)

// Reference Screenshot Palette Tokens
val UnitedHeaderBlueDark = Color(0xFF0078DF)
val UnitedHeaderBlueLight = Color(0xFF00A0E2)
val UnitedLimeAccent = Color(0xFFC4D852)
val UnitedLimePill = Color(0xFFADC768)
val UnitedCardBlue = Color(0xFF0078DF)
val UnitedCardSheen = Color(0xFF8CAEE6)
val UnitedCanvasLight = Color(0xFFF5F7FB)
val UnitedGlassCard = Color(0xFFFFFFFF)

// Accent & Rewards Colors
val UnitedAccentGold = Color(0xFFFFB800)
val UnitedGoldGlow = Color(0xFFFFE27D)

// Surfaces & Neutrals
val UnitedWhite = Color(0xFFFFFFFF)
val UnitedBackgroundLight = Color(0xFFF4F7FC)
val UnitedBorderLight = Color(0xFFE2E8F0)
val UnitedSurfaceSubtle = Color(0xFFEDF2F9)

val UnitedTextPrimary = Color(0xFF0F172A)
val UnitedTextSecondary = Color(0xFF64748B)

// Transactional Semantics
val UnitedSuccess = Color(0xFF00C853)
val UnitedSuccessContainer = Color(0xFFE8F8EE)
val UnitedPending = Color(0xFFFF9100)
val UnitedPendingContainer = Color(0xFFFFF4E5)
val UnitedError = Color(0xFFD50000)
val UnitedErrorContainer = Color(0xFFFDEAEA)

// Gradients (Derived from Logo 'U' Gradient)
val UnitedHeaderGradient = Brush.verticalGradient(
    colors = listOf(
        UnitedMoneyBlue,
        Color(0xFF0090E6),
        UnitedShieldCyan
    )
)

val UnitedShieldGradient = Brush.verticalGradient(
    colors = listOf(
        UnitedShieldCyan,
        UnitedRoyalBlue,
        UnitedDeepBlue
    )
)

val UnitedPosterGradient = Brush.verticalGradient(
    colors = listOf(
        UnitedMidnightNavy,
        Color(0xFF081C4A),
        UnitedMidnightNavy
    )
)

// iOS Glassmorphic & Atmospheric Grainy Gradient Palette
val UnitedGradientBlueDeep = Color(0xFF0242D6)
val UnitedGradientBlueVibrant = Color(0xFF0078DF)
val UnitedGradientBlueMid = Color(0xFF0090E6)
val UnitedGradientBlueSoft = Color(0xFF5AB4FA)
val UnitedGradientIceWhite = Color(0xFFDCEBFE)
val UnitedGradientMist = Color(0xFFF0F5FD)
val UnitedSagePill = Color(0xFFAFC777)
val UnitedSagePillText = Color(0xFF1E293B)

// Glassmorphism Specular Border & Translucent Surface Brushes
val UnitedGlassSurfaceBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFFFF).copy(alpha = 0.88f),
        Color(0xFFFFFFFF).copy(alpha = 0.72f)
    )
)

val UnitedGlassBorderBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFFFF).copy(alpha = 0.95f),
        Color(0xFFFFFFFF).copy(alpha = 0.40f)
    )
)

val UnitedDockGlassSurfaceBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFFFF).copy(alpha = 0.95f),
        Color(0xFFF8FAFC).copy(alpha = 0.92f)
    )
)

val UnitedDockBorderBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFFFF).copy(alpha = 0.98f),
        Color(0xFFE2E8F0).copy(alpha = 0.70f)
    )
)

val UnitedAtmosphericGrainyGradient = Brush.verticalGradient(
    colorStops = arrayOf(
        0.00f to Color(0xFF0078DF), // Signature logo 'U' vibrant blue
        0.16f to Color(0xFF008AE8),
        0.30f to Color(0xFF00A0E2), // Upper crest tone of logo 'U'
        0.44f to Color(0xFF5AB4FA),
        0.58f to Color(0xFFA2CEFD),
        0.72f to Color(0xFFDCEBFE),
        0.84f to Color(0xFFF0F5FD),
        1.00f to Color(0xFFF4F7FC)
    )
)

val UnitedCardGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFEEF2FF),
        Color(0xFFDCE7FE),
        Color(0xFF6B93F2),
        UnitedMoneyBlue
    )
)
