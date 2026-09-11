package com.unitedpay.feature.home.services.utilities

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedEmptyState
import com.unitedpay.core.model.BbpsBillSummary
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager
import com.unitedpay.core.designsystem.components.UnitedNpciMpinModalSheet
import com.unitedpay.core.designsystem.components.UnitedPaymentProcessingDialog
import com.unitedpay.core.designsystem.components.UnitedPinnedBottomBar
import com.unitedpay.core.designsystem.components.UnitedPrimaryButton
import com.unitedpay.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MunicipalTaxScreen(onBackClick: () -> Unit) {
    val initialTax = UserSessionManager.getCurrentMunicipalTax()
    var assessmentNo by remember { mutableStateOf(initialTax?.consumerNumber ?: "") }
    var selectedCorp by remember { mutableStateOf(initialTax?.billerName ?: "") }
    var showProcessing by remember { mutableStateOf(false) }
    var showMpinSheet by remember { mutableStateOf(false) }
    var corporations by remember { mutableStateOf(com.unitedpay.core.model.mock.UnitedMockData.municipalTaxCorporations) }
    var taxSummary by remember { mutableStateOf<BbpsBillSummary?>(initialTax) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.bbps.getMunicipalTaxCorporations().collect { res ->
            if (res is Resource.Success) corporations = res.data
        }
    }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.bbps.getMunicipalTax().collect { res ->
            if (res is Resource.Success) {
                taxSummary = res.data
                selectedCorp = res.data.billerName
                assessmentNo = res.data.consumerNumber
            }
        }
    }

    val billAmount = taxSummary?.formattedAmount ?: "₹3,450.00"

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Municipal Property Tax", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
            if (taxSummary != null) {
                UnitedPinnedBottomBar {
                    UnitedPrimaryButton(
                        text = "Pay Tax $billAmount Now",
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
            if (taxSummary == null) {
                item {
                    UnitedEmptyState(
                        title = "No Pending Municipal Tax",
                        subtitle = "Great! There are no unpaid municipal property tax assessments associated with this account.",
                        icon = Icons.Default.CheckCircle
                    )
                }
            } else {
                item {
                    Text("Select Municipal Corporation", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UnitedTextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        corporations.forEach { corp ->
                            val isSelected = selectedCorp == corp
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCorp = corp },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = UnitedWhite)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            1.5.dp,
                                            if (isSelected) UnitedMoneyBlue else UnitedBorderLight,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(corp, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = UnitedTextPrimary)
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Property Assessment / Ward Number", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UnitedTextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = assessmentNo,
                        onValueChange = { assessmentNo = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UnitedMoneyBlue,
                            unfocusedBorderColor = UnitedBorderLight,
                            focusedTextColor = UnitedTextPrimary,
                            unfocusedTextColor = UnitedTextPrimary,
                            cursorColor = UnitedMoneyBlue
                        )
                    )
                }

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
                                Text("FY 2026-27 Assessment Tax", fontSize = 12.sp, color = UnitedTextSecondary)
                                Text("Rebate of 5% included for early payment", fontSize = 11.sp, color = UnitedSuccess)
                            }
                            Text(billAmount, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = UnitedMoneyBlue)
                        }
                    }
                }
            }
        }

        if (showMpinSheet) {
            UnitedNpciMpinModalSheet(
                subtitle = "Paying Property Tax ₹3,450.00 for $assessmentNo ($selectedCorp)",
                onDismissRequest = { showMpinSheet = false },
                onPinSubmitted = {
                    showMpinSheet = false
                    showProcessing = true
                }
            )
        }

        if (showProcessing) {
            val activeBank = UserSessionManager.getCurrentBankAccounts().firstOrNull { it.isPrimary } ?: UserSessionManager.getCurrentBankAccounts().firstOrNull()
            val bankTitle = activeBank?.bankName ?: "State Bank of India"
            val maskedAcc = activeBank?.accountNumberMasked ?: "•••• 4821"

            UnitedPaymentProcessingDialog(
                amount = billAmount,
                recipientName = selectedCorp,
                recipientSubtitle = "Assessment No: $assessmentNo",
                bankName = bankTitle,
                accountMasked = maskedAcc,
                transactionCategory = "Municipal Property Tax",
                onPaymentCompleted = { utr ->
                    val amtClean = billAmount.replace("₹", "").replace(",", "").trim()
                    val amtVal = amtClean.toDoubleOrNull() ?: 3450.0
                    com.unitedpay.core.model.TransactionRepository.addTransaction(
                        com.unitedpay.core.model.UpiTransaction(
                            id = java.util.UUID.randomUUID().toString(),
                            utrNumber = utr,
                            payeeName = selectedCorp,
                            payeeVpa = "bbps.tax@gov.in",
                            amount = amtVal,
                            timestamp = System.currentTimeMillis(),
                            status = com.unitedpay.core.model.PaymentStatus.SUCCESS,
                            type = com.unitedpay.core.model.TransactionType.DEBIT,
                            bankName = bankTitle,
                            bankAccountNumberMasked = maskedAcc,
                            note = "Property Tax for $assessmentNo"
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
