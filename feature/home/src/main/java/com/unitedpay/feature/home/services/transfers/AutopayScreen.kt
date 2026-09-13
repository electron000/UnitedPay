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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedEmptyState
import com.unitedpay.core.designsystem.components.UnitedGlassCard
import com.unitedpay.core.designsystem.components.UnitedIcons
import com.unitedpay.core.designsystem.components.UnitedToast
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.AutopayMandate
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager

data class MandateExecutionRecord(
    val id: String,
    val executionDate: String,
    val amount: String,
    val status: String,
    val utr: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutopayScreen(onBackClick: () -> Unit) {
    var mandates by remember {
        mutableStateOf(UserSessionManager.getCurrentAutopayMandates())
    }
    var pausedMandateIds by remember { mutableStateOf(setOf<String>()) }
    var selectedMandateForHistory by remember { mutableStateOf<AutopayMandate?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        UnitedPayApi.client.banking.getAutopayMandates().collect { res ->
            if (res is Resource.Success) {
                mandates = res.data
            }
        }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "UPI Autopay Mandates",
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary,
                            fontSize = 17.5.sp
                        )
                        Text(
                            text = "Recurring payments authorized by NPCI",
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE RECURRING MANDATES (${mandates.size})",
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

                items(mandates, key = { it.id }) { mandate ->
                    val isPaused = pausedMandateIds.contains(mandate.id)

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
                            // Top Row: Service Name (Flex-weighted) + Clean Amount Block (No wrap)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                // Service Branding & Title
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    mandate.serviceName.contains("Mutual Fund", ignoreCase = true) -> Color(0xFFEFF6FF)
                                                    mandate.serviceName.contains("Netflix", ignoreCase = true) -> Color(0xFFFEF2F2)
                                                    else -> Color(0xFFF0FDF4)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                mandate.serviceName.contains("Mutual Fund", ignoreCase = true) -> Icons.Default.TrendingUp
                                                mandate.serviceName.contains("Netflix", ignoreCase = true) -> Icons.Default.Movie
                                                else -> Icons.Default.Headphones
                                            },
                                            contentDescription = null,
                                            tint = when {
                                                mandate.serviceName.contains("Mutual Fund", ignoreCase = true) -> UnitedMoneyBlue
                                                mandate.serviceName.contains("Netflix", ignoreCase = true) -> Color(0xFFDC2626)
                                                else -> Color(0xFF16A34A)
                                            },
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mandate.serviceName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = UnitedTextPrimary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            lineHeight = 18.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = mandate.nextDebitText,
                                            fontSize = 11.5.sp,
                                            color = UnitedTextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Clean Fixed Amount Block
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = mandate.amountText,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = UnitedMoneyBlue,
                                        softWrap = false
                                    )
                                    Text(
                                        text = mandate.frequency,
                                        fontSize = 11.sp,
                                        color = UnitedTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Metadata Strip: Linked Bank + Status
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = UnitedTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "${mandate.bankName} •••• 1024",
                                        fontSize = 11.sp,
                                        color = UnitedTextSecondary
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (isPaused) Color(0xFFFEF3C7) else Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = if (isPaused) "PAUSED" else "ACTIVE",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPaused) Color(0xFFB45309) else Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Responsive Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .clickable {
                                            pausedMandateIds = if (isPaused) {
                                                pausedMandateIds - mandate.id
                                            } else {
                                                pausedMandateIds + mandate.id
                                            }
                                            val action = if (isPaused) "resumed" else "paused"
                                            UnitedToast.info("Mandate $action: ${mandate.serviceName}")
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isPaused) Color(0xFFF0FDF4) else Color(0xFFFEF2F2),
                                    border = BorderStroke(0.5.dp, if (isPaused) Color(0xFF86EFAC) else Color(0xFFFECACA))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                            contentDescription = null,
                                            tint = if (isPaused) Color(0xFF16A34A) else Color(0xFFDC2626),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = if (isPaused) "Resume" else "Pause",
                                            color = if (isPaused) Color(0xFF16A34A) else Color(0xFFDC2626),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Surface(
                                    modifier = Modifier
                                        .weight(1.6f)
                                        .height(38.dp)
                                        .clickable {
                                            selectedMandateForHistory = mandate
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF1F5F9),
                                    border = BorderStroke(0.5.dp, Color(0xFFCBD5E1))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = UnitedTextPrimary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "View Mandate History",
                                            color = UnitedTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mandate Execution History Bottom Sheet
        if (selectedMandateForHistory != null) {
            val mandate = selectedMandateForHistory!!
            val records = getRecordedHistoryForMandate(mandate.id, mandate.amountText)

            ModalBottomSheet(
                onDismissRequest = { selectedMandateForHistory = null },
                sheetState = sheetState,
                containerColor = UnitedWhite,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 32.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mandate Execution History",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mandate.serviceName,
                                fontSize = 12.5.sp,
                                color = UnitedMoneyBlue,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(onClick = { selectedMandateForHistory = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = UnitedTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mandate Summary Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("URN / Mandate ID", fontSize = 11.5.sp, color = UnitedTextSecondary)
                                Text(
                                    text = "UMP${mandate.id.replace("_", "").uppercase()}9926",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = UnitedTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Debit Amount", fontSize = 11.5.sp, color = UnitedTextSecondary)
                                Text(mandate.amountText, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = UnitedMoneyBlue)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Linked Bank", fontSize = 11.5.sp, color = UnitedTextSecondary)
                                Text("${mandate.bankName} •••• 1024", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = UnitedTextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "PLATFORM RECORDED DEBITS (${records.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextSecondary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    records.forEachIndexed { index, record ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = UnitedWhite,
                            border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = record.executionDate,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary
                                    )
                                    Text(
                                        text = record.amount,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = UnitedMoneyBlue
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = record.description,
                                        fontSize = 11.5.sp,
                                        color = UnitedTextSecondary
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = Color(0xFFDCFCE7)
                                    ) {
                                        Text(
                                            text = record.status,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "UTR: ${record.utr}",
                                    fontSize = 10.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = UnitedTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getRecordedHistoryForMandate(mandateId: String, amountText: String): List<MandateExecutionRecord> {
    val cleanAmount = amountText.substringBefore(" /").ifBlank { amountText }
    return when (mandateId) {
        "man_2" -> listOf(
            MandateExecutionRecord("rec_2_1", "05 Sep 2026, 09:30 AM", cleanAmount, "SUCCESSFUL", "425619842109", "Monthly SIP Auto-Debit"),
            MandateExecutionRecord("rec_2_2", "05 Aug 2026, 09:30 AM", cleanAmount, "SUCCESSFUL", "422509124810", "Monthly SIP Auto-Debit"),
            MandateExecutionRecord("rec_2_3", "05 Jul 2026, 09:30 AM", cleanAmount, "SUCCESSFUL", "419401824701", "Monthly SIP Auto-Debit"),
            MandateExecutionRecord("rec_2_4", "05 Jun 2026, 09:30 AM", cleanAmount, "SUCCESSFUL", "416309812499", "Monthly SIP Auto-Debit")
        )
        "man_1" -> listOf(
            MandateExecutionRecord("rec_1_1", "24 Aug 2026, 12:15 PM", cleanAmount, "SUCCESSFUL", "424519283401", "Netflix Premium Subscription"),
            MandateExecutionRecord("rec_1_2", "24 Jul 2026, 12:15 PM", cleanAmount, "SUCCESSFUL", "421419283402", "Netflix Premium Subscription"),
            MandateExecutionRecord("rec_1_3", "24 Jun 2026, 12:15 PM", cleanAmount, "SUCCESSFUL", "418319283403", "Netflix Premium Subscription")
        )
        else -> listOf(
            MandateExecutionRecord("rec_3_1", "12 Aug 2026, 02:45 PM", cleanAmount, "SUCCESSFUL", "423319283403", "Spotify Individual Plan"),
            MandateExecutionRecord("rec_3_2", "12 Jul 2026, 02:45 PM", cleanAmount, "SUCCESSFUL", "420219283404", "Spotify Individual Plan"),
            MandateExecutionRecord("rec_3_3", "12 Jun 2026, 02:45 PM", cleanAmount, "SUCCESSFUL", "417119283405", "Spotify Individual Plan")
        )
    }
}
