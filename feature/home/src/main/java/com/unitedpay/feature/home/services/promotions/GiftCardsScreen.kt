package com.unitedpay.feature.home.services.promotions

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedNpciMpinModalSheet
import com.unitedpay.core.designsystem.components.UnitedPaymentProcessingDialog
import com.unitedpay.core.designsystem.components.UnitedPinnedBottomBar
import com.unitedpay.core.designsystem.components.UnitedPrimaryButton
import com.unitedpay.core.model.GiftCardBrandItem
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.designsystem.theme.*

typealias GiftCardBrand = GiftCardBrandItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftCardsScreen(onBackClick: () -> Unit) {
    var selectedBrand by remember { mutableStateOf("Amazon Pay") }
    var selectedAmount by remember { mutableStateOf("₹1,000") }
    var recipientPhone by remember { mutableStateOf("98765 43210") }
    var showProcessing by remember { mutableStateOf(false) }
    var showMpinSheet by remember { mutableStateOf(false) }
    var brands by remember { mutableStateOf(com.unitedpay.core.model.mock.UnitedMockData.giftCardBrands) }
    var denominations by remember { mutableStateOf(com.unitedpay.core.model.mock.UnitedMockData.giftCardDenominations) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.promotions.getBrandGiftCards().collect { res ->
            if (res is Resource.Success) brands = res.data
        }
    }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.promotions.getGiftCardDenominations().collect { res ->
            if (res is Resource.Success) denominations = res.data
        }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Brand Gift Cards", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
                    text = "Buy $selectedBrand Card ($selectedAmount)",
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
                // Select Brand
                item {
                    Text("Select Brand", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = UnitedTextPrimary)
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        brands.forEach { brand ->
                            val isSelected = selectedBrand == brand.name
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedBrand = brand.name },
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
                                    Column {
                                        Text(brand.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UnitedTextPrimary)
                                        Text(brand.category, fontSize = 11.5.sp, color = UnitedTextSecondary)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(UnitedSuccessContainer)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(brand.discountPercent, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedSuccess)
                                    }
                                }
                            }
                        }
                    }
                }

                // Denominations
                item {
                    Text("Select Denomination", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = UnitedTextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        denominations.forEach { denom ->
                            val isSelected = selectedAmount == denom
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) UnitedMoneyBlue else UnitedWhite)
                                    .border(1.dp, if (isSelected) UnitedMoneyBlue else UnitedBorderLight, RoundedCornerShape(6.dp))
                                    .clickable { selectedAmount = denom }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    denom,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) UnitedWhite else UnitedTextPrimary
                                )
                            }
                        }
                    }
                }

                // Recipient
                item {
                    Text("Recipient Mobile Number", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UnitedTextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = recipientPhone,
                        onValueChange = { recipientPhone = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

            }

        if (showMpinSheet) {
            UnitedNpciMpinModalSheet(
                subtitle = "Purchasing $selectedBrand Gift Card ($selectedAmount) for $recipientPhone",
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
                amount = selectedAmount,
                recipientName = "$selectedBrand Gift Voucher",
                recipientSubtitle = "Recipient: +91 $recipientPhone",
                bankName = bankTitle,
                accountMasked = maskedAcc,
                transactionCategory = "Gift Card",
                onPaymentCompleted = { utr ->
                    val cleanAmt = selectedAmount.replace("₹", "").replace(",", "").trim()
                    val amtVal = cleanAmt.toDoubleOrNull() ?: 1000.0
                    com.unitedpay.core.model.TransactionRepository.addTransaction(
                        com.unitedpay.core.model.UpiTransaction(
                            id = java.util.UUID.randomUUID().toString(),
                            utrNumber = utr,
                            payeeName = "$selectedBrand Gift Card",
                            payeeVpa = "vouchers@giftcard.npci",
                            amount = amtVal,
                            timestamp = System.currentTimeMillis(),
                            status = com.unitedpay.core.model.PaymentStatus.SUCCESS,
                            type = com.unitedpay.core.model.TransactionType.DEBIT,
                            bankName = bankTitle,
                            bankAccountNumberMasked = maskedAcc,
                            note = "$selectedBrand voucher for +91 $recipientPhone"
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
