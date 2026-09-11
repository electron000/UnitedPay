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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedCenteredAmountCard
import com.unitedpay.core.designsystem.components.UnitedEmptyState
import com.unitedpay.core.designsystem.components.UnitedNpciMpinModalSheet
import com.unitedpay.core.designsystem.components.UnitedPaymentProcessingDialog
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.BankAccount
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfTransferScreen(onBackClick: () -> Unit) {
    var accounts by remember { mutableStateOf(UserSessionManager.getCurrentBankAccounts()) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.banking.getLinkedBankAccounts().collect { result ->
            if (result is Resource.Success) {
                accounts = result.data
            }
        }
    }

    val fromAccount = accounts.firstOrNull { it.isPrimary } ?: accounts.firstOrNull()
    val targetAccounts = accounts.filter { it != fromAccount }
    val initialTargetLabel = targetAccounts.firstOrNull()?.let { "${it.bankName} (${it.accountNumberMasked})" } ?: ""
    var selectedTarget by remember { mutableStateOf(initialTargetLabel) }
    var amount by remember { mutableStateOf("1500") }
    var showProcessing by remember { mutableStateOf(false) }
    var showMpinSheet by remember { mutableStateOf(false) }
    val hasMultipleAccounts = accounts.size >= 2

    LaunchedEffect(targetAccounts) {
        if (selectedTarget.isBlank() && targetAccounts.isNotEmpty()) {
            selectedTarget = "${targetAccounts.first().bankName} (${targetAccounts.first().accountNumberMasked})"
        }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Transfer to Self Account", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
            if (hasMultipleAccounts) {
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
                            enabled = amount.isNotBlank() && selectedTarget.isNotBlank()
                        ) {
                            Text(
                                text = "Transfer ₹$amount to Own Account",
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
            if (!hasMultipleAccounts) {
                UnitedEmptyState(
                    icon = Icons.Default.AccountBalance,
                    title = "Only 1 Account Linked",
                    subtitle = "Self Transfer requires at least two bank accounts linked to your UPI profile. Add another account to transfer funds between them.",
                    actionText = "Go Back",
                    onActionClick = onBackClick
                )
            } else {
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
                        Text("FROM ACCOUNT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${fromAccount?.bankName ?: "Primary Bank"} (${fromAccount?.accountNumberMasked ?: "•••• 0000"})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = UnitedTextPrimary)
                        Text("Available Balance: ₹ ${String.format("%.2f", fromAccount?.balance ?: 0.0)}", fontSize = 12.sp, color = UnitedMoneyBlue)

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("TO YOUR OWN ACCOUNT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        targetAccounts.forEach { targetAcc ->
                            val target = "${targetAcc.bankName} (${targetAcc.accountNumberMasked})"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedTarget == target) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                                    .border(1.dp, if (selectedTarget == target) UnitedMoneyBlue else UnitedBorderLight, RoundedCornerShape(8.dp))
                                    .clickable { selectedTarget = target }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(target, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = UnitedTextPrimary)
                                if (selectedTarget == target) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                UnitedCenteredAmountCard(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "TRANSFER AMOUNT",
                    maxDigits = 6
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showMpinSheet) {
            UnitedNpciMpinModalSheet(
                subtitle = "Transferring ₹$amount to $selectedTarget",
                onDismissRequest = { showMpinSheet = false },
                onPinSubmitted = {
                    showMpinSheet = false
                    showProcessing = true
                }
            )
        }

        if (showProcessing) {
            UnitedPaymentProcessingDialog(
                amount = amount,
                recipientName = "Self Transfer",
                recipientSubtitle = selectedTarget,
                bankName = fromAccount?.bankName ?: "Primary Bank",
                accountMasked = fromAccount?.accountNumberMasked ?: "•••• 0000",
                transactionCategory = "Self Account Transfer",
                onPaymentCompleted = { utr ->
                    val amtVal = amount.toDoubleOrNull() ?: 0.0
                    com.unitedpay.core.model.TransactionRepository.addTransaction(
                        com.unitedpay.core.model.UpiTransaction(
                            id = java.util.UUID.randomUUID().toString(),
                            utrNumber = utr,
                            payeeName = "Self Transfer ($selectedTarget)",
                            payeeVpa = "self@unitedpay",
                            amount = amtVal,
                            timestamp = System.currentTimeMillis(),
                            status = com.unitedpay.core.model.PaymentStatus.SUCCESS,
                            type = com.unitedpay.core.model.TransactionType.DEBIT,
                            bankName = fromAccount?.bankName ?: "Primary Bank",
                            bankAccountNumberMasked = fromAccount?.accountNumberMasked ?: "•••• 0000",
                            note = "Transfer to $selectedTarget"
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
