package com.unitedpay.feature.home.services.banking

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Security
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
 * HookoluPay Aadhaar Enabled Payment System (AEPS) Screen.
 * Fully compliant with NPCI AEPS specifications, zero-emoji policy,
 * and high-fidelity iOS frosted glassmorphism styling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AepsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedType by remember { mutableStateOf(AepsTransactionType.CASH_WITHDRAWAL) }
    var aadhaarNumber by remember { mutableStateOf("482190124912") }
    var selectedBankIin by remember { mutableStateOf("607094") } // Default SBI
    var withdrawalAmount by remember { mutableStateOf("2000") }

    var isCapturingBiometric by remember { mutableStateOf(false) }
    var biometricCaptured by remember { mutableStateOf(false) }
    var captureQuality by remember { mutableIntStateOf(0) }

    var isProcessing by remember { mutableStateOf(false) }
    var transactionReceipt by remember { mutableStateOf<AepsTransactionReceipt?>(null) }
    var miniStatementEntries by remember { mutableStateOf<List<AepsStatementEntry>>(emptyList()) }

    val supportedBanks = remember { HookoluServices.aeps }
    var banksList by remember { mutableStateOf<List<AepsBank>>(emptyList()) }
    var rdDevices by remember { mutableStateOf<List<RdServiceDevice>>(emptyList()) }

    LaunchedEffect(Unit) {
        banksList = HookoluServices.aeps.getSupportedBanks()
        rdDevices = HookoluServices.aeps.getRdServiceDevices()
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
                                text = "Hookolu AEPS Banking",
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Aadhaar Enabled Payment System • NPCI",
                                fontSize = 11.sp,
                                color = UnitedTextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = UnitedTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = UnitedWhite)
            )
        },
        bottomBar = {
            if (transactionReceipt == null) {
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
                                    isProcessing = true
                                    if (selectedType == AepsTransactionType.MINI_STATEMENT) {
                                        miniStatementEntries = HookoluServices.aeps.getMiniStatement(aadhaarNumber, selectedBankIin)
                                    }
                                    val receipt = HookoluServices.aeps.performAepsTransaction(
                                        type = selectedType,
                                        aadhaar = aadhaarNumber,
                                        bankIin = selectedBankIin,
                                        amount = withdrawalAmount.toDoubleOrNull() ?: 0.0
                                    )
                                    transactionReceipt = receipt
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
                            enabled = aadhaarNumber.length == 12 && biometricCaptured && !isProcessing
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = UnitedWhite,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Connecting NPCI AEPS Gateway...", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            } else {
                                val buttonLabel = when (selectedType) {
                                    AepsTransactionType.CASH_WITHDRAWAL -> "Authorize Cash Out ₹$withdrawalAmount"
                                    AepsTransactionType.BALANCE_ENQUIRY -> "Enquire Aadhaar Balance"
                                    AepsTransactionType.MINI_STATEMENT -> "Fetch 5 Mini-Statement Entries"
                                    AepsTransactionType.AADHAAR_PAY -> "Merchant Aadhaar Pay ₹$withdrawalAmount"
                                }
                                Text(buttonLabel, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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

            // 1. Transaction Mode Selector Tabs
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
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AepsTransactionType.entries.forEach { type ->
                            val isSelected = selectedType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) UnitedMoneyBlue else Color.Transparent)
                                    .clickable {
                                        selectedType = type
                                        transactionReceipt = null
                                    }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type.title.replace(" ", "\n"),
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

            // Success Receipt Display
            if (transactionReceipt != null) {
                item {
                    val receipt = transactionReceipt!!
                    val selectedBankName = banksList.firstOrNull { it.iin == selectedBankIin }?.bankName ?: "Selected Bank"

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
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "AEPS Transaction Successful",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary
                            )
                            Text(
                                text = receipt.txnType.title + " • NPCI Auth Code: " + receipt.stan,
                                fontSize = 12.sp,
                                color = UnitedTextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = UnitedBorderLight)
                            Spacer(modifier = Modifier.height(16.dp))

                            // Breakdown rows
                            ReceiptRow("Customer Name", receipt.customerName)
                            ReceiptRow("Aadhaar Number", receipt.aadhaarMasked)
                            ReceiptRow("Bank Name", receipt.bankName)
                            ReceiptRow("RRN / Stan", "${receipt.rrn} / ${receipt.stan}")
                            ReceiptRow("BC Terminal ID", receipt.terminalId)

                            if (receipt.txnType == AepsTransactionType.CASH_WITHDRAWAL || receipt.txnType == AepsTransactionType.AADHAAR_PAY) {
                                ReceiptRow("Dispensed Amount", receipt.formattedAmount, isBold = true, valueColor = UnitedMoneyBlue)
                            }
                            ReceiptRow("Ledger Balance", receipt.formattedBalance, isBold = true, valueColor = Color(0xFF10B981))

                            // Mini-Statement entries if fetched
                            if (miniStatementEntries.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Recent Account Statement (Last 5 Txns)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UnitedTextPrimary,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                miniStatementEntries.forEach { entry ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        color = UnitedCanvasLight,
                                        border = BorderStroke(0.5.dp, UnitedBorderLight)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(entry.narration, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextPrimary)
                                                Text(entry.date + " • " + entry.formattedBalance, fontSize = 10.sp, color = UnitedTextSecondary)
                                            }
                                            Text(
                                                entry.formattedAmount,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (entry.isCredit) Color(0xFF10B981) else Color(0xFFEF4444)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Share Receipt Button
                            OutlinedButton(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        ReceiptShareHelper.shareTransactionReceipt(
                                            context = activity,
                                            receiptDetails = ReceiptShareHelper.ReceiptDetails(
                                                title = "HookoluPay AEPS Receipt",
                                                amount = if (receipt.amount > 0) receipt.formattedAmount else receipt.formattedBalance,
                                                sender = receipt.customerName,
                                                receiver = "${receipt.bankName} (AEPS)",
                                                utr = receipt.rrn,
                                                date = "13 Sep 2026",
                                                time = "10:30 PM",
                                                status = "SUCCESSFUL",
                                                bankName = receipt.bankName,
                                                paymentMode = "AEPS Biometric",
                                                note = "Aadhaar: ${receipt.aadhaarMasked}"
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
                // 2. Aadhaar Number & Masked Display Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = UnitedWhite,
                        border = BorderStroke(1.dp, UnitedBorderLight),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Customer Aadhaar Number",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UnitedTextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "UIDAI Registered",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = aadhaarNumber,
                                onValueChange = { if (it.length <= 12 && it.all { char -> char.isDigit() }) aadhaarNumber = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                placeholder = { Text("Enter 12-digit Aadhaar Number") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = UnitedMoneyBlue
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = UnitedMoneyBlue,
                                    unfocusedBorderColor = UnitedBorderLight
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Customer Name: Arunjyoti Changkakoty (Pre-bound)",
                                fontSize = 11.sp,
                                color = UnitedTextSecondary
                            )
                        }
                    }
                }

                // 3. Bank IIN Selector
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
                                text = "Select Customer Bank (IIN)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(banksList) { bank ->
                                    val isSelected = selectedBankIin == bank.iin
                                    Surface(
                                        modifier = Modifier
                                            .width(168.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable { selectedBankIin = bank.iin },
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                                        border = BorderStroke(
                                            if (isSelected) 1.5.dp else 0.5.dp,
                                            if (isSelected) UnitedMoneyBlue else UnitedBorderLight
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isSelected) UnitedMoneyBlue else Color(0xFFE2E8F0)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = bank.bankName.take(2).uppercase(),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = if (isSelected) UnitedWhite else UnitedTextSecondary
                                                    )
                                                }
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = UnitedMoneyBlue,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = bank.bankName,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) UnitedMoneyBlue else UnitedTextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "IIN: ${bank.iin}",
                                                fontSize = 10.sp,
                                                color = UnitedTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Amount Input (if Cash Withdrawal or Aadhaar Pay)
                if (selectedType == AepsTransactionType.CASH_WITHDRAWAL || selectedType == AepsTransactionType.AADHAAR_PAY) {
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
                                    text = "Withdrawal Amount (₹)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UnitedTextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = withdrawalAmount,
                                    onValueChange = { if (it.all { char -> char.isDigit() }) withdrawalAmount = it },
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
                                    items(listOf("500", "1000", "2000", "5000", "10000")) { preset ->
                                        val isSelected = withdrawalAmount == preset
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable { withdrawalAmount = preset },
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

                // 5. Biometric RD Service Capture Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = UnitedWhite,
                        border = BorderStroke(
                            if (biometricCaptured) 1.5.dp else 1.dp,
                            if (biometricCaptured) Color(0xFF10B981) else UnitedBorderLight
                        ),
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Biometric RD Service",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Mantra MFS100 Optical Scanner",
                                        fontSize = 11.sp,
                                        color = UnitedTextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (biometricCaptured) Color(0xFFDCFCE7) else Color(0xFFEFF6FF)
                                ) {
                                    Text(
                                        text = if (biometricCaptured) "Captured ($captureQuality%)" else "Scanner Ready",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (biometricCaptured) Color(0xFF15803D) else UnitedMoneyBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(if (biometricCaptured) Color(0xFFDCFCE7) else UnitedCanvasLight)
                                    .border(
                                        2.dp,
                                        if (biometricCaptured) Color(0xFF10B981) else UnitedMoneyBlue.copy(alpha = 0.4f),
                                        CircleShape
                                    )
                                    .clickable {
                                        isCapturingBiometric = true
                                        coroutineScope.launch {
                                            kotlinx.coroutines.delay(800)
                                            captureQuality = 92
                                            biometricCaptured = true
                                            isCapturingBiometric = false
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCapturingBiometric) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(36.dp),
                                        color = UnitedMoneyBlue,
                                        strokeWidth = 3.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = UnitedIcons.AepsFingerprint,
                                        contentDescription = "Capture Biometric",
                                        tint = if (biometricCaptured) Color(0xFF10B981) else UnitedMoneyBlue,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (biometricCaptured) "Fingerprint Verified • Quality: $captureQuality% (UIDAI SLA Passed)" else "Tap sensor above to scan customer thumb",
                                fontSize = 12.sp,
                                fontWeight = if (biometricCaptured) FontWeight.Bold else FontWeight.Normal,
                                color = if (biometricCaptured) Color(0xFF15803D) else UnitedTextSecondary
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
