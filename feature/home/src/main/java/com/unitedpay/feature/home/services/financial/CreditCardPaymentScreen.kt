package com.unitedpay.feature.home.services.financial

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
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedEmptyState
import com.unitedpay.core.designsystem.components.UnitedNpciMpinModalSheet
import com.unitedpay.core.designsystem.components.UnitedPaymentProcessingDialog
import com.unitedpay.core.designsystem.components.UnitedPinnedBottomBar
import com.unitedpay.core.designsystem.components.UnitedPrimaryButton
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.CreditCardStatement
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditCardScreen(onBackClick: () -> Unit) = CreditCardPaymentScreen(onBackClick)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditCardPaymentScreen(onBackClick: () -> Unit) {
    var cardStatement by remember { mutableStateOf(UserSessionManager.getCurrentCreditCardStatement()) }
    var payAmount by remember { mutableStateOf(cardStatement?.totalAmountDue?.toInt()?.toString() ?: "") }
    var showMpinSheet by remember { mutableStateOf(false) }
    var showProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.financial.getCreditCardSummary().collect { res ->
            if (res is Resource.Success) {
                cardStatement = res.data
                payAmount = res.data.totalAmountDue.toInt().toString()
            } else if (res is Resource.Error) {
                cardStatement = null
            }
        }
    }

    val statement = cardStatement

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Credit Card Bill", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
            if (statement != null && payAmount.isNotBlank()) {
                UnitedPinnedBottomBar {
                    UnitedPrimaryButton(
                        text = "Pay Bill ₹$payAmount",
                        onClick = { showMpinSheet = true }
                    )
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
            if (statement == null) {
                UnitedEmptyState(
                    title = "No Credit Card Linked",
                    subtitle = "Link your RuPay or Visa credit card to pay bills seamlessly via UPI.",
                    icon = Icons.Default.CreditCard
                )
            } else {
                // Credit Card Visual Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(statement.cardType, color = UnitedWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(statement.bankName, color = Color(0xFF93C5FD), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(statement.cardNumberMasked, color = UnitedWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp, letterSpacing = 2.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("CARD HOLDER", color = Color(0xFF93C5FD), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(statement.cardHolderName, color = UnitedWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text("EXPIRES", color = Color(0xFF93C5FD), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(statement.expiry, color = UnitedWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Outstanding Due Summary Card
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
                        Text("TOTAL AMOUNT DUE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("₹${statement.totalAmountDue.toInt()}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = UnitedTextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Due Date: ${statement.dueDate}", fontSize = 12.sp, color = UnitedError, fontWeight = FontWeight.SemiBold)
                            Text("Min Due: ₹${statement.minimumAmountDue.toInt()}", fontSize = 12.sp, color = UnitedTextSecondary)
                        }
                    }
                }

                // Payment Options Selector
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
                        Text("Select Payment Option", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                        Spacer(modifier = Modifier.height(10.dp))

                        val totalDueStr = statement.totalAmountDue.toInt().toString()
                        val minDueStr = statement.minimumAmountDue.toInt().toString()

                        listOf(
                            Pair("Total Amount Due", totalDueStr),
                            Pair("Minimum Amount Due", minDueStr)
                        ).forEach { (label, amt) ->
                            val isSelected = payAmount == amt
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { payAmount = amt }
                                    .border(1.dp, if (isSelected) UnitedMoneyBlue else UnitedBorderLight, RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(label, fontSize = 13.sp, color = UnitedTextPrimary)
                                Text("₹$amt", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isSelected) UnitedMoneyBlue else UnitedTextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = payAmount,
                            onValueChange = { payAmount = it },
                            label = { Text("Custom Amount") },
                            leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = UnitedMoneyBlue, modifier = Modifier.padding(start = 12.dp)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = TextStyle(color = UnitedTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        )
                    }
                }
            }
        }

        if (showMpinSheet && statement != null) {
            UnitedNpciMpinModalSheet(
                subtitle = "Paying ${statement.bankName} Credit Card ₹$payAmount",
                onDismissRequest = { showMpinSheet = false },
                onPinSubmitted = {
                    showMpinSheet = false
                    showProcessing = true
                }
            )
        }

        if (showProcessing && statement != null) {
            val activeBank = UserSessionManager.getCurrentBankAccounts().firstOrNull()
            val activeProfile = UserSessionManager.getCurrentProfile()
            val activeBankName = activeBank?.bankName ?: "State Bank of India"
            val activeBankMasked = activeBank?.accountNumberMasked ?: "•••• 9821"

            UnitedPaymentProcessingDialog(
                amount = "₹$payAmount",
                recipientName = "${statement.bankName} ${statement.cardType}",
                recipientSubtitle = "Card: ${statement.cardNumberMasked}",
                bankName = activeBankName,
                accountMasked = activeBankMasked,
                transactionCategory = "Credit Card Bill",
                onPaymentCompleted = { utr ->
                    val amtVal = payAmount.toDoubleOrNull() ?: 18450.0
                    com.unitedpay.core.model.TransactionRepository.addTransaction(
                        com.unitedpay.core.model.UpiTransaction(
                            id = java.util.UUID.randomUUID().toString(),
                            utrNumber = utr,
                            payeeName = "${statement.bankName} Credit Card",
                            payeeVpa = "creditcard.hdfc@axisbank",
                            payerName = activeProfile.fullName,
                            payerVpa = activeProfile.primaryVpa,
                            amount = amtVal,
                            timestamp = System.currentTimeMillis(),
                            status = com.unitedpay.core.model.PaymentStatus.SUCCESS,
                            type = com.unitedpay.core.model.TransactionType.DEBIT,
                            bankName = activeBankName,
                            bankAccountNumberMasked = activeBankMasked,
                            note = "Bill payment for ${statement.cardNumberMasked}"
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
