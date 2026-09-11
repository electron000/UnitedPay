package com.unitedpay.feature.home.services.utilities

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
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
import com.unitedpay.core.model.BbpsBillSummary
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterBillScreen(onBackClick: () -> Unit) {
    val initialBill = UserSessionManager.getCurrentWaterBill()
    var waterBoard by remember { mutableStateOf(initialBill?.billerName ?: "") }
    var connectionId by remember { mutableStateOf(initialBill?.consumerNumber ?: "") }
    var billAmount by remember { mutableStateOf(initialBill?.formattedAmount ?: "") }
    var showProcessing by remember { mutableStateOf(false) }
    var showMpinSheet by remember { mutableStateOf(false) }
    var boards by remember { mutableStateOf(com.unitedpay.core.model.mock.UnitedMockData.waterBillers) }
    var billSummary by remember { mutableStateOf<BbpsBillSummary?>(initialBill) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.bbps.getWaterBillers().collect { res ->
            if (res is Resource.Success) boards = res.data
        }
    }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.bbps.getWaterBill().collect { res ->
            if (res is Resource.Success) {
                billSummary = res.data
                waterBoard = res.data.billerName
                connectionId = res.data.consumerNumber
                billAmount = res.data.formattedAmount
            } else if (res is Resource.Error) {
                billSummary = null
            }
        }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Water Tax & Bills", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
            if (billSummary != null && billAmount.isNotBlank()) {
                UnitedPinnedBottomBar {
                    UnitedPrimaryButton(
                        text = "Pay Bill $billAmount",
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
            if (billSummary == null) {
                item {
                    UnitedEmptyState(
                        title = "No Pending Water Bills",
                        subtitle = "Great! There are no unpaid water bills associated with this account.",
                        icon = Icons.Default.CheckCircle
                    )
                }
            }

            item {
                Text("WATER BOARD & CONNECTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
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
                        Text("Select Water Board", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        boards.forEach { board ->
                            val isSelected = waterBoard == board
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                                    .border(1.dp, if (isSelected) UnitedMoneyBlue else UnitedBorderLight, RoundedCornerShape(8.dp))
                                    .clickable { waterBoard = board }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = board,
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
                            value = connectionId,
                            onValueChange = { connectionId = it },
                            singleLine = true,
                            label = { Text("Consumer Connection ID / RR No", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            leadingIcon = { Icon(Icons.Default.WaterDrop, contentDescription = null, tint = UnitedMoneyBlue) },
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

                if (billSummary != null) {
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
                                Text("Consumer Name", fontSize = 12.sp, color = UnitedTextSecondary)
                                Text(billSummary?.consumerName ?: UserSessionManager.getCurrentProfile().fullName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Bill Cycle", fontSize = 12.sp, color = UnitedTextSecondary)
                                Text(billSummary?.billCycle ?: "August 2026", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextPrimary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Amount Payable", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                Text(billAmount, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = UnitedMoneyBlue)
                            }
                        }
                    }
                }
            }
        }

        if (showMpinSheet) {
            UnitedNpciMpinModalSheet(
                subtitle = "Paying Water Bill $billAmount",
                onDismissRequest = { showMpinSheet = false },
                onPinSubmitted = {
                    showMpinSheet = false
                    showProcessing = true
                }
            )
        }

        if (showProcessing) {
            val activeBank = UserSessionManager.getCurrentBankAccounts().firstOrNull()
            val activeProfile = UserSessionManager.getCurrentProfile()
            val activeBankName = activeBank?.bankName ?: "State Bank of India"
            val activeBankMasked = activeBank?.accountNumberMasked ?: "•••• 9821"

            UnitedPaymentProcessingDialog(
                amount = billAmount,
                recipientName = waterBoard,
                recipientSubtitle = "Connection: $connectionId",
                bankName = activeBankName,
                accountMasked = activeBankMasked,
                transactionCategory = "Water Bill Payment",
                onPaymentCompleted = { utr ->
                    val amtClean = billAmount.replace("₹", "").replace(",", "").trim()
                    val amtVal = amtClean.toDoubleOrNull() ?: 380.0
                    com.unitedpay.core.model.TransactionRepository.addTransaction(
                        com.unitedpay.core.model.UpiTransaction(
                            id = java.util.UUID.randomUUID().toString(),
                            utrNumber = utr,
                            payeeName = waterBoard,
                            payeeVpa = "bbps.water@djb.npci",
                            payerName = activeProfile.fullName,
                            payerVpa = activeProfile.primaryVpa,
                            amount = amtVal,
                            timestamp = System.currentTimeMillis(),
                            status = com.unitedpay.core.model.PaymentStatus.SUCCESS,
                            type = com.unitedpay.core.model.TransactionType.DEBIT,
                            bankName = activeBankName,
                            bankAccountNumberMasked = activeBankMasked,
                            note = "Water Bill for $connectionId"
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
