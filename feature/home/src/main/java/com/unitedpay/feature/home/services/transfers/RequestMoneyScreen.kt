package com.unitedpay.feature.home.services.transfers

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.UnitedGlassCard
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.session.UserSessionManager

/**
 * GPay / Paytm-style "Request Money" (UPI Collect) screen.
 *
 * Features:
 * 1. Unified Search & Input header with 123 (numeric) vs ABC (text) keypad toggle.
 * 2. Autocomplete UPI handle chips (@okhdfcbank, @okaxis, @oksbi, @paytm, @unitedpay).
 * 3. Default state: 4-in-a-row circular avatar grid of recent contacts (up to 20 max) with
 *    colorful initials and First Name below, plus complete UPI contact directory.
 * 4. Live search state: Instantly hides the 4-per-row grid to display matching contacts
 *    or direct collect cards for custom numbers / UPI IDs.
 * 5. Collect Sheet (ModalBottomSheet): Enter collect amount (with quick increment chips),
 *    purpose note ("Dinner split", "Rent"), and receiving bank account selection.
 * 6. Verified Collect Confirmation: NPCI collect mandate receipt with 24-hour expiration
 *    notice and native WhatsApp / SMS share sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestMoneyScreen(
    onBackClick: () -> Unit,
    onNavigateToMyQr: () -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val isSim1 = UserSessionManager.isSim1Active
    val allContacts = remember(isSim1) {
        if (isSim1) getSim1MockContacts() else emptyList()
    }

    val upiHandleSuggestions = listOf("@okhdfcbank", "@okaxis", "@oksbi", "@paytm", "@unitedpay")

    var searchQuery by remember { mutableStateOf("") }
    var isNumericKeyboard by remember { mutableStateOf(false) }

    // Selected contact for collect sheet
    var selectedPayee by remember { mutableStateOf<UpiPayeeItem?>(null) }
    var showCollectSheet by remember { mutableStateOf(false) }
    var collectAmount by remember { mutableStateOf("500") }
    var collectNote by remember { mutableStateOf("") }

    // Sent collect request confirmation state
    var confirmedRequest by remember { mutableStateOf<ConfirmedCollectRequest?>(null) }

    val filteredContacts = remember(searchQuery, allContacts) {
        val q = searchQuery.trim().lowercase()
        if (q.isBlank()) {
            emptyList()
        } else {
            allContacts.filter { contact ->
                contact.name.lowercase().contains(q) ||
                contact.phoneNumber.replace(" ", "").contains(q.replace(" ", "")) ||
                contact.vpa.lowercase().contains(q)
            }
        }
    }

    val isSearchActive = searchQuery.isNotBlank()

    // When collect request is successfully confirmed, show full-screen receipt
    if (confirmedRequest != null) {
        CollectSuccessView(
            request = confirmedRequest!!,
            onShareLink = {
                val shareText = "UnitedPay UPI Collect Request: Please pay ₹${confirmedRequest!!.amount} to Arunjyoti Changkakoty (${confirmedRequest!!.receivingVpa}) for \"${confirmedRequest!!.note.ifBlank { "UPI Collect Request" }}\". Tap here: upi://pay?pa=${confirmedRequest!!.receivingVpa}&pn=Arunjyoti%20Changkakoty&am=${confirmedRequest!!.amount}&cu=INR"
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Share UPI Collect Request via")
                context.startActivity(shareIntent)
            },
            onRequestAnother = {
                confirmedRequest = null
                searchQuery = ""
                collectAmount = "500"
                collectNote = ""
            },
            onDone = onBackClick
        )
        return
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Request Money",
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary,
                            fontSize = 17.5.sp
                        )
                        Text(
                            text = "Collect payments from any UPI contact or mobile number",
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
                    IconButton(onClick = onNavigateToMyQr) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "My QR",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search & Input Header Surface
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = UnitedWhite,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Search Text Field with Keyboard Toggle (123 vs ABC)
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = if (isNumericKeyboard) "Enter 10-digit mobile number" else "Enter name, mobile no. or UPI ID",
                                fontSize = 13.5.sp,
                                color = UnitedTextSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isNumericKeyboard) Icons.Default.Phone else Icons.Default.Search,
                                contentDescription = null,
                                tint = UnitedMoneyBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { searchQuery = "" },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = UnitedTextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                // Keyboard Type Switcher Toggle (123 vs ABC)
                                Surface(
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                        .clickable { isNumericKeyboard = !isNumericKeyboard },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isNumericKeyboard) UnitedMoneyBlue.copy(alpha = 0.12f) else Color(0xFFF1F5F9),
                                    border = BorderStroke(0.5.dp, if (isNumericKeyboard) UnitedMoneyBlue else Color(0xFFCBD5E1))
                                ) {
                                    Text(
                                        text = if (isNumericKeyboard) "123" else "ABC",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isNumericKeyboard) UnitedMoneyBlue else UnitedTextPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (isNumericKeyboard) KeyboardType.Phone else KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UnitedMoneyBlue,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        )
                    )

                    // Popular UPI Handle Chips (Autocomplete)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        upiHandleSuggestions.forEach { handle ->
                            Surface(
                                modifier = Modifier.clickable {
                                    if (searchQuery.isBlank()) {
                                        searchQuery = handle
                                    } else if (!searchQuery.contains("@")) {
                                        searchQuery = "${searchQuery.trim()}$handle"
                                    } else {
                                        val prefix = searchQuery.substringBefore("@")
                                        searchQuery = "$prefix$handle"
                                    }
                                },
                                shape = RoundedCornerShape(100.dp),
                                color = Color(0xFFF1F5F9),
                                border = BorderStroke(0.5.dp, Color(0xFFCBD5E1))
                            ) {
                                Text(
                                    text = handle,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = UnitedMoneyBlue,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Body: Recents Grid vs Live Search List
            if (isSearchActive) {
                // Live Search Mode: 4-per-row Recents Grid is completely HIDDEN
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val trimmed = searchQuery.trim()
                    val isCustomNumber = trimmed.replace("+91", "").replace(" ", "").length >= 10 && trimmed.all { it.isDigit() || it == '+' || it == ' ' }
                    val isCustomVpa = trimmed.contains("@") && trimmed.length >= 5

                    // Direct Collect Card if custom mobile or UPI ID entered
                    if (isCustomNumber || isCustomVpa) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val payee = if (isCustomVpa) {
                                            UpiPayeeItem(
                                                id = "custom_vpa",
                                                name = trimmed,
                                                firstName = trimmed.substringBefore("@"),
                                                initials = trimmed.take(2).uppercase(),
                                                phoneNumber = "",
                                                vpa = trimmed,
                                                avatarBgColor = UnitedMoneyBlue
                                            )
                                        } else {
                                            val cleanPhone = trimmed.replace(" ", "")
                                            UpiPayeeItem(
                                                id = "custom_phone",
                                                name = "Mobile Recipient",
                                                firstName = "Mobile",
                                                initials = "MO",
                                                phoneNumber = cleanPhone,
                                                vpa = "$cleanPhone@upi",
                                                avatarBgColor = UnitedMoneyBlue
                                            )
                                        }
                                        selectedPayee = payee
                                        showCollectSheet = true
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = UnitedMoneyBlue.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, UnitedMoneyBlue.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(UnitedMoneyBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDownward,
                                            contentDescription = null,
                                            tint = UnitedWhite,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isCustomVpa) "Request from UPI ID: $trimmed" else "Request from Mobile: $trimmed",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UnitedMoneyBlue
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Verified NPCI recipient • Tap to request",
                                            fontSize = 11.sp,
                                            color = UnitedTextSecondary
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = UnitedMoneyBlue
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    if (filteredContacts.isEmpty() && !isCustomNumber && !isCustomVpa) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.PersonSearch,
                                        contentDescription = null,
                                        tint = UnitedTextSecondary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No contact found for \"$searchQuery\"",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Enter a valid 10-digit mobile number or UPI ID above",
                                        fontSize = 12.sp,
                                        color = UnitedTextSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "MATCHING CONTACTS (${filteredContacts.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextSecondary,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(filteredContacts, key = { it.id }) { contact ->
                            CollectContactRowItem(
                                contact = contact,
                                onClick = {
                                    selectedPayee = contact
                                    showCollectSheet = true
                                }
                            )
                        }
                    }
                }
            } else {
                // Default Mode: Recent Requests in 4-per-row Circular Grid (up to 20 max)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (allContacts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.PeopleOutline,
                                        contentDescription = null,
                                        tint = UnitedTextSecondary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No Recent Contacts Yet",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Enter any mobile number or UPI ID above to send a collect request.",
                                        fontSize = 12.sp,
                                        color = UnitedTextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        // Section: Recent Requests (4 items per row)
                        item {
                            UnitedGlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 16.dp,
                                elevation = 2.dp
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Recent Requests",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UnitedTextPrimary
                                        )
                                        Text(
                                            text = "${allContacts.take(20).size} Recents",
                                            fontSize = 11.5.sp,
                                            color = UnitedMoneyBlue,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // 4-in-a-row Avatar Grid
                                    val recents = allContacts.take(20)
                                    val rowCount = (recents.size + 3) / 4

                                    for (r in 0 until rowCount) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            for (c in 0 until 4) {
                                                val index = r * 4 + c
                                                if (index < recents.size) {
                                                    val contact = recents[index]
                                                    CollectAvatarItem(
                                                        contact = contact,
                                                        onClick = {
                                                            selectedPayee = contact
                                                            showCollectSheet = true
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                } else {
                                                    Spacer(modifier = Modifier.weight(1f))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Section: All Contacts List
                        item {
                            Text(
                                text = "ALL CONTACTS ON UPI (${allContacts.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextSecondary,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        items(allContacts, key = { it.id }) { contact ->
                            CollectContactRowItem(
                                contact = contact,
                                onClick = {
                                    selectedPayee = contact
                                    showCollectSheet = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Amount, Note & Receiving Bank Collect Sheet (ModalBottomSheet)
    if (showCollectSheet && selectedPayee != null) {
        val payee = selectedPayee!!
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showCollectSheet = false },
            sheetState = sheetState,
            containerColor = UnitedWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp)
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                // Header with Payer Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(payee.avatarBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = payee.initials,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = payee.avatarTextColor
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = payee.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (payee.vpa.isNotBlank()) payee.vpa else payee.phoneNumber,
                            fontSize = 12.sp,
                            color = UnitedTextSecondary
                        )
                    }

                    IconButton(onClick = { showCollectSheet = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = UnitedTextSecondary
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    color = Color(0xFFF1F5F9),
                    thickness = 1.dp
                )

                // Rupee Collect Amount Input
                Text(
                    text = "ENTER REQUEST AMOUNT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedTextSecondary,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = collectAmount,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() }
                        if (filtered.length <= 6) {
                            collectAmount = filtered
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    prefix = {
                        Text(
                            text = "₹",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedMoneyBlue,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextPrimary
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UnitedMoneyBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    )
                )

                // Quick Increment Amount Chips
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickAddValues = listOf(100, 200, 500, 1000, 2000)
                    quickAddValues.forEach { chipAmount ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val current = collectAmount.toLongOrNull() ?: 0L
                                    collectAmount = (current + chipAmount).coerceAtMost(100000L).toString()
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9),
                            border = BorderStroke(0.5.dp, Color(0xFFCBD5E1))
                        ) {
                            Text(
                                text = "+₹$chipAmount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedMoneyBlue,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 7.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Purpose Note Field
                Text(
                    text = "PURPOSE NOTE (OPTIONAL)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedTextSecondary,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = collectNote,
                    onValueChange = { if (it.length <= 50) collectNote = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "e.g. Dinner split, Rent, Grocery",
                            fontSize = 13.sp,
                            color = UnitedTextSecondary
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UnitedMoneyBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Receiving Bank Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = UnitedMoneyBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Receiving Bank Account",
                                fontSize = 11.sp,
                                color = UnitedTextSecondary
                            )
                            Text(
                                text = if (isSim1) "State Bank of India •••• 4821" else "United Primary Bank •••• 1092",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFFECFDF5),
                            border = BorderStroke(0.5.dp, Color(0xFF10B981))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Verified",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Regulatory Notice
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = UnitedTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Collect request valid for 24 hours via NPCI UPI mandate.",
                        fontSize = 11.sp,
                        color = UnitedTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Send Collect Request Button
                val parsedAmount = collectAmount.toLongOrNull() ?: 0L
                val isButtonEnabled = parsedAmount in 1..100000

                Button(
                    onClick = {
                        val refNumber = "UPI/REQ/${System.currentTimeMillis().toString().takeLast(9)}"
                        confirmedRequest = ConfirmedCollectRequest(
                            payerName = payee.name,
                            payerVpa = if (payee.vpa.isNotBlank()) payee.vpa else payee.phoneNumber,
                            payerInitials = payee.initials,
                            amount = collectAmount,
                            note = collectNote,
                            receivingBank = if (isSim1) "State Bank of India (•••• 4821)" else "United Primary Bank (•••• 1092)",
                            receivingVpa = if (isSim1) "arunjyoti.c@okhdfcbank" else "user@unitedpay",
                            mandateRef = refNumber,
                            timestamp = "Valid for 24 Hours"
                        )
                        showCollectSheet = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UnitedMoneyBlue,
                        disabledContainerColor = Color(0xFFCBD5E1)
                    ),
                    enabled = isButtonEnabled
                ) {
                    Text(
                        text = if (parsedAmount > 0) "Send UPI Collect Request (₹$collectAmount)" else "Enter Valid Amount",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedWhite
                    )
                }
            }
        }
    }
}

/**
 * Confirmed Collect Request Data Model.
 */
