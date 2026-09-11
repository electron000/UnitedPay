package com.unitedpay.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.components.UnitedBottomBar
import com.unitedpay.core.designsystem.components.UnitedGlassCard
import com.unitedpay.core.designsystem.components.UnitedPayLogo
import com.unitedpay.core.designsystem.components.grainyGradientBackground
import com.unitedpay.core.designsystem.theme.UnitedBorderLight
import com.unitedpay.core.designsystem.theme.UnitedCanvasLight
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedSuccess
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite
import com.unitedpay.core.model.UserProfile
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.mock.UnitedMockData
import com.unitedpay.core.model.session.UserSessionManager

/**
 * Dedicated Profile Screen for the 5th tab in the bottom navigation bar.
 * Bank-grade profile, UPI ID management, security controls, and persistent bottom bar.
 */
@Composable
fun ProfileScreen(
    onNavigateHome: () -> Unit,
    onNavigateCards: () -> Unit,
    onNavigateScan: () -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateToMyQr: () -> Unit = {},
    onNavigateToBiometrics: () -> Unit = {},
    onNavigateToChangeMpin: () -> Unit = {},
    onNavigateToSoundbox: () -> Unit = {},
    onNavigateToDisputeCenter: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var userProfile by remember { mutableStateOf(UserSessionManager.getCurrentProfile()) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.banking.getUserProfile().collect { res ->
            if (res is Resource.Success) {
                userProfile = res.data
            }
        }
    }
    Scaffold(
        containerColor = UnitedCanvasLight,
        bottomBar = {
            UnitedBottomBar(
                currentRoute = "profile",
                onNavigateHome = onNavigateHome,
                onNavigateCards = onNavigateCards,
                onNavigateScan = onNavigateScan,
                onNavigateHistory = onNavigateHistory,
                onNavigateProfile = {}
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Profile & Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = UnitedTextPrimary
                )
            }

            // User Identity Header Card
            item {
                UnitedGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    elevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(UnitedMoneyBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                val initials = userProfile.userName.split(" ")
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.toString() }
                                    .joinToString("")
                                    .ifEmpty { "AC" }
                                Text(
                                    text = initials,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UnitedWhite
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userProfile.userName,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Verified KYC",
                                        tint = UnitedSuccess,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = userProfile.vpa,
                                    fontSize = 13.sp,
                                    color = UnitedMoneyBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${userProfile.phoneNumber} • Full KYC Done",
                                    fontSize = 11.sp,
                                    color = UnitedTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // "My QR Code" Quick Action (8dp reduced radius)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable(onClick = onNavigateToMyQr)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = UnitedMoneyBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "View My QR Code",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = UnitedTextPrimary
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = UnitedTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Security & Settings Group Card
            item {
                Text(
                    text = "SECURITY & UPI PREFERENCES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedTextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                UnitedGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp,
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ProfileSettingRow(
                            icon = Icons.Default.Fingerprint,
                            title = "Biometric App Lock",
                            subtitle = "Fingerprint / Face ID required on launch",
                            onClick = onNavigateToBiometrics
                        )
                        ProfileSettingRow(
                            icon = Icons.Default.Lock,
                            title = "Change UPI MPIN",
                            subtitle = "Reset 6-digit bank security PIN",
                            onClick = onNavigateToChangeMpin
                        )
                        ProfileSettingRow(
                            icon = Icons.Default.VolumeUp,
                            title = "Smart Soundbox Voice Language",
                            subtitle = "Current: Assamese & English audio alerts",
                            onClick = onNavigateToSoundbox
                        )
                        ProfileSettingRow(
                            icon = Icons.Default.HeadsetMic,
                            title = "24x7 NPCI Help & Dispute Center",
                            subtitle = "Instant dispute resolution and ticket tracker",
                            onClick = onNavigateToDisputeCenter,
                            isLast = true
                        )
                    }
                }
            }

            // Log Out Option Card
            item {
                UnitedGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp,
                    elevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF2F2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Log Out of UnitedPay",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626)
                                )
                                Text(
                                    text = "Switch SIM or sign in to another account",
                                    fontSize = 11.sp,
                                    color = UnitedTextSecondary
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFFDC2626).copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // NPCI & Version Footer
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UnitedPayLogo(size = 48.dp, asCardBadge = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "United Pay v1.0.0",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextPrimary
                    )
                    Text(
                        text = "NPCI & RBI Certified Payment Application",
                        fontSize = 10.sp,
                        color = UnitedTextSecondary
                    )
                }
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text(
                        text = "Log out of UnitedPay?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = UnitedTextPrimary
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to log out? You will need to verify your SIM card to access your account again. All your bank accounts and cards will remain completely safe.",
                        fontSize = 13.sp,
                        color = UnitedTextSecondary,
                        lineHeight = 18.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            UserSessionManager.logout()
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Log Out", color = UnitedWhite, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel", color = UnitedTextSecondary, fontWeight = FontWeight.Medium)
                    }
                },
                containerColor = UnitedWhite,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun ProfileSettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isLast: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEBF2FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = UnitedMoneyBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = UnitedTextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = UnitedTextSecondary
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = UnitedTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
        if (!isLast) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF1F5F9))
            )
        }
    }
}
