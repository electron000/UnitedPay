package com.unitedpay.feature.home.services.utilities

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
 * HookoluPay BBPS Education Fees Payment Screen.
 * Provides fee payment presentment for schools, colleges, and universities across Assam.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationFeesScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var feeDetails by remember { mutableStateOf<EducationFeeDetails?>(null) }
    var isPaying by remember { mutableStateOf(false) }
    var paymentDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        feeDetails = HookoluServices.utility.getEducationFeeDetails()
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
                                text = "Education Fees",
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "BBPS School & College Fees",
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
        },
        bottomBar = {
            if (!paymentDone) {
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
                                coroutineScope.launch {
                                    isPaying = true
                                    kotlinx.coroutines.delay(1200)
                                    paymentDone = true
                                    isPaying = false
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
                            enabled = !isPaying
                        ) {
                            if (isPaying) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = UnitedWhite, strokeWidth = 2.5.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Processing Fee Payment...", fontSize = 14.sp)
                            } else {
                                Text("Pay Semester Fee • ₹12,400.00", fontWeight = FontWeight.Bold, fontSize = 15.sp)
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

            feeDetails?.let { d ->
                if (paymentDone) {
                    item {
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
                                Text("Fee Payment Successful", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                Text("BBPS Transaction ID: BBPS-EDU-89210", fontSize = 12.sp, color = UnitedTextSecondary)

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = UnitedBorderLight)
                                Spacer(modifier = Modifier.height(16.dp))

                                ReceiptRow("Institution", d.instituteName)
                                ReceiptRow("Student Name", d.studentName)
                                ReceiptRow("Roll / Reg No", d.rollNumber)
                                ReceiptRow("Academic Term", d.academicTerm)
                                ReceiptRow("Fee Amount Paid", d.formattedFee, isBold = true, valueColor = UnitedMoneyBlue)

                                Spacer(modifier = Modifier.height(20.dp))

                                OutlinedButton(
                                    onClick = {
                                        val activity = context as? Activity
                                        if (activity != null) {
                                            val currentProfile = com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile()
                                            val activeBank = com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull()
                                            val bankTitle = activeBank?.let { "${it.bankName} (${it.accountNumberMasked})" } ?: "State Bank of India (•••• 4821)"

                                            ReceiptShareHelper.shareTransactionReceipt(
                                                context = activity,
                                                receiptDetails = ReceiptShareHelper.ReceiptDetails(
                                                    title = "College Tuition Fee Receipt",
                                                    amount = d.formattedFee,
                                                    sender = currentProfile.fullName,
                                                    receiver = d.instituteName,
                                                    utr = "BBPS-EDU-89210",
                                                    date = "13 Sep 2026",
                                                    time = "10:30 PM",
                                                    status = "SUCCESSFUL",
                                                    bankName = bankTitle,
                                                    paymentMode = "BBPS Education",
                                                    note = "Student: ${d.studentName} • Roll: ${d.rollNumber}"
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
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = UnitedWhite,
                            border = BorderStroke(1.dp, UnitedBorderLight),
                            shadowElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEFF6FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(d.instituteName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                        Text("Affiliated State University", fontSize = 11.sp, color = UnitedTextSecondary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = UnitedBorderLight)
                                Spacer(modifier = Modifier.height(16.dp))

                                ReceiptRow("Student Full Name", d.studentName)
                                ReceiptRow("Roll Number", d.rollNumber)
                                ReceiptRow("Fee Description", d.academicTerm)
                                ReceiptRow("Due Date", d.dueDate)
                                ReceiptRow("Total Bill Amount", d.formattedFee, isBold = true, valueColor = UnitedMoneyBlue)
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
