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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
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
import com.unitedpay.core.model.InsurancePolicySummary
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceScreen(onBackClick: () -> Unit) {
    val initialSummary = UserSessionManager.getCurrentInsurancePolicySummary()
    var selectedInsurer by remember { mutableStateOf(initialSummary?.insurerName ?: "") }
    var policyNo by remember { mutableStateOf(initialSummary?.policyNumber ?: "") }
    var insurers by remember { mutableStateOf(com.unitedpay.core.model.mock.UnitedMockData.insurers) }
    var policySummary by remember { mutableStateOf<InsurancePolicySummary?>(initialSummary) }
    var showProcessing by remember { mutableStateOf(false) }
    var showMpinSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.financial.getInsurers().collect { res ->
            if (res is Resource.Success) insurers = res.data
        }
    }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.financial.getInsuranceDetails().collect { res ->
            if (res is Resource.Success) {
                policySummary = res.data
                policyNo = res.data.policyNumber
                selectedInsurer = res.data.insurerName
            } else if (res is Resource.Error) {
                policySummary = null
            }
        }
    }

    val summary = policySummary

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Insurance Premium", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
                        text = "Pay Premium ₹${summary.premiumDue.toInt()}.00",
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
                        title = "No Active Policies",
                        subtitle = "No insurance premiums due. Link your insurance policy to pay directly via UPI.",
                        icon = Icons.Default.HealthAndSafety
                    )
                }
            }

            item {
                Text("Select Insurance Company", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UnitedTextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    insurers.forEach { insurer ->
                        val isSelected = selectedInsurer == insurer
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedInsurer = insurer },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFEFF6FF) else UnitedWhite),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) UnitedMoneyBlue else UnitedBorderLight)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(insurer, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = UnitedTextPrimary)
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = policyNo,
                    onValueChange = { policyNo = it },
                    singleLine = true,
                    label = { Text("Policy Number", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingIcon = { Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = UnitedMoneyBlue) },
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
                                Text("Policy Holder", fontSize = 12.sp, color = UnitedTextSecondary)
                                Text(summary.policyHolderName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Due Date", fontSize = 12.sp, color = UnitedTextSecondary)
                                Text(summary.gracePeriodClose, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextPrimary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Premium Payable", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                Text("₹${summary.premiumDue.toInt()}.00", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = UnitedMoneyBlue)
                            }
                        }
                    }
                }
            }
        }

        if (showMpinSheet && summary != null) {
            UnitedNpciMpinModalSheet(
                subtitle = "Paying Insurance Premium ₹${summary.premiumDue.toInt()}.00 to $selectedInsurer ($policyNo)",
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
                amount = "₹${summary.premiumDue.toInt()}.00",
                recipientName = selectedInsurer,
                recipientSubtitle = "Policy No: $policyNo",
                bankName = activeBankName,
                accountMasked = activeBankMasked,
                transactionCategory = "Insurance Premium",
                onPaymentCompleted = { utr ->
                    com.unitedpay.core.model.TransactionRepository.addTransaction(
                        com.unitedpay.core.model.UpiTransaction(
                            id = java.util.UUID.randomUUID().toString(),
                            utrNumber = utr,
                            payeeName = selectedInsurer,
                            payeeVpa = "premium@licindia.npci",
                            payerName = activeProfile.fullName,
                            payerVpa = activeProfile.primaryVpa,
                            amount = summary.premiumDue,
                            timestamp = System.currentTimeMillis(),
                            status = com.unitedpay.core.model.PaymentStatus.SUCCESS,
                            type = com.unitedpay.core.model.TransactionType.DEBIT,
                            bankName = activeBankName,
                            bankAccountNumberMasked = activeBankMasked,
                            note = "Premium for $policyNo"
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
