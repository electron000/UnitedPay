package com.unitedpay.feature.home.services.financial

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.components.UnitedIcons
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.designsystem.util.ReceiptShareHelper
import com.unitedpay.core.model.*
import com.unitedpay.core.model.repository.HookoluServices
import kotlinx.coroutines.launch

/**
 * HookoluPay Embedded Digital Lending & Credit Line Screen.
 * Allows merchants and users to access pre-approved working capital
 * and instant micro-loans with transparent tenure and EMI calculations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LendingScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var creditScore by remember { mutableIntStateOf(785) }
    var loanOffers by remember { mutableStateOf<List<LoanOffer>>(emptyList()) }
    var selectedOfferId by remember { mutableStateOf("loan_vyapar_01") }

    var selectedTenureMonths by remember { mutableIntStateOf(6) }
    var appliedAmount by remember { mutableDoubleStateOf(100000.0) }

    var isDisbursing by remember { mutableStateOf(false) }
    var disbursalReceipt by remember { mutableStateOf<LoanDisbursalReceipt?>(null) }

    LaunchedEffect(Unit) {
        creditScore = HookoluServices.lending.getCreditScore()
        loanOffers = HookoluServices.lending.getLoanOffers()
    }

    val activeOffer = loanOffers.firstOrNull { it.id == selectedOfferId } ?: loanOffers.firstOrNull()

    val calculatedEmi = remember(appliedAmount, selectedTenureMonths, activeOffer) {
        if (activeOffer != null) {
            val r = (activeOffer.interestRatePerMonth / 100.0)
            val n = selectedTenureMonths.toDouble()
            (appliedAmount * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1)
        } else 0.0
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandShield(size = 26.dp, asCardBadge = true)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Hookolu Credit & Loans",
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Instant Working Capital & Disbursal",
                                fontSize = 11.sp,
                                color = UnitedTextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = UnitedTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = UnitedWhite)
            )
        },
        bottomBar = {
            if (disbursalReceipt == null) {
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
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isDisbursing = true
                                    val receipt = HookoluServices.lending.applyLoan(
                                        selectedOfferId,
                                        appliedAmount,
                                        selectedTenureMonths
                                    )
                                    disbursalReceipt = receipt
                                    isDisbursing = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UnitedMoneyBlue,
                                contentColor = UnitedWhite
                            ),
                            enabled = !isDisbursing
                        ) {
                            if (isDisbursing) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = UnitedWhite, strokeWidth = 2.5.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Disbursing to SBI •••• 4821...", fontSize = 14.sp)
                            } else {
                                Text(
                                    "Disburse ₹${appliedAmount.toInt()} Instantly",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Credit Score Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = UnitedWhite,
                    border = BorderStroke(1.dp, UnitedBorderLight),
                    shadowElevation = 1.dp
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
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(28.dp))
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("CIBIL Score: $creditScore", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFDCFCE7)) {
                                    Text("Excellent", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Text("Pre-qualified for instant 0% processing fee credit", fontSize = 11.sp, color = UnitedTextSecondary)
                        }
                    }
                }
            }

            // Disbursal Receipt Display
            if (disbursalReceipt != null) {
                item {
                    val receipt = disbursalReceipt!!
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = UnitedWhite,
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f)),
                        shadowElevation = 3.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(32.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Loan Disbursed Successfully", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                            Text("Loan ID: ${receipt.loanId} • Instant Settlement", fontSize = 12.sp, color = UnitedTextSecondary)

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = UnitedBorderLight)
                            Spacer(modifier = Modifier.height(16.dp))

                            ReceiptRow("Borrower Name", receipt.borrowerName)
                            ReceiptRow("Credit Facility", receipt.loanTitle)
                            ReceiptRow("Sanctioned Amount", receipt.formattedSanctioned, isBold = true, valueColor = UnitedMoneyBlue)
                            ReceiptRow("Monthly EMI", receipt.formattedEmi, isBold = true)
                            ReceiptRow("Tenure", "${receipt.tenureMonths} Months")
                            ReceiptRow("Disbursal Account", receipt.disbursalAccount)

                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedButton(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        ReceiptShareHelper.shareTransactionReceipt(
                                            context = activity,
                                            receiptDetails = ReceiptShareHelper.ReceiptDetails(
                                                title = "Hookolu Vyapar Credit Disbursal",
                                                amount = receipt.formattedSanctioned,
                                                sender = "Hookolu Lending Partner",
                                                receiver = receipt.borrowerName,
                                                utr = receipt.loanId,
                                                date = "13 Sep 2026",
                                                time = "10:30 PM",
                                                status = "SUCCESSFUL",
                                                bankName = "State Bank of India (•••• 4821)",
                                                paymentMode = "Instant Disbursal",
                                                note = "EMI: ${receipt.formattedEmi} • Tenure: ${receipt.tenureMonths} Months"
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, UnitedMoneyBlue)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp), tint = UnitedMoneyBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share Loan Sanction Slip", color = UnitedMoneyBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // 2. Pre-Approved Credit Offers
                item {
                    Text("Available Pre-Approved Offers", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                }

                items(loanOffers) { offer ->
                    val isSelected = selectedOfferId == offer.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                selectedOfferId = offer.id
                                appliedAmount = offer.maxAmount / 2
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) UnitedMoneyBlue.copy(alpha = 0.06f) else UnitedWhite,
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) UnitedMoneyBlue else UnitedBorderLight
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(offer.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                    Text(offer.category, fontSize = 11.sp, color = UnitedTextSecondary)
                                }
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFDCFCE7)) {
                                    Text("Pre-Approved", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Max Limit: ${offer.formattedMaxAmount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedMoneyBlue)
                                Text("Interest: ${offer.interestRatePerMonth}% / mo", fontSize = 12.sp, color = UnitedTextSecondary)
                            }
                        }
                    }
                }

                // 3. Amount & Tenure Configurator
                activeOffer?.let { offer ->
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = UnitedWhite,
                            border = BorderStroke(1.dp, UnitedBorderLight),
                            shadowElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Select Desired Loan Amount", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("₹${appliedAmount.toInt()}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = UnitedMoneyBlue)

                                Slider(
                                    value = appliedAmount.toFloat(),
                                    onValueChange = { appliedAmount = it.toDouble() },
                                    valueRange = 10000f..offer.maxAmount.toFloat(),
                                    steps = 19,
                                    colors = SliderDefaults.colors(thumbColor = UnitedMoneyBlue, activeTrackColor = UnitedMoneyBlue)
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Select Tenure (Months)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(3, 6, 9, 12).filter { it in offer.minTenureMonths..offer.maxTenureMonths }.forEach { tenure ->
                                        val isSelected = selectedTenureMonths == tenure
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { selectedTenureMonths = tenure },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) UnitedMoneyBlue else UnitedCanvasLight,
                                            border = BorderStroke(1.dp, if (isSelected) UnitedMoneyBlue else UnitedBorderLight)
                                        ) {
                                            Text(
                                                text = "$tenure Mo",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) UnitedWhite else UnitedTextPrimary,
                                                modifier = Modifier.padding(vertical = 10.dp),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = UnitedCanvasLight,
                                    border = BorderStroke(0.5.dp, UnitedBorderLight)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Calculated Monthly EMI:", fontSize = 12.sp, color = UnitedTextSecondary)
                                        Text(
                                            "₹${String.format("%.2f", calculatedEmi)} / mo",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF10B981)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = UnitedTextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = UnitedTextSecondary)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor
        )
    }
}
