package com.unitedpay.feature.home.services.banking

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.designsystem.util.ReceiptShareHelper
import com.unitedpay.core.model.*
import com.unitedpay.core.model.repository.HookoluServices
import kotlinx.coroutines.launch

/**
 * HookoluPay Domestic Money Transfer (DMT) Screen.
 * Provides IMPS/NEFT remittances, penny-drop beneficiary validation,
 * and RBI transfer quota management for Arunjyoti Changkakoty.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DmtScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var sender by remember { mutableStateOf<DmtSender?>(null) }
    var beneficiaries by remember { mutableStateOf<List<DmtBeneficiary>>(emptyList()) }
    var selectedBeneficiaryId by remember { mutableStateOf<String?>(null) }
    var transferMode by remember { mutableStateOf(DmtTransferMode.IMPS) }
    var amountText by remember { mutableStateOf("5000") }

    var isAddingBeneficiary by remember { mutableStateOf(false) }
    var newBenName by remember { mutableStateOf("") }
    var newBenAccount by remember { mutableStateOf("") }
    var newBenIfsc by remember { mutableStateOf("SBIN0000123") }
    var newBenBank by remember { mutableStateOf("State Bank of India") }

    var isProcessing by remember { mutableStateOf(false) }
    var transferReceipt by remember { mutableStateOf<DmtTransactionReceipt?>(null) }

    LaunchedEffect(Unit) {
        sender = HookoluServices.dmt.getSenderProfile("6002239926")
        val list = HookoluServices.dmt.getBeneficiaries()
        beneficiaries = list
        if (list.isNotEmpty()) selectedBeneficiaryId = list.first().id
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
                                text = "Money Transfer (DMT)",
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Domestic Remittance • IMPS & NEFT",
                                fontSize = 11.sp,
                                color = UnitedTextSecondary
                            )
                        }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = UnitedWhite)
            )
        },
        bottomBar = {
            if (transferReceipt == null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding(),
                    color = UnitedWhite,
                    shadowElevation = 8.dp,
                    border = BorderStroke(0.5.dp, UnitedBorderLight)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Button(
                            onClick = {
                                val benId = selectedBeneficiaryId ?: return@Button
                                val amt = amountText.toDoubleOrNull() ?: return@Button
                                coroutineScope.launch {
                                    isProcessing = true
                                    val receipt = HookoluServices.dmt.sendMoney(benId, amt, transferMode)
                                    transferReceipt = receipt
                                    isProcessing = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UnitedMoneyBlue,
                                contentColor = UnitedWhite
                            ),
                            enabled = selectedBeneficiaryId != null && amountText.toDoubleOrNull() != null && !isProcessing
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = UnitedWhite,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Routing through IMPS Gateway...", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            } else {
                                Text("Transfer ₹$amountText via ${transferMode.name}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
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

            // 1. Sender Limit Card (Arunjyoti Changkakoty)
            sender?.let { s ->
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = s.fullName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${s.mobileNumber} • ${s.kycTier}",
                                        fontSize = 11.sp,
                                        color = UnitedTextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("RBI Verified", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Limit Progress Bar
                            val progress = (s.usedLimit / s.monthlyLimit).toFloat().coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = UnitedMoneyBlue,
                                trackColor = UnitedCanvasLight,
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Monthly Remaining: ${s.formattedRemainingLimit}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = "Limit: ${s.formattedMonthlyLimit}",
                                    fontSize = 11.sp,
                                    color = UnitedTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Receipt Display
            if (transferReceipt != null) {
                item {
                    val receipt = transferReceipt!!
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = UnitedWhite,
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f)),
                        shadowElevation = 3.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(32.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "DMT Transfer Successful",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary
                            )
                            Text(
                                text = "UTR: ${receipt.utr} • Mode: ${receipt.transferMode}",
                                fontSize = 12.sp,
                                color = UnitedTextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = UnitedBorderLight)
                            Spacer(modifier = Modifier.height(16.dp))

                            ReceiptRow("Beneficiary Name", receipt.beneficiaryName)
                            ReceiptRow("Bank & Account", "${receipt.bankName} (${receipt.accountNumberMasked})")
                            ReceiptRow("Sender Name", receipt.senderName)
                            ReceiptRow("Transfer Amount", receipt.formattedAmount, isBold = true, valueColor = UnitedMoneyBlue)
                            ReceiptRow("Service Fee / GST", receipt.formattedFee)
                            ReceiptRow("Total Debited", receipt.formattedTotal, isBold = true)

                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedButton(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        ReceiptShareHelper.shareTransactionReceipt(
                                            context = activity,
                                            receiptDetails = ReceiptShareHelper.ReceiptDetails(
                                                title = "HookoluPay DMT Remittance",
                                                amount = receipt.formattedAmount,
                                                sender = receipt.senderName,
                                                receiver = receipt.beneficiaryName,
                                                utr = receipt.utr,
                                                date = "13 Sep 2026",
                                                time = "10:30 PM",
                                                status = "SUCCESSFUL",
                                                bankName = receipt.bankName,
                                                paymentMode = "IMPS Remittance",
                                                note = "A/c: ${receipt.accountNumberMasked} • Fee: ${receipt.formattedFee}"
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, UnitedMoneyBlue)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp), tint = UnitedMoneyBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share Native Receipt Slip", color = UnitedMoneyBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // 2. Beneficiaries Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Saved Beneficiaries",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary
                        )
                        TextButton(onClick = { isAddingBeneficiary = !isAddingBeneficiary }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = UnitedMoneyBlue)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isAddingBeneficiary) "Cancel" else "Add New", color = UnitedMoneyBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // Add Beneficiary Form
                if (isAddingBeneficiary) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = UnitedCanvasLight,
                            border = BorderStroke(1.dp, UnitedMoneyBlue.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Add Beneficiary (Penny-Drop Verification)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = newBenName,
                                    onValueChange = { newBenName = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Beneficiary Full Name") },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newBenAccount,
                                    onValueChange = { newBenAccount = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Bank Account Number") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newBenIfsc,
                                    onValueChange = { newBenIfsc = it.uppercase() },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("IFSC Code") },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        if (newBenName.isNotBlank() && newBenAccount.isNotBlank()) {
                                            coroutineScope.launch {
                                                val created = HookoluServices.dmt.addBeneficiary(
                                                    newBenName, newBenAccount, newBenIfsc, newBenBank
                                                )
                                                beneficiaries = HookoluServices.dmt.getBeneficiaries()
                                                selectedBeneficiaryId = created.id
                                                isAddingBeneficiary = false
                                                newBenName = ""
                                                newBenAccount = ""
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue)
                                ) {
                                    Text("Verify & Save Beneficiary", color = UnitedWhite, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Beneficiary Cards List
                items(beneficiaries) { ben ->
                    val isSelected = selectedBeneficiaryId == ben.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedBeneficiaryId = ben.id },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) UnitedMoneyBlue.copy(alpha = 0.08f) else UnitedWhite,
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) UnitedMoneyBlue else UnitedBorderLight
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) UnitedMoneyBlue else UnitedCanvasLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (isSelected) UnitedWhite else UnitedMoneyBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = ben.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = "${ben.bankName} • ${ben.maskedAccountNumber} • ${ben.ifsc}",
                                    fontSize = 11.sp,
                                    color = UnitedTextSecondary
                                )
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedBeneficiaryId = ben.id },
                                colors = RadioButtonDefaults.colors(selectedColor = UnitedMoneyBlue)
                            )
                        }
                    }
                }

                // 3. Transfer Amount & Mode
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = UnitedWhite,
                        border = BorderStroke(1.dp, UnitedBorderLight),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Transfer Amount & Mode",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = amountText,
                                onValueChange = { if (it.all { char -> char.isDigit() }) amountText = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                leadingIcon = {
                                    Text("₹", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = UnitedMoneyBlue)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = UnitedMoneyBlue,
                                    unfocusedBorderColor = UnitedBorderLight
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                listOf(DmtTransferMode.IMPS, DmtTransferMode.NEFT).forEach { mode ->
                                    val isSelected = transferMode == mode
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { transferMode = mode },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) UnitedMoneyBlue else UnitedCanvasLight,
                                        border = BorderStroke(1.dp, if (isSelected) UnitedMoneyBlue else UnitedBorderLight)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = mode.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) UnitedWhite else UnitedTextPrimary
                                            )
                                            Text(
                                                text = if (mode == DmtTransferMode.IMPS) "Instant 24x7" else "Batch RTGS/NEFT",
                                                fontSize = 10.sp,
                                                color = if (isSelected) UnitedWhite.copy(alpha = 0.8f) else UnitedTextSecondary
                                            )
                                        }
                                    }
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

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = UnitedTextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = UnitedTextSecondary)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
