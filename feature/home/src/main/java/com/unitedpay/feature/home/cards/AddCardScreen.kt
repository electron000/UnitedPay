package com.unitedpay.feature.home.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.components.UnitedPinnedBottomBar
import com.unitedpay.core.designsystem.components.UnitedPrimaryButton
import com.unitedpay.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(onBackClick: () -> Unit) {
    var cardNumber by remember { mutableStateOf("6521 8900 1234 5678") }
    var cardHolder by remember { mutableStateOf("ARUNJYOTI CHANGKAKOTY") }
    var expiryDate by remember { mutableStateOf("11/28") }
    var cvv by remember { mutableStateOf("492") }
    var isSuccess by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Add New Card", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
            if (!isSuccess) {
                UnitedPinnedBottomBar {
                    UnitedPrimaryButton(
                        text = "Save & Authenticate Card (₹2 refundable)",
                        onClick = { isSuccess = true }
                    )
                }
            }
        }
    ) { innerPadding ->
        if (isSuccess) {
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
                Text("Card Added Successfully!", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = UnitedTextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Your RuPay card ending with ${cardNumber.takeLast(4)} has been verified and tokenized per RBI guidelines.", fontSize = 13.sp, color = UnitedTextSecondary, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue, contentColor = UnitedWhite)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold, color = UnitedWhite, fontSize = 15.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Live Card Preview
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BrandShield(size = 32.dp, asCardBadge = true)
                                Text("RuPay Platinum", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedWhite)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = cardNumber.ifBlank { "•••• •••• •••• ••••" },
                                fontFamily = FontFamily.Monospace,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = UnitedWhite
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("CARD HOLDER", fontSize = 9.sp, color = UnitedWhite.copy(alpha = 0.7f))
                                    Text(cardHolder.ifBlank { "NAME SURNAME" }, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedWhite)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("VALID THRU", fontSize = 9.sp, color = UnitedWhite.copy(alpha = 0.7f))
                                    Text(expiryDate.ifBlank { "MM/YY" }, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UnitedWhite)
                                }
                            }
                        }
                    }
                }

                // Input Form
                item {
                    Text("Card Details", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = UnitedTextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it },
                        label = { Text("16-Digit Card Number", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UnitedMoneyBlue,
                            unfocusedBorderColor = UnitedBorderLight,
                            focusedTextColor = UnitedTextPrimary,
                            unfocusedTextColor = UnitedTextPrimary,
                            cursorColor = UnitedMoneyBlue
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = cardHolder,
                        onValueChange = { cardHolder = it },
                        label = { Text("Cardholder Name", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UnitedMoneyBlue,
                            unfocusedBorderColor = UnitedBorderLight,
                            focusedTextColor = UnitedTextPrimary,
                            unfocusedTextColor = UnitedTextPrimary,
                            cursorColor = UnitedMoneyBlue
                        )
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = { expiryDate = it },
                            label = { Text("Expiry (MM/YY)", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UnitedMoneyBlue,
                                unfocusedBorderColor = UnitedBorderLight,
                                focusedTextColor = UnitedTextPrimary,
                                unfocusedTextColor = UnitedTextPrimary,
                                cursorColor = UnitedMoneyBlue
                            )
                        )

                        OutlinedTextField(
                            value = cvv,
                            onValueChange = { cvv = it },
                            label = { Text("CVV (3-digit)", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UnitedMoneyBlue,
                                unfocusedBorderColor = UnitedBorderLight,
                                focusedTextColor = UnitedTextPrimary,
                                unfocusedTextColor = UnitedTextPrimary,
                                cursorColor = UnitedMoneyBlue
                            )
                        )
                    }
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = UnitedSuccess, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Encrypted with 256-bit bank level tokenization", fontSize = 11.5.sp, color = UnitedTextSecondary)
                    }
                }
            }
        }
    }
}
