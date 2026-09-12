package com.unitedpay.feature.passbook

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.CardDefaults
import com.unitedpay.core.designsystem.components.UnitedEmptyState
import com.unitedpay.core.designsystem.components.UnitedGlassCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.extensions.toInrCurrency
import com.unitedpay.core.designsystem.components.UnitedBottomBar
import com.unitedpay.core.designsystem.theme.UnitedBackgroundLight
import com.unitedpay.core.designsystem.theme.UnitedRoyalBlue
import com.unitedpay.core.designsystem.theme.UnitedSuccess
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite
import com.unitedpay.core.model.TransactionType
import com.unitedpay.core.model.UpiTransaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassbookScreen(
    viewModel: PassbookViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onTransactionClick: (String) -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateCards: () -> Unit = {},
    onNavigateScan: () -> Unit = {},
    onNavigateServices: () -> Unit = {},
    onNavigateProfile: () -> Unit = onNavigateServices
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = UnitedBackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Transaction History", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = UnitedTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateHome) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = UnitedTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Export PDF statement */ }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export Statement", tint = UnitedRoyalBlue)
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
        bottomBar = {
            UnitedBottomBar(
                currentRoute = "history",
                onNavigateHome = onNavigateHome,
                onNavigateCards = onNavigateCards,
                onNavigateScan = onNavigateScan,
                onNavigateHistory = {},
                onNavigateServices = onNavigateServices
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ALL TRANSACTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedTextSecondary,
                    letterSpacing = 1.sp
                )
            }

            if (uiState.transactions.isEmpty()) {
                item {
                    UnitedEmptyState(
                        title = "No Transactions Yet",
                        subtitle = "Scan any UPI QR or make a transfer to get started.",
                        icon = Icons.Default.ReceiptLong
                    )
                }
            } else {
                items(uiState.transactions) { txn ->
                    TransactionRowItem(transaction = txn, onClick = { onTransactionClick(txn.id) })
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TransactionRowItem(
    transaction: UpiTransaction,
    onClick: () -> Unit
) {
    val isDebit = transaction.type == TransactionType.DEBIT
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(transaction.timestamp) { dateFormat.format(Date(transaction.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = UnitedWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Direction Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isDebit) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDebit) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (isDebit) Color(0xFFDC2626) else UnitedSuccess,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Payee Title & Date (WEIGHTED 1f, maxLines = 1, ellipsis so it NEVER crushes amount!)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = transaction.payeeName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.5.sp,
                    color = UnitedTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = formattedDate,
                    fontSize = 11.5.sp,
                    color = UnitedTextSecondary,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Amount & UTR (Unconstrained natural width, right aligned, NEVER wraps!)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${if (isDebit) "-" else "+"}${transaction.amount.toInrCurrency()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.5.sp,
                    color = if (isDebit) UnitedTextPrimary else UnitedSuccess,
                    maxLines = 1,
                    softWrap = false
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "UTR: ${transaction.utrNumber.takeLast(4)}",
                    fontSize = 10.5.sp,
                    color = UnitedTextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
