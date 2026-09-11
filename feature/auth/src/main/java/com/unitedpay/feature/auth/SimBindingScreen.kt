package com.unitedpay.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.components.UnitedPayLogo
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.session.UserSessionManager
import com.unitedpay.core.security.biometric.BiometricAuthHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class OnboardingStep {
    SIM_SELECTION,
    VERIFYING_SMS,
    SELECT_BANK,
    ACCOUNT_DISCOVERED,
    SET_UPI_PIN,
    BIOMETRIC_SETUP,
    ONBOARDING_SUCCESS
}

data class BankChoice(
    val name: String,
    val ifscPrefix: String,
    val initial: String,
    val bg: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimBindingScreen(
    onBindingSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val coroutineScope = rememberCoroutineScope()

    var currentStep by remember { mutableStateOf(OnboardingStep.SIM_SELECTION) }
    var selectedSimSlot by remember { mutableIntStateOf(1) }

    // Verification progress (1 = sending SMS, 2 = server check, 3 = bound)
    var verificationPhase by remember { mutableIntStateOf(1) }

    // Onboarding form state for SIM 2
    var selectedBank by remember {
        mutableStateOf(BankChoice("State Bank of India", "SBIN0001824", "SBI", Color(0xFF003366)))
    }
    var cardLast6 by remember { mutableStateOf("458219") }
    var cardExpiry by remember { mutableStateOf("08/29") }
    var mockOtp by remember { mutableStateOf("482910") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var pinErrorMessage by remember { mutableStateOf<String?>(null) }

    // Biometric fallback prompt state
    var showBiometricFallbackModal by remember { mutableStateOf(false) }

    val popularBanks = remember {
        listOf(
            BankChoice("State Bank of India", "SBIN0001824", "SBI", Color(0xFF003366)),
            BankChoice("HDFC Bank", "HDFC0001234", "HDFC", Color(0xFF004C8F)),
            BankChoice("ICICI Bank", "ICIC0000092", "ICICI", Color(0xFFF37024)),
            BankChoice("Axis Bank", "UTIB0000045", "AXIS", Color(0xFF97144D)),
            BankChoice("Punjab National Bank", "PUNB0002140", "PNB", Color(0xFFA21C2B)),
            BankChoice("Bank of Baroda", "BARB0000010", "BOB", Color(0xFFF26522)),
            BankChoice("Kotak Mahindra Bank", "KKBK0000123", "KOTAK", Color(0xFFED1C24)),
            BankChoice("Canara Bank", "CNRB0000456", "CANARA", Color(0xFF008080))
        )
    }

    // Function to trigger native Android biometric prompt
    fun triggerBiometricPrompt(
        targetPhone: String,
        targetSimSlot: Int,
        onSuccessAction: () -> Unit
    ) {
        if (activity != null && BiometricAuthHelper.canAuthenticate(activity)) {
            BiometricAuthHelper.showBiometricPrompt(
                activity = activity,
                title = "Unlock UnitedPay",
                subtitle = "Touch fingerprint sensor on your phone",
                onSuccess = {
                    UserSessionManager.login(targetPhone, targetSimSlot)
                    onSuccessAction()
                },
                onCancel = {
                    showBiometricFallbackModal = true
                },
                onError = { _, _ ->
                    showBiometricFallbackModal = true
                }
            )
        } else {
            // Hardware sensor not enrolled or unavailable -> proceed directly
            UserSessionManager.login(targetPhone, targetSimSlot)
            onSuccessAction()
        }
    }

    // Function to initiate SMS verification animation
    fun startSmsVerification() {
        currentStep = OnboardingStep.VERIFYING_SMS
        verificationPhase = 1
        coroutineScope.launch {
            delay(1000)
            verificationPhase = 2
            delay(1100)
            verificationPhase = 3
            delay(900)

            if (selectedSimSlot == 1) {
                // SIM 1 is Arunjyoti Changkakoty's registered account
                if (UserSessionManager.isBiometricEnabledForUser(UserSessionManager.SIM_1_PHONE)) {
                    triggerBiometricPrompt(
                        targetPhone = UserSessionManager.SIM_1_PHONE,
                        targetSimSlot = 1,
                        onSuccessAction = onBindingSuccess
                    )
                } else {
                    UserSessionManager.login(UserSessionManager.SIM_1_PHONE, 1)
                    onBindingSuccess()
                }
            } else {
                // SIM 2 check
                if (UserSessionManager.isUserOnboarded(UserSessionManager.SIM_2_PHONE)) {
                    if (UserSessionManager.isBiometricEnabledForUser(UserSessionManager.SIM_2_PHONE)) {
                        triggerBiometricPrompt(
                            targetPhone = UserSessionManager.SIM_2_PHONE,
                            targetSimSlot = 2,
                            onSuccessAction = onBindingSuccess
                        )
                    } else {
                        UserSessionManager.login(UserSessionManager.SIM_2_PHONE, 2)
                        onBindingSuccess()
                    }
                } else {
                    // New user onboarding journey!
                    currentStep = OnboardingStep.SELECT_BANK
                }
            }
        }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            if (currentStep != OnboardingStep.SIM_SELECTION && currentStep != OnboardingStep.VERIFYING_SMS && currentStep != OnboardingStep.ONBOARDING_SUCCESS) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentStep) {
                                OnboardingStep.SELECT_BANK -> "Select Your Bank"
                                OnboardingStep.ACCOUNT_DISCOVERED -> "Account Found"
                                OnboardingStep.SET_UPI_PIN -> "Set 6-Digit UPI PIN"
                                OnboardingStep.BIOMETRIC_SETUP -> "Security Setup"
                                else -> "UPI Registration"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = UnitedTextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            currentStep = when (currentStep) {
                                OnboardingStep.SELECT_BANK -> OnboardingStep.SIM_SELECTION
                                OnboardingStep.ACCOUNT_DISCOVERED -> OnboardingStep.SELECT_BANK
                                OnboardingStep.SET_UPI_PIN -> OnboardingStep.ACCOUNT_DISCOVERED
                                OnboardingStep.BIOMETRIC_SETUP -> OnboardingStep.SET_UPI_PIN
                                else -> OnboardingStep.SIM_SELECTION
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = UnitedTextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = UnitedWhite)
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (currentStep) {
                OnboardingStep.SIM_SELECTION -> {
                    SimSelectionView(
                        selectedSlot = selectedSimSlot,
                        onSlotSelected = { selectedSimSlot = it },
                        onProceed = { startSmsVerification() }
                    )
                }

                OnboardingStep.VERIFYING_SMS -> {
                    SmsVerifyingView(
                        slotNumber = selectedSimSlot,
                        phase = verificationPhase,
                        phoneNumber = if (selectedSimSlot == 1) "+91 ${UserSessionManager.SIM_1_PHONE}" else "+91 ${UserSessionManager.SIM_2_PHONE}"
                    )
                }

                OnboardingStep.SELECT_BANK -> {
                    BankSelectionView(
                        banks = popularBanks,
                        selectedBank = selectedBank,
                        onSelectBank = { bank ->
                            selectedBank = bank
                            currentStep = OnboardingStep.ACCOUNT_DISCOVERED
                        }
                    )
                }

                OnboardingStep.ACCOUNT_DISCOVERED -> {
                    AccountDiscoveredView(
                        bank = selectedBank,
                        phoneNumber = "+91 ${UserSessionManager.SIM_2_PHONE}",
                        onProceedToSetPin = { currentStep = OnboardingStep.SET_UPI_PIN }
                    )
                }

                OnboardingStep.SET_UPI_PIN -> {
                    SetUpiPinView(
                        bank = selectedBank,
                        cardLast6 = cardLast6,
                        onCardLast6Change = { if (it.length <= 6) cardLast6 = it },
                        cardExpiry = cardExpiry,
                        onCardExpiryChange = { if (it.length <= 5) cardExpiry = it },
                        mockOtp = mockOtp,
                        onMockOtpChange = { if (it.length <= 6) mockOtp = it },
                        newPin = newPin,
                        onNewPinChange = { if (it.length <= 6) newPin = it },
                        confirmPin = confirmPin,
                        onConfirmPinChange = { if (it.length <= 6) confirmPin = it },
                        errorMessage = pinErrorMessage,
                        onConfirmPin = {
                            if (newPin.length != 6) {
                                pinErrorMessage = "UPI PIN must be exactly 6 digits"
                            } else if (newPin != confirmPin) {
                                pinErrorMessage = "UPI PINs do not match"
                            } else {
                                pinErrorMessage = null
                                currentStep = OnboardingStep.BIOMETRIC_SETUP
                            }
                        }
                    )
                }

                OnboardingStep.BIOMETRIC_SETUP -> {
                    BiometricSetupView(
                        onEnableBiometric = {
                            if (activity != null && BiometricAuthHelper.canAuthenticate(activity)) {
                                BiometricAuthHelper.showBiometricPrompt(
                                    activity = activity,
                                    title = "Setup Fingerprint Lock",
                                    subtitle = "Touch sensor to register fingerprint with UnitedPay",
                                    onSuccess = {
                                        UserSessionManager.completeSim2Onboarding(
                                            bankName = selectedBank.name,
                                            maskedAccount = "•••• 9821",
                                            ifscCode = selectedBank.ifscPrefix,
                                            cardLast6 = cardLast6,
                                            cardExpiry = cardExpiry,
                                            upiPin = newPin,
                                            enableBiometric = true
                                        )
                                        currentStep = OnboardingStep.ONBOARDING_SUCCESS
                                    },
                                    onCancel = {
                                        UserSessionManager.completeSim2Onboarding(
                                            bankName = selectedBank.name,
                                            maskedAccount = "•••• 9821",
                                            ifscCode = selectedBank.ifscPrefix,
                                            cardLast6 = cardLast6,
                                            cardExpiry = cardExpiry,
                                            upiPin = newPin,
                                            enableBiometric = false
                                        )
                                        currentStep = OnboardingStep.ONBOARDING_SUCCESS
                                    },
                                    onError = { _, _ ->
                                        UserSessionManager.completeSim2Onboarding(
                                            bankName = selectedBank.name,
                                            maskedAccount = "•••• 9821",
                                            ifscCode = selectedBank.ifscPrefix,
                                            cardLast6 = cardLast6,
                                            cardExpiry = cardExpiry,
                                            upiPin = newPin,
                                            enableBiometric = false
                                        )
                                        currentStep = OnboardingStep.ONBOARDING_SUCCESS
                                    }
                                )
                            } else {
                                UserSessionManager.completeSim2Onboarding(
                                    bankName = selectedBank.name,
                                    maskedAccount = "•••• 9821",
                                    ifscCode = selectedBank.ifscPrefix,
                                    cardLast6 = cardLast6,
                                    cardExpiry = cardExpiry,
                                    upiPin = newPin,
                                    enableBiometric = false
                                )
                                currentStep = OnboardingStep.ONBOARDING_SUCCESS
                            }
                        },
                        onSkip = {
                            UserSessionManager.completeSim2Onboarding(
                                bankName = selectedBank.name,
                                maskedAccount = "•••• 9821",
                                ifscCode = selectedBank.ifscPrefix,
                                cardLast6 = cardLast6,
                                cardExpiry = cardExpiry,
                                upiPin = newPin,
                                enableBiometric = false
                            )
                            currentStep = OnboardingStep.ONBOARDING_SUCCESS
                        }
                    )
                }

                OnboardingStep.ONBOARDING_SUCCESS -> {
                    OnboardingSuccessView(
                        bankName = selectedBank.name,
                        vpa = "user98765@unitedpay",
                        onGetStarted = onBindingSuccess
                    )
                }
            }

            // Fallback Modal if Biometric Prompt is Dismissed
            if (showBiometricFallbackModal) {
                AlertDialog(
                    onDismissRequest = { showBiometricFallbackModal = false },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = UnitedMoneyBlue,
                            modifier = Modifier.size(36.dp)
                        )
                    },
                    title = {
                        Text(
                            text = "Biometric Verification",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = UnitedTextPrimary
                        )
                    },
                    text = {
                        Text(
                            text = if (selectedSimSlot == 1)
                                "Touch your phone's fingerprint sensor to unlock Arunjyoti Changkakoty's account."
                            else
                                "Touch your phone's fingerprint sensor to unlock your UnitedPay account.",
                            fontSize = 13.sp,
                            color = UnitedTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showBiometricFallbackModal = false
                                triggerBiometricPrompt(
                                    targetPhone = if (selectedSimSlot == 1) UserSessionManager.SIM_1_PHONE else UserSessionManager.SIM_2_PHONE,
                                    targetSimSlot = selectedSimSlot,
                                    onSuccessAction = onBindingSuccess
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UnitedRoyalBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Try Fingerprint Again", color = UnitedWhite, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showBiometricFallbackModal = false
                                UserSessionManager.login(
                                    if (selectedSimSlot == 1) UserSessionManager.SIM_1_PHONE else UserSessionManager.SIM_2_PHONE,
                                    selectedSimSlot
                                )
                                onBindingSuccess()
                            }
                        ) {
                            Text("Enter Directly", color = UnitedTextSecondary, fontWeight = FontWeight.Medium)
                        }
                    },
                    containerColor = UnitedWhite,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Sub-Views for Steps
// -------------------------------------------------------------------------------------------------

@Composable
private fun SimSelectionView(
    selectedSlot: Int,
    onSlotSelected: (Int) -> Unit,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        UnitedPayLogo(size = 80.dp, asCardBadge = true)

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Select SIM for UPI Verification",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = UnitedTextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "NPCI requires sending an encrypted SMS to bind your bank account securely to this device.",
            fontSize = 13.sp,
            color = UnitedTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // SIM 1 (Arunjyoti Changkakoty)
        SimSlotCard(
            slotNumber = 1,
            carrierName = "Jio 5G",
            phoneNumber = "+91 ${UserSessionManager.SIM_1_PHONE}",
            accountBadge = "Arunjyoti Changkakoty (Registered)",
            isSelected = selectedSlot == 1,
            onSelect = { onSlotSelected(1) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // SIM 2 (New User / Fresh Registration)
        SimSlotCard(
            slotNumber = 2,
            carrierName = "Airtel 5G",
            phoneNumber = "+91 ${UserSessionManager.SIM_2_PHONE}",
            accountBadge = if (UserSessionManager.isUserOnboarded(UserSessionManager.SIM_2_PHONE)) "Linked UPI Account" else "New Registration (Blank A/C)",
            isSelected = selectedSlot == 2,
            onSelect = { onSlotSelected(2) }
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = UnitedSuccess,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "End-to-End Encrypted via NPCI UPI Rail",
                fontSize = 11.sp,
                color = UnitedTextSecondary
            )
        }

        Button(
            onClick = onProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .defaultMinSize(minHeight = 52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = UnitedRoyalBlue)
        ) {
            Text(
                text = "Proceed with SIM $selectedSlot",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = UnitedWhite,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SimSlotCard(
    slotNumber: Int,
    carrierName: String,
    phoneNumber: String,
    accountBadge: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) UnitedRoyalBlue else UnitedBorderLight,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF0F5FF) else UnitedWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) UnitedRoyalBlue else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SimCard,
                        contentDescription = null,
                        tint = if (isSelected) UnitedWhite else UnitedTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SIM $slotNumber: $carrierName",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = UnitedTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = phoneNumber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = UnitedTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = accountBadge,
                        fontSize = 11.sp,
                        color = if (slotNumber == 1) UnitedMoneyBlue else Color(0xFFD97706),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (isSelected) UnitedRoyalBlue else UnitedBorderLight, CircleShape)
                    .background(if (isSelected) UnitedRoyalBlue else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(UnitedWhite))
                }
            }
        }
    }
}

