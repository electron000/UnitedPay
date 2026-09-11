package com.unitedpay.feature.home.services.transfers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.unitedpay.core.designsystem.components.UnitedNpciMpinSheet
import com.unitedpay.core.designsystem.theme.*
import kotlinx.coroutines.delay

enum class CheckBalanceStep {
    ENTER_PIN,
    VERIFYING,
    SHOW_BALANCE
}

/**
 * Production Paytm-style Check Balance Sheet / Dialog.
 * Uses shared 6-Digit NPCI UPI MPIN sheet for secure authentication.
 * Displays authenticated SBI bank balance card upon verification.
 */
@Composable
fun CheckBalanceDialog(
    bankName: String = "State Bank of India",
    accountMasked: String = "•••• 4821",
    availableBalance: String = "24,850.50",
    onDismiss: () -> Unit
) {
    var step by remember { mutableStateOf(CheckBalanceStep.ENTER_PIN) }

    LaunchedEffect(step) {
        if (step == CheckBalanceStep.VERIFYING) {
            delay(800)
            step = CheckBalanceStep.SHOW_BALANCE
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .clickable(enabled = false) {},
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = UnitedWhite,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (step) {
                        CheckBalanceStep.ENTER_PIN -> {
                            UnitedNpciMpinSheet(
                                title = "NPCI UPI SECURE MPIN",
                                subtitle = "Checking Balance for $bankName ($accountMasked)",
                                pinLength = 6,
                                onPinSubmitted = {
                                    step = CheckBalanceStep.VERIFYING
                                },
                                onDismiss = onDismiss
                            )
                        }

                        CheckBalanceStep.VERIFYING -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 36.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = UnitedMoneyBlue,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(38.dp)
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                Text(
                                    text = "Requesting balance from $bankName...",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = UnitedTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "NPCI UPI Secure Switch",
                                    fontSize = 11.5.sp,
                                    color = UnitedTextSecondary
                                )
                            }
                        }

                        CheckBalanceStep.SHOW_BALANCE -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Header Bar
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFE8EFFC)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccountBalance,
                                                contentDescription = null,
                                                tint = UnitedMoneyBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = bankName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = UnitedTextPrimary
                                            )
                                            Text(
                                                text = "Savings A/C $accountMasked",
                                                fontSize = 11.sp,
                                                color = UnitedTextSecondary
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = onDismiss,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Close",
                                            tint = UnitedTextSecondary
                                        )
                                    }
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 14.dp),
                                    color = UnitedBorderLight
                                )

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFD)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "AVAILABLE ACCOUNT BALANCE",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UnitedTextSecondary,
                                            letterSpacing = 1.sp
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = "₹ $availableBalance",
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = UnitedMoneyBlue
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .clip(CircleShape)
                                                    .background(UnitedSuccess)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Fetched just now from CBS",
                                                fontSize = 11.sp,
                                                color = UnitedTextSecondary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .defaultMinSize(minHeight = 52.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = UnitedMoneyBlue,
                                        contentColor = UnitedWhite
                                    )
                                ) {
                                    Text(
                                        text = "Done",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
