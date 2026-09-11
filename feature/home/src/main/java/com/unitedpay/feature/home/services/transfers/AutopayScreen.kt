package com.unitedpay.feature.home.services.transfers

import com.unitedpay.core.designsystem.components.UnitedToast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
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
import com.unitedpay.core.designsystem.components.UnitedEmptyState
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager
import com.unitedpay.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutopayScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var mandates by remember {
        mutableStateOf(
            UserSessionManager.getCurrentAutopayMandates().map {
                Triple(it.serviceName, it.amountText, it.nextDebitText)
            }
        )
    }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.banking.getAutopayMandates().collect { res ->
            if (res is Resource.Success) {
                mandates = res.data.map {
                    Triple(it.serviceName, it.amountText, it.nextDebitText)
                }
            }
        }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("UPI Autopay Mandates", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = UnitedTextPrimary)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (mandates.isEmpty()) {
                item {
                    UnitedEmptyState(
                        icon = Icons.Default.Autorenew,
                        title = "No Active Mandates",
                        subtitle = "You do not have any active recurring UPI autopay mandates set up on your accounts."
                    )
                }
            } else {
                item {
                    Text("ACTIVE RECURRING MANDATES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
                }

                items(mandates) { (title, frequency, nextDate) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = UnitedWhite)
                    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = UnitedTextPrimary)
                            Text(frequency, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = UnitedMoneyBlue)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(nextDate, fontSize = 12.sp, color = UnitedTextSecondary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFEF2F2))
                                    .clickable { UnitedToast.info("Mandate paused: $title") }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Pause", color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .clickable { UnitedToast.info("Mandate details for $title") }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("View Mandate History", color = UnitedTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}
}
