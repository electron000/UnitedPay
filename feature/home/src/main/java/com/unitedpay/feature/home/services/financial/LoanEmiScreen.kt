package com.unitedpay.feature.home.services.financial

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedEmptyState
import com.unitedpay.core.designsystem.components.UnitedNpciMpinModalSheet
import com.unitedpay.core.designsystem.components.UnitedPaymentProcessingDialog
import com.unitedpay.core.designsystem.components.UnitedPinnedBottomBar
import com.unitedpay.core.designsystem.components.UnitedPrimaryButton
import com.unitedpay.core.designsystem.theme.UnitedBorderLight
import com.unitedpay.core.designsystem.theme.UnitedCanvasLight
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedSuccess
import com.unitedpay.core.designsystem.theme.UnitedSurfaceSubtle
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite
import com.unitedpay.core.model.LoanEmiSummary
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanEmiScreen(onBackClick: () -> Unit) {
    val initialSummary = UserSessionManager.getCurrentLoanEmiSummary()
    var selectedLender by remember { mutableStateOf(initialSummary?.lenderName ?: "") }
    var loanAccountNo by remember { mutableStateOf(initialSummary?.loanAccountNo ?: "") }
    var lenders by remember { mutableStateOf(com.unitedpay.core.model.mock.UnitedMockData.loanLenders) }
    var loanSummary by remember { mutableStateOf<LoanEmiSummary?>(initialSummary) }
    var showProcessing by remember { mutableStateOf(false) }
    var showMpinSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.financial.getLoanLenders().collect { res ->
            if (res is Resource.Success) lenders = res.data
        }
    }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.financial.getLoanEmiDetails().collect { res ->
            if (res is Resource.Success) {
                loanSummary = res.data
                loanAccountNo = res.data.loanAccountNo
                selectedLender = res.data.lenderName
            } else if (res is Resource.Error) {
                loanSummary = null
            }
        }
    }

    val summary = loanSummary

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Loan EMI Repayment", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
            if (summary != null) {
                UnitedPinnedBottomBar {
                    UnitedPrimaryButton(
                        text = "Pay EMI ₹${summary.emiAmount.toInt()}.00",
                        onClick = { showMpinSheet = true }
                    )
                }
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
            if (summary == null) {
                item {
                    UnitedEmptyState(
                        title = "No Active Loans Found",
                        subtitle = "No outstanding loan EMIs are linked to your registered mobile number.",
                        icon = Icons.Default.AccountBalance
                    )
                }
            }

            item {
                Text("LENDER / NBFC & LOAN NUMBER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
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
                            .padding(16.dp)
                    ) {
                        Text("Select Loan Provider / NBFC", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        lenders.forEach { lender ->
                            val isSelected = selectedLender == lender
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                                    .border(1.dp, if (isSelected) UnitedMoneyBlue else UnitedBorderLight, RoundedCornerShape(8.dp))
                                    .clickable { selectedLender = lender }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = lender,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) UnitedMoneyBlue else UnitedTextPrimary
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = UnitedMoneyBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = loanAccountNo,
                            onValueChange = { loanAccountNo = it },
                            singleLine = true,
                            label = { Text("Loan Account Number / LAN", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = UnitedMoneyBlue) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = TextStyle(color = UnitedTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = UnitedTextPrimary,
                                unfocusedTextColor = UnitedTextPrimary,
                                focusedBorderColor = UnitedMoneyBlue,
                                unfocusedBorderColor = UnitedBorderLight,
                                focusedContainerColor = UnitedSurfaceSubtle,
                                unfocusedContainerColor = UnitedSurfaceSubtle
                            )
                        )
                    }
                }

                if (summary != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Borrower Name", fontSize = 12.sp, color = UnitedTextSecondary)
                                Text(summary.customerName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("EMI Due Date", fontSize = 12.sp, color = UnitedTextSecondary)
                                Text(summary.dueDate, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextPrimary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Monthly EMI", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                Text("₹${summary.emiAmount.toInt()}.00", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = UnitedMoneyBlue)
                            }
                        }
                    }
                }
            }
        }

        if (showMpinSheet && summary != null) {
            UnitedNpciMpinModalSheet(
                subtitle = "Paying EMI ₹${summary.emiAmount.toInt()}.00 to $selectedLender ($loanAccountNo)",
                onDismissRequest = { showMpinSheet = false },
                onPinSubmitted = {
                    showMpinSheet = false
                    showProcessing = true
                }
            )
        }

        if (showProcessing && summary != null) {
            val activeBank = UserSessionManager.getCurrentBankAccounts().firstOrNull()
            val activeProfile = UserSessionManager.getCurrentProfile()
            val activeBankName = activeBank?.bankName ?: "State Bank of India"
            val activeBankMasked = activeBank?.accountNumberMasked ?: "•••• 9821"

            UnitedPaymentProcessingDialog(
                amount = "₹${summary.emiAmount.toInt()}.00",
                recipientName = selectedLender,
                recipientSubtitle = "Loan A/C: $loanAccountNo",
                bankName = activeBankName,
                accountMasked = activeBankMasked,
                transactionCategory = "Loan EMI",
                onPaymentCompleted = { utr ->
                    com.unitedpay.core.model.TransactionRepository.addTransaction(
                        com.unitedpay.core.model.UpiTransaction(
                            id = java.util.UUID.randomUUID().toString(),
                            utrNumber = utr,
                            payeeName = selectedLender,
                            payeeVpa = "emi.repay@bajaj.npci",
                            payerName = activeProfile.fullName,
                            payerVpa = activeProfile.primaryVpa,
                            amount = summary.emiAmount,
                            timestamp = System.currentTimeMillis(),
                            status = com.unitedpay.core.model.PaymentStatus.SUCCESS,
                            type = com.unitedpay.core.model.TransactionType.DEBIT,
                            bankName = activeBankName,
                            bankAccountNumberMasked = activeBankMasked,
                            note = "EMI Payment for $loanAccountNo"
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
