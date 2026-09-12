package com.unitedpay.feature.passbook

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.core.content.FileProvider
import com.unitedpay.core.designsystem.components.UnitedToast
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.designsystem.util.ReceiptShareHelper
import com.unitedpay.core.model.TransactionRepository
import com.unitedpay.core.model.TransactionType
import com.unitedpay.core.model.UpiTransaction
import com.unitedpay.core.model.mock.UnitedMockData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Authentic Tier-1 UPI Transaction Detail Screen matching Image 3 (media_1789196510463.jpg):
 * - Hero Status Header with circular green checkmark badge & large bold amount
 * - Outlined "Share receipt" and filled "Pay again" action buttons
 * - Detailed "TRANSACTION DETAILS" card with copyable UTR & Transaction ID
 * - Full-screen branded receipt image generation & sharing via native Android intent
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    onBackClick: () -> Unit,
    onPayAgain: (UpiTransaction) -> Unit = {}
) {
    val context = LocalContext.current
    val transaction: UpiTransaction = remember(transactionId) {
        TransactionRepository.getTransactionById(transactionId)
            ?: UnitedMockData.initialTransactions.find { it.id == transactionId }
            ?: UnitedMockData.initialTransactions.first()
    }

    val isDebit = transaction.type == TransactionType.DEBIT
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(transaction.timestamp) { dateFormat.format(Date(transaction.timestamp)) }

    Scaffold(
        containerColor = Color(0xFFF4F6FB),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Transaction details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = UnitedTextPrimary
                    )
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
                actions = {
                    IconButton(onClick = { shareTransactionReceipt(context, transaction) }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = UnitedMoneyBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UnitedWhite,
                    titleContentColor = UnitedTextPrimary,
                    navigationIconContentColor = UnitedTextPrimary,
                    actionIconContentColor = UnitedTextPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Hero Status Header (Circular Green Checkmark Badge)
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00C853)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Payment Successful",
                        tint = UnitedWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isDebit) "Paid to ${transaction.payeeName}" else "Received from ${transaction.payeeName}",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = UnitedTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "₹${String.format(Locale.getDefault(), "%,.2f", transaction.amount)}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp,
                color = UnitedTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF00C853),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Completed • $formattedDate",
                    fontSize = 12.5.sp,
                    color = UnitedTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 2. Action Buttons (Share receipt & Pay again)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Share Receipt Button
                OutlinedButton(
                    onClick = { shareTransactionReceipt(context, transaction) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = UnitedMoneyBlue),
                    border = androidx.compose.foundation.BorderStroke(1.25.dp, UnitedMoneyBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = UnitedMoneyBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share receipt",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                // Pay Again Button
                Button(
                    onClick = { onPayAgain(transaction) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UnitedMoneyBlue,
                        contentColor = UnitedWhite
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = UnitedWhite
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pay again",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Detailed Transaction Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Text(
                        text = "TRANSACTION DETAILS",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextSecondary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // To
                    DetailRow(
                        label = "To",
                        value = "${transaction.payeeName}\n${transaction.payeeVpa}",
                        showCopy = false
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                    // From
                    DetailRow(
                        label = "From",
                        value = "${transaction.payerName}\n${transaction.payerVpa}",
                        showCopy = false
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                    // Debited / Credited Account
                    DetailRow(
                        label = if (isDebit) "Debited from" else "Credited to",
                        value = "${transaction.bankName}\nSavings Account ${transaction.bankAccountNumberMasked}",
                        showCopy = false
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                    // UPI Ref / UTR Number (with Copy)
                    DetailRow(
                        label = "UPI Ref / UTR",
                        value = transaction.utrNumber,
                        showCopy = true,
                        onCopy = {
                            copyToClipboard(context, "UTR Number", transaction.utrNumber)
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                    // United Pay Transaction ID (with Copy)
                    DetailRow(
                        label = "Transaction ID",
                        value = transaction.id,
                        showCopy = true,
                        onCopy = {
                            copyToClipboard(context, "Transaction ID", transaction.id)
                        }
                    )

                    val noteText = transaction.note
                    if (!noteText.isNullOrBlank()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                        DetailRow(
                            label = "Payment note",
                            value = noteText,
                            showCopy = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Regulatory & Security Trust Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFD)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF00C853),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "UPI payments are protected with 256-bit encryption and regulated under RBI & NPCI guidelines.",
                        fontSize = 11.5.sp,
                        color = UnitedTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Having issues with this payment? Dispute Center",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = UnitedMoneyBlue,
                modifier = Modifier
                    .clickable {
                        UnitedToast.info("Opening Dispute Center...")
                    }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    showCopy: Boolean = false,
    onCopy: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            color = UnitedTextSecondary,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.width(105.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = UnitedTextPrimary,
                textAlign = TextAlign.End,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f, fill = false)
            )

            if (showCopy) {
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = UnitedMoneyBlue,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    UnitedToast.success("$label copied to clipboard")
}

/**
 * Opens Android's native system sharing intent with complete formatted transaction receipt
 * as both a visual high-resolution receipt image and formatted plain text.
 */
fun shareTransactionReceipt(context: Context, transaction: UpiTransaction) {
    ReceiptShareHelper.shareReceipt(context, transaction)
}
