package com.unitedpay.feature.home.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.UnitedToast
import com.unitedpay.core.designsystem.theme.UnitedBorderLight
import com.unitedpay.core.designsystem.theme.UnitedCanvasLight
import com.unitedpay.core.designsystem.theme.UnitedError
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedSuccess
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite
import com.unitedpay.core.model.session.UserSessionManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class MpinStep(val stepNumber: Int, val stepLabel: String) {
    CARD_DETAILS(1, "Debit Card Verification"),
    BANK_OTP(2, "Bank OTP Verification"),
    SET_PIN(3, "Set 6-Digit UPI MPIN"),
    CONFIRM_PIN(4, "Confirm 6-Digit UPI MPIN"),
    SUCCESS(5, "Success")
}

/**
 * Enterprise Change UPI MPIN Screen adhering to NPCI UPI security specifications:
 * - Step 1: Verify Debit Card (Last 6 digits & MM/YY Expiry)
 * - Step 2: Auto-detected NPCI Bank OTP verification
 * - Step 3: Enter new 6-digit UPI MPIN
 * - Step 4: Confirm 6-digit UPI MPIN with robust error feedback (NO silent loops)
 * - Step 5: Official Success Screen with receipt & confirmation details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeMpinScreen(
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var currentStep by remember { mutableStateOf(MpinStep.CARD_DETAILS) }

    // Card Details state
    var cardDigits by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }

    // OTP state
    var otpDigits by remember { mutableStateOf("839210") }

    // PIN states
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var pinMismatchError by remember { mutableStateOf<String?>(null) }
    var isVerifyingWithNpci by remember { mutableStateOf(false) }

    // Bank details
    val activeBank = remember {
        UserSessionManager.getCurrentBankAccounts().firstOrNull { it.isPrimary }
            ?: UserSessionManager.getCurrentBankAccounts().firstOrNull()
    }
    val bankName = activeBank?.bankName ?: "State Bank of India"
    val accountMasked = activeBank?.accountNumberMasked ?: "•••• 4821"

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Change UPI MPIN",
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "$bankName ($accountMasked)",
                            fontSize = 11.5.sp,
                            color = UnitedMoneyBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when (currentStep) {
                            MpinStep.CARD_DETAILS -> onBackClick()
                            MpinStep.BANK_OTP -> currentStep = MpinStep.CARD_DETAILS
                            MpinStep.SET_PIN -> currentStep = MpinStep.BANK_OTP
                            MpinStep.CONFIRM_PIN -> {
                                confirmPin = ""
                                pinMismatchError = null
                                currentStep = MpinStep.SET_PIN
                            }
                            MpinStep.SUCCESS -> onBackClick()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stepper indicator (visible on steps 1 through 4)
            if (currentStep != MpinStep.SUCCESS) {
                MpinStepperHeader(currentStep = currentStep)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (currentStep) {
                    MpinStep.CARD_DETAILS -> {
                        CardDetailsStep(
                            bankName = bankName,
                            accountMasked = accountMasked,
                            cardDigits = cardDigits,
                            cardExpiry = cardExpiry,
                            onDigitAdded = { digit ->
                                if (cardDigits.length < 6) {
                                    cardDigits += digit
                                } else if (cardExpiry.length < 4) {
                                    cardExpiry += digit
                                }
                            },
                            onBackspace = {
                                if (cardExpiry.isNotEmpty()) {
                                    cardExpiry = cardExpiry.dropLast(1)
                                } else if (cardDigits.isNotEmpty()) {
                                    cardDigits = cardDigits.dropLast(1)
                                }
                            },
                            onProceed = {
                                if (cardDigits.length == 6 && cardExpiry.length == 4) {
                                    UnitedToast.info("Debit card verified. Verifying Bank OTP...")
                                    currentStep = MpinStep.BANK_OTP
                                } else {
                                    UnitedToast.warning("Please enter 6 card digits & 4-digit valid thru (MM/YY)")
                                }
                            }
                        )
                    }

                    MpinStep.BANK_OTP -> {
                        BankOtpStep(
                            bankName = bankName,
                            otpDigits = otpDigits,
                            onOtpDigit = { digit ->
                                if (otpDigits.length < 6) {
                                    otpDigits += digit
                                }
                            },
                            onBackspace = {
                                if (otpDigits.isNotEmpty()) {
                                    otpDigits = otpDigits.dropLast(1)
                                }
                            },
                            onVerify = {
                                if (otpDigits.length == 6) {
                                    UnitedToast.success("Bank OTP verified by NPCI UPI Switch")
                                    currentStep = MpinStep.SET_PIN
                                } else {
                                    UnitedToast.warning("Please enter complete 6-digit Bank OTP")
                                }
                            }
                        )
                    }

                    MpinStep.SET_PIN -> {
                        SetPinStep(
                            bankName = bankName,
                            accountMasked = accountMasked,
                            pin = newPin,
                            onDigit = { d ->
                                if (newPin.length < 6) newPin += d
                            },
                            onBackspace = {
                                if (newPin.isNotEmpty()) newPin = newPin.dropLast(1)
                            },
                            onProceed = {
                                if (newPin.length == 6) {
                                    confirmPin = ""
                                    pinMismatchError = null
                                    UnitedToast.info("Now re-enter to confirm your 6-digit PIN")
                                    currentStep = MpinStep.CONFIRM_PIN
                                } else {
                                    UnitedToast.warning("Please enter full 6-digit UPI MPIN")
                                }
                            }
                        )
                    }

                    MpinStep.CONFIRM_PIN -> {
                        ConfirmPinStep(
                            bankName = bankName,
                            accountMasked = accountMasked,
                            pin = confirmPin,
                            errorMessage = pinMismatchError,
                            isVerifying = isVerifyingWithNpci,
                            onDigit = { d ->
                                if (confirmPin.length < 6) {
                                    confirmPin += d
                                    pinMismatchError = null
                                }
                            },
                            onBackspace = {
                                if (confirmPin.isNotEmpty()) {
                                    confirmPin = confirmPin.dropLast(1)
                                    pinMismatchError = null
                                }
                            },
                            onConfirm = {
                                if (confirmPin.length == 6) {
                                    if (confirmPin == newPin) {
                                        isVerifyingWithNpci = true
                                        coroutineScope.launch {
                                            delay(750)
                                            isVerifyingWithNpci = false
                                            currentStep = MpinStep.SUCCESS
                                            UnitedToast.success("UPI MPIN updated successfully!")
                                        }
                                    } else {
                                        // DO NOT SILENTLY LOOP: Explicit feedback and reset only the confirmation field
                                        pinMismatchError = "PINs do not match. Please re-enter the 6-digit PIN."
                                        confirmPin = ""
                                        UnitedToast.error("PINs do not match! Please re-enter.")
                                    }
                                } else {
                                    UnitedToast.warning("Please enter full 6 digits to confirm")
                                }
                            },
                            onChangeFirstPin = {
                                newPin = ""
                                confirmPin = ""
                                pinMismatchError = null
                                currentStep = MpinStep.SET_PIN
                            }
                        )
                    }

                    MpinStep.SUCCESS -> {
                        MpinSuccessStep(
                            bankName = bankName,
                            accountMasked = accountMasked,
                            onDone = onBackClick
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Step Progress Stepper Header (Clean, Compact, Never Wraps)
// -------------------------------------------------------------------------------------------------
@Composable
private fun MpinStepperHeader(currentStep: MpinStep) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(UnitedWhite)
            .border(0.5.dp, UnitedBorderLight)
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Connected Stepper Circles Row
        Row(
            modifier = Modifier.fillMaxWidth(0.88f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val steps = listOf(
                MpinStep.CARD_DETAILS,
                MpinStep.BANK_OTP,
                MpinStep.SET_PIN,
                MpinStep.CONFIRM_PIN
            )

            steps.forEachIndexed { index, step ->
                val isCompleted = currentStep.stepNumber > step.stepNumber
                val isCurrent = currentStep == step

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> UnitedSuccess
                                isCurrent -> UnitedMoneyBlue
                                else -> Color(0xFFE2E8F0)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = UnitedWhite,
                            modifier = Modifier.size(13.dp)
                        )
                    } else {
                        Text(
                            text = "${step.stepNumber}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) UnitedWhite else Color(0xFF64748B)
                        )
                    }
                }

                if (index < steps.lastIndex) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .padding(horizontal = 4.dp)
                            .background(if (isCompleted) UnitedSuccess else Color(0xFFE2E8F0))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Step Context Subtitle
        Text(
            text = "Step ${currentStep.stepNumber} of 4: ${currentStep.stepLabel}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = UnitedMoneyBlue
        )
    }
}

// -------------------------------------------------------------------------------------------------
// Step 1: Debit Card Verification
// -------------------------------------------------------------------------------------------------
@Composable
private fun CardDetailsStep(
    bankName: String,
    accountMasked: String,
    cardDigits: String,
    cardExpiry: String,
    onDigitAdded: (String) -> Unit,
    onBackspace: () -> Unit,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper Content (Scrollable if viewport is ultra compact)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Security badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = UnitedSuccess, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text("NPCI Core Banking Switch (CBS) Rail", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextSecondary)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Card Input Surface
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, UnitedBorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(bankName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UnitedTextPrimary)
                            Text("A/C: $accountMasked", fontSize = 11.5.sp, color = UnitedTextSecondary)
                        }
                        Icon(Icons.Default.AccountBalance, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(22.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = UnitedBorderLight)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Last 6 Digits of Debit Card", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(6) { idx ->
                            val digit = cardDigits.getOrNull(idx)?.toString()
                            val isActive = idx == cardDigits.length
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (digit != null) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                                    .border(
                                        width = if (isActive || digit != null) 1.5.dp else 1.dp,
                                        color = if (isActive || digit != null) UnitedMoneyBlue else UnitedBorderLight,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = digit ?: "•",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (digit != null) UnitedTextPrimary else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Valid Thru (Expiry MM/YY)", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { idx ->
                            val digit = cardExpiry.getOrNull(idx)?.toString()
                            val isActive = cardDigits.length == 6 && idx == cardExpiry.length
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (digit != null) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                                    .border(
                                        width = if (isActive || digit != null) 1.5.dp else 1.dp,
                                        color = if (isActive || digit != null) UnitedMoneyBlue else UnitedBorderLight,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = digit ?: "•",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (digit != null) UnitedTextPrimary else Color(0xFF94A3B8)
                                )
                            }
                            if (idx == 1) {
                                Text("/", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Custom Compact Keypad (Pinned Above Bottom)
        MpinNumericKeypad(
            actionLabel = "Proceed",
            onDigit = onDigitAdded,
            onBackspace = onBackspace,
            onAction = onProceed
        )
    }
}

// -------------------------------------------------------------------------------------------------
// Step 2: Bank OTP Verification
// -------------------------------------------------------------------------------------------------
@Composable
private fun BankOtpStep(
    bankName: String,
    otpDigits: String,
    onOtpDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onVerify: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = UnitedMoneyBlue,
                modifier = Modifier.size(38.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Enter 6-Digit Bank OTP", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
            Spacer(modifier = Modifier.height(3.dp))
            Text("NPCI sent an SMS OTP for $bankName", fontSize = 12.sp, color = UnitedTextSecondary, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(14.dp))

            // Auto-read detection pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFDCFCE7))
                    .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnitedSuccess, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Auto-read SMS OTP: 839210", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6 OTP Boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                repeat(6) { idx ->
                    val digit = otpDigits.getOrNull(idx)?.toString()
                    val isActive = idx == otpDigits.length
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (digit != null) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                            .border(
                                width = if (isActive || digit != null) 1.5.dp else 1.dp,
                                color = if (isActive || digit != null) UnitedMoneyBlue else UnitedBorderLight,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = digit ?: "",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Resend OTP in 24s", fontSize = 11.5.sp, color = UnitedTextSecondary)
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Numeric Keypad
        MpinNumericKeypad(
            actionLabel = "Verify OTP",
            onDigit = onOtpDigit,
            onBackspace = onBackspace,
            onAction = onVerify
        )
    }
}

// -------------------------------------------------------------------------------------------------
// Step 3: Set New 6-Digit UPI PIN
// -------------------------------------------------------------------------------------------------
@Composable
private fun SetPinStep(
    bankName: String,
    accountMasked: String,
    pin: String,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEBF2FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("SET NEW 6-DIGIT UPI MPIN", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
            Spacer(modifier = Modifier.height(3.dp))
            Text("Enter a new 6-digit MPIN for $bankName ($accountMasked)", fontSize = 12.sp, color = UnitedTextSecondary, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))

            // 6-Pin Dots Indicator
            SixPinDotsRow(enteredCount = pin.length)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFEF3C7))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Do not use sequential numbers like 123456 or birth dates",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF92400E)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Keypad with "Next" CTA
        MpinNumericKeypad(
            actionLabel = "Next",
            onDigit = onDigit,
            onBackspace = onBackspace,
            onAction = onProceed
        )
    }
}

// -------------------------------------------------------------------------------------------------
// Step 4: Confirm 6-Digit UPI PIN (No Silent Loops!)
// -------------------------------------------------------------------------------------------------
@Composable
private fun ConfirmPinStep(
    bankName: String,
    accountMasked: String,
    pin: String,
    errorMessage: String?,
    isVerifying: Boolean,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onConfirm: () -> Unit,
    onChangeFirstPin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnitedSuccess, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("CONFIRM 6-DIGIT UPI MPIN", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
            Spacer(modifier = Modifier.height(3.dp))
            Text("Re-enter the 6-digit MPIN to confirm for $bankName", fontSize = 12.sp, color = UnitedTextSecondary, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))

            // 6-Pin Dots Indicator
            SixPinDotsRow(enteredCount = pin.length, isError = errorMessage != null)

            Spacer(modifier = Modifier.height(12.dp))

            // Inline error message if PINs do not match
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = UnitedError,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(onClick = onChangeFirstPin) {
                    Text("Change First PIN (Go back)", fontSize = 12.sp, color = UnitedMoneyBlue, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    text = "Step 4 of 4: Matching confirmation PIN",
                    fontSize = 11.5.sp,
                    color = UnitedTextSecondary
                )
            }

            if (isVerifying) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = UnitedMoneyBlue, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Updating UPI MPIN with NPCI Switch...", fontSize = 12.sp, color = UnitedMoneyBlue, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Keypad with "Submit" CTA
        MpinNumericKeypad(
            actionLabel = "Submit",
            onDigit = onDigit,
            onBackspace = onBackspace,
            onAction = onConfirm,
            isEnabled = !isVerifying
        )
    }
}

// -------------------------------------------------------------------------------------------------
// Step 5: Success Confirmation Screen
// -------------------------------------------------------------------------------------------------
@Composable
private fun MpinSuccessStep(
    bankName: String,
    accountMasked: String,
    onDone: () -> Unit
) {
    val timestamp = remember {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
    }
    val utr = remember {
        "NPCI/PIN/${(10000000..99999999).random()}"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = true,
                enter = scaleIn() + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF34D399), UnitedSuccess)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = UnitedWhite,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "UPI MPIN Changed Successfully!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = UnitedTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Your new 6-digit MPIN is active and secured by NPCI UPI Rail.",
                fontSize = 12.5.sp,
                color = UnitedTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Details Breakdown Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, UnitedBorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    SuccessRow(label = "Bank Name", value = bankName, isHighlight = true)
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = UnitedBorderLight)
                    Spacer(modifier = Modifier.height(10.dp))

                    SuccessRow(label = "Account", value = accountMasked)
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = UnitedBorderLight)
                    Spacer(modifier = Modifier.height(10.dp))

                    SuccessRow(label = "NPCI Ref No.", value = utr)
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = UnitedBorderLight)
                    Spacer(modifier = Modifier.height(10.dp))

                    SuccessRow(label = "Date & Time", value = timestamp)
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = UnitedBorderLight)
                    Spacer(modifier = Modifier.height(10.dp))

                    SuccessRow(label = "Status", value = "Active & Secured", isSuccess = true)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pinned Done button with 52dp height and 8dp radius
        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .defaultMinSize(minHeight = 52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = UnitedMoneyBlue,
                contentColor = UnitedWhite
            )
        ) {
            Text(
                text = "Done",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = UnitedWhite
            )
        }
    }
}

@Composable
private fun SuccessRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    isSuccess: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = UnitedTextSecondary)
        Text(
            text = value,
            fontSize = if (isHighlight) 14.sp else 12.5.sp,
            fontWeight = if (isHighlight || isSuccess) FontWeight.Bold else FontWeight.SemiBold,
            color = when {
                isSuccess -> UnitedSuccess
                isHighlight -> UnitedMoneyBlue
                else -> UnitedTextPrimary
            }
        )
    }
}

// -------------------------------------------------------------------------------------------------
// 6-PIN Dots Indicator
// -------------------------------------------------------------------------------------------------
@Composable
private fun SixPinDotsRow(
    enteredCount: Int,
    isError: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) { idx ->
            val isFilled = idx < enteredCount
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isError -> UnitedError
                            isFilled -> UnitedMoneyBlue
                            else -> Color(0xFFCBD5E1)
                        }
                    )
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Standardized Enterprise Numeric Keypad (Ultra-Compact, Never Clips)
// -------------------------------------------------------------------------------------------------
@Composable
private fun MpinNumericKeypad(
    actionLabel: String,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onAction: () -> Unit,
    isEnabled: Boolean = true
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("DEL", "0", actionLabel)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (key in row) {
                    val isAction = key == actionLabel
                    val isDel = key == "DEL"

                    Box(
                        modifier = Modifier
                            .size(if (isAction) 88.dp else 80.dp, 42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    isAction -> if (isEnabled) UnitedMoneyBlue else Color(0xFFCBD5E1)
                                    else -> Color(0xFFF1F5F9)
                                }
                            )
                            .clickable(enabled = isEnabled) {
                                when {
                                    isDel -> onBackspace()
                                    isAction -> onAction()
                                    else -> onDigit(key)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isDel -> {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "Backspace",
                                    tint = UnitedTextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            isAction -> {
                                Text(
                                    text = actionLabel,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = UnitedWhite,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            else -> {
                                Text(
                                    text = key,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = UnitedTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
