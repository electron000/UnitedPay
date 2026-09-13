package com.unitedpay.feature.home.services.transfers

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedGlassCard
import com.unitedpay.core.designsystem.components.UnitedNpciMpinModalSheet
import com.unitedpay.core.designsystem.components.UnitedToast
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.BankAccount
import com.unitedpay.core.model.UpiCreditCard
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager

/**
 * Wrapper representing any financial instrument eligible for UPI balance check.
 */
sealed class BalanceCheckTarget {
    data class Bank(val account: BankAccount) : BalanceCheckTarget()
    data class Credit(val card: UpiCreditCard) : BalanceCheckTarget()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckBalanceScreen(onBackClick: () -> Unit) {
    val isSim1 = UserSessionManager.isSim1Active

    var bankAccounts by remember {
        mutableStateOf(UserSessionManager.getCurrentBankAccounts())
    }
    var creditCards by remember {
        mutableStateOf(UserSessionManager.getCurrentCreditCards())
    }

    // Map storing revealed balance values keyed by instrument id
    var revealedBalances by remember { mutableStateOf(mapOf<String, Double>()) }
    var revealedTimestamps by remember { mutableStateOf(mapOf<String, String>()) }

    // State for MPIN sheet
    var activeTargetForMpin by remember { mutableStateOf<BalanceCheckTarget?>(null) }

