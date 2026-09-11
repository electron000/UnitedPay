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
import com.unitedpay.core.designsystem.components.UnitedToast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.extensions.toInrCurrency
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.PaymentStatus
import com.unitedpay.core.model.TransactionRepository
import com.unitedpay.core.model.TransactionType
import com.unitedpay.core.model.UpiTransaction
import com.unitedpay.core.model.mock.UnitedMockData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Authentic Google Pay-style Transaction Detail Screen.
 * Displays bank-grade verification status, full UPI transaction metadata,
 * one-tap UTR / reference copy, and native Android system share sheet invocation.
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
        containerColor = UnitedBackgroundLight,
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
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Hero Status Header (GPay style)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE6F4EA)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(UnitedSuccess),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Payment Successful",
                        tint = UnitedWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isDebit) "Paid to ${transaction.payeeName}" else "Received from ${transaction.payeeName}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = UnitedTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "₹${String.format(Locale.getDefault(), "%,.2f", transaction.amount)}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34.sp,
                color = UnitedTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = UnitedSuccess,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Completed • $formattedDate",
                    fontSize = 12.sp,
                    color = UnitedTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = UnitedMoneyBlue),
                    border = androidx.compose.foundation.BorderStroke(1.dp, UnitedMoneyBlue)
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
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Pay Again Button
                Button(
                    onClick = { onPayAgain(transaction) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
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
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Detailed Transaction Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "TRANSACTION DETAILS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnitedTextSecondary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Regulatory & Security Trust Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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
                        tint = UnitedSuccess,
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
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = UnitedMoneyBlue,
                modifier = Modifier
                    .clickable {
                        UnitedToast.info("Opening Dispute Center...")
                    }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = UnitedTextSecondary,
            modifier = Modifier.width(100.dp)
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = UnitedTextPrimary,
                lineHeight = 18.sp
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
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Copies string data to clipboard and shows an instant toast.
 */
private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    UnitedToast.success("$label copied to clipboard")
}

/**
 * Generates an executive, high-resolution Google Pay-style receipt bitmap and stores it in cache.
 * Returns the secure content Uri via FileProvider.
 */
private fun generateReceiptImage(context: Context, transaction: UpiTransaction): Uri? {
    return try {
        val width = 1080
        val height = 1500
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Canvas Background
        canvas.drawColor(android.graphics.Color.parseColor("#F4F7FC"))

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Main White Card
        val cardRect = RectF(60f, 60f, 1020f, 1440f)
        paint.color = android.graphics.Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(cardRect, 32f, 32f, paint)

        // Top Brand Header Banner (United Logo 'U' Blue)
        paint.color = android.graphics.Color.parseColor("#0078DF")
        val headerRect = RectF(60f, 60f, 1020f, 220f)
        canvas.drawRoundRect(headerRect, 32f, 32f, paint)
        val headerFill = RectF(60f, 160f, 1020f, 220f)
        canvas.drawRect(headerFill, paint)

        // Brand Title
        paint.color = android.graphics.Color.WHITE
        paint.textSize = 40f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("UNITED PAY", 540f, 135f, paint)

        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("INSTANT UPI PAYMENT RECEIPT", 540f, 180f, paint)

        // Green Success Badge
        paint.color = android.graphics.Color.parseColor("#00C853")
        canvas.drawCircle(540f, 330f, 60f, paint)

        // White Checkmark
        paint.color = android.graphics.Color.WHITE
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(512f, 330f, 532f, 350f, paint)
        canvas.drawLine(532f, 350f, 568f, 308f, paint)

        // Status Text
        paint.style = Paint.Style.FILL
        paint.color = android.graphics.Color.parseColor("#00C853")
        paint.textSize = 32f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Payment Successful", 540f, 435f, paint)

        // Amount Display
        paint.color = android.graphics.Color.parseColor("#0F172A")
        paint.textSize = 68f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val formattedAmt = "₹${String.format(Locale.getDefault(), "%,.2f", transaction.amount)}"
        canvas.drawText(formattedAmt, 540f, 520f, paint)

        // Payee Subtitle
        paint.color = android.graphics.Color.parseColor("#64748B")
        paint.textSize = 26f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val isDebit = transaction.type == TransactionType.DEBIT
        canvas.drawText("${if (isDebit) "Paid to" else "Received from"} ${transaction.payeeName}", 540f, 570f, paint)
        canvas.drawText("(${transaction.payeeVpa})", 540f, 605f, paint)

        // Divider Line
        paint.color = android.graphics.Color.parseColor("#E2E8F0")
        paint.strokeWidth = 2f
        canvas.drawLine(120f, 650f, 960f, 650f, paint)

        // Details Key-Value Table
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(transaction.timestamp))
        val details = listOf(
            "Transaction ID" to transaction.id,
            "UPI Ref / UTR" to transaction.utrNumber,
            "Debited From" to "${transaction.bankName} ${transaction.bankAccountNumberMasked}",
            "Paid By" to "${transaction.payerName} (${transaction.payerVpa})",
            "Date & Time" to formattedDate
        )

        var yOffset = 715f
        for ((key, value) in details) {
            paint.style = Paint.Style.FILL
            paint.textAlign = Paint.Align.LEFT
            paint.color = android.graphics.Color.parseColor("#64748B")
            paint.textSize = 26f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(key, 120f, yOffset, paint)

            paint.textAlign = Paint.Align.RIGHT
            paint.color = android.graphics.Color.parseColor("#0F172A")
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val truncatedVal = if (value.length > 28) value.take(25) + "..." else value
            canvas.drawText(truncatedVal, 960f, yOffset, paint)

            yOffset += 70f
        }

        if (!transaction.note.isNullOrBlank()) {
            paint.textAlign = Paint.Align.LEFT
            paint.color = android.graphics.Color.parseColor("#64748B")
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Note", 120f, yOffset, paint)

            paint.textAlign = Paint.Align.RIGHT
            paint.color = android.graphics.Color.parseColor("#0F172A")
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(transaction.note.orEmpty(), 960f, yOffset, paint)
            yOffset += 70f
        }

        // Bottom Divider
        canvas.drawLine(120f, yOffset + 15f, 960f, yOffset + 15f, paint)

        // Regulatory & Trust Footer
        paint.textAlign = Paint.Align.CENTER
        paint.color = android.graphics.Color.parseColor("#0078DF")
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("NPCI & RBI REGULATED • 256-BIT SSL SECURED", 540f, yOffset + 70f, paint)

        paint.color = android.graphics.Color.parseColor("#94A3B8")
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("United Pay — North East's Trusted Fintech Platform", 540f, yOffset + 105f, paint)

        // Save Bitmap to Cache
        val receiptsDir = File(context.cacheDir, "receipts").apply { mkdirs() }
        val safeFileName = "receipt_${transaction.id.replace(Regex("[^a-zA-Z0-9]"), "_")}.png"
        val receiptFile = File(receiptsDir, safeFileName)
        val stream = FileOutputStream(receiptFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            receiptFile
        )
    } catch (e: Exception) {
        null
    }
}

