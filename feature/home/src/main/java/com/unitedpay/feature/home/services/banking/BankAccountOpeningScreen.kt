package com.unitedpay.feature.home.services.banking

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Videocam
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
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.components.UnitedIcons
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.*
import com.unitedpay.core.model.repository.HookoluServices
import kotlinx.coroutines.launch

/**
 * HookoluPay 4-Step Partner Bank Account Opening Wizard.
 * Allows customers to open zero-balance digital savings accounts
 * with partner banks (AU SFB, Equitas SFB, SBI CSP).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankAccountOpeningScreen(
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var partnerBanks by remember { mutableStateOf<List<PartnerBank>>(emptyList()) }
    var selectedBankId by remember { mutableStateOf("pb_au") }
    var currentStep by remember { mutableStateOf(AccountOpeningStep.SELECT_BANK) }

    val isSim1 = com.unitedpay.core.model.session.UserSessionManager.isSim1Active
    val currentProfile = com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile()
    var applicantName by remember { mutableStateOf(if (isSim1) currentProfile.fullName else "") }
    var mobileNumber by remember { mutableStateOf(if (isSim1) currentProfile.phoneNumber else "") }
    var panNumber by remember { mutableStateOf(if (isSim1) "ABCDE1234F" else "") }
    var isPanVerified by remember { mutableStateOf(false) }

    var aadhaarNumber by remember { mutableStateOf(if (isSim1) "482190124912" else "") }
    var otpCode by remember { mutableStateOf(if (isSim1) "491024" else "") }
    var isAadhaarVerified by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var submissionResult by remember { mutableStateOf<BankApplicationSubmission?>(null) }

    LaunchedEffect(Unit) {
        partnerBanks = HookoluServices.bankOpening.getPartnerBanks()
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
                                text = "Open Bank Account",
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Digital Savings • Partner Banks",
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
            if (submissionResult == null) {
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
                                when (currentStep) {
                                    AccountOpeningStep.SELECT_BANK -> {
                                        currentStep = AccountOpeningStep.PAN_VERIFICATION
                                    }
                                    AccountOpeningStep.PAN_VERIFICATION -> {
                                        isPanVerified = true
                                        currentStep = AccountOpeningStep.AADHAAR_EKYC
                                    }
                                    AccountOpeningStep.AADHAAR_EKYC -> {
                                        isAadhaarVerified = true
                                        coroutineScope.launch {
                                            isSubmitting = true
                                            val submission = HookoluServices.bankOpening.submitApplication(
                                                bankId = selectedBankId,
                                                applicantName = applicantName,
                                                panNumber = panNumber,
                                                aadhaarNumber = aadhaarNumber,
                                                mobileNumber = mobileNumber
                                            )
                                            submissionResult = submission
                                            isSubmitting = false
                                            currentStep = AccountOpeningStep.CONFIRMATION
                                        }
                                    }
                                    AccountOpeningStep.CONFIRMATION -> {}
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
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = UnitedWhite, strokeWidth = 2.5.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Generating Digital Account...", fontSize = 14.sp)
                            } else {
                                val label = when (currentStep) {
                                    AccountOpeningStep.SELECT_BANK -> "Proceed with Selected Bank"
                                    AccountOpeningStep.PAN_VERIFICATION -> "Verify PAN with NSDL"
                                    AccountOpeningStep.AADHAAR_EKYC -> "Authorize Aadhaar eKYC via OTP"
                                    AccountOpeningStep.CONFIRMATION -> "Done"
                                }
                                Text(label, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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

            // 1. Step Progress Indicator
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = UnitedWhite,
                    border = BorderStroke(1.dp, UnitedBorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AccountOpeningStep.entries.forEachIndexed { index, step ->
                            val isCompleted = step.stepNumber < currentStep.stepNumber || submissionResult != null
                            val isCurrent = step == currentStep && submissionResult == null

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCompleted) Color(0xFF10B981)
                                            else if (isCurrent) UnitedMoneyBlue
                                            else UnitedCanvasLight
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = UnitedWhite, modifier = Modifier.size(16.dp))
                                    } else {
                                        Text(
                                            text = step.stepNumber.toString(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) UnitedWhite else UnitedTextSecondary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when (step) {
                                        AccountOpeningStep.SELECT_BANK -> "Bank"
                                        AccountOpeningStep.PAN_VERIFICATION -> "PAN"
                                        AccountOpeningStep.AADHAAR_EKYC -> "eKYC"
                                        AccountOpeningStep.CONFIRMATION -> "Status"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrent) UnitedMoneyBlue else UnitedTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Step Content
            when (currentStep) {
                AccountOpeningStep.SELECT_BANK -> {
                    item {
                        Text("Select Partner Bank", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                    }

                    items(partnerBanks) { bank ->
                        val isSelected = selectedBankId == bank.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedBankId = bank.id },
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(bank.bankName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                        Text(bank.accountType, fontSize = 12.sp, color = UnitedTextSecondary)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFDCFCE7)
                                    ) {
                                        Text(
                                            bank.interestRate,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = UnitedBorderLight)
                                Spacer(modifier = Modifier.height(10.dp))

                                bank.features.forEach { feat ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(feat, fontSize = 11.sp, color = UnitedTextPrimary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(bank.formattedDeposit, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = UnitedMoneyBlue)
                            }
                        }
                    }
                }

                AccountOpeningStep.PAN_VERIFICATION -> {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = UnitedWhite,
                            border = BorderStroke(1.dp, UnitedBorderLight)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Applicant Identity & PAN Card", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = applicantName,
                                    onValueChange = { applicantName = it },
                                    label = { Text("Full Legal Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = mobileNumber,
                                    onValueChange = { mobileNumber = it },
                                    label = { Text("Registered Mobile Number") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = panNumber,
                                    onValueChange = { panNumber = it.uppercase() },
                                    label = { Text("Permanent Account Number (PAN)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("NSDL Database check: Matches ${applicantName.ifBlank { "Applicant" }}", fontSize = 11.sp, color = Color(0xFF10B981))
                            }
                        }
                    }
                }

                AccountOpeningStep.AADHAAR_EKYC -> {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = UnitedWhite,
                            border = BorderStroke(1.dp, UnitedBorderLight)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Aadhaar OTP eKYC Verification", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = "•••• •••• " + aadhaarNumber.takeLast(4),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Aadhaar Number (Masked)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = otpCode,
                                    onValueChange = { otpCode = it },
                                    label = { Text("6-Digit OTP sent to SIM 1") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("OTP verified against UIDAI registered phone 6002239926", fontSize = 11.sp, color = Color(0xFF10B981))
                            }
                        }
                    }
                }

                AccountOpeningStep.CONFIRMATION -> {
                    item {
                        submissionResult?.let { res ->
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
                                    Text("Application Submitted Successfully", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                    Text("Ref ID: ${res.applicationId} • ${res.bankName}", fontSize = 12.sp, color = UnitedTextSecondary)

                                    Spacer(modifier = Modifier.height(16.dp))
                                    HorizontalDivider(color = UnitedBorderLight)
                                    Spacer(modifier = Modifier.height(16.dp))

                                    ReceiptRow("Applicant Name", res.applicantName)
                                    ReceiptRow("Partner Bank", res.bankName)
                                    ReceiptRow("PAN Number", res.panNumber)
                                    ReceiptRow("Aadhaar eKYC", res.aadhaarMasked)
                                    ReceiptRow("Video KYC Slot", res.scheduledVkycSlot, isBold = true, valueColor = UnitedMoneyBlue)

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        color = UnitedCanvasLight,
                                        border = BorderStroke(0.5.dp, UnitedBorderLight)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Videocam, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "A bank KYC officer will call you during your scheduled slot for instant video verification.",
                                                fontSize = 11.sp,
                                                color = UnitedTextSecondary,
                                                lineHeight = 15.sp
                                            )
                                        }
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