@Composable
private fun SmsVerifyingView(
    slotNumber: Int,
    phase: Int,
    phoneNumber: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        UnitedPayLogo(size = 80.dp, asCardBadge = true)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Verifying SIM $slotNumber",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = UnitedTextPrimary
        )
        Text(
            text = phoneNumber,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = UnitedMoneyBlue
        )

        Spacer(modifier = Modifier.height(36.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = UnitedWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, UnitedBorderLight, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                VerificationStatusRow(
                    label = "Sending outbound encrypted SMS",
                    isComplete = phase >= 2,
                    isActive = phase == 1
                )
                VerificationStatusRow(
                    label = "Verifying mobile with NPCI switch",
                    isComplete = phase >= 3,
                    isActive = phase == 2
                )
                VerificationStatusRow(
                    label = "Device binding confirmed",
                    isComplete = phase >= 3,
                    isActive = phase == 3
                )
            }
        }
    }
}

@Composable
private fun VerificationStatusRow(
    label: String,
    isComplete: Boolean,
    isActive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isComplete) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDCFCE7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = UnitedSuccess, modifier = Modifier.size(16.dp))
            }
        } else if (isActive) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = UnitedRoyalBlue,
                strokeWidth = 2.5.dp
            )
        } else {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xFFCBD5E1), CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isComplete || isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isComplete || isActive) UnitedTextPrimary else UnitedTextSecondary
        )
    }
}

