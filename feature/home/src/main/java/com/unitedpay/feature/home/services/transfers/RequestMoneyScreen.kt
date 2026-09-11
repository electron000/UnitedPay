package com.unitedpay.feature.home.services.transfers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.unitedpay.core.designsystem.components.UnitedCenteredAmountCard
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.mock.UnitedMockData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestMoneyScreen(onBackClick: () -> Unit) {
    val defaultRequest = UnitedMockData.samplePaymentRequest
    var vpa by remember { mutableStateOf(defaultRequest.vpa) }
    var amount by remember { mutableStateOf(defaultRequest.defaultAmount) }
    var note by remember { mutableStateOf(defaultRequest.note) }
    var isRequested by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Request Money", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
        },
        bottomBar = {
            if (!isRequested) {
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
                            onClick = { isRequested = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .defaultMinSize(minHeight = 52.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue, contentColor = UnitedWhite),
                            enabled = amount.isNotBlank() && vpa.isNotBlank()
                        ) {
                            Text(
                                text = "Request ₹$amount via UPI",
                                fontWeight = FontWeight.Bold,
                                color = UnitedWhite,
                                fontSize = 15.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isRequested) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnitedSuccess, modifier = Modifier.size(68.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Payment Request Sent!", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = UnitedTextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Requested ₹$amount from $vpa", fontSize = 13.sp, color = UnitedTextSecondary)
                Text("Payer has 24 hours to approve and pay via UPI", fontSize = 11.5.sp, color = UnitedMoneyBlue)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .defaultMinSize(minHeight = 52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue, contentColor = UnitedWhite)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold, color = UnitedWhite, fontSize = 15.sp)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                        OutlinedTextField(
                            value = vpa,
                            onValueChange = { vpa = it },
                            label = { Text("Payer UPI ID / Mobile Number", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = UnitedTextPrimary,
                                unfocusedTextColor = UnitedTextPrimary,
                                focusedBorderColor = UnitedMoneyBlue,
                                unfocusedBorderColor = UnitedBorderLight,
                                cursorColor = UnitedMoneyBlue
                            )
                        )
                    }
                }

                UnitedCenteredAmountCard(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "REQUESTED AMOUNT",
                    maxDigits = 6
                )

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
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            singleLine = true,
                            maxLines = 1,
                            label = { Text("Remarks / Note (Optional)", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = UnitedTextPrimary,
                                unfocusedTextColor = UnitedTextPrimary,
                                focusedBorderColor = UnitedMoneyBlue,
                                unfocusedBorderColor = UnitedBorderLight,
                                cursorColor = UnitedMoneyBlue
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
