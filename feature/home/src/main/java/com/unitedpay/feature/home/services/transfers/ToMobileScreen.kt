package com.unitedpay.feature.home.services.transfers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.UnitedGlassCard
import com.unitedpay.core.designsystem.components.UnitedIcons
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.session.UserSessionManager

/**
 * Contact representation for UPI Mobile & VPA transfers.
 */
data class UpiPayeeItem(
    val id: String,
    val name: String,
    val firstName: String,
    val initials: String,
    val phoneNumber: String,
    val vpa: String,
    val avatarBgColor: Color,
    val avatarTextColor: Color = UnitedWhite
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToMobileScreen(
    onBackClick: () -> Unit,
    onSelectContact: (name: String, vpa: String) -> Unit,
    onNavigateToQrScan: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var isNumericKeyboard by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val isSim1 = UserSessionManager.isSim1Active
    val allContacts = remember(isSim1) {
        if (isSim1) getSim1MockContacts() else emptyList()
    }

    val upiHandleSuggestions = listOf("@okhdfcbank", "@okaxis", "@oksbi", "@paytm", "@unitedpay")

    // Filter contacts based on query (name, phone number, or UPI ID)
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

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Pay to Mobile Number",
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary,
                            fontSize = 17.5.sp
                        )
                        Text(
                            text = "Instant transfer to any UPI app or bank",
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
                    IconButton(onClick = onNavigateToQrScan) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan QR",
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
            // Search & Input Header Box
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
                    // Search Text Field with Keyboard Toggle
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

            // Body Content: Recents Grid vs Live Search List
            if (isSearchActive) {
                // Live Search Mode: 4-per-row grid is HIDDEN; filtered contact rows are shown
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Direct Pay Option if custom phone / UPI ID typed
                    val trimmed = searchQuery.trim()
                    val isCustomNumber = trimmed.replace("+91", "").replace(" ", "").length >= 10 && trimmed.all { it.isDigit() || it == '+' || it == ' ' }
                    val isCustomVpa = trimmed.contains("@") && trimmed.length >= 5

                    if (isCustomNumber || isCustomVpa) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isCustomVpa) {
                                            onSelectContact("UPI Payee ($trimmed)", trimmed)
                                        } else {
                                            val cleanPhone = trimmed.replace(" ", "")
                                            onSelectContact("Mobile ($cleanPhone)", "$cleanPhone@upi")
                                        }
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
                                            imageVector = Icons.Default.ArrowOutward,
                                            contentDescription = null,
                                            tint = UnitedWhite,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isCustomVpa) "Pay to UPI ID: $trimmed" else "Pay to Mobile: $trimmed",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UnitedMoneyBlue
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Verified NPCI recipient • Tap to transfer",
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
                            ContactRowItem(
                                contact = contact,
                                onClick = { onSelectContact(contact.name, contact.vpa) }
                            )
                        }
                    }
                }
            } else {
                // Default Mode: Recent Payments in 4-per-row Circular Grid (up to 20 max)
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
                                        text = "No Recent Payments Yet",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Type any mobile number or UPI ID above to make your first payment.",
                                        fontSize = 12.sp,
                                        color = UnitedTextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        // Section: Recent Payments (4 items per row)
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
                                            text = "Recent Payments",
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
                                                    RecentAvatarItem(
                                                        contact = contact,
                                                        onClick = { onSelectContact(contact.name, contact.vpa) },
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
                            ContactRowItem(
                                contact = contact,
                                onClick = { onSelectContact(contact.name, contact.vpa) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 4-per-row Circular Avatar Item with Initials and First Name below.
 */
@Composable
private fun RecentAvatarItem(
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
 * Detailed Contact Row with Avatar, Full Name, Phone Number, and UPI ID.
 */
@Composable
private fun ContactRowItem(
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
                    text = "Pay",
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
 * Rich mock contacts for SIM 1 (Arunjyoti Changkakoty) with exactly 16 people
 * (4 rows of 4 items) conforming to the prompt specification (BJ, KK, RB, etc.).
 */
internal fun getSim1MockContacts(): List<UpiPayeeItem> = listOf(
    UpiPayeeItem("c1", "Bikash Jyoti Kalita", "Bikash", "BJ", "+91 88760 12345", "bikash@okhdfcbank", Color(0xFF3B82F6)),
    UpiPayeeItem("c2", "Kalyan Kalita", "Kalyan", "KK", "+91 94350 54321", "kalyan@okaxis", Color(0xFF10B981)),
    UpiPayeeItem("c3", "Rituraj Barman", "Rituraj", "RB", "+91 98640 99887", "rituraj@oksbi", Color(0xFF8B5CF6)),
    UpiPayeeItem("c4", "Pronob Barman", "Pronob", "PB", "+91 98640 11223", "pronob@unitedpay", Color(0xFFF59E0B)),
    UpiPayeeItem("c5", "Dimpu Kakati", "Dimpu", "DK", "+91 97060 44556", "dimpu@paytm", Color(0xFFEC4899)),
    UpiPayeeItem("c6", "Ankur Sarma", "Ankur", "AS", "+91 98540 77889", "ankur@okhdfcbank", Color(0xFF06B6D4)),
    UpiPayeeItem("c7", "Barsha Nath", "Barsha", "BN", "+91 91010 33445", "barsha@okaxis", Color(0xFF6366F1)),
    UpiPayeeItem("c8", "Rahul Sarma", "Rahul", "RS", "+91 99540 66778", "rahul@oksbi", Color(0xFF14B8A6)),
    UpiPayeeItem("c9", "Jahnabee Devi", "Jahnabee", "JD", "+91 94351 22334", "jahnabee@unitedpay", Color(0xFFF97316)),
    UpiPayeeItem("c10", "Aneesh Jain", "Aneesh", "AJ", "+91 98200 55667", "aneesh@okaxis", Color(0xFF0EA5E9)),
    UpiPayeeItem("c11", "Swiggy Delivery", "Swiggy", "SD", "+91 98765 00112", "swiggy.pay@icici", Color(0xFFFC8019)),
    UpiPayeeItem("c12", "Zomato Partner", "Zomato", "ZP", "+91 98765 00223", "zomato@hdfcbank", Color(0xFFE23744)),
    UpiPayeeItem("c13", "Ramesh Sharma", "Ramesh", "RS", "+91 98111 22334", "ramesh@unitedpay", Color(0xFF0284C7)),
    UpiPayeeItem("c14", "Priya Patel", "Priya", "PP", "+91 98222 33445", "priya.patel@unitedpay", Color(0xFF9333EA)),
    UpiPayeeItem("c15", "Rohit Verma", "Rohit", "RV", "+91 98333 44556", "rohit.v@unitedpay", Color(0xFF059669)),
    UpiPayeeItem("c16", "Ananya Roy", "Ananya", "AR", "+91 98444 55667", "ananya.roy@unitedpay", Color(0xFFDB2777))
)
