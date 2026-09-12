package com.unitedpay.core.designsystem.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import com.unitedpay.core.designsystem.R
import com.unitedpay.core.model.TransactionType
import com.unitedpay.core.model.UpiTransaction
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Universal Android Native Sharing Helper for United Pay receipts and transactions.
 * Guarantees uniform visual preview in Android Sharesheet via ClipData,
 * high-resolution 1080p branded receipt image, and professional fintech text message.
 */
object ReceiptShareHelper {

    /**
     * Builds the clean, professional, executive message without ASCII banners or AI artifacts.
     */
    fun buildShareMessage(transaction: UpiTransaction): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(transaction.timestamp))
        val isDebit = transaction.type == TransactionType.DEBIT
        val amountFormatted = "₹" + String.format(Locale.getDefault(), "%,.2f", transaction.amount)

        return buildString {
            if (isDebit) {
                appendLine("Payment of $amountFormatted to ${transaction.payeeName} was successful.")
            } else {
                appendLine("Payment of $amountFormatted received from ${transaction.payerName}.")
            }
            appendLine()
            appendLine("UPI Ref / UTR: ${transaction.utrNumber}")
            if (isDebit) {
                appendLine("Paid to: ${transaction.payeeName} (${transaction.payeeVpa})")
                appendLine("Debited from: ${transaction.bankName} ${transaction.bankAccountNumberMasked}")
            } else {
                appendLine("Received from: ${transaction.payerName} (${transaction.payerVpa})")
                appendLine("Credited to: ${transaction.bankName} ${transaction.bankAccountNumberMasked}")
            }
            appendLine("Date & Time: $formattedDate")
            if (!transaction.note.isNullOrBlank()) {
                appendLine("Note: ${transaction.note}")
            }
            appendLine()
            appendLine("Powered by UnitedPay • 100% Secure NPCI Unified Payments Interface.")
        }
    }

    /**
     * Renders a high-resolution 1080x1620 branded transaction receipt bitmap with official UnitedPay logo.
     */
    fun generateReceiptBitmap(context: Context? = null, transaction: UpiTransaction): Bitmap {
        val width = 1080
        val height = 1620
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background
        canvas.drawColor(AndroidColor.WHITE)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // 2. Top Royal Blue Brand Header
        paint.color = AndroidColor.parseColor("#0052CC")
        canvas.drawRect(0f, 0f, width.toFloat(), 210f, paint)

        // Decode official brand logo
        val logoBmp: Bitmap? = if (context != null) {
            try {
                BitmapFactory.decodeResource(context.resources, R.drawable.brand_logo_transparent)
                    ?: BitmapFactory.decodeResource(context.resources, R.drawable.brand_logo)
            } catch (e: Exception) {
                null
            }
        } else null

        // 3. Brand Wordmark & Official Logo Badge in Header
        if (logoBmp != null) {
            val badgeSize = 84f
            val gap = 20f
            paint.color = AndroidColor.WHITE
            paint.textSize = 48f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val titleWidth = paint.measureText("UNITED PAY")
            val clusterWidth = badgeSize + gap + titleWidth
            val clusterStartX = (width - clusterWidth) / 2f

            // White rounded badge for logo
            val badgeRect = RectF(clusterStartX, 36f, clusterStartX + badgeSize, 36f + badgeSize)
            paint.color = AndroidColor.WHITE
            paint.style = Paint.Style.FILL
            canvas.drawRoundRect(badgeRect, 18f, 18f, paint)

            paint.color = AndroidColor.parseColor("#DBEAFE")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            canvas.drawRoundRect(badgeRect, 18f, 18f, paint)

            // Draw Logo inside badge
            paint.style = Paint.Style.FILL
            val pad = 8f
            val logoRect = RectF(badgeRect.left + pad, badgeRect.top + pad, badgeRect.right - pad, badgeRect.bottom - pad)
            canvas.drawBitmap(logoBmp, null, logoRect, paint)

            // Title "UNITED PAY"
            val textStartX = clusterStartX + badgeSize + gap
            paint.textAlign = Paint.Align.LEFT
            paint.color = AndroidColor.WHITE
            paint.textSize = 48f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("UNITED PAY", textStartX, 84f, paint)

            // Subtitle under title
            paint.textSize = 20f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.color = AndroidColor.parseColor("#DBEAFE")
            canvas.drawText("OFFICIAL UPI TRANSACTION RECEIPT", textStartX, 114f, paint)

            // Trust tag at bottom of header banner
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 21f
            paint.color = AndroidColor.parseColor("#93C5FD")
            canvas.drawText("100% SECURE • NPCI UNIFIED PAYMENTS INTERFACE", width / 2f, 175f, paint)
        } else {
            paint.color = AndroidColor.WHITE
            paint.textSize = 52f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("UNITED PAY", 540f, 100f, paint)

            paint.textSize = 24f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.color = AndroidColor.parseColor("#DBEAFE")
            canvas.drawText("OFFICIAL UPI TRANSACTION RECEIPT", 540f, 145f, paint)
        }

        // 4. Success Green Checkmark Circle
        paint.color = AndroidColor.parseColor("#16A34A")
        paint.style = Paint.Style.FILL
        canvas.drawCircle(540f, 310f, 60f, paint)

        // Checkmark Icon Path
        paint.color = AndroidColor.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 9f
        paint.strokeCap = Paint.Cap.ROUND
        val checkPath = android.graphics.Path().apply {
            moveTo(515f, 310f)
            lineTo(533f, 328f)
            lineTo(570f, 290f)
        }
        canvas.drawPath(checkPath, paint)

        // 5. Payment Success Label
        paint.style = Paint.Style.FILL
        paint.color = AndroidColor.parseColor("#16A34A")
        paint.textSize = 34f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Payment Successful", 540f, 430f, paint)

        // 6. Amount Display
        paint.color = AndroidColor.parseColor("#0F172A")
        paint.textSize = 68f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val formattedAmt = "₹" + String.format(Locale.getDefault(), "%,.2f", transaction.amount)
        canvas.drawText(formattedAmt, 540f, 520f, paint)

        // 7. Payee Subtitle
        paint.color = AndroidColor.parseColor("#64748B")
        paint.textSize = 26f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val isDebit = transaction.type == TransactionType.DEBIT
        canvas.drawText("${if (isDebit) "Paid to" else "Received from"} ${transaction.payeeName}", 540f, 570f, paint)
        canvas.drawText("(${transaction.payeeVpa})", 540f, 605f, paint)

        // 8. Divider Line
        paint.color = AndroidColor.parseColor("#E2E8F0")
        paint.strokeWidth = 2f
        canvas.drawLine(100f, 650f, 980f, 650f, paint)

        // 9. Details Key-Value Table
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(transaction.timestamp))
        val details = listOf(
            "Transaction ID" to transaction.id,
            "UPI Ref / UTR" to transaction.utrNumber,
            "Debited From" to "${transaction.bankName} ${transaction.bankAccountNumberMasked}",
            "Paid By" to "${transaction.payerName} (${transaction.payerVpa})",
            "Date & Time" to formattedDate
        )

        var yOffset = 720f
        for ((key, value) in details) {
            paint.style = Paint.Style.FILL
            paint.textAlign = Paint.Align.LEFT
            paint.color = AndroidColor.parseColor("#64748B")
            paint.textSize = 26f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(key, 100f, yOffset, paint)

            paint.textAlign = Paint.Align.RIGHT
            paint.color = AndroidColor.parseColor("#0F172A")
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val truncatedVal = if (value.length > 32) value.take(30) + "..." else value
            canvas.drawText(truncatedVal, 980f, yOffset, paint)

            yOffset += 72f
        }

        if (!transaction.note.isNullOrBlank()) {
            paint.textAlign = Paint.Align.LEFT
            paint.color = AndroidColor.parseColor("#64748B")
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Note", 100f, yOffset, paint)

            paint.textAlign = Paint.Align.RIGHT
            paint.color = AndroidColor.parseColor("#0F172A")
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val truncatedNote = if (transaction.note.orEmpty().length > 32) transaction.note.orEmpty().take(30) + "..." else transaction.note.orEmpty()
            canvas.drawText(truncatedNote, 980f, yOffset, paint)
            yOffset += 72f
        }

        // 10. Bottom Divider
        paint.color = AndroidColor.parseColor("#E2E8F0")
        canvas.drawLine(100f, yOffset + 20f, 980f, yOffset + 20f, paint)

        // 11. Regulatory & Trust Footer with Official Logo Seal
        if (logoBmp != null) {
            val sealSize = 52f
            val sealRect = RectF(540f - sealSize / 2f, yOffset + 40f, 540f + sealSize / 2f, yOffset + 40f + sealSize)
            paint.color = AndroidColor.parseColor("#F8FAFC")
            paint.style = Paint.Style.FILL
            canvas.drawRoundRect(sealRect, 14f, 14f, paint)

            paint.color = AndroidColor.parseColor("#CBD5E1")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1.5f
            canvas.drawRoundRect(sealRect, 14f, 14f, paint)

            paint.style = Paint.Style.FILL
            canvas.drawBitmap(logoBmp, null, RectF(sealRect.left + 6f, sealRect.top + 6f, sealRect.right - 6f, sealRect.bottom - 6f), paint)
        }

        val footerTextY = if (logoBmp != null) yOffset + 120f else yOffset + 80f
        paint.textAlign = Paint.Align.CENTER
        paint.color = AndroidColor.parseColor("#0078DF")
        paint.textSize = 23f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("NPCI & RBI REGULATED • 256-BIT ENCRYPTION SECURED", 540f, footerTextY, paint)

        paint.color = AndroidColor.parseColor("#94A3B8")
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("United Pay — North East's Trusted Fintech Platform", 540f, footerTextY + 35f, paint)

        return bitmap
    }

    /**
     * Backward-compatible overload for generateReceiptBitmap.
     */
    fun generateReceiptBitmap(transaction: UpiTransaction): Bitmap = generateReceiptBitmap(null, transaction)

    /**
     * Saves the receipt bitmap to the cache directory and generates a FileProvider content URI.
     */
    fun getReceiptImageUri(context: Context, transaction: UpiTransaction): Uri? {
        return try {
            val bitmap = generateReceiptBitmap(context, transaction)
            val receiptsDir = File(context.cacheDir, "receipts").apply { mkdirs() }
            val safeFileName = "receipt_${transaction.id.replace(Regex("[^a-zA-Z0-9]"), "_")}.png"
            val receiptFile = File(receiptsDir, safeFileName)
            FileOutputStream(receiptFile).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
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
     * Launches the native Android Sharesheet with image preview (via clipData)
     * and accompanying clean fintech message.
     */
    fun shareReceipt(context: Context, transaction: UpiTransaction) {
        val imageUri = getReceiptImageUri(context, transaction)
        val shareMessage = buildShareMessage(transaction)
        val amountFormatted = "₹" + String.format(Locale.getDefault(), "%,.2f", transaction.amount)

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            if (imageUri != null) {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                clipData = ClipData.newRawUri("UPI Transaction Receipt", imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                type = "text/plain"
            }
            putExtra(Intent.EXTRA_SUBJECT, "Payment Receipt - $amountFormatted")
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }

        val chooser = Intent.createChooser(sendIntent, "Share Payment Receipt").apply {
            if (imageUri != null) {
                clipData = ClipData.newRawUri("UPI Transaction Receipt", imageUri)
            }
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(chooser)
    }
}
