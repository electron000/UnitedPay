package com.unitedpay.feature.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.extensions.toInrCurrency
import com.unitedpay.core.designsystem.theme.UnitedBackgroundLight
import com.unitedpay.core.designsystem.theme.UnitedBorderLight
import com.unitedpay.core.designsystem.theme.UnitedError
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedRoyalBlue
import com.unitedpay.core.designsystem.theme.UnitedSuccess
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite
import com.unitedpay.core.designsystem.components.UnitedCenteredAmountField
import com.unitedpay.core.designsystem.components.UnitedPaymentProcessingDialog
import com.unitedpay.core.designsystem.components.UnitedPaymentStep
import com.unitedpay.feature.payment.components.NpciMpinSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    payeeVpa: String,
    payeeName: String,
    viewModel: PaymentViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit,
    onPaymentSuccess: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(payeeVpa, payeeName) {
        viewModel.setPayee(payeeVpa, payeeName)
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            // UI state completedTransaction handles transition to CompletionView
        }
    }

    Scaffold(
        containerColor = UnitedBackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Transfer Money", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = UnitedTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = UnitedTextPrimary
                        )
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    val currentAmount = uiState.amountInput.toDoubleOrNull() ?: 0.0
                    val isExceedingLimit = currentAmount > 100000.0

                    if (uiState.isProcessing) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(color = UnitedMoneyBlue, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Connecting to NPCI UPI Switch...", fontSize = 13.sp, color = UnitedTextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.onProceedToPay() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .defaultMinSize(minHeight = 52.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UnitedMoneyBlue,
                                contentColor = UnitedWhite,
                                disabledContainerColor = Color(0xFFCBD5E1),
                                disabledContentColor = Color(0xFF64748B)
                            ),
                            enabled = currentAmount in 1.0..100000.0
                        ) {
                            val buttonText = when {
                                isExceedingLimit -> "Amount exceeds ₹1,00,000 limit"
                                currentAmount > 0.0 -> "Proceed to Pay ₹${uiState.amountInput}"
                                else -> "Proceed to Pay"
                            }
                            Text(
                                text = buttonText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Payee Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(UnitedMoneyBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.payeeName.take(1).ifBlank { "M" }.uppercase(),
                            color = UnitedWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiState.payeeName.ifBlank { "Verified Merchant" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = UnitedTextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified VPA",
                                tint = UnitedSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = uiState.payeeVpa.ifBlank { "merchant@unitedpay" },
                            fontSize = 12.sp,
                            color = UnitedTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Amount Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ENTER AMOUNT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextSecondary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    UnitedCenteredAmountField(
                        value = uiState.amountInput,
                        onValueChange = { digits ->
                            viewModel.onAmountChanged(digits)
                        },
                        symbolSize = 38.sp,
                        amountSize = 44.sp,
                        placeholder = "0"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fast Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(100, 500, 1000, 2000).forEach { chipAmount ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, UnitedBorderLight, RoundedCornerShape(6.dp))
                                    .clickable {
                                        val current = uiState.amountInput.toDoubleOrNull() ?: 0.0
                                        val newAmt = (current + chipAmount).toInt().toString()
                                        if (newAmt.length <= 6) {
                                            viewModel.onAmountChanged(newAmt)
                                        }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = "+₹$chipAmount",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = UnitedMoneyBlue,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    val currentAmount = uiState.amountInput.toDoubleOrNull() ?: 0.0
                    val isExceedingLimit = currentAmount > 100000.0

                    if (isExceedingLimit) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "NPCI UPI limit: Maximum ₹1,00,000 per transaction",
                            color = UnitedError,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.isMpinSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.onDismissMpin() },
                sheetState = sheetState,
                containerColor = UnitedWhite
            ) {
                NpciMpinSheet(
                    amount = "₹${uiState.amountInput}",
                    payeeName = uiState.payeeName.ifBlank { "Verified Payee" },
                    onPinSubmitted = { pin ->
                        viewModel.submitPin(pin)
                    },
                    onDismiss = { viewModel.onDismissMpin() }
                )
            }
        }

        if (uiState.isProcessing || uiState.completedTransaction != null) {
            val completedTxn = uiState.completedTransaction
            UnitedPaymentProcessingDialog(
                amount = uiState.amountInput,
                recipientName = uiState.payeeName.ifBlank { "Verified Payee" },
                recipientSubtitle = uiState.payeeVpa.ifBlank { "merchant@unitedpay" },
                bankName = completedTxn?.bankName ?: "State Bank of India",
                accountMasked = completedTxn?.bankAccountNumberMasked ?: "•••• 4821",
                transactionCategory = "UPI Transfer",
                initialStep = if (completedTxn != null) UnitedPaymentStep.SUCCESS else UnitedPaymentStep.PROCESSING,
                onDone = {
                    onPaymentSuccess(completedTxn?.id ?: "")
                }
            )
        }
    }
}
