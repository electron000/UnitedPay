package com.unitedpay.core.designsystem.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.unitedpay.core.designsystem.theme.UnitedBackgroundLight
import com.unitedpay.core.designsystem.theme.UnitedBorderLight
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedObsidian
import com.unitedpay.core.designsystem.theme.UnitedRoyalBlue
import com.unitedpay.core.designsystem.theme.UnitedSuccess
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class UnitedPaymentStep {
    PROCESSING,
    SUCCESS
}

/**
 * Universal, enterprise-grade Payment Processing & Completion UI component.
 * Shared across 100% of payment flows, transfers, recharges, utility bills, and investments.
 *
 * Provides:
 * - Phase 1: High-fidelity processing animation with NPCI 256-bit security badge, pulsing radar waves,
 *   dynamic progress steps, and debit account details. Prevents accidental cancellation.
 * - Phase 2: Animated completion receipt with celebratory spring checkmark, amount, timestamp,
 *   UTR reference, native Share Receipt intent, and single-line Done button.
 */
@Composable
fun UnitedPaymentProcessingDialog(
    amount: String,
    recipientName: String,
    recipientSubtitle: String = "",
    bankName: String = "State Bank of India",
    accountMasked: String = "•••• 4821",
    transactionCategory: String = "Payment",
    initialStep: UnitedPaymentStep = UnitedPaymentStep.PROCESSING,
    processingDurationMillis: Long = 1800L,
    onPaymentCompleted: ((utr: String) -> Unit)? = null,
    onDone: () -> Unit
) {
    var currentStep by remember { mutableStateOf(initialStep) }
    var processingStatusText by remember { mutableStateOf("Connecting securely to bank...") }
    val context = LocalContext.current

    val utrNumber = remember {
        (100000000000L..999999999999L).random().toString()
    }

    val transactionTimestamp = remember {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date())
    }

    val formattedAmount = remember(amount) {
        val clean = amount.replace("₹", "").trim()
        val d = clean.toDoubleOrNull()
        if (d != null) String.format(Locale.ENGLISH, "₹%,.2f", d) else "₹$amount"
    }

    LaunchedEffect(currentStep) {
        if (currentStep == UnitedPaymentStep.PROCESSING) {
            delay(500)
            processingStatusText = "Routing through NPCI UPI switch..."
            delay(600)
            processingStatusText = "Authorizing with $bankName..."
            delay(processingDurationMillis.coerceAtLeast(600L) - 1100L)
            currentStep = UnitedPaymentStep.SUCCESS
            onPaymentCompleted?.invoke(utrNumber)
        }
    }

    Dialog(
        onDismissRequest = {
            if (currentStep == UnitedPaymentStep.SUCCESS) {
                onDone()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = (currentStep == UnitedPaymentStep.SUCCESS),
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UnitedWhite)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            when (currentStep) {
                UnitedPaymentStep.PROCESSING -> {
                    ProcessingView(
                        amount = formattedAmount,
                        recipientName = recipientName,
                        recipientSubtitle = recipientSubtitle,
                        bankName = bankName,
                        accountMasked = accountMasked,
                        statusText = processingStatusText
                    )
                }

                UnitedPaymentStep.SUCCESS -> {
                    CompletionView(
                        amount = formattedAmount,
                        recipientName = recipientName,
                        recipientSubtitle = recipientSubtitle,
                        bankName = bankName,
                        accountMasked = accountMasked,
                        transactionCategory = transactionCategory,
                        utrNumber = utrNumber,
                        timestamp = transactionTimestamp,
                        onDone = onDone,
                        onShare = {
                            val shareText = """
                                United Pay - Transaction Successful!
                                Amount: $formattedAmount
                                Paid To: $recipientName
                                Category: $transactionCategory
                                Bank: $bankName ($accountMasked)
                                UPI Ref (UTR): $utrNumber
                                Date: $transactionTimestamp
                                Secured by NPCI UPI
                            """.trimIndent()

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Payment Receipt"))
                        },
                        onCopyUtr = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("UTR", utrNumber)
                            clipboard.setPrimaryClip(clip)
                            UnitedToast.success("UTR $utrNumber copied to clipboard")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProcessingView(
    amount: String,
    recipientName: String,
    recipientSubtitle: String,
    bankName: String,
    accountMasked: String,
    statusText: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.40f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Security Header
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF1F5F9))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = UnitedSuccess,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Secured by 256-Bit NPCI UPI Switch",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = UnitedTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Center Visuals
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Radar pulse around the bank spinner
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Pulse Ring
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(UnitedMoneyBlue.copy(alpha = pulseAlpha * 0.4f))
                )
                // Mid Ring
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(UnitedMoneyBlue.copy(alpha = pulseAlpha * 0.7f))
                )
                // Center Spinner
                CircularProgressIndicator(
                    modifier = Modifier.size(68.dp),
                    color = UnitedMoneyBlue,
                    strokeWidth = 4.dp
                )
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = UnitedRoyalBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Transaction Info
            Text(
                text = "Transferring $amount",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = UnitedTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "To $recipientName",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = UnitedTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (recipientSubtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = recipientSubtitle,
                    fontSize = 12.sp,
                    color = UnitedTextSecondary.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Dynamic Step Progress Box
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEFF6FF))
                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = statusText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = UnitedMoneyBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Bottom Account & Safety Notice
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Debiting $bankName ($accountMasked)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = UnitedTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please do not press back or switch apps",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CompletionView(
    amount: String,
    recipientName: String,
    recipientSubtitle: String,
    bankName: String,
    accountMasked: String,
    transactionCategory: String,
    utrNumber: String,
    timestamp: String,
    onDone: () -> Unit,
    onShare: () -> Unit,
    onCopyUtr: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Animated Celebratory Success Icon
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f)) + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF34D399), UnitedSuccess)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = UnitedWhite,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val headline = when {
                transactionCategory.contains("Transfer", ignoreCase = true) -> "Transfer Successful"
                transactionCategory.contains("Recharge", ignoreCase = true) -> "Recharge Successful"
                transactionCategory.contains("Bill", ignoreCase = true) -> "Bill Paid Successfully"
                else -> "Payment Successful"
            }

            Text(
                text = headline,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = UnitedTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = timestamp,
                fontSize = 12.sp,
                color = UnitedTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = amount,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = UnitedObsidian,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Receipt Breakdown Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, UnitedBorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    ReceiptRow(label = "Paid To", value = recipientName, isHighlight = true)

                    if (recipientSubtitle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = recipientSubtitle,
                            fontSize = 11.5.sp,
                            color = UnitedTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = UnitedBorderLight)
                    Spacer(modifier = Modifier.height(12.dp))

                    ReceiptRow(label = "Debited From", value = "$bankName ($accountMasked)")

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = UnitedBorderLight)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("UPI Ref No. (UTR)", fontSize = 11.5.sp, color = UnitedTextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(utrNumber, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                        }
                        IconButtonCopy(onClick = onCopyUtr)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = UnitedBorderLight)
                    Spacer(modifier = Modifier.height(12.dp))

                    ReceiptRow(label = "Category", value = transactionCategory)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onShare,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .defaultMinSize(minHeight = 52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = UnitedMoneyBlue),
                border = androidx.compose.foundation.BorderStroke(1.dp, UnitedMoneyBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share Receipt",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Button(
                onClick = onDone,
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

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = UnitedTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            fontSize = if (isHighlight) 14.5.sp else 13.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
            color = UnitedTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun IconButtonCopy(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFF1F5F9))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy UTR",
            tint = UnitedMoneyBlue,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedMoneyBlue)
    }
}
