package com.unitedpay.feature.home.services.banking

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
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
import androidx.compose.ui.text.style.TextAlign
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
 * HookoluPay Micro-ATM & mPOS Terminal Screen.
 * Enables Bluetooth EMV Chip card cash withdrawal, card sales,
 * and balance enquiry for BC agents and retailers.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MicroAtmScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var terminal by remember { mutableStateOf<MicroAtmTerminal?>(null) }
    var selectedOperation by remember { mutableStateOf(MicroAtmOperation.CASH_WITHDRAWAL) }
    var amountText by remember { mutableStateOf("1500") }

    var isProcessing by remember { mutableStateOf(false) }
    var terminalReceipt by remember { mutableStateOf<MicroAtmReceipt?>(null) }

    LaunchedEffect(Unit) {
        terminal = HookoluServices.microAtm.getTerminalDetails()
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
                                text = "Micro-ATM / mPOS",
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Bluetooth EMV Reader • Cash-out & Sale",
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
            if (terminalReceipt == null) {
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
                                val amt = amountText.toDoubleOrNull() ?: 0.0
                                coroutineScope.launch {
                                    isProcessing = true
                                    val receipt = HookoluServices.microAtm.executeOperation(selectedOperation, amt)
                                    terminalReceipt = receipt
                                    isProcessing = false
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
                            enabled = !isProcessing && (selectedOperation == MicroAtmOperation.BALANCE_ENQUIRY || amountText.isNotBlank())
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = UnitedWhite,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Insert/Swipe Card on mPOS Terminal...", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            } else {
                                val label = when (selectedOperation) {
                                    MicroAtmOperation.CASH_WITHDRAWAL -> "Initiate Cash Withdrawal ₹$amountText"
                                    MicroAtmOperation.BALANCE_ENQUIRY -> "Enquire Debit Card Balance"
                                    MicroAtmOperation.CARD_SALE -> "Charge mPOS Card Sale ₹$amountText"
                                }
                                Text(label, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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

            // 1. Bluetooth Paired Terminal Status Card
            terminal?.let { t ->
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = UnitedWhite,
                        border = BorderStroke(1.dp, UnitedBorderLight),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = UnitedIcons.MicroAtm,
                                    contentDescription = null,
                                    tint = UnitedMoneyBlue,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = t.modelName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.BluetoothConnected,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "Terminal ID: ${t.terminalId} • Battery: ${t.batteryLevel}%",
                                    fontSize = 11.sp,
                                    color = UnitedTextSecondary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = "Online",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Receipt Display
            if (terminalReceipt != null) {
                item {
                    val receipt = terminalReceipt!!
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
                            Text(
                                text = "Micro-ATM Transaction Approved",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary
                            )
                            Text(
                                text = "RRN: ${receipt.rrn} • Auth: 894102",
                                fontSize = 12.sp,
                                color = UnitedTextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = UnitedBorderLight)
                            Spacer(modifier = Modifier.height(16.dp))

                            ReceiptRow("Card Scheme", receipt.cardScheme)
                            ReceiptRow("Card Number", "•••• •••• •••• ${receipt.cardLast4}")
                            ReceiptRow("Cardholder Name", receipt.cardHolderName)
                            ReceiptRow("Terminal ID", receipt.terminalId)
                            ReceiptRow("Operation", receipt.operation.title)
                            if (receipt.amount > 0) {
                                ReceiptRow("Transaction Amount", receipt.formattedAmount, isBold = true, valueColor = UnitedMoneyBlue)
                            }
                            ReceiptRow("Available Balance", receipt.formattedBalance, isBold = true, valueColor = Color(0xFF10B981))

                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedButton(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        ReceiptShareHelper.shareTransactionReceipt(
                                            context = activity,
                                            receiptDetails = ReceiptShareHelper.ReceiptDetails(
                                                title = "HookoluPay Micro-ATM Slip",
                                                amount = if (receipt.amount > 0) receipt.formattedAmount else receipt.formattedBalance,
                                                sender = receipt.cardHolderName,
                                                receiver = "Micro-ATM Cash Dispensation",
                                                utr = receipt.rrn,
                                                date = "13 Sep 2026",
                                                time = "10:30 PM",
                                                status = "SUCCESSFUL",
                                                bankName = "RuPay EMV Interoperable",
                                                paymentMode = "mPOS EMV Chip",
                                                note = "Card: •••• ${receipt.cardLast4} • Terminal: ${receipt.terminalId}"
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
                // 2. Operation Type Selector
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
                            MicroAtmOperation.entries.forEach { op ->
                                val isSelected = selectedOperation == op
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) UnitedMoneyBlue else Color.Transparent)
                                        .clickable {
                                            selectedOperation = op
                                            terminalReceipt = null
                                        }
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = op.title.replace(" ", "\n"),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) UnitedWhite else UnitedTextSecondary,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Amount Input (if Withdrawal or Sale)
                if (selectedOperation != MicroAtmOperation.BALANCE_ENQUIRY) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = UnitedWhite,
                            border = BorderStroke(1.dp, UnitedBorderLight),
                            shadowElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Transaction Amount (₹)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UnitedTextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = amountText,
                                    onValueChange = { if (it.all { char -> char.isDigit() }) amountText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    leadingIcon = {
                                        Text("₹", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = UnitedMoneyBlue)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = UnitedMoneyBlue,
                                        unfocusedBorderColor = UnitedBorderLight
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(listOf("500", "1000", "1500", "2000", "5000")) { preset ->
                                        val isSelected = amountText == preset
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable { amountText = preset },
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                                            border = BorderStroke(
                                                if (isSelected) 1.5.dp else 0.5.dp,
                                                if (isSelected) UnitedMoneyBlue else UnitedBorderLight
                                            )
                                        ) {
                                            Text(
                                                text = "+₹$preset",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) UnitedMoneyBlue else UnitedTextPrimary,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Instructions Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = UnitedCanvasLight,
                        border = BorderStroke(0.5.dp, UnitedBorderLight)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("mPOS EMV Processing Guidelines", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "1. Ensure Hookolu mPOS device is powered on and within Bluetooth range.\n2. Ask customer to insert chip card and enter 4-digit ATM PIN on the hardware keypad.\n3. Cash must be dispensed immediately upon approval receipt display.",
                                fontSize = 11.sp,
                                color = UnitedTextSecondary,
                                lineHeight = 16.sp
                            )
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
