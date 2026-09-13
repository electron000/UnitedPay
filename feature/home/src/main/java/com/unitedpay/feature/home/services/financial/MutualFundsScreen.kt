package com.unitedpay.feature.home.services.financial

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedNpciMpinModalSheet
import com.unitedpay.core.designsystem.components.UnitedPaymentProcessingDialog
import com.unitedpay.core.model.MutualFund
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MutualFundsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var funds by remember { mutableStateOf<List<MutualFund>>(emptyList()) }
    var selectedFund by remember { mutableStateOf<MutualFund?>(null) }
    var showMpinSheet by remember { mutableStateOf(false) }
    var showProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.financial.getMutualFunds().collect { result ->
            if (result is Resource.Success) {
                funds = result.data
            }
        }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Mutual Funds & SIP", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SIP Discovery Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F766E))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text("BUILD LONG-TERM WEALTH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCCFBF1), letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Start a Mutual Fund SIP with ₹500/month", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = UnitedWhite)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Zero commission direct plans with SEBI registered AMCs", fontSize = 11.5.sp, color = Color(0xFFCCFBF1).copy(alpha = 0.85f))
                    }
                }
            }

            item {
                Text("Curated High-Growth Funds", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = UnitedTextPrimary)
            }

            items(funds) { fund ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(fund.name, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = UnitedTextPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(fund.category, fontSize = 11.5.sp, color = UnitedTextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(fund.threeYearReturn, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = UnitedSuccess)
                                Text("3Y CAGR", fontSize = 10.sp, color = UnitedTextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Min SIP Amount", fontSize = 10.5.sp, color = UnitedTextSecondary)
                                Text(fund.minSip, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = UnitedTextPrimary)
                            }

                            Button(
                                onClick = {
                                    selectedFund = fund
                                    showMpinSheet = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue, contentColor = UnitedWhite)
                            ) {
                                Text(
                                    "Invest Now",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UnitedWhite,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showMpinSheet && selectedFund != null) {
            val fund = selectedFund!!
            UnitedNpciMpinModalSheet(
                subtitle = "Start SIP of ${fund.minSip} in ${fund.name}",
                onDismissRequest = { showMpinSheet = false },
                onPinSubmitted = {
                    showMpinSheet = false
                    showProcessing = true
                }
            )
        }

        if (showProcessing && selectedFund != null) {
            val fund = selectedFund!!
            val activeBank = com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull { it.isPrimary }
                ?: com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull()
            val bankTitle = activeBank?.bankName ?: "State Bank of India"
            val maskedAcc = activeBank?.accountNumberMasked ?: "•••• 4821"

            UnitedPaymentProcessingDialog(
                amount = fund.minSip,
                recipientName = fund.name,
                recipientSubtitle = "${fund.category} • Direct Growth Plan",
                bankName = bankTitle,
                accountMasked = maskedAcc,
                transactionCategory = "Mutual Fund SIP",
                onPaymentCompleted = { utr ->
                    val cleanAmt = fund.minSip.replace("₹", "").substringBefore("/").replace(",", "").trim()
                    val amtVal = cleanAmt.toDoubleOrNull() ?: 500.0
                    com.unitedpay.core.model.TransactionRepository.addTransaction(
                        com.unitedpay.core.model.UpiTransaction(
                            id = java.util.UUID.randomUUID().toString(),
                            utrNumber = utr,
                            payeeName = fund.name,
                            payeeVpa = "mf.sip@bse.npci",
                            amount = amtVal,
                            timestamp = System.currentTimeMillis(),
                            status = com.unitedpay.core.model.PaymentStatus.SUCCESS,
                            type = com.unitedpay.core.model.TransactionType.DEBIT,
                            bankName = bankTitle,
                            bankAccountNumberMasked = maskedAcc,
                            note = "SIP installment for ${fund.name}"
                        )
                    )
                },
                onDone = {
                    showProcessing = false
                    selectedFund = null
                    onBackClick()
                }
            )
        }
    }
}
