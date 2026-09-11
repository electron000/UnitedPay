package com.unitedpay.feature.home.profile

import com.unitedpay.core.designsystem.components.UnitedToast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedEmptyState
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.DisputeTicket
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisputeCenterScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var disputeTicket by remember { mutableStateOf<DisputeTicket?>(UserSessionManager.getCurrentDisputeTickets().firstOrNull()) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.communications.getDisputes().collect { result ->
            if (result is Resource.Success) {
                disputeTicket = result.data.firstOrNull()
            }
        }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("NPCI Help & Disputes", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = UnitedWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("24x7 UPI Dispute Resolution", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = UnitedTextPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "NPCI guidelines guarantee auto-reversal of failed debits within T+1 working days under the Ombudsman Scheme.",
                            fontSize = 12.sp,
                            color = UnitedTextSecondary
                        )
                    }
                }
            }

            item {
                Text("RECENT TRANSACTION STATUS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))

                val ticket = disputeTicket
                if (ticket != null) {
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
                                Column {
                                    Text("Payment to ${ticket.payeeName}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UnitedTextPrimary)
                                    Text("UTR: ${ticket.utr} • ${ticket.bank}", fontSize = 11.5.sp, color = UnitedTextSecondary)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(UnitedSuccessContainer)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(ticket.status, color = UnitedSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("₹${String.format("%,.2f", ticket.amount)}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = UnitedTextPrimary)
                        }
                    }
                } else {
                    UnitedEmptyState(
                        title = "No Disputes Found",
                        subtitle = "You have no pending or past transaction disputes. All your payments are settled smoothly.",
                        icon = Icons.Default.CheckCircle
                    )
                }
            }

            item {
                Text("COMMON QUERIES & DISPUTE TYPES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = UnitedWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                    ) {
                        listOf(
                            "Money debited but not received by receiver",
                            "Paid to incorrect UPI ID / Account",
                            "Double debit for single bill payment",
                            "Recharge failed but money deducted"
                        ).forEachIndexed { index, query ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        UnitedToast.success("Dispute ticket raised for: $query")
                                    }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(query, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = UnitedTextPrimary, modifier = Modifier.weight(1f))
                                Text("Raise Ticket", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = UnitedMoneyBlue)
                            }
                            if (index < 3) {
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(UnitedBorderLight))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        UnitedToast.info("Calling NPCI Toll-Free: 1800-120-1740")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .defaultMinSize(minHeight = 52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue, contentColor = UnitedWhite)
                ) {
                    Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = UnitedWhite, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call NPCI Help Desk (1800-120-1740)", fontWeight = FontWeight.Bold, color = UnitedWhite, fontSize = 14.5.sp)
                }
            }
        }
    }
}
