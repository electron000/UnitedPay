package com.unitedpay.feature.home.communications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedEmptyState
import com.unitedpay.core.designsystem.components.UnitedSearchField
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.ChatContact
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onBackClick: () -> Unit,
    onOpenChat: (contactId: String, contactName: String, contactVpa: String) -> Unit,
    onNewPayment: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    var contacts by remember { mutableStateOf(UserSessionManager.getCurrentChatContacts()) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.communications.getChatContacts().collect { result ->
            if (result is Resource.Success) {
                contacts = result.data
            }
        }
    }

    val filteredContacts = remember(searchQuery, selectedFilter, contacts) {
        contacts.filter { contact ->
            val matchesQuery = contact.name.contains(searchQuery, ignoreCase = true) ||
                    contact.vpa.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                "People" -> !contact.isMerchant
                "Businesses" -> contact.isMerchant
                "Requests" -> contact.isRequest
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Messages & UPI Chats",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = UnitedTextPrimary
                        )
                        Text(
                            "Instant UPI conversations & payments",
                            fontSize = 11.sp,
                            color = UnitedTextSecondary
                        )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UnitedWhite,
                    titleContentColor = UnitedTextPrimary,
                    navigationIconContentColor = UnitedTextPrimary
                )
            )
        },
        containerColor = UnitedBackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar with single-line ellipsis placeholder and fixed height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                UnitedSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search by name, UPI ID or phone...",
                    onClear = { searchQuery = "" }
                )
            }

            // Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("All", "People", "Businesses", "Requests")
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                filter,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) UnitedWhite else UnitedTextPrimary
                            )
                        },
                        shape = RoundedCornerShape(6.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = UnitedMoneyBlue,
                            containerColor = UnitedWhite
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) UnitedMoneyBlue else UnitedBorderLight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Conversation List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredContacts.isEmpty()) {
                    item {
                        UnitedEmptyState(
                            title = "No Messages Yet",
                            subtitle = if (searchQuery.isNotEmpty()) "No results matching \"$searchQuery\"" else "Start a new chat or transfer money to see conversations here.",
                            actionText = if (searchQuery.isEmpty()) "Start New Payment" else null,
                            onActionClick = if (searchQuery.isEmpty()) onNewPayment else null
                        )
                    }
                } else {
                    items(filteredContacts) { contact ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOpenChat(contact.id, contact.name, contact.vpa)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color(contact.avatarHexColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                val initials = contact.name.split(" ")
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.toString() }
                                    .joinToString("")
                                Text(
                                    initials,
                                    color = UnitedWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            contact.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = UnitedTextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (contact.isMerchant) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                Icons.Default.Verified,
                                                contentDescription = "Verified Merchant",
                                                tint = UnitedMoneyBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        contact.lastTime,
                                        fontSize = 11.sp,
                                        color = UnitedTextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (contact.isPayment) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = UnitedSuccess,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            contact.lastSnippet,
                                            fontSize = 13.sp,
                                            color = if (contact.isRequest) UnitedPending else UnitedTextSecondary,
                                            fontWeight = if (contact.unreadCount > 0 || contact.isRequest) FontWeight.SemiBold else FontWeight.Normal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    if (contact.unreadCount > 0) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(UnitedMoneyBlue),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "${contact.unreadCount}",
                                                color = UnitedWhite,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else if (contact.isRequest) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFFF4E5)
                                        ) {
                                            Text(
                                                "Action",
                                                color = UnitedPending,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