data class ConfirmedCollectRequest(
    val payerName: String,
    val payerVpa: String,
    val payerInitials: String,
    val amount: String,
    val note: String,
    val receivingBank: String,
    val receivingVpa: String,
    val mandateRef: String,
    val timestamp: String
)

/**
 * 4-in-a-row Circular Avatar Item with Initials and First Name below.
 */
@Composable
private fun CollectAvatarItem(
    contact: UpiPayeeItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(contact.avatarBgColor)
                .border(1.5.dp, UnitedWhite, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.initials,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = contact.avatarTextColor,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = contact.firstName,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = UnitedTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Detailed Contact Row with Avatar, Full Name, Phone Number, UPI ID, and "Request" action pill.
 */
@Composable
private fun CollectContactRowItem(
    contact: UpiPayeeItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = UnitedWhite,
        border = BorderStroke(0.5.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Initials Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(contact.avatarBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.initials,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = contact.avatarTextColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${contact.phoneNumber} • ${contact.vpa}",
                    fontSize = 11.5.sp,
                    color = UnitedTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                shape = RoundedCornerShape(100.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(0.5.dp, UnitedMoneyBlue.copy(alpha = 0.2f))
            ) {
                Text(
                    text = "Request",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedMoneyBlue,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Full-screen NPCI Collect Confirmation Receipt.
 */
@Composable
private fun CollectSuccessView(
    request: ConfirmedCollectRequest,
    onShareLink: () -> Unit,
    onRequestAnother: () -> Unit,
    onDone: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = UnitedWhite,
                shadowElevation = 8.dp,
                border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Share Button
                    Button(
                        onClick = onShareLink,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = UnitedWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Share Request Link",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = UnitedWhite
                        )
                    }

                    // Request Another & Done Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRequestAnother,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Text(
                                text = "Request Another",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = UnitedTextPrimary
                            )
                        }

                        Button(
                            onClick = onDone,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                        ) {
                            Text(
                                text = "Done",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = UnitedWhite
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success Icon Pill
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFECFDF5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Collect Request Sent!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = UnitedTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "₹${request.amount}",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = UnitedMoneyBlue
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Collect request sent to ${request.payerName}",
                fontSize = 13.sp,
                color = UnitedTextSecondary,
                textAlign = TextAlign.Center
            )

            if (request.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Text(
                        text = "\"${request.note}\"",
                        fontSize = 12.sp,
                        color = UnitedTextPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Mandate & Receipt Details Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = UnitedWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "COLLECT MANDATE DETAILS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextSecondary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ReceiptInfoRow(label = "Requested From", value = "${request.payerName} (${request.payerVpa})")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                    ReceiptInfoRow(label = "Receiving Bank", value = request.receivingBank)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                    ReceiptInfoRow(label = "Mandate Reference", value = request.mandateRef)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                    ReceiptInfoRow(label = "Validity", value = request.timestamp)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                    // Status Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status",
                            fontSize = 12.sp,
                            color = UnitedTextSecondary
                        )

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFFFEF3C7),
                            border = BorderStroke(0.5.dp, Color(0xFFF59E0B))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Awaiting Payer Approval",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Informational Callout Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(0.5.dp, UnitedMoneyBlue.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = UnitedMoneyBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "We have sent an SMS and UPI notification to ${request.payerName}. Once they approve the collect request on Google Pay, PhonePe, Paytm, or BHIM, the money will be credited directly to your bank account.",
                        fontSize = 11.5.sp,
                        color = Color(0xFF1E3A8A),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Receipt Label-Value Row Helper.
 */
@Composable
private fun ReceiptInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = UnitedTextSecondary
        )
        Text(
            text = value,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = UnitedTextPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
