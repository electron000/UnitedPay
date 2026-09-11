package com.unitedpay.feature.payment.components

import androidx.compose.runtime.Composable
import com.unitedpay.core.designsystem.components.UnitedNpciMpinSheet

/**
 * NPCI-compliant 6-Digit MPIN bottom sheet with isolated soft keypad.
 */
@Composable
fun NpciMpinSheet(
    amount: String,
    payeeName: String,
    pinLength: Int = 6,
    onPinSubmitted: (CharArray) -> Unit,
    onDismiss: () -> Unit
) {
    val subtitle = if (amount.isNotBlank()) "Paying $amount to $payeeName" else "Paying to $payeeName"
    UnitedNpciMpinSheet(
        title = "NPCI UPI SECURE MPIN",
        subtitle = subtitle,
        pinLength = pinLength,
        onPinSubmitted = onPinSubmitted,
        onDismiss = onDismiss
    )
}
