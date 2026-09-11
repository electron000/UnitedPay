package com.unitedpay.feature.home.services.transfers

import com.unitedpay.core.designsystem.components.UnitedToast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedNpciMpinModalSheet
import com.unitedpay.core.designsystem.components.UnitedPaymentProcessingDialog
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalRupeeScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var balance by remember { mutableIntStateOf(2450) }
    var showMpinSheet by remember { mutableStateOf(false) }
    var showProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.banking.getDigitalRupeeWallet().collect { res ->
            if (res is Resource.Success) {
                balance = res.data.balance.toInt()
            }
        }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Digital Rupee (e₹)", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            showMpinSheet = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .defaultMinSize(minHeight = 52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue, contentColor = UnitedWhite)
                    ) {
                        Text(
                            "Load e₹",
                            fontWeight = FontWeight.Bold,
                            color = UnitedWhite,
                            fontSize = 15.sp,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = {
                            UnitedToast.info("Opening e₹ QR Scanner")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .defaultMinSize(minHeight = 52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A), contentColor = UnitedWhite)
                    ) {
                        Text(
                            "Send e₹",
                            fontWeight = FontWeight.Bold,
                            color = UnitedWhite,
                            fontSize = 15.sp,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("RBI Central Bank Digital Currency", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("e₹ WALLET", color = Color(0xFF38BDF8), fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("e₹ $balance.00", color = UnitedWhite, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Legal tender issued by Reserve Bank of India", color = Color(0xFF94A3B8), fontSize = 11.5.sp)
                }
            }

            Text("DENOMINATIONS IN WALLET", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("₹10 x 5", "₹50 x 2", "₹100 x 8", "₹500 x 3").forEach { note ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(UnitedWhite)
                            .border(1.dp, UnitedBorderLight, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(note, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = UnitedTextPrimary)
                    }
                }
            }
        }

        if (showMpinSheet) {
            UnitedNpciMpinModalSheet(
                subtitle = "Load e₹ 500.00 into RBI Digital Rupee Wallet",
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
                amount = "₹500.00",
                recipientName = "RBI Digital Rupee Wallet",
                recipientSubtitle = "Central Bank Digital Currency",
                bankName = bankTitle,
                accountMasked = maskedAcc,
                transactionCategory = "e₹ Wallet Load",
                onPaymentCompleted = { utr ->
                    balance += 500
                    com.unitedpay.core.model.TransactionRepository.addTransaction(
                        com.unitedpay.core.model.UpiTransaction(
                            id = java.util.UUID.randomUUID().toString(),
                            utrNumber = utr,
                            payeeName = "RBI Digital Rupee Wallet",
                            payeeVpa = "cbdc.wallet@rbi.npci",
                            amount = 500.0,
                            timestamp = System.currentTimeMillis(),
                            status = com.unitedpay.core.model.PaymentStatus.SUCCESS,
                            type = com.unitedpay.core.model.TransactionType.DEBIT,
                            bankName = bankTitle,
                            bankAccountNumberMasked = maskedAcc,
                            note = "Loaded e₹ 500 into wallet"
                        )
                    )
                },
                onDone = {
                    showProcessing = false
                }
            )
        }
    }
}
