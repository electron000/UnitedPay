package com.unitedpay.feature.home.services.retailer

import com.unitedpay.core.designsystem.components.UnitedToast
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.components.UnitedIcons
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.designsystem.util.ReceiptShareHelper
import com.unitedpay.core.model.*
import com.unitedpay.core.model.repository.HookoluServices
import kotlinx.coroutines.launch

/**
 * HookoluPay Retailer & Business Correspondent (BC) Agent Operations Hub.
 * Features live business volumes, commission ledger, instant wallet settlement,
 * and customer credit/khata tracking for Arunjyoti Enterprise.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetailerDashboardScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var metrics by remember { mutableStateOf<RetailerMetrics?>(null) }
    var slabs by remember { mutableStateOf<List<RetailerCommissionSlab>>(emptyList()) }
    var khataEntries by remember { mutableStateOf<List<CustomerKhataEntry>>(emptyList()) }

    var isSettling by remember { mutableStateOf(false) }
    var settlementReceipt by remember { mutableStateOf<SettlementReceipt?>(null) }

    var isAddingKhata by remember { mutableStateOf(false) }
    var newKhataName by remember { mutableStateOf("") }
    var newKhataPhone by remember { mutableStateOf("") }
    var newKhataAmount by remember { mutableStateOf("") }
    var newKhataNote by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        metrics = HookoluServices.retailer.getMetrics()
        slabs = HookoluServices.retailer.getCommissionSlabs()
        khataEntries = HookoluServices.retailer.getKhataEntries()
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandShield(size = 26.dp, asCardBadge = true)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Merchant & BC Hub",
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Commission Ledger & Instant Settlement",
                                fontSize = 11.sp,
                                color = UnitedTextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = UnitedTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = UnitedWhite)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Merchant Identity & Today's Volume Banner
            metrics?.let { m ->
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 3.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(Color(0xFF0F2B5C), Color(0xFF004F9F), Color(0xFF0078DF))
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(Color(0x33FFFFFF)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Store,
                                                contentDescription = null,
                                                tint = UnitedWhite,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = m.merchantName,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = UnitedWhite
                                            )
                                            Spacer(modifier = Modifier.height(1.dp))
                                            Text(
                                                text = "Agent ID: ${m.agentId}",
                                                fontSize = 11.sp,
                                                color = UnitedWhite.copy(alpha = 0.8f)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = Color(0xFFDCFCE7),
                                        border = BorderStroke(0.5.dp, Color(0xFF10B981))
                                    ) {
                                        Text(
                                            text = "BC Active",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D),
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Today's Volume Sub-card
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0x26FFFFFF))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text("Today's Volume", fontSize = 11.sp, color = UnitedWhite.copy(alpha = 0.8f))
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(m.formattedVolume, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = UnitedWhite)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text("${m.todayTxnCount} Transactions", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = UnitedLimeAccent)
                                        }
                                    }

                                    // Earned Commission Sub-card
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0x26FFFFFF))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text("Commission", fontSize = 11.sp, color = UnitedWhite.copy(alpha = 0.8f))
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(m.formattedCommission, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = UnitedLimeAccent)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text("Auto-credited", fontSize = 10.5.sp, color = UnitedWhite.copy(alpha = 0.85f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Instant Wallet Settlement to Bank
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = UnitedWhite,
                        border = BorderStroke(1.dp, UnitedBorderLight),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Wallet Settlement Balance", fontSize = 12.sp, color = UnitedTextSecondary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(m.formattedWallet, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = UnitedTextPrimary)
                                }

                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = Color(0xFFEFF6FF),
                                    border = BorderStroke(0.5.dp, UnitedMoneyBlue.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "T+0 IMPS Instant",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedMoneyBlue,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Bank Destination Box
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = UnitedMoneyBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "To: ${m.primarySettlementBank} (${m.primarySettlementAccountMasked})",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = UnitedTextPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isSettling = true
                                        val receipt = HookoluServices.retailer.settleToBank(m.walletBalance)
                                        settlementReceipt = receipt
                                        isSettling = false
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                enabled = !isSettling && m.walletBalance > 0
                            ) {
                                if (isSettling) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = UnitedWhite, strokeWidth = 2.5.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Dispatching Settlement...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                } else {
                                    Icon(UnitedIcons.SettlementVault, contentDescription = null, modifier = Modifier.size(16.dp), tint = UnitedWhite)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Settle Wallet to Bank Instant", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Settlement Success Slip
            settlementReceipt?.let { s ->
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFDCFCE7),
                        border = BorderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Settlement Dispatched Successfully", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Amount: ${s.formattedAmount} • UTR: ${s.utr}\nCredited to ${s.toBank} (${s.toAccountMasked})", fontSize = 11.sp, color = Color(0xFF15803D))
                        }
                    }
                }
            }

            // 3. Service Commission Breakdown
            item {
                Text("Today's Commission Earnings by Service", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
            }

            items(slabs) { slab ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = UnitedWhite,
                    border = BorderStroke(1.dp, UnitedBorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(slab.serviceName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                            Text(slab.commissionRule, fontSize = 11.sp, color = UnitedTextSecondary)
                        }
                        Text(
                            "+${slab.formattedEarned}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }

            // 4. Customer Digital Khata Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Customer Digital Khata (Udhar)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                    TextButton(onClick = { isAddingKhata = !isAddingKhata }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = UnitedMoneyBlue)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isAddingKhata) "Cancel" else "Add Entry", color = UnitedMoneyBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Add Khata Entry Form
            if (isAddingKhata) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = UnitedCanvasLight,
                        border = BorderStroke(1.dp, UnitedMoneyBlue.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("New Customer Khata Entry", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newKhataName,
                                onValueChange = { newKhataName = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Customer Name") },
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newKhataPhone,
                                onValueChange = { newKhataPhone = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Customer Phone (+91)") },
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newKhataAmount,
                                onValueChange = { newKhataAmount = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Due Amount (₹)") },
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newKhataNote,
                                onValueChange = { newKhataNote = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Item / Transaction Note") },
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    if (newKhataName.isNotBlank() && newKhataAmount.isNotBlank()) {
                                        coroutineScope.launch {
                                            HookoluServices.retailer.addKhataEntry(
                                                name = newKhataName,
                                                phone = newKhataPhone,
                                                amount = newKhataAmount.toDoubleOrNull() ?: 0.0,
                                                isDebit = true,
                                                note = newKhataNote
                                            )
                                            khataEntries = HookoluServices.retailer.getKhataEntries()
                                            isAddingKhata = false
                                            newKhataName = ""
                                            newKhataPhone = ""
                                            newKhataAmount = ""
                                            newKhataNote = ""
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue)
                            ) {
                                Text("Record in Khata", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Customer Khata List
            items(khataEntries) { entry ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = UnitedWhite,
                    border = BorderStroke(1.dp, UnitedBorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.customerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                            Text("${entry.phoneNumber} • ${entry.lastNote}", fontSize = 11.sp, color = UnitedTextSecondary)
                            Text(entry.lastTxnDate, fontSize = 10.sp, color = UnitedTextSecondary)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (entry.isDebit) "Due: ${entry.formattedBalance}" else "Cleared",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (entry.isDebit) Color(0xFFEF4444) else Color(0xFF10B981)
                            )
                            if (entry.isDebit) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEFF6FF),
                                    border = BorderStroke(1.dp, UnitedMoneyBlue.copy(alpha = 0.2f)),
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .clickable {
                                            UnitedToast.success("WhatsApp reminder sent to ${entry.customerName}")
                                        }
                                ) {
                                    Text(
                                        text = "Send Reminder",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedMoneyBlue,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}