@Composable
private fun BankSelectionView(
    banks: List<BankChoice>,
    selectedBank: BankChoice,
    onSelectBank: (BankChoice) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Bank Account",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = UnitedTextPrimary
        )
        Text(
            text = "Choose the bank registered with your mobile number",
            fontSize = 12.sp,
            color = UnitedTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(banks) { bank ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(1.dp, if (bank == selectedBank) UnitedRoyalBlue else UnitedBorderLight, RoundedCornerShape(12.dp))
                        .clickable { onSelectBank(bank) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(bank.bg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(bank.initial, color = UnitedWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = bank.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = UnitedTextPrimary,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountDiscoveredView(
    bank: BankChoice,
    phoneNumber: String,
    onProceedToSetPin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCFCE7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = UnitedSuccess, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Bank Account Found!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = UnitedTextPrimary
        )
        Text(
            text = "Linked with $phoneNumber via NPCI CBS",
            fontSize = 12.sp,
            color = UnitedTextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = UnitedWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, UnitedBorderLight, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(bank.bg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(bank.initial, color = UnitedWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(bank.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = UnitedTextPrimary)
                        Text("Savings Account", fontSize = 12.sp, color = UnitedTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = UnitedBorderLight)
                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Account Number", fontSize = 12.sp, color = UnitedTextSecondary)
                    Text("•••• 9821", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("IFSC Code", fontSize = 12.sp, color = UnitedTextSecondary)
                    Text(bank.ifscPrefix, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("UPI Status", fontSize = 12.sp, color = UnitedTextSecondary)
                    Text("Requires PIN Setup", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onProceedToSetPin,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .defaultMinSize(minHeight = 52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = UnitedRoyalBlue)
        ) {
            Text("Set 6-Digit UPI PIN", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = UnitedWhite)
        }
    }
}

@Composable
private fun SetUpiPinView(
    bank: BankChoice,
    cardLast6: String,
    onCardLast6Change: (String) -> Unit,
    cardExpiry: String,
    onCardExpiryChange: (String) -> Unit,
    mockOtp: String,
    onMockOtpChange: (String) -> Unit,
    newPin: String,
    onNewPinChange: (String) -> Unit,
    confirmPin: String,
    onConfirmPinChange: (String) -> Unit,
    errorMessage: String?,
    onConfirmPin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Enter Debit Card Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
        Text("For ${bank.name} •••• 9821", fontSize = 12.sp, color = UnitedTextSecondary)

        Spacer(modifier = Modifier.height(18.dp))

        // Card fields row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = cardLast6,
                onValueChange = onCardLast6Change,
                label = { Text("Last 6 Digits") },
                placeholder = { Text("458219") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1.5f),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = cardExpiry,
                onValueChange = onCardExpiryChange,
                label = { Text("Valid Thru") },
                placeholder = { Text("MM/YY") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Auto Bank OTP
        OutlinedTextField(
            value = mockOtp,
            onValueChange = onMockOtpChange,
            label = { Text("Bank OTP (Auto-detected)") },
            trailingIcon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnitedSuccess)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = UnitedBorderLight)
        Spacer(modifier = Modifier.height(20.dp))

        Text("Create 6-Digit UPI PIN", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
        Text("This PIN will be required for every money transfer", fontSize = 12.sp, color = UnitedTextSecondary)

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = newPin,
            onValueChange = onNewPinChange,
            label = { Text("Set 6-Digit PIN") },
            placeholder = { Text("••••••") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPin,
            onValueChange = onConfirmPinChange,
            label = { Text("Confirm 6-Digit PIN") },
            placeholder = { Text("••••••") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onConfirmPin,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .defaultMinSize(minHeight = 52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = UnitedRoyalBlue)
        ) {
            Text("Confirm & Link Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = UnitedWhite)
        }
    }
}

@Composable
private fun BiometricSetupView(
    onEnableBiometric: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color(0xFFEBF2FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(54.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Enable Fingerprint Unlock",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = UnitedTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Unlock UnitedPay instantly using your phone's hardware biometric sensor. Kept safe in Android Keymaster secure enclave.",
            fontSize = 13.sp,
            color = UnitedTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onEnableBiometric,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .defaultMinSize(minHeight = 52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = UnitedRoyalBlue)
        ) {
            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = UnitedWhite, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enable Fingerprint Sensor", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UnitedWhite)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Skip for Now", fontSize = 14.sp, color = UnitedTextSecondary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun OnboardingSuccessView(
    bankName: String,
    vpa: String,
    onGetStarted: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCFCE7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = UnitedSuccess, modifier = Modifier.size(44.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Welcome to UnitedPay!",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = UnitedTextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Your UPI account has been activated successfully.",
            fontSize = 13.sp,
            color = UnitedTextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = UnitedWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, UnitedBorderLight, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("UPI ID", fontSize = 12.sp, color = UnitedTextSecondary)
                    Text(vpa, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedMoneyBlue)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Primary Account", fontSize = 12.sp, color = UnitedTextSecondary)
                    Text("$bankName (•••• 9821)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Opening Balance", fontSize = 12.sp, color = UnitedTextSecondary)
                    Text("₹10,000.00", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedSuccess)
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .defaultMinSize(minHeight = 52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = UnitedRoyalBlue)
        ) {
            Text("Start Using UnitedPay", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = UnitedWhite)
        }
    }
}
