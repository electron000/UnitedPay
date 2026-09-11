package com.unitedpay.feature.home.services.recharge

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsSubway
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
import com.unitedpay.core.model.MetroCardDetails
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetroScreen(onBackClick: () -> Unit) {
    MetroCardScreen(onBackClick = onBackClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetroCardScreen(onBackClick: () -> Unit) {
    val initialMetro = UserSessionManager.getCurrentMetroCardDetails()
    var metroCity by remember { mutableStateOf(initialMetro?.metroAuthority ?: "Delhi Metro (DMRC)") }
    var smartCardNumber by remember { mutableStateOf(initialMetro?.cardNumber ?: "") }
    var rechargeAmount by remember { mutableStateOf("₹500") }
    var showProcessing by remember { mutableStateOf(false) }
    var showMpinSheet by remember { mutableStateOf(false) }
    var metroAgencies by remember { mutableStateOf(com.unitedpay.core.model.mock.UnitedMockData.metroAuthorities) }
    var cardDetails by remember { mutableStateOf<MetroCardDetails?>(initialMetro) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.recharge.getMetroAuthorities().collect { res ->
            if (res is Resource.Success) metroAgencies = res.data
        }
    }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.recharge.getMetroCardDetails().collect { res ->
            if (res is Resource.Success) {
                cardDetails = res.data
                smartCardNumber = res.data.cardNumber
                metroCity = res.data.metroAuthority
            }
        }
    }

    val quickAmounts = listOf("₹100", "₹200", "₹500", "₹1,000", "₹2,000")

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Metro Smart Card", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
                    text = "Recharge $rechargeAmount",
                    enabled = smartCardNumber.isNotBlank() && rechargeAmount.isNotBlank(),
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
                item {
                    Text("METRO AGENCY & SMART CARD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
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
                            Text("Select Metro System", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            metroAgencies.forEach { agency ->
                                val isSelected = metroCity == agency
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                                        .border(1.dp, if (isSelected) UnitedMoneyBlue else UnitedBorderLight, RoundedCornerShape(8.dp))
                                        .clickable { metroCity = agency }
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = agency,
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
                                value = smartCardNumber,
                                onValueChange = { smartCardNumber = it },
                                singleLine = true,
                                label = { Text("Smart Card Number", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                leadingIcon = { Icon(Icons.Default.DirectionsSubway, contentDescription = null, tint = UnitedMoneyBlue) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                }

                item {
                    Text("RECHARGE AMOUNT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
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
                            Text(
                                text = rechargeAmount,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = UnitedMoneyBlue
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(quickAmounts) { amt ->
                                    val isSelected = rechargeAmount == amt
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) UnitedMoneyBlue else Color(0xFFF1F5F9))
                                            .clickable { rechargeAmount = amt }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = amt,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) UnitedWhite else UnitedTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }

        if (showMpinSheet) {
            UnitedNpciMpinModalSheet(
                subtitle = "Recharging Metro Card $rechargeAmount for $smartCardNumber ($metroCity)",
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
                amount = rechargeAmount,
                recipientName = metroCity,
                recipientSubtitle = "Card: $smartCardNumber",
                bankName = bankTitle,
                accountMasked = maskedAcc,
                transactionCategory = "Metro Card Recharge",
                onPaymentCompleted = { utr ->
                    val amtClean = rechargeAmount.replace("₹", "").replace(",", "").trim()
                    val amtVal = amtClean.toDoubleOrNull() ?: 500.0
                    com.unitedpay.core.model.TransactionRepository.addTransaction(
                        com.unitedpay.core.model.UpiTransaction(
                            id = java.util.UUID.randomUUID().toString(),
                            utrNumber = utr,
                            payeeName = "$metroCity Card",
                            payeeVpa = "metro@dmrc.npci",
                            amount = amtVal,
                            timestamp = System.currentTimeMillis(),
                            status = com.unitedpay.core.model.PaymentStatus.SUCCESS,
                            type = com.unitedpay.core.model.TransactionType.DEBIT,
                            bankName = bankTitle,
                            bankAccountNumberMasked = maskedAcc,
                            note = "Metro Card Recharge for $smartCardNumber"
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
