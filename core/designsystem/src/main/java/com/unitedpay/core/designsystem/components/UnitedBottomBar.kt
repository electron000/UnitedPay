package com.unitedpay.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.theme.UnitedBorderLight
import com.unitedpay.core.designsystem.theme.UnitedDockBorderBrush
import com.unitedpay.core.designsystem.theme.UnitedDockGlassSurfaceBrush
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedSagePill
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite

/**
 * Custom Arched Cradle Shape matching the hand-drawn red sketch in media_1789038596031.png:
 * Creates a smooth dome that arches upward to enclose the elevated center QR scanner button.
 */
val BottomBarArchedShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cradleRadius = 42.dp.value * 2.75f
    val domeHeight = 18.dp.value * 2.75f
    val cornerRadius = 22.dp.value * 2.75f

    // Start at top-left corner
    moveTo(0f, cornerRadius)
    quadraticTo(0f, 0f, cornerRadius, 0f)

    // Flat line towards center
    lineTo(cx - cradleRadius, 0f)

    // Smooth arch up over the center scanner button
    cubicTo(
        cx - cradleRadius * 0.55f, 0f,
        cx - cradleRadius * 0.45f, -domeHeight,
        cx, -domeHeight
    )
    cubicTo(
        cx + cradleRadius * 0.45f, -domeHeight,
        cx + cradleRadius * 0.55f, 0f,
        cx + cradleRadius, 0f
    )

    // Flat line towards top-right
    lineTo(w - cornerRadius, 0f)
    quadraticTo(w, 0f, w, cornerRadius)

    // Down to bottom
    lineTo(w, h)
    lineTo(0f, h)
    close()
}

/**
 * 5-Item Modern Bottom Navigation Bar:
 * - Active tab features a luminous bluish shadow blur effect with frosted blue pill
 * - Cradled center QR scanner button with multi-depth blue shadow and white halo
 * - Respects system gesture navigation bar (navigationBarsPadding)
 * - Optimized for smaller displays (Realme 3 Pro / 360dp width)
 */
@Composable
fun UnitedBottomBar(
    currentRoute: String = "home",
    onNavigateHome: () -> Unit,
    onNavigateCards: () -> Unit,
    onNavigateScan: () -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 10.dp, top = 2.dp)
    ) {
        // Floating frosted glass dock surface with soft elevation and specular hairline border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .defaultMinSize(minHeight = 64.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(26.dp),
                    spotColor = UnitedMoneyBlue.copy(alpha = 0.18f),
                    ambientColor = UnitedMoneyBlue.copy(alpha = 0.08f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(UnitedDockGlassSurfaceBrush)
                .border(androidx.compose.foundation.BorderStroke(1.5.dp, UnitedDockBorderBrush), RoundedCornerShape(26.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home Tab
                BottomNavItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isSelected = currentRoute == "home",
                    onClick = onNavigateHome,
                    modifier = Modifier.weight(1f)
                )

                // 2. Cards Tab
                BottomNavItem(
                    icon = UnitedIcons.Cards,
                    label = "Cards",
                    isSelected = currentRoute == "cards",
                    onClick = onNavigateCards,
                    modifier = Modifier.weight(1f)
                )

                // 3. Center Space reserved for elevated Scanner FAB
                Spacer(modifier = Modifier.width(60.dp))

                // 4. History Tab
                BottomNavItem(
                    icon = UnitedIcons.History,
                    label = "History",
                    isSelected = currentRoute == "history",
                    onClick = onNavigateHistory,
                    modifier = Modifier.weight(1f)
                )

                // 5. Profile Tab
                BottomNavItem(
                    icon = Icons.Default.Person,
                    label = "Profile",
                    isSelected = currentRoute == "profile",
                    onClick = onNavigateProfile,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Elevated Center Scanner Button (Cradled in Center with Glowing Halo)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
                .size(62.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    spotColor = UnitedMoneyBlue.copy(alpha = 0.35f),
                    ambientColor = UnitedMoneyBlue.copy(alpha = 0.20f)
                )
                .clip(CircleShape)
                .background(UnitedWhite)
                .padding(3.dp) // Crisp white outer halo ring
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF00A0E2),
                            UnitedMoneyBlue,
                            Color(0xFF0242D6)
                        )
                    )
                )
                .clickable(onClick = onNavigateScan),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = UnitedIcons.ScanReticle,
                contentDescription = "Scan Any UPI QR",
                tint = UnitedSagePill,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

/**
 * Bottom Nav Item with Logo 'U' Blue Glow on Active Tab
 */
@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 3.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Active Tab: Pure Brand Blue Icon & Text, Ambient Soft Glow
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .scale(1.05f)
                    .padding(vertical = 4.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(UnitedMoneyBlue.copy(alpha = 0.12f))
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = UnitedMoneyBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedMoneyBlue,
                    maxLines = 1,
                    softWrap = false
                )
            }
        } else {
            // Inactive Tab (Subtle Slate Muted)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 5.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = UnitedTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = UnitedTextSecondary,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}
