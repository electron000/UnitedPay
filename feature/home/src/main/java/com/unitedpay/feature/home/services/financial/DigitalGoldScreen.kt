package com.unitedpay.feature.home.services.financial

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedCenteredAmountCard
import com.unitedpay.core.designsystem.components.UnitedNpciMpinModalSheet
import com.unitedpay.core.designsystem.components.UnitedPaymentProcessingDialog
import com.unitedpay.core.designsystem.components.UnitedPinnedBottomBar
import com.unitedpay.core.designsystem.components.UnitedPrimaryButton
import com.unitedpay.core.model.DigitalGoldQuote
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalGoldScreen(onBackClick: () -> Unit) {
    var amountInput by remember { mutableStateOf("1000") }
    var showProcessing by remember { mutableStateOf(false) }
    var showMpinSheet by remember { mutableStateOf(false) }
    var goldQuote by remember { mutableStateOf<DigitalGoldQuote?>(null) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.financial.getDigitalGoldQuote().collect { res ->
            if (res is Resource.Success) goldQuote = res.data
        }
    }

    val liveQuote = goldQuote ?: com.unitedpay.core.model.mock.UnitedMockData.digitalGoldQuote
    val quickAmounts = listOf("₹500", "₹1,000", "₹5,000", "₹10,000")

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Digital Gold 24K 99.9%", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
            UnitedPinnedBottomBar {
                UnitedPrimaryButton(
                    text = "Proceed to Buy 24K Gold (₹$amountInput)",
                    onClick = { showMpinSheet = true }
                )
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
                // Gold Live Price Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF78350F))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("LIVE BUY PRICE (24K 99.9%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE68A), letterSpacing = 0.5.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF59E0B))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("+1.4% Today", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${liveQuote.formattedBuyPrice} / gm", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = UnitedWhite)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Secured in 100% insured ${liveQuote.partner} vault with BRINKS", fontSize = 11.5.sp, color = Color(0xFFFDE68A).copy(alpha = 0.85f))
                        }
                    }
                }

                // Current Holdings
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
                                Text("Your Gold Locker Balance", fontSize = 12.sp, color = UnitedTextSecondary)
                                Text("${liveQuote.userVaultGrams} grams (Value: ${liveQuote.formattedVaultValue})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                            }
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(24.dp))
                        }
                    }
                }

                // Buy Mode Switch
                item {
                    UnitedCenteredAmountCard(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = "ENTER PURCHASE AMOUNT",
                        maxDigits = 6
                    )
                }

                // Quick Chips
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickAmounts.forEach { amt ->
                            val raw = amt.replace("₹", "").replace(",", "")
                            val isSelected = amountInput == raw
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) UnitedMoneyBlue else UnitedWhite)
                                    .border(1.dp, if (isSelected) UnitedMoneyBlue else UnitedBorderLight, RoundedCornerShape(6.dp))
                                    .clickable { amountInput = raw }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    amt,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) UnitedWhite else UnitedTextPrimary,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

            }

        if (showMpinSheet) {
            UnitedNpciMpinModalSheet(
                subtitle = "Buying 24K Digital Gold (₹$amountInput)",
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
                amount = "₹$amountInput",
                recipientName = "MMTC-PAMP Digital Gold",
                recipientSubtitle = "24K 99.9% Pure Vault Locker",
                bankName = bankTitle,
                accountMasked = maskedAcc,
                transactionCategory = "Digital Gold",
                onPaymentCompleted = { utr ->
                    val amtVal = amountInput.toDoubleOrNull() ?: 1000.0
                    com.unitedpay.core.model.TransactionRepository.addTransaction(
                        com.unitedpay.core.model.UpiTransaction(
                            id = java.util.UUID.randomUUID().toString(),
                            utrNumber = utr,
                            payeeName = "MMTC-PAMP Gold",
                            payeeVpa = "goldvault@mmtcpamp.npci",
                            amount = amtVal,
                            timestamp = System.currentTimeMillis(),
                            status = com.unitedpay.core.model.PaymentStatus.SUCCESS,
                            type = com.unitedpay.core.model.TransactionType.DEBIT,
                            bankName = bankTitle,
                            bankAccountNumberMasked = maskedAcc,
                            note = "24K Gold purchase ₹$amountInput"
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
