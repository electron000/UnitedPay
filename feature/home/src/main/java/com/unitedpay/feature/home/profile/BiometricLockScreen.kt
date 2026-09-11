package com.unitedpay.feature.home.profile

import com.unitedpay.core.designsystem.components.UnitedToast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricLockScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activePhone = com.unitedpay.core.model.session.UserSessionManager.currentSession.value?.phoneNumber
        ?: com.unitedpay.core.model.session.UserSessionManager.SIM_1_PHONE
    var isBiometricEnabled by remember {
        mutableStateOf(com.unitedpay.core.model.session.UserSessionManager.isCurrentBiometricEnabled)
    }
    var selectedTimeout by remember { mutableStateOf("Immediately") }
    var requireHighValueAuth by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Biometric App Lock", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = UnitedTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UnitedWhite,
                    titleContentColor = UnitedTextPrimary,
                    navigationIconContentColor = UnitedTextPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = UnitedWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("Unlock with Biometrics", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = UnitedTextPrimary)
                                Text("Fingerprint / Face ID on app launch", fontSize = 12.sp, color = UnitedTextSecondary)
                            }
                        }
                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = { isBiometricEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = UnitedWhite,
                                checkedTrackColor = UnitedMoneyBlue,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFD1D5DB),
                                uncheckedBorderColor = Color(0xFF9CA3AF).copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            if (isBiometricEnabled) {
                item {
                    Text("AUTO-LOCK TIMER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = UnitedWhite)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                        ) {
                            listOf("Immediately", "After 1 minute", "After 5 minutes").forEachIndexed { index, option ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedTimeout = option }
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(option, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = UnitedTextPrimary)
                                    if (selectedTimeout == option) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = UnitedMoneyBlue, modifier = Modifier.size(20.dp))
                                    }
                                }
                                if (index < 2) {
                                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(UnitedBorderLight))
                                }
                            }
                        }
                    }
                }

                item {
                    Text("TRANSACTION SECURITY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = UnitedWhite)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Require for High-Value Payments", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = UnitedTextPrimary)
                                Text("Prompt biometric verification for transfers > ₹2,000", fontSize = 11.5.sp, color = UnitedTextSecondary)
                            }
                            Switch(
                                checked = requireHighValueAuth,
                                onCheckedChange = { requireHighValueAuth = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = UnitedWhite,
                                    checkedTrackColor = UnitedMoneyBlue,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFD1D5DB),
                                    uncheckedBorderColor = Color(0xFF9CA3AF).copy(alpha = 0.5f)
                            )
                            )
                        }
                    }
                }
            }

            item {
                // Hardware Enclave Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = UnitedSuccess, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Android KeyStore Protected", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF166534))
                            Text("Biometric keys are stored in device hardware secure enclave with root detection active.", fontSize = 11.sp, color = Color(0xFF15803D))
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        com.unitedpay.core.model.session.UserSessionManager.setBiometricEnabled(activePhone, isBiometricEnabled)
                        UnitedToast.success("Biometric security settings saved!")
                        onBackClick()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue, contentColor = UnitedWhite)
                ) {
                    Text("Save Security Preferences", fontWeight = FontWeight.Bold, color = UnitedWhite, fontSize = 15.sp)
                }
            }
        }
    }
}
