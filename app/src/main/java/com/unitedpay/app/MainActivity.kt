package com.unitedpay.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.unitedpay.app.navigation.AppNavGraph
import com.unitedpay.core.designsystem.components.UnitedToastHost
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedPayTheme
import com.unitedpay.core.designsystem.theme.UnitedWhite
import com.unitedpay.core.model.session.UserSessionManager
import com.unitedpay.core.security.biometric.BiometricAuthHelper

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Material3_Light_NoActionBar)
        super.onCreate(savedInstanceState)

        // Initialize UserSessionManager with persistence
        UserSessionManager.init(this)

        // Lock window status bar styling
        @Suppress("DEPRECATION")
        window.statusBarColor = Color.parseColor("#0078DF")
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            UnitedPayTheme {
                // Determine if biometric gate is needed
                val needsBiometric = UserSessionManager.isLoggedIn &&
                        UserSessionManager.isCurrentBiometricEnabled &&
                        BiometricAuthHelper.canAuthenticate(this@MainActivity)

                var isUnlocked by remember { mutableStateOf(!needsBiometric) }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (isUnlocked) {
                        // Main app content — NavGraph + Toast
                        AppNavGraph()
                        UnitedToastHost()
                    } else {
                        // Biometric lock screen gate
                        BiometricLockGate(
                            activity = this@MainActivity,
                            userName = UserSessionManager.currentSession.value?.fullName ?: "User",
                            onUnlocked = { isUnlocked = true }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Full-screen biometric lock gate shown before the app content.
 * Automatically triggers the fingerprint prompt on first composition.
 */
@Composable
private fun BiometricLockGate(
    activity: FragmentActivity,
    userName: String,
    onUnlocked: () -> Unit
) {
    var authError by remember { mutableStateOf<String?>(null) }
    var attemptCount by remember { mutableIntStateOf(0) }

    // Auto-trigger biometric prompt on first display
    LaunchedEffect(attemptCount) {
        authError = null
        BiometricAuthHelper.showBiometricPrompt(
            activity = activity,
            title = "Unlock UnitedPay",
            subtitle = "Verify your identity to continue",
            negativeButtonText = "Cancel",
            onSuccess = { onUnlocked() },
            onError = { errorCode, errString ->
                authError = errString.toString()
            },
            onCancel = {
                authError = "Authentication cancelled. Tap to retry."
            }
        )
    }

    // Full-screen lock UI with UnitedPay branding
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        UnitedMoneyBlue,
                        androidx.compose.ui.graphics.Color(0xFF005BB5)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Lock icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(UnitedWhite.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = UnitedWhite,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "UnitedPay",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = UnitedWhite
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Welcome back, ${userName.split(" ").first()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = UnitedWhite.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Verify your fingerprint to unlock",
                fontSize = 13.sp,
                color = UnitedWhite.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Fingerprint button
            FilledTonalButton(
                onClick = { attemptCount++ },
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = UnitedWhite.copy(alpha = 0.2f),
                    contentColor = UnitedWhite
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Unlock",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Error message
            if (authError != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = authError!!,
                    fontSize = 12.sp,
                    color = UnitedWhite.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Secured by info
            Text(
                text = "Secured by Android BiometricPrompt",
                fontSize = 10.sp,
                color = UnitedWhite.copy(alpha = 0.35f)
            )
        }
    }
}