/**
 * Opens Android's native system sharing intent with complete formatted transaction receipt
 * as both a visual receipt image and formatted plain text.
 */
fun shareTransactionReceipt(context: Context, transaction: UpiTransaction) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(transaction.timestamp))
    val isDebit = transaction.type == TransactionType.DEBIT

    val receiptText = buildString {
        appendLine("==========================================")
        appendLine("       UNITED PAY - UPI RECEIPT           ")
        appendLine("==========================================")
        appendLine("Status: ${transaction.status.name} (COMPLETED)")
        appendLine("Amount: ₹${String.format(Locale.getDefault(), "%,.2f", transaction.amount)}")
        appendLine("${if (isDebit) "Paid to:" else "Received from:"} ${transaction.payeeName} (${transaction.payeeVpa})")
        appendLine("${if (isDebit) "Debited from:" else "Credited to:"} ${transaction.bankName} ${transaction.bankAccountNumberMasked}")
        appendLine("Payer: ${transaction.payerName} (${transaction.payerVpa})")
        appendLine("UPI Ref / UTR: ${transaction.utrNumber}")
        appendLine("United Pay ID: ${transaction.id}")
        appendLine("Date & Time: $formattedDate")
        if (!transaction.note.isNullOrBlank()) {
            appendLine("Note: ${transaction.note}")
        }
        appendLine("------------------------------------------")
        appendLine("NPCI & RBI Regulated | 256-Bit SSL Secured")
        appendLine("==========================================")
    }

    val imageUri = generateReceiptImage(context, transaction)

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        if (imageUri != null) {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            type = "text/plain"
        }
        putExtra(Intent.EXTRA_SUBJECT, "United Pay Transaction Receipt - ₹${transaction.amount}")
        putExtra(Intent.EXTRA_TEXT, receiptText)
    }
    val chooser = Intent.createChooser(sendIntent, "Share UPI Transaction Receipt").apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(chooser)
}
