package com.unitedpay.feature.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.R
import com.unitedpay.core.designsystem.theme.UnitedObsidian
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Startup splash screen:
 * - Brand emblem (U & P) is statically positioned in its exact place from frame 0.
 * - Below text ("United Pay" and security tagline) reveals smoothly from Left to Right.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val textRevealProgress = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // "United Pay" wordmark reveals Left to Right
        launch {
            delay(150)
            textAlpha.animateTo(1f, tween(200))
            textRevealProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing)
            )
        }

        // Hold briefly and transition to Home
        delay(1800)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UnitedWhite)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSplashFinished
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Static Brand Emblem (U & P) in its exact dimensions and position
            Box(
                modifier = Modifier.size(width = 150.dp, height = 110.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.brand_emblem),
                    contentDescription = "United Pay Official Emblem",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated "United Pay" Wordmark (Reveals Left to Right)
            val reveal = textRevealProgress.value
            Box(
                modifier = Modifier
                    .drawWithContent {
                        val clipW = size.width * reveal
                        clipRect(left = 0f, top = 0f, right = clipW, bottom = size.height) {
                            this@drawWithContent.drawContent()
                        }
                    }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "United ",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        color = UnitedObsidian.copy(alpha = textAlpha.value)
                    )
                    Text(
                        text = "Pay",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                        color = UnitedObsidian.copy(alpha = textAlpha.value)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle Tagline
            Text(
                text = "Secure • Instant • NPCI Certified",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                color = UnitedTextSecondary.copy(alpha = textRevealProgress.value)
            )
        }
    }
}
