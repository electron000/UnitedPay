package com.unitedpay.feature.home.services.transfers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedCenteredAmountField
import com.unitedpay.core.designsystem.components.UnitedNpciMpinModalSheet
import com.unitedpay.core.designsystem.components.UnitedPaymentProcessingDialog
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.mock.UnitedMockData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMoneyScreen(onBackClick: () -> Unit) {
    var liteBalance by remember { mutableStateOf(UnitedMockData.upiLiteBalance) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.banking.getUpiLiteBalance().collect { res ->
            if (res is Resource.Success) {
                liteBalance = res.data
            }
        }
    }

    var addAmount by remember { mutableStateOf("500") }
    var showProcessing by remember { mutableStateOf(false) }
    var showMpinSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Add Money to UPI Lite", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
                        onClick = { showMpinSheet = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .defaultMinSize(minHeight = 52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue, contentColor = UnitedWhite),
                        enabled = addAmount.isNotBlank()
                    ) {
                        Text(
                            text = "Add ₹$addAmount to UPI Lite",
                            fontWeight = FontWeight.Bold,
                            color = UnitedWhite,
                            fontSize = 15.sp,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = UnitedWhite)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                        .padding(18.dp)
                ) {
                    Text("Current UPI Lite Balance", fontSize = 12.sp, color = UnitedTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("₹${String.format("%,.2f", liteBalance)}", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = UnitedMoneyBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Zero-PIN instant payments up to ₹500 per tap", fontSize = 11.sp, color = UnitedTextSecondary)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = UnitedWhite)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AMOUNT TO ADD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextSecondary,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    UnitedCenteredAmountField(
                        value = addAmount,
                        onValueChange = { addAmount = it },
                        maxDigits = 5,
                        placeholder = "0"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("200", "500", "1000", "2000").forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (addAmount == preset) Color(0xFFEFF6FF) else Color(0xFFF1F5F9))
                                    .border(1.dp, if (addAmount == preset) UnitedMoneyBlue else Color.Transparent, RoundedCornerShape(6.dp))
                                    .clickable { addAmount = preset }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    "+₹$preset",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = if (addAmount == preset) UnitedMoneyBlue else UnitedTextPrimary,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    val activeBank = com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull { it.isPrimary }
                        ?: com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull()
                    val bankTitle = activeBank?.bankName ?: "State Bank of India"
                    val maskedAcc = activeBank?.accountNumberMasked ?: "•••• 4821"
                    Text("From: $bankTitle ($maskedAcc)", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showMpinSheet) {
            UnitedNpciMpinModalSheet(
                subtitle = "Adding ₹$addAmount to UPI Lite",
                onDismissRequest = { showMpinSheet = false },
                onPinSubmitted = {
                    showMpinSheet = false
                    showProcessing = true
                }
            )
        }

        if (showProcessing) {
            val activeBank = com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull { it.isPrimary }
                ?: com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull()
            val bankTitle = activeBank?.bankName ?: "State Bank of India"
            val maskedAcc = activeBank?.accountNumberMasked ?: "•••• 4821"

            UnitedPaymentProcessingDialog(
                amount = addAmount,
                recipientName = "UPI Lite Wallet",
                recipientSubtitle = "Top-up from $bankTitle ($maskedAcc)",
                bankName = bankTitle,
                accountMasked = maskedAcc,
                transactionCategory = "UPI Lite Top-Up",
                onPaymentCompleted = { utr ->
                    val amtVal = addAmount.toDoubleOrNull() ?: 0.0
                    com.unitedpay.core.model.TransactionRepository.addTransaction(
                        com.unitedpay.core.model.UpiTransaction(
                            id = java.util.UUID.randomUUID().toString(),
                            utrNumber = utr,
                            payeeName = "UPI Lite Top-Up",
                            payeeVpa = "lite@unitedpay",
                            amount = amtVal,
                            timestamp = System.currentTimeMillis(),
                            status = com.unitedpay.core.model.PaymentStatus.SUCCESS,
                            type = com.unitedpay.core.model.TransactionType.DEBIT,
                            bankName = bankTitle,
                            bankAccountNumberMasked = maskedAcc,
                            note = "Added to UPI Lite"
                        )
                    )
                },
                onDone = {
                    showProcessing = false
                    onBackClick()
                }
            )
        }
    }
}
