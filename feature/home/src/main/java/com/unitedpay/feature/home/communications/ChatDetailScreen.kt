package com.unitedpay.feature.home.communications

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.unitedpay.core.designsystem.components.UnitedToast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.unitedpay.core.designsystem.components.UnitedCenteredAmountField
import com.unitedpay.core.designsystem.components.UnitedNpciMpinSheet
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.designsystem.util.ReceiptShareHelper
import com.unitedpay.core.model.PaymentStatus
import com.unitedpay.core.model.TransactionRepository
import com.unitedpay.core.model.TransactionType
import com.unitedpay.core.model.UpiTransaction
import com.unitedpay.feature.home.services.transfers.CheckBalanceDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.model.ChatMessage
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.mock.UnitedMockData
import com.unitedpay.core.model.session.UserSessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    contactId: String = "ramesh",
    contactName: String = "Ramesh Sharma",
    contactVpa: String = "ramesh@unitedpay",
    onBackClick: () -> Unit,
    onNavigateToTransactionDetail: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var showCheckBalanceDialog by remember { mutableStateOf(false) }

    fun mapMessageToEntry(msg: ChatMessage): ChatEntry = when (msg.type) {
        "DATE_HEADER" -> ChatEntry.DateHeader(msg.text)
        "PAYMENT_SENT" -> ChatEntry.PaymentSent(
            id = msg.id,
            amount = msg.amount ?: "450",
            recipientName = contactName,
            time = msg.time,
            status = "Paid",
            utr = msg.utr ?: "429104829104",
            bank = msg.bankMasked ?: "State Bank of India •••• 8821"
        )
        "PAYMENT_RECEIVED" -> ChatEntry.PaymentReceived(
            id = msg.id,
            amount = msg.amount ?: "1,200",
            senderName = contactName,
            time = msg.time,
            status = "Received",
            utr = msg.utr ?: "429188021940",
            bank = msg.bankMasked ?: "State Bank of India •••• 8821"
        )
        "PAYMENT_REQUEST" -> ChatEntry.PaymentRequest(
            id = msg.id,
            amount = msg.amount ?: "250",
            requesterName = contactName,
            note = msg.note ?: "",
            time = msg.time,
            isPaid = msg.isPaid
        )
        else -> ChatEntry.TextMsg(
            id = msg.id,
            text = msg.text,
            time = msg.time,
            isFromMe = msg.isFromMe
        )
    }

    // Interactive Chat History
    val chatHistory = remember {
        mutableStateListOf<ChatEntry>().apply {
            addAll(com.unitedpay.core.model.session.UserSessionManager.getCurrentChatMessages(contactId).map { mapMessageToEntry(it) })
        }
    }

    LaunchedEffect(contactId) {
        UnitedPayApi.client.communications.getChatMessages(contactId).collect { result ->
            if (result is Resource.Success) {
                chatHistory.clear()
                chatHistory.addAll(result.data.map { mapMessageToEntry(it) })
            }
        }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    // Interactive Slide-Up Panel State (Pay Flow)
    var isPayPanelOpen by remember { mutableStateOf(false) }
    var payStep by remember { mutableStateOf(PayFlowStep.CONFIRM_AMOUNT) }
    var targetPayAmount by remember { mutableStateOf("") }
    var targetPayNote by remember { mutableStateOf("") }
    var enteredPin by remember { mutableStateOf("") }
    var associatedRequestId by remember { mutableStateOf<String?>(null) }

    // Interactive Slide-Up Panel State (Request Flow)
    var isRequestPanelOpen by remember { mutableStateOf(false) }
    var requestAmountInput by remember { mutableStateOf("") }
    var requestNoteInput by remember { mutableStateOf("") }
    var isRequestSuccessStep by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E88E5)),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials = contactName.split(" ")
                                .take(2)
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .joinToString("")
                            Text(
                                initials,
                                color = UnitedWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    contactName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = UnitedTextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = UnitedMoneyBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                contactVpa,
                                fontSize = 11.sp,
                                color = UnitedTextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = UnitedTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        UnitedToast.info("Voice call via UPI coming soon")
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = UnitedTextPrimary)
                    }
                    IconButton(onClick = {
                        UnitedToast.info("UPI User: $contactVpa")
                    }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = UnitedTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UnitedWhite,
                    titleContentColor = UnitedTextPrimary,
                    navigationIconContentColor = UnitedTextPrimary,
                    actionIconContentColor = UnitedTextPrimary
                )
            )
        },
        containerColor = UnitedBackgroundLight,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding(),
                color = UnitedWhite,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // Quick Action Pills Row (Pay & Request with executive 8dp radii)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // PAY BUTTON
                        Button(
                            onClick = {
                                targetPayAmount = ""
                                targetPayNote = ""
                                enteredPin = ""
                                associatedRequestId = null
                                payStep = PayFlowStep.CONFIRM_AMOUNT
                                isPayPanelOpen = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UnitedMoneyBlue,
                                contentColor = UnitedWhite
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                "Pay",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = UnitedWhite
                            )
                        }

                        // REQUEST BUTTON
                        OutlinedButton(
                            onClick = {
                                requestAmountInput = ""
                                requestNoteInput = ""
                                isRequestSuccessStep = false
                                isRequestPanelOpen = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = UnitedMoneyBlue
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = Brush.linearGradient(listOf(UnitedMoneyBlue, UnitedMoneyBlue)),
                                width = 1.5.dp
                            )
                        ) {
                            Text(
                                "Request",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = UnitedMoneyBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Text Input Bar with Quick Send
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Message or enter ₹ amount...", fontSize = 13.sp, color = UnitedTextSecondary) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = TextStyle(color = UnitedTextPrimary, fontSize = 14.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = UnitedTextPrimary,
                                unfocusedTextColor = UnitedTextPrimary,
                                cursorColor = UnitedMoneyBlue,
                                focusedBorderColor = UnitedMoneyBlue,
                                unfocusedBorderColor = UnitedBorderLight,
                                focusedContainerColor = UnitedSurfaceSubtle,
                                unfocusedContainerColor = UnitedSurfaceSubtle
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (inputText.isNotBlank()) {
                                    val trimmed = inputText.trim()
                                    val amountOnly = trimmed.removePrefix("₹").trim().toIntOrNull()
                                    if (amountOnly != null && amountOnly > 0) {
                                        targetPayAmount = amountOnly.toString()
                                        targetPayNote = ""
                                        enteredPin = ""
                                        associatedRequestId = null
                                        payStep = PayFlowStep.CONFIRM_AMOUNT
                                        isPayPanelOpen = true
                                    } else {
                                        chatHistory.add(
                                            ChatEntry.TextMsg(
                                                id = "m_${System.currentTimeMillis()}",
                                                text = trimmed,
                                                time = "Now",
                                                isFromMe = true
                                            )
                                        )
                                    }
                                    inputText = ""
                                }
                            })
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Send Button
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    val trimmed = inputText.trim()
                                    val amountOnly = trimmed.removePrefix("₹").trim().toIntOrNull()
                                    if (amountOnly != null && amountOnly > 0) {
                                        targetPayAmount = amountOnly.toString()
                                        targetPayNote = ""
                                        enteredPin = ""
                                        associatedRequestId = null
                                        payStep = PayFlowStep.CONFIRM_AMOUNT
                                        isPayPanelOpen = true
                                    } else {
                                        chatHistory.add(
                                            ChatEntry.TextMsg(
                                                id = "m_${System.currentTimeMillis()}",
                                                text = trimmed,
                                                time = "Now",
                                                isFromMe = true
                                            )
                                        )
                                    }
                                    inputText = ""
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(UnitedMoneyBlue)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = UnitedWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            if (chatHistory.isEmpty()) {
                item {
                    com.unitedpay.core.designsystem.components.UnitedEmptyState(
                        title = "No Messages Yet",
                        subtitle = "Send a message or pay $contactName directly via UPI.",
                        icon = Icons.Default.ChatBubbleOutline
                    )
                }
            } else {
                items(chatHistory) { item ->
                    when (item) {
                    is ChatEntry.DateHeader -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = UnitedBorderLight.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = item.date,
                                    fontSize = 11.sp,
                                    color = UnitedTextSecondary,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    is ChatEntry.TextMsg -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (item.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = if (item.isFromMe) 12.dp else 2.dp,
                                    bottomEnd = if (item.isFromMe) 2.dp else 12.dp
                                ),
                                color = if (item.isFromMe) UnitedMoneyBlue.copy(alpha = 0.12f) else UnitedWhite,
                                shadowElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth(0.78f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = item.text,
                                        fontSize = 14.sp,
                                        color = UnitedTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.time,
                                            fontSize = 10.sp,
                                            color = UnitedTextSecondary
                                        )
                                        if (item.isFromMe) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                Icons.Default.DoneAll,
                                                contentDescription = null,
                                                tint = UnitedMoneyBlue,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is ChatEntry.PaymentSent -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clickable {
                                        val activeProf = UserSessionManager.getCurrentProfile()
                                        val activeBank = UserSessionManager.getCurrentBankAccounts().firstOrNull()
                                        val txn = UpiTransaction(
                                            id = "UP/2026/09/10/" + item.utr.takeLast(8),
                                            utrNumber = item.utr,
                                            payeeName = contactName,
                                            payeeVpa = contactVpa,
                                            payerName = activeProf.fullName,
                                            payerVpa = activeProf.primaryVpa,
                                            amount = item.amount.replace(",", "").toDoubleOrNull() ?: 450.0,
                                            timestamp = System.currentTimeMillis(),
                                            status = PaymentStatus.SUCCESS,
                                            type = TransactionType.DEBIT,
                                            bankName = activeBank?.bankName ?: "State Bank of India",
                                            bankAccountNumberMasked = activeBank?.accountNumberMasked ?: "•••• 4821",
                                            note = "Payment to $contactName"
                                        )
                                        TransactionRepository.addTransaction(txn)
                                        onNavigateToTransactionDetail(txn.id)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Payment to ${item.recipientName}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = UnitedTextSecondary
                                        )
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = UnitedSuccess,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        "₹${item.amount}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = UnitedTextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = UnitedSuccess,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "Paid • ${item.time}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = UnitedSuccess
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = UnitedBorderLight)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        item.bank,
                                        fontSize = 11.sp,
                                        color = UnitedTextSecondary
                                    )
                                    Text(
                                        "UPI Ref: ${item.utr}",
                                        fontSize = 10.sp,
                                        color = UnitedTextSecondary.copy(alpha = 0.8f)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Check balance",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UnitedMoneyBlue,
                                            modifier = Modifier.clickable {
                                                showCheckBalanceDialog = true
                                            }
                                        )
                                        Text(
                                            "Share receipt",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UnitedMoneyBlue,
                                            modifier = Modifier.clickable {
                                                val activeProf = UserSessionManager.getCurrentProfile()
                                                val activeBank = UserSessionManager.getCurrentBankAccounts().firstOrNull()
                                                val txn = UpiTransaction(
                                                    id = "UP/2026/09/10/" + item.utr.takeLast(8),
                                                    utrNumber = item.utr,
                                                    payeeName = contactName,
                                                    payeeVpa = contactVpa,
                                                    payerName = activeProf.fullName,
                                                    payerVpa = activeProf.primaryVpa,
                                                    amount = item.amount.replace(",", "").toDoubleOrNull() ?: 450.0,
                                                    timestamp = System.currentTimeMillis(),
                                                    status = PaymentStatus.SUCCESS,
                                                    type = TransactionType.DEBIT,
                                                    bankName = activeBank?.bankName ?: "State Bank of India",
                                                    bankAccountNumberMasked = activeBank?.accountNumberMasked ?: "•••• 4821",
                                                    note = "Payment to $contactName"
                                                )
                                                TransactionRepository.addTransaction(txn)
                                                shareChatReceipt(context, txn)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is ChatEntry.PaymentReceived -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clickable {
                                        val activeProf = com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile()
                                        val activeBank = com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull()
                                        val txn = UpiTransaction(
                                            id = "UP/2026/09/10/" + item.utr.takeLast(8),
                                            utrNumber = item.utr,
                                            payeeName = contactName,
                                            payeeVpa = contactVpa,
                                            payerName = activeProf.fullName,
                                            payerVpa = activeProf.primaryVpa,
                                            amount = item.amount.replace(",", "").toDoubleOrNull() ?: 1200.0,
                                            timestamp = System.currentTimeMillis(),
                                            status = PaymentStatus.SUCCESS,
                                            type = TransactionType.CREDIT,
                                            bankName = activeBank?.bankName ?: "State Bank of India",
                                            bankAccountNumberMasked = activeBank?.accountNumberMasked ?: "•••• 9821",
                                            note = "Payment from ${item.senderName}"
                                        )
                                        TransactionRepository.addTransaction(txn)
                                        onNavigateToTransactionDetail(txn.id)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Payment from ${item.senderName}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = UnitedTextSecondary
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = UnitedSuccessContainer
                                        ) {
                                            Text(
                                                "Received",
                                                color = UnitedSuccess,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        "+ ₹${item.amount}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = UnitedSuccess
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        "Deposited in ${item.bank}",
                                        fontSize = 12.sp,
                                        color = UnitedTextSecondary
                                    )
                                    Text(
                                        "UPI Ref: ${item.utr} • ${item.time}",
                                        fontSize = 10.sp,
                                        color = UnitedTextSecondary.copy(alpha = 0.8f)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = UnitedBorderLight)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Check balance",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UnitedMoneyBlue,
                                            modifier = Modifier.clickable {
                                                showCheckBalanceDialog = true
                                            }
                                        )
                                        Text(
                                            "Share receipt",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UnitedMoneyBlue,
                                            modifier = Modifier.clickable {
                                                val activeProf = com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile()
                                                val activeBank = com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull()
                                                val txn = UpiTransaction(
                                                    id = "UP/2026/09/10/" + item.utr.takeLast(8),
                                                    utrNumber = item.utr,
                                                    payeeName = contactName,
                                                    payeeVpa = contactVpa,
                                                    payerName = activeProf.fullName,
                                                    payerVpa = activeProf.primaryVpa,
                                                    amount = item.amount.replace(",", "").toDoubleOrNull() ?: 1200.0,
                                                    timestamp = System.currentTimeMillis(),
                                                    status = PaymentStatus.SUCCESS,
                                                    type = TransactionType.CREDIT,
                                                    bankName = activeBank?.bankName ?: "State Bank of India",
                                                    bankAccountNumberMasked = activeBank?.accountNumberMasked ?: "•••• 9821",
                                                    note = "Payment from ${item.senderName}"
                                                )
                                                TransactionRepository.addTransaction(txn)
                                                shareChatReceipt(context, txn)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is ChatEntry.PaymentRequest -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(0.88f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                                border = if (!item.isPaid && !item.isDeclined) {
                                    CardDefaults.outlinedCardBorder().copy(
                                        brush = Brush.linearGradient(listOf(UnitedPending, UnitedPending)),
                                        width = 1.dp
                                    )
                                } else null,
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Payment request",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UnitedPending
                                        )
                                        Text(
                                            item.time,
                                            fontSize = 11.sp,
                                            color = UnitedTextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        "₹${item.amount}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = UnitedTextPrimary
                                    )

                                    if (item.note.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            item.note,
                                            fontSize = 13.sp,
                                            color = UnitedTextPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    if (!item.isPaid && !item.isDeclined) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    targetPayAmount = item.amount
                                                    targetPayNote = item.note
                                                    enteredPin = ""
                                                    associatedRequestId = item.id
                                                    payStep = PayFlowStep.CONFIRM_AMOUNT
                                                    isPayPanelOpen = true
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = UnitedMoneyBlue,
                                                    contentColor = UnitedWhite
                                                )
                                            ) {
                                                Text("Pay ₹${item.amount}", fontWeight = FontWeight.Bold, color = UnitedWhite)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    item.isDeclined = true
                                                    UnitedToast.info("Payment request declined")
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Decline", color = UnitedTextSecondary)
                                            }
                                        }
                                    } else if (item.isPaid) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = UnitedSuccessContainer
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnitedSuccess, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Paid ₹${item.amount}", color = UnitedSuccess, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = UnitedSurfaceSubtle
                                        ) {
                                            Text(
                                                "Declined",
                                                color = UnitedTextSecondary,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }

    // =========================================================================
    // MULTI-STEP SLIDE-UP BOTTOM SHEET: PAYMENT FLOW (EXECUTIVE 16DP TOP CORNERS)
    // =========================================================================
    if (isPayPanelOpen) {
        ModalBottomSheet(
            onDismissRequest = { isPayPanelOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = UnitedWhite,
            dragHandle = { BottomSheetDefaults.DragHandle(color = UnitedBorderLight) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                when (payStep) {
                    PayFlowStep.CONFIRM_AMOUNT -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text(
                                "Pay to $contactName",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary
                            )
                            Text(
                                contactVpa,
                                fontSize = 12.sp,
                                color = UnitedTextSecondary
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = UnitedCanvasLight)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                                        .padding(vertical = 16.dp, horizontal = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "ENTER AMOUNT",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextSecondary,
                                        letterSpacing = 1.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    UnitedCenteredAmountField(
                                        value = targetPayAmount,
                                        onValueChange = { targetPayAmount = it },
                                        symbolSize = 30.sp,
                                        amountSize = 36.sp,
                                        maxDigits = 6,
                                        placeholder = "0"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(100, 250, 500, 1000).forEach { chip ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFEFF6FF),
                                        modifier = Modifier
                                            .clickable {
                                                val cur = targetPayAmount.toIntOrNull() ?: 0
                                                val sum = (cur + chip).toString()
                                                if (sum.length <= 6) {
                                                    targetPayAmount = sum
                                                }
                                            }
                                    ) {
                                        Text(
                                            "+₹$chip",
                                            color = UnitedMoneyBlue,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = targetPayNote,
                                onValueChange = { targetPayNote = it },
                                singleLine = true,
                                label = { Text("Add a note (optional)", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                textStyle = TextStyle(color = UnitedTextPrimary, fontSize = 14.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = UnitedTextPrimary,
                                    unfocusedTextColor = UnitedTextPrimary,
                                    cursorColor = UnitedMoneyBlue,
                                    focusedBorderColor = UnitedMoneyBlue,
                                    unfocusedBorderColor = UnitedBorderLight,
                                    focusedContainerColor = UnitedWhite,
                                    unfocusedContainerColor = UnitedWhite
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = UnitedSurfaceSubtle)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = UnitedMoneyBlue)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        val payBank = com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull()
                                        val payBankLabel = if (payBank != null) "${payBank.bankName} ${payBank.accountNumberMasked}" else "UnitedPay Payments"
                                        Text(payBankLabel, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = UnitedTextPrimary)
                                        Text("Primary UPI Account • Zero fee", fontSize = 11.sp, color = UnitedTextSecondary)
                                    }
                                }
                            }

                            val payNum = targetPayAmount.toDoubleOrNull() ?: 0.0
                            val isOverPayLimit = payNum > 100000.0

                            if (isOverPayLimit) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "NPCI UPI limit: Maximum ₹1,00,000 per transaction",
                                    color = UnitedError,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // PINNED BOTTOM ACTION CONTAINER (NEVER SHRINKS, RESTS ABOVE KEYBOARD)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = UnitedWhite,
                            shadowElevation = 8.dp,
                            border = BorderStroke(0.5.dp, UnitedBorderLight)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                val payNum = targetPayAmount.toDoubleOrNull() ?: 0.0
                                val isOverPayLimit = payNum > 100000.0
                                Button(
                                    onClick = {
                                        val amt = targetPayAmount.toIntOrNull()
                                        if (amt != null && amt in 1..100000) {
                                            enteredPin = ""
                                            payStep = PayFlowStep.ENTER_PIN
                                        } else if (amt != null && amt > 100000) {
                                            UnitedToast.error("Maximum ₹1,00,000 per transaction")
                                        } else {
                                            UnitedToast.error("Please enter a valid amount")
                                        }
                                    },
                                    enabled = payNum in 1.0..100000.0,
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
                                    )
                                ) {
                                    val buttonText = when {
                                        isOverPayLimit -> "Amount exceeds ₹1,00,000 limit"
                                        payNum > 0.0 -> "Proceed to Enter UPI PIN (₹$targetPayAmount)"
                                        else -> "Proceed to Enter UPI PIN"
                                    }
                                    Text(
                                        text = buttonText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    PayFlowStep.ENTER_PIN -> {
                        UnitedNpciMpinSheet(
                            title = "NPCI UPI SECURE MPIN",
                            subtitle = "Paying ₹$targetPayAmount to $contactName",
                            pinLength = 6,
                            onPinSubmitted = {
                                payStep = PayFlowStep.SUCCESS
                            },
                            onDismiss = {
                                isPayPanelOpen = false
                            }
                        )
                    }

                    PayFlowStep.SUCCESS -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                .navigationBarsPadding(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(UnitedSuccessContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = UnitedSuccess,
                                    modifier = Modifier.size(54.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                "Payment Successful",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                "₹$targetPayAmount",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = UnitedMoneyBlue
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                "Paid securely to $contactName ($contactVpa)",
                                fontSize = 13.sp,
                                color = UnitedTextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val generatedUtr = "4291" + (10000000..99999999).random()
                            Text(
                                "UPI Ref: $generatedUtr",
                                fontSize = 11.sp,
                                color = UnitedTextSecondary.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    val newUtr = generatedUtr
                                    val newTxnId = "UP/" + SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date()) + "/" + (10000000..99999999).random()
                                    val activeProf = com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile()
                                    val activeBank = com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull()
                                    val newTxn = UpiTransaction(
                                        id = newTxnId,
                                         utrNumber = newUtr,
                                         payeeName = contactName,
                                         payeeVpa = contactVpa,
                                         payerName = activeProf.fullName,
                                         payerVpa = activeProf.primaryVpa,
                                         amount = targetPayAmount.replace(",", "").toDoubleOrNull() ?: 100.0,
                                         timestamp = System.currentTimeMillis(),
                                         status = PaymentStatus.SUCCESS,
                                         type = TransactionType.DEBIT,
                                         bankName = activeBank?.bankName ?: "State Bank of India",
                                         bankAccountNumberMasked = activeBank?.accountNumberMasked ?: "•••• 9821",
                                         note = targetPayNote.ifBlank { "Payment via United Pay" }
                                     )
                                     TransactionRepository.addTransaction(newTxn)

                                    chatHistory.add(
                                        ChatEntry.PaymentSent(
                                            id = "tx_${System.currentTimeMillis()}",
                                            amount = targetPayAmount,
                                            recipientName = contactName,
                                            time = "Now",
                                            status = "Paid",
                                            utr = generatedUtr
                                        )
                                    )

                                    if (associatedRequestId != null) {
                                        chatHistory.find { it is ChatEntry.PaymentRequest && it.id == associatedRequestId }?.let {
                                            (it as ChatEntry.PaymentRequest).isPaid = true
                                        }
                                    }

                                    isPayPanelOpen = false
                                },
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
                                Text("Done", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = UnitedWhite)
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // MULTI-STEP SLIDE-UP BOTTOM SHEET: REQUEST FLOW (LIGHT THEME)
    // =========================================================================
    if (isRequestPanelOpen) {
        ModalBottomSheet(
            onDismissRequest = { isRequestPanelOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = UnitedWhite,
            dragHandle = { BottomSheetDefaults.DragHandle(color = UnitedBorderLight) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                if (!isRequestSuccessStep) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            "Request from $contactName",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary
                        )
                        Text(
                            contactVpa,
                            fontSize = 12.sp,
                            color = UnitedTextSecondary
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = UnitedCanvasLight)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                                    .padding(vertical = 16.dp, horizontal = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "ENTER REQUEST AMOUNT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UnitedTextSecondary,
                                    letterSpacing = 1.sp,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                UnitedCenteredAmountField(
                                    value = requestAmountInput,
                                    onValueChange = { requestAmountInput = it },
                                    symbolSize = 30.sp,
                                    amountSize = 36.sp,
                                    maxDigits = 6,
                                    placeholder = "0"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = requestNoteInput,
                            onValueChange = { requestNoteInput = it },
                            singleLine = true,
                            label = { Text("What is this for?", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            placeholder = { Text("e.g. Dinner split, Uber ride...", color = UnitedTextSecondary) },
                            textStyle = TextStyle(color = UnitedTextPrimary, fontSize = 14.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = UnitedTextPrimary,
                                unfocusedTextColor = UnitedTextPrimary,
                                cursorColor = UnitedMoneyBlue,
                                focusedBorderColor = UnitedMoneyBlue,
                                unfocusedBorderColor = UnitedBorderLight,
                                focusedContainerColor = UnitedWhite,
                                unfocusedContainerColor = UnitedWhite
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        val reqNum = requestAmountInput.toDoubleOrNull() ?: 0.0
                        val isOverReqLimit = reqNum > 100000.0

                        if (isOverReqLimit) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "NPCI UPI limit: Maximum ₹1,00,000 per request",
                                color = UnitedError,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // PINNED BOTTOM ACTION CONTAINER
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = UnitedWhite,
                        shadowElevation = 8.dp,
                        border = BorderStroke(0.5.dp, UnitedBorderLight)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            val reqNum = requestAmountInput.toDoubleOrNull() ?: 0.0
                            val isOverReqLimit = reqNum > 100000.0
                            Button(
                                onClick = {
                                    val amt = requestAmountInput.toIntOrNull()
                                    if (amt != null && amt in 1..100000) {
                                        isRequestSuccessStep = true
                                    } else if (amt != null && amt > 100000) {
                                        UnitedToast.error("Maximum ₹1,00,000 per request")
                                    } else {
                                        UnitedToast.error("Please enter a valid amount")
                                    }
                                },
                                enabled = reqNum in 1.0..100000.0,
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
                                )
                            ) {
                                val buttonText = when {
                                    isOverReqLimit -> "Amount exceeds ₹1,00,000 limit"
                                    reqNum > 0.0 -> "Send Request for ₹$requestAmountInput"
                                    else -> "Send Request"
                                }
                                Text(
                                    text = buttonText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .navigationBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFF4E5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Request Sent",
                                tint = UnitedPending,
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            "Payment Request Sent",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            "₹$requestAmountInput",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = UnitedPending
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            "Sent to $contactName ($contactVpa)",
                            fontSize = 13.sp,
                            color = UnitedTextSecondary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                chatHistory.add(
                                    ChatEntry.PaymentRequest(
                                        id = "req_${System.currentTimeMillis()}",
                                        amount = requestAmountInput,
                                        requesterName = "You",
                                        note = requestNoteInput.ifBlank { "Payment request" },
                                        time = "Now"
                                    )
                                )
                                isRequestPanelOpen = false
                            },
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
                            Text("Done", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = UnitedWhite)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    if (showCheckBalanceDialog) {
        val activeBank = UserSessionManager.getCurrentBankAccounts().firstOrNull()
        CheckBalanceDialog(
            bankName = activeBank?.bankName ?: "State Bank of India",
            accountMasked = activeBank?.accountNumberMasked ?: "•••• 4821",
            availableBalance = String.format(Locale.getDefault(), "%,.2f", activeBank?.balance ?: 10000.0),
            onDismiss = { showCheckBalanceDialog = false }
        )
    }
}

private fun shareChatReceipt(context: Context, txn: UpiTransaction) {
    ReceiptShareHelper.shareReceipt(context, txn)
}
