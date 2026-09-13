package com.unitedpay.feature.home.communications

import com.unitedpay.core.designsystem.components.UnitedToast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.FintechNotificationItem
import com.unitedpay.core.model.api.UnitedPayApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    onNavigateToElectricity: () -> Unit = {},
    onNavigateToRewards: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("All") }

    val notifications = remember {
        mutableStateListOf<FintechNotification>().apply {
            addAll(com.unitedpay.core.model.session.UserSessionManager.getCurrentNotifications().map {
                FintechNotification(it.id, it.title, it.message, it.time, it.category, it.iconType, it.actionText)
            })
        }
    }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.communications.getNotifications().collect { result ->
            if (result is Resource.Success) {
                notifications.clear()
                notifications.addAll(result.data.map {
                    FintechNotification(it.id, it.title, it.message, it.time, it.category, it.iconType, it.actionText)
                })
            }
        }
    }

    val filteredList = remember(selectedTab, notifications) {
        when (selectedTab) {
            "Payments" -> notifications.filter { it.iconType in listOf("cashback", "autopay") }
            "Reminders" -> notifications.filter { it.iconType in listOf("bill", "card") }
            "Rewards" -> notifications.filter { it.iconType == "reward" }
            else -> notifications
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Notifications",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = UnitedTextPrimary
                        )
                        Text(
                            "${notifications.size} updates • Security & Bills",
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
                actions = {
                    TextButton(onClick = {
                        UnitedToast.success("All notifications marked as read")
                    }) {
                        Text("Mark all read", fontSize = 12.sp, color = UnitedMoneyBlue, fontWeight = FontWeight.Bold)
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
            // Filter Tabs
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf("All", "Payments", "Reminders", "Rewards")
                items(tabs) { tab ->
                    val isSelected = selectedTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        label = {
                            Text(
                                tab,
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

            Spacer(modifier = Modifier.height(4.dp))

            // Notifications List Grouped
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredList.isEmpty()) {
                    item {
                        com.unitedpay.core.designsystem.components.UnitedEmptyState(
                            title = "No Notifications",
                            subtitle = "You're all caught up! Transactions, rewards, and security updates will appear here.",
                            icon = Icons.Default.Notifications
                        )
                    }
                } else {
                    items(filteredList) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Category Icon
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (item.iconType) {
                                            "cashback" -> Color(0xFFE8F8EE)
                                            "bill" -> Color(0xFFFFF4E5)
                                            "security" -> Color(0xFFEDF2F9)
                                            "reward" -> Color(0xFFFFF9E6)
                                            else -> Color(0xFFF0F4FF)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                val icon = when (item.iconType) {
                                    "cashback" -> Icons.Default.CheckCircle
                                    "bill" -> Icons.Default.ElectricBolt
                                    "security" -> Icons.Default.Security
                                    "reward" -> Icons.Default.Star
                                    else -> Icons.Default.Notifications
                                }
                                val iconTint = when (item.iconType) {
                                    "cashback" -> UnitedSuccess
                                    "bill" -> UnitedPending
                                    "security" -> UnitedMoneyBlue
                                    "reward" -> UnitedAccentGold
                                    else -> UnitedMoneyBlue
                                }
                                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        item.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = UnitedTextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        item.time,
                                        fontSize = 11.sp,
                                        color = UnitedTextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    item.message,
                                    fontSize = 12.sp,
                                    color = UnitedTextSecondary,
                                    lineHeight = 17.sp
                                )

                                if (item.actionText != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            when (item.actionText) {
                                                "Pay Now" -> onNavigateToElectricity()
                                                "View Rewards" -> onNavigateToRewards()
                                                else -> UnitedToast.info("Action: ${item.actionText}")
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = UnitedMoneyBlue,
                                            contentColor = UnitedWhite
                                        ),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(item.actionText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = UnitedWhite)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
