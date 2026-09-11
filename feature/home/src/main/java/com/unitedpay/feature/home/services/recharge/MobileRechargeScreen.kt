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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.model.api.UnitedPayApi
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

data class RechargePlan(
    val amount: String,
    val validity: String,
    val data: String,
    val description: String,
    val isPopular: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeScreen(onBackClick: () -> Unit) {
    MobileRechargeScreen(onBackClick = onBackClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileRechargeScreen(onBackClick: () -> Unit) {
    var phoneNumber by remember { mutableStateOf("9864012345") }
    var selectedOperator by remember { mutableStateOf("Jio Prepaid") }
    var selectedPlan by remember { mutableStateOf("₹299") }
    var showProcessing by remember { mutableStateOf(false) }
    var showMpinSheet by remember { mutableStateOf(false) }

    var plans by remember {
        mutableStateOf(
            listOf(
                RechargePlan("₹299", "28 Days", "1.5 GB/Day", "Unlimited 5G Data + Unlimited Voice Calls + 100 SMS/day", isPopular = true),
                RechargePlan("₹719", "84 Days", "1.5 GB/Day", "Unlimited 5G Data + Unlimited Calls + Free National Roaming"),
                RechargePlan("₹2999", "365 Days", "2.5 GB/Day", "Annual 5G Hero Pack + Unlimited Calls"),
                RechargePlan("₹19", "1 Day", "1 GB Total", "Data Booster Add-on")
            )
        )
    }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.recharge.getMobilePlans().collect { res ->
            if (res is Resource.Success) {
                plans = res.data.map {
                    RechargePlan(
                        amount = it.formattedPrice,
                        validity = it.validity,
                        data = it.dataAllowance,
                        description = "${it.callBenefit} • ${it.additionalPerks}",
                        isPopular = it.category == "Hero Pack"
                    )
                }
            }
        }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Mobile Recharge", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
                    text = "Recharge $selectedPlan",
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
                    Text("MOBILE NUMBER & OPERATOR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
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
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { if (it.length <= 10) phoneNumber = it },
                                singleLine = true,
                                label = { Text("Mobile Number", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = UnitedMoneyBlue) },
                                trailingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = UnitedTextSecondary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Select Telecom Operator", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val operators = listOf("Jio", "Airtel", "Vi", "BSNL")
                                items(operators) { op ->
                                    val isSelected = selectedOperator.startsWith(op)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) UnitedMoneyBlue else Color(0xFFF1F5F9))
                                            .clickable { selectedOperator = "$op Prepaid" }
                                            .padding(horizontal = 14.dp, vertical = 7.dp)
                                    ) {
                                        Text(
                                            text = op,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) UnitedWhite else UnitedTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text("RECOMMENDED PLANS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
                }

                items(plans) { plan ->
                    val isSelected = selectedPlan == plan.amount
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlan = plan.amount },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFEFF6FF) else UnitedWhite)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, if (isSelected) UnitedMoneyBlue else UnitedBorderLight, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = plan.amount,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = UnitedMoneyBlue
                                    )
                                    if (plan.isPopular) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFDCFCE7))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "BESTSELLER",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF15803D)
                                            )
                                        }
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = UnitedMoneyBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("Validity: ${plan.validity}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextPrimary)
                                Text("Data: ${plan.data}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(plan.description, fontSize = 11.5.sp, color = UnitedTextSecondary, lineHeight = 16.sp)
                        }
                    }
                }

            }

        if (showMpinSheet) {
            UnitedNpciMpinModalSheet(
                subtitle = "Recharge $selectedPlan for $phoneNumber ($selectedOperator)",
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
                amount = selectedPlan,
                recipientName = selectedOperator,
                recipientSubtitle = "Mobile: +91 $phoneNumber",
                bankName = bankTitle,
                accountMasked = maskedAcc,
                transactionCategory = "Prepaid Recharge",
                onPaymentCompleted = { utr ->
                    val amtClean = selectedPlan.replace("₹", "").trim()
                    val amtVal = amtClean.toDoubleOrNull() ?: 299.0
                    com.unitedpay.core.model.TransactionRepository.addTransaction(
                        com.unitedpay.core.model.UpiTransaction(
                            id = java.util.UUID.randomUUID().toString(),
                            utrNumber = utr,
                            payeeName = "$selectedOperator Recharge",
                            payeeVpa = "recharge@jio.npci",
                            amount = amtVal,
                            timestamp = System.currentTimeMillis(),
                            status = com.unitedpay.core.model.PaymentStatus.SUCCESS,
                            type = com.unitedpay.core.model.TransactionType.DEBIT,
                            bankName = bankTitle,
                            bankAccountNumberMasked = maskedAcc,
                            note = "Recharge for +91 $phoneNumber"
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