    // Dialog state for adding bank/card
    var showAddBankDialog by remember { mutableStateOf(false) }
    var showLinkCreditCardDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.banking.getLinkedBankAccounts().collect { res ->
            if (res is Resource.Success && res.data.isNotEmpty()) {
                bankAccounts = res.data
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Check Balance",
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary,
                            fontSize = 17.5.sp
                        )
                        Text(
                            text = "Bank accounts & RuPay credit cards",
                            fontSize = 11.5.sp,
                            color = UnitedTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = UnitedTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        UnitedToast.info("UPI Balance uses 256-bit NPCI encryption directly with your bank.")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Security Info",
                            tint = UnitedMoneyBlue
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // SECTION 1: BANK ACCOUNTS & LINKED DEBIT CARDS
            // ==========================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BANK ACCOUNTS (${bankAccounts.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextSecondary,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFFDCFCE7),
                        border = BorderStroke(0.5.dp, Color(0xFF16A34A))
                    ) {
                        Text(
                            text = "NPCI Verified",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            items(bankAccounts, key = { it.id }) { account ->
                val balance = revealedBalances[account.id]
                val timestamp = revealedTimestamps[account.id]

                BankAccountBalanceCard(
                    account = account,
                    revealedBalance = balance,
                    lastCheckedTime = timestamp,
                    onCheckBalanceClick = {
                        activeTargetForMpin = BalanceCheckTarget.Bank(account)
                    }
                )
            }

            // "+ Add Bank Account" Action Card
            item {
                AddInstrumentButton(
                    title = "Add Bank Account",
                    subtitle = "Link savings or current account via UPI",
                    icon = Icons.Default.AccountBalance,
                    onClick = { showAddBankDialog = true }
                )
            }

            // ==========================================
            // SECTION 2: RUPAY CREDIT CARDS ON UPI
            // ==========================================
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RUPAY CREDIT CARDS ON UPI (${creditCards.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextSecondary,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(0.5.dp, UnitedMoneyBlue.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "RuPay on UPI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedMoneyBlue,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (creditCards.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = UnitedWhite,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Credit Cards Linked Yet",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Link your RuPay credit card to pay merchants directly using UPI.",
                                fontSize = 11.5.sp,
                                color = UnitedTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(creditCards, key = { it.id }) { card ->
                    val availableLimit = revealedBalances[card.id]
                    val timestamp = revealedTimestamps[card.id]

                    CreditCardBalanceCard(
                        card = card,
                        revealedLimit = availableLimit,
                        lastCheckedTime = timestamp,
                        onCheckLimitClick = {
                            activeTargetForMpin = BalanceCheckTarget.Credit(card)
                        }
                    )
                }
            }

            // "+ Link RuPay Credit Card" Action Card
            item {
                AddInstrumentButton(
                    title = "Link RuPay Credit Card on UPI",
                    subtitle = "Pay from credit card at any UPI QR code",
                    icon = Icons.Default.CreditCard,
                    onClick = { showLinkCreditCardDialog = true }
                )
            }

            // ==========================================
            // SECTION 3: WALLETS & DIGITAL CURRENCY
            // ==========================================
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "PREPAID WALLETS & DIGITAL CURRENCY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedTextSecondary,
                    letterSpacing = 1.sp
                )
            }

            item {
                UnitedGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 14.dp,
                    elevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = UnitedMoneyBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Hookolu RuPay Prepaid Wallet",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text("Active • Contactless •••• 9024", fontSize = 11.sp, color = UnitedTextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "₹14,250.00",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = UnitedMoneyBlue,
                                maxLines = 1
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF0FDF4)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CurrencyRupee,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "RBI Digital Rupee (e₹)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text("Official CBDC Wallet", fontSize = 11.sp, color = UnitedTextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "e₹ 1,500.00",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF16A34A),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // NPCI 6-DIGIT MPIN MODAL SHEET
        // ==========================================
        if (activeTargetForMpin != null) {
            val target = activeTargetForMpin!!
            val subtitleText = when (target) {
                is BalanceCheckTarget.Bank -> "Checking Balance for ${target.account.bankName} (${target.account.accountNumberMasked})"
                is BalanceCheckTarget.Credit -> "Checking Available Limit for ${target.card.cardName} (${target.card.cardNumberMasked})"
            }

            UnitedNpciMpinModalSheet(
                title = "NPCI UPI SECURE MPIN",
                subtitle = subtitleText,
                pinLength = 6,
                onPinSubmitted = { _ ->
                    when (target) {
                        is BalanceCheckTarget.Bank -> {
                            val bal = target.account.balance ?: 24850.50
                            revealedBalances = revealedBalances + (target.account.id to bal)
                            revealedTimestamps = revealedTimestamps + (target.account.id to "Just now")
                        }
                        is BalanceCheckTarget.Credit -> {
                            val limit = target.card.availableLimit
                            revealedBalances = revealedBalances + (target.card.id to limit)
                            revealedTimestamps = revealedTimestamps + (target.card.id to "Just now")
                        }
                    }
                    activeTargetForMpin = null
                },
                onDismissRequest = { activeTargetForMpin = null }
            )
        }

        // Balance is shown inline on each card — no slide-up receipt needed

        // Add Bank Account Dialog
        if (showAddBankDialog) {
            AlertDialog(
                onDismissRequest = { showAddBankDialog = false },
                icon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = UnitedMoneyBlue) },
                title = { Text("Add Bank Account", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                text = {
                    Text(
                        text = "United Pay will discover your bank accounts registered with mobile number +91 94350 9926 via NPCI SIM binding.",
                        fontSize = 13.sp,
                        color = UnitedTextSecondary,
                        lineHeight = 18.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showAddBankDialog = false
                            UnitedToast.success("Bank accounts discovery initiated via NPCI.")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue)
                    ) {
                        Text("Discover Accounts")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddBankDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Link Credit Card Dialog
        if (showLinkCreditCardDialog) {
            AlertDialog(
                onDismissRequest = { showLinkCreditCardDialog = false },
                icon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = UnitedMoneyBlue) },
                title = { Text("Link RuPay Credit Card", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                text = {
                    Text(
                        text = "Link your RuPay Credit Card on UPI to make seamless merchant payments. Supported banks: HDFC, ICICI, SBI, Axis, PNB, Canara, and Kotak.",
                        fontSize = 13.sp,
                        color = UnitedTextSecondary,
                        lineHeight = 18.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLinkCreditCardDialog = false
                            UnitedToast.success("RuPay Credit Card discovery initiated.")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue)
                    ) {
                        Text("Link RuPay Card")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLinkCreditCardDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

/**
 * Bank Account Card with linked Debit Card details and inline Balance check/reveal.
 */
@Composable
private fun BankAccountBalanceCard(
    account: BankAccount,
    revealedBalance: Double?,
    lastCheckedTime: String?,
    onCheckBalanceClick: () -> Unit
) {
    UnitedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Bank Icon, Bank Name, Account info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                account.bankName.contains("State Bank", ignoreCase = true) -> Color(0xFF1E3A8A)
                                account.bankName.contains("HDFC", ignoreCase = true) -> Color(0xFF0F172A)
                                account.bankName.contains("ICICI", ignoreCase = true) -> Color(0xFF831843)
                                else -> UnitedMoneyBlue
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = UnitedWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = account.bankName,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (account.isPrimary) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = "PRIMARY",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${account.accountType.lowercase().replaceFirstChar { it.uppercase() }} A/C ${account.accountNumberMasked}",
                        fontSize = 12.sp,
                        color = UnitedTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Linked Debit Card Strip (Debit card links to same bank account)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = UnitedTextSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Linked Debit Card: RuPay Platinum ${account.linkedDebitCardMasked}",
                    fontSize = 11.5.sp,
                    color = UnitedTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Balance Display / Action Row
            if (revealedBalance != null) {
                // Revealed Balance State
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Available Balance ($lastCheckedTime)",
                                fontSize = 11.sp,
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹ ${String.format("%.2f", revealedBalance)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UnitedMoneyBlue
                        )
                    }

                    // Refresh Button
                    IconButton(
                        onClick = onCheckBalanceClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Balance",
                            tint = UnitedMoneyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                // Unchecked State: "Check Balance" text button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Balance: ••••••••",
                        fontSize = 12.sp,
                        color = UnitedTextSecondary
                    )

                    OutlinedButton(
                        onClick = onCheckBalanceClick,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, UnitedMoneyBlue),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = UnitedMoneyBlue,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Check Balance", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = UnitedMoneyBlue)
                    }
                }
            }
        }
    }
}

/**
 * RuPay Credit Card Card with credit limit and inline Available Limit check/reveal.
 */
@Composable
private fun CreditCardBalanceCard(
    card: UpiCreditCard,
    revealedLimit: Double?,
    lastCheckedTime: String?,
    onCheckLimitClick: () -> Unit
) {
    UnitedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Card Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1E293B), Color(0xFF334155))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.cardName,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${card.cardNetwork} Credit Card ${card.cardNumberMasked}",
                        fontSize = 12.sp,
                        color = UnitedTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Text(
                        text = "Limit: ₹${String.format("%,.0f", card.totalLimit)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Balance Display / Action Row
            if (revealedLimit != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Available Limit ($lastCheckedTime)",
                            fontSize = 11.sp,
                            color = Color(0xFF15803D),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹ ${String.format("%.2f", revealedLimit)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF15803D)
                        )
                        Text(
                            text = "Outstanding: ₹${String.format("%.2f", card.outstandingAmount)}",
                            fontSize = 11.sp,
                            color = UnitedTextSecondary
                        )
                    }

                    IconButton(
                        onClick = onCheckLimitClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Limit",
                            tint = UnitedMoneyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Outstanding: ₹${String.format("%.2f", card.outstandingAmount)}",
                        fontSize = 12.sp,
                        color = UnitedTextSecondary
                    )

                    OutlinedButton(
                        onClick = onCheckLimitClick,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Check Limit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    }
                }
            }
        }
    }
}

/**
 * Add Instrument Action Button with dashed/subtle border.
 */
@Composable
private fun AddInstrumentButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = UnitedWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(UnitedMoneyBlue.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = UnitedMoneyBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedMoneyBlue
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = UnitedTextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = UnitedTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
