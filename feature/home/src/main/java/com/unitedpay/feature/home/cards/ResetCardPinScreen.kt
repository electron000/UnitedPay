package com.unitedpay.feature.home.cards

import com.unitedpay.core.designsystem.components.UnitedToast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.unitedpay.core.designsystem.components.UnitedPinnedBottomBar
import com.unitedpay.core.designsystem.components.UnitedPrimaryButton
import com.unitedpay.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetCardPinScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var cvv by remember { mutableStateOf("492") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Reset Card PIN", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
                        text = "Update PIN Instantly",
                        onClick = {
                            if (newPin.length == 4 && newPin == confirmPin) {
                                isSuccess = true
                            } else {
                                UnitedToast.error("Please enter matching 4-digit PIN")
                            }
                        }
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
                Text("ATM PIN Changed Successfully!", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = UnitedTextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Your 4-digit PIN for RuPay Platinum ending with 0000 has been updated instantly.", fontSize = 13.sp, color = UnitedTextSecondary, textAlign = TextAlign.Center)
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
                item {
                    Text("Enter New 4-Digit PIN", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = UnitedTextPrimary)
                    Text("You will need this PIN for ATM cash withdrawals and merchant POS swipes", fontSize = 12.sp, color = UnitedTextSecondary)
                }

                item {
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { cvv = it },
                        label = { Text("Card CVV (3-digit)", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        modifier = Modifier.fillMaxWidth(),
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

                item {
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { if (it.length <= 4) newPin = it },
                        label = { Text("New 4-Digit PIN", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        modifier = Modifier.fillMaxWidth(),
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

                item {
                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { if (it.length <= 4) confirmPin = it },
                        label = { Text("Re-enter New PIN", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        modifier = Modifier.fillMaxWidth(),
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
        }
    }
}
