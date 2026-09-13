package com.unitedpay.feature.home.services.utilities

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.components.UnitedIcons
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.designsystem.util.ReceiptShareHelper
import com.unitedpay.core.model.*
import com.unitedpay.core.model.repository.HookoluServices
import kotlinx.coroutines.launch

/**
 * HookoluPay Digital Subscriptions & OTT Bill Payment Screen.
 * Allows users to renew OTT services, media passes, and news subscriptions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var plans by remember { mutableStateOf<List<SubscriptionPlan>>(emptyList()) }
    var selectedPlanId by remember { mutableStateOf("sub_hotstar") }

    var isPaying by remember { mutableStateOf(false) }
    var paymentDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        plans = HookoluServices.utility.getSubscriptionPlans()
    }

    val activePlan = plans.firstOrNull { it.providerId == selectedPlanId } ?: plans.firstOrNull()

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandShield(size = 26.dp, asCardBadge = true)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Subscriptions & OTT",
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "OTT Entertainment & Streaming Passes",
                                fontSize = 11.sp,
                                color = UnitedTextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = UnitedTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = UnitedWhite)
            )
        },
        bottomBar = {
            if (!paymentDone && activePlan != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding(),
                    color = UnitedWhite,
                    shadowElevation = 8.dp,
                    border = BorderStroke(0.5.dp, UnitedBorderLight)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isPaying = true
                                    kotlinx.coroutines.delay(1000)
                                    paymentDone = true
                                    isPaying = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UnitedMoneyBlue,
                                contentColor = UnitedWhite
                            ),
                            enabled = !isPaying
                        ) {
                            if (isPaying) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = UnitedWhite, strokeWidth = 2.5.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Activating Subscription...", fontSize = 14.sp)
                            } else {
                                Text("Renew ${activePlan.providerName} • ${activePlan.formattedPrice}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            if (paymentDone && activePlan != null) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = UnitedWhite,
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f)),
                        shadowElevation = 3.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(32.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Subscription Activated!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                            Text("Voucher Code: OTT-PASS-982104", fontSize = 12.sp, color = UnitedTextSecondary)

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = UnitedBorderLight)
                            Spacer(modifier = Modifier.height(16.dp))

                            val currentProfile = com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile()
                            val activeBank = com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull()
                            val bankTitle = activeBank?.let { "${it.bankName} (${it.accountNumberMasked})" } ?: "State Bank of India (•••• 4821)"

                            ReceiptRow("Subscriber Name", currentProfile.fullName)
                            ReceiptRow("Streaming Partner", activePlan.providerName)
                            ReceiptRow("Plan Title", activePlan.planTitle)
                            ReceiptRow("Validity Period", activePlan.validity)
                            ReceiptRow("Amount Paid", activePlan.formattedPrice, isBold = true, valueColor = UnitedMoneyBlue)

                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedButton(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        ReceiptShareHelper.shareTransactionReceipt(
                                            context = activity,
                                            receiptDetails = ReceiptShareHelper.ReceiptDetails(
                                                title = "OTT Subscription Renewal",
                                                amount = activePlan.formattedPrice,
                                                sender = currentProfile.fullName,
                                                receiver = activePlan.providerName,
                                                utr = "OTT-PASS-982104",
                                                date = "13 Sep 2026",
                                                time = "10:30 PM",
                                                status = "SUCCESSFUL",
                                                bankName = bankTitle,
                                                paymentMode = "BBPS Digital",
                                                note = "Plan: ${activePlan.planTitle} • Validity: ${activePlan.validity}"
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, UnitedMoneyBlue)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp), tint = UnitedMoneyBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share Native Receipt Slip", color = UnitedMoneyBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("Select Digital Subscription Plan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                }

                items(plans) { plan ->
                    val isSelected = selectedPlanId == plan.providerId
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedPlanId = plan.providerId },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) UnitedMoneyBlue.copy(alpha = 0.06f) else UnitedWhite,
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) UnitedMoneyBlue else UnitedBorderLight
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) UnitedMoneyBlue else UnitedCanvasLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = UnitedIcons.SubscriptionsOtt,
                                    contentDescription = null,
                                    tint = if (isSelected) UnitedWhite else UnitedMoneyBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(plan.providerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                Text("${plan.planTitle} • ${plan.validity}", fontSize = 12.sp, color = UnitedTextSecondary)
                            }

                            Text(plan.formattedPrice, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UnitedMoneyBlue)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = UnitedTextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = UnitedTextSecondary)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
