package com.unitedpay.feature.home.cards

import com.unitedpay.core.designsystem.components.UnitedToast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.UnitedPinnedBottomBar
import com.unitedpay.core.designsystem.components.UnitedPrimaryButton
import com.unitedpay.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardLimitsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var atmLimit by remember { mutableFloatStateOf(50000f) }
    var posLimit by remember { mutableFloatStateOf(100000f) }
    var onlineLimit by remember { mutableFloatStateOf(75000f) }
    var isInternationalEnabled by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Card Usage Limits", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
        },
        bottomBar = {
            UnitedPinnedBottomBar {
                UnitedPrimaryButton(
                    text = "Save Card Limits",
                    onClick = {
                        UnitedToast.success("Card limits updated successfully!")
                        onBackClick()
                    }
                )
            }
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
                Text("Set Daily Transaction Limits", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = UnitedTextPrimary)
                Text("Configure individual spending caps for your RuPay card", fontSize = 12.sp, color = UnitedTextSecondary)
            }

            // ATM Limit Slider
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = UnitedWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ATM Cash Withdrawal", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UnitedTextPrimary)
                            Text("₹${atmLimit.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = UnitedMoneyBlue)
                        }
                        Slider(
                            value = atmLimit,
                            onValueChange = { atmLimit = it },
                            valueRange = 10000f..100000f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = UnitedMoneyBlue,
                                activeTrackColor = UnitedMoneyBlue
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Min: ₹10,000", fontSize = 10.5.sp, color = UnitedTextSecondary)
                            Text("Max: ₹1,00,000", fontSize = 10.5.sp, color = UnitedTextSecondary)
                        }
                    }
                }
            }

            // POS Merchant Limit
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = UnitedWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Store POS Card Swipes", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UnitedTextPrimary)
                            Text("₹${posLimit.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = UnitedMoneyBlue)
                        }
                        Slider(
                            value = posLimit,
                            onValueChange = { posLimit = it },
                            valueRange = 10000f..200000f,
                            steps = 19,
                            colors = SliderDefaults.colors(
                                thumbColor = UnitedMoneyBlue,
                                activeTrackColor = UnitedMoneyBlue
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Min: ₹10,000", fontSize = 10.5.sp, color = UnitedTextSecondary)
                            Text("Max: ₹2,00,000", fontSize = 10.5.sp, color = UnitedTextSecondary)
                        }
                    }
                }
            }

            // Online Payments Limit
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = UnitedWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Online E-Commerce", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UnitedTextPrimary)
                            Text("₹${onlineLimit.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = UnitedMoneyBlue)
                        }
                        Slider(
                            value = onlineLimit,
                            onValueChange = { onlineLimit = it },
                            valueRange = 10000f..150000f,
                            steps = 14,
                            colors = SliderDefaults.colors(
                                thumbColor = UnitedMoneyBlue,
                                activeTrackColor = UnitedMoneyBlue
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Min: ₹10,000", fontSize = 10.5.sp, color = UnitedTextSecondary)
                            Text("Max: ₹1,50,000", fontSize = 10.5.sp, color = UnitedTextSecondary)
                        }
                    }
                }
            }

            // International Toggle
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
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("International Usage", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UnitedTextPrimary)
                            Text("Enable transactions outside India", fontSize = 11.5.sp, color = UnitedTextSecondary)
                        }
                        Switch(
                            checked = isInternationalEnabled,
                            onCheckedChange = { isInternationalEnabled = it },
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
    }
}
