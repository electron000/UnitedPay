package com.unitedpay.feature.home.services.transfers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.designsystem.components.UnitedNpciMpinSheet
import com.unitedpay.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckBalanceScreen(onBackClick: () -> Unit) {
    var isPinVerified by remember { mutableStateOf(false) }
    var account by remember {
        mutableStateOf(com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull()
            ?: com.unitedpay.core.model.mock.UnitedMockData.linkedBankAccounts.first())
    }
    var balanceAmount by remember { mutableStateOf(account.balance ?: 10000.00) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.banking.getLinkedBankAccounts().collect { res ->
            if (res is Resource.Success && res.data.isNotEmpty()) {
                val primary = res.data.find { it.isPrimary } ?: res.data.first()
                account = primary
                balanceAmount = primary.balance ?: 10000.00
            }
        }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Check Bank Balance", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
        if (isPinVerified) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(14.dp))
                Text(account.bankName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = UnitedTextSecondary)
                Text("A/C ${account.accountNumberMasked}", fontSize = 13.sp, color = UnitedTextSecondary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Available Balance", fontSize = 13.sp, color = UnitedTextSecondary)
                Text("₹ ${String.format("%.2f", balanceAmount)}", fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, color = UnitedMoneyBlue)
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .defaultMinSize(minHeight = 52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue, contentColor = UnitedWhite)
                ) {
                    Text(
                        "Done",
                        fontWeight = FontWeight.Bold,
                        color = UnitedWhite,
                        fontSize = 15.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.BottomCenter
            ) {
                UnitedNpciMpinSheet(
                    title = "NPCI UPI SECURE MPIN",
                    subtitle = "Checking Bank Balance for ${account.bankName} (${account.accountNumberMasked})",
                    pinLength = 6,
                    onPinSubmitted = {
                        isPinVerified = true
                    },
                    onDismiss = onBackClick
                )
            }
        }
    }
}
