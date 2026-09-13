package com.unitedpay.feature.home.services.utilities

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
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
 * HookoluPay LPG Cylinder Refill Booking Screen.
 * Supports Indane Gas, Bharat Gas, and HP Gas booking with DBTL subsidy tracking.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LpgCylinderScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var lpgDetails by remember { mutableStateOf<LpgBookingDetails?>(null) }
    var selectedProvider by remember { mutableStateOf("Indane LPG Gas") }
    var consumerIdInput by remember { mutableStateOf("17290184920491024") }

    var isBooking by remember { mutableStateOf(false) }
    var bookingConfirmed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        lpgDetails = HookoluServices.utility.getLpgDetails()
    }

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
                                text = "Book LPG Cylinder",
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "BBPS Gas Refill • Indane, Bharat, HP",
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
            if (!bookingConfirmed) {
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
                                    isBooking = true
                                    HookoluServices.utility.bookLpgCylinder(consumerIdInput, selectedProvider)
                                    bookingConfirmed = true
                                    isBooking = false
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
                            enabled = !isBooking && consumerIdInput.isNotBlank()
                        ) {
                            if (isBooking) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = UnitedWhite, strokeWidth = 2.5.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Dispatching Refill Order to OMC...", fontSize = 14.sp)
                            } else {
                                Text("Pay & Book Refill • ₹860.50", fontWeight = FontWeight.Bold, fontSize = 15.sp)
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

            // 1. Gas Provider Selector
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = UnitedWhite,
                    border = BorderStroke(1.dp, UnitedBorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        listOf("Indane LPG Gas", "Bharat Gas", "HP Gas").forEach { provider ->
                            val isSelected = selectedProvider == provider
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) UnitedMoneyBlue else Color.Transparent)
                                    .clickable { selectedProvider = provider }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = provider,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) UnitedWhite else UnitedTextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Booking Success Confirmation
            if (bookingConfirmed) {
                item {
                    val d = lpgDetails!!
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
                            Text("LPG Cylinder Refill Booked!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                            Text("Booking Reference: OMC-GAS-89104", fontSize = 12.sp, color = UnitedTextSecondary)

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = UnitedBorderLight)
                            Spacer(modifier = Modifier.height(16.dp))

                            val currentProfile = com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile()
                            val activeBank = com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull()
                            val bankTitle = activeBank?.let { "${it.bankName} (${it.accountNumberMasked})" } ?: "State Bank of India (•••• 4821)"

                            ReceiptRow("Consumer Name", currentProfile.fullName)
                            ReceiptRow("17-Digit LPG ID", d.consumerId)
                            ReceiptRow("Cylinder Type", d.cylinderSize)
                            ReceiptRow("Refill Price", d.formattedPrice, isBold = true, valueColor = UnitedMoneyBlue)
                            ReceiptRow("Direct Subsidy", d.formattedSubsidy, isBold = true, valueColor = Color(0xFF10B981))
                            ReceiptRow("Delivery Address", d.deliveryAddress)

                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedButton(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        val currentProfile = com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile()
                                        val activeBank = com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull()
                                        val bankTitle = activeBank?.let { "${it.bankName} (${it.accountNumberMasked})" } ?: "State Bank of India (•••• 4821)"

                                        ReceiptShareHelper.shareTransactionReceipt(
                                            context = activity,
                                            receiptDetails = ReceiptShareHelper.ReceiptDetails(
                                                title = "LPG Cylinder Refill Booking",
                                                amount = d.formattedPrice,
                                                sender = currentProfile.fullName,
                                                receiver = selectedProvider,
                                                utr = "OMC-GAS-89104",
                                                date = "13 Sep 2026",
                                                time = "10:30 PM",
                                                status = "CONFIRMED",
                                                bankName = bankTitle,
                                                paymentMode = "BBPS Refill",
                                                note = "LPG ID: ${d.consumerId} • Subsidy: ${d.formattedSubsidy}"
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
                                Text("Share Gas Refill Slip", color = UnitedMoneyBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // 2. Consumer Details Input
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = UnitedWhite,
                        border = BorderStroke(1.dp, UnitedBorderLight),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("17-Digit LPG ID or Registered Mobile", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = consumerIdInput,
                                onValueChange = { consumerIdInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = {
                                    Icon(UnitedIcons.GasCylinder, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(22.dp))
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Linked Consumer: Arunjyoti Changkakoty (DBTL Active)", fontSize = 11.sp, color = Color(0xFF10B981))
                        }
                    }
                }

                // 3. Bill & Subsidy Details
                lpgDetails?.let { d ->
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = UnitedWhite,
                            border = BorderStroke(1.dp, UnitedBorderLight),
                            shadowElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Refill Booking Summary", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                Spacer(modifier = Modifier.height(12.dp))

                                ReceiptRow("Cylinder Size", d.cylinderSize)
                                ReceiptRow("Refill Price", d.formattedPrice, isBold = true, valueColor = UnitedMoneyBlue)
                                ReceiptRow("DBTL Bank Subsidy", d.formattedSubsidy, isBold = true, valueColor = Color(0xFF10B981))
                                ReceiptRow("Delivery Location", "Guwahati, Assam - 781024")

                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Subsidy is auto-credited to your Aadhaar-linked SBI Account within 48 hours of delivery.", fontSize = 11.sp, color = UnitedTextSecondary)
                            }
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
