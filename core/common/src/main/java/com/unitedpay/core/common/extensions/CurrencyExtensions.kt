package com.unitedpay.core.common.extensions

import java.text.NumberFormat
import java.util.Locale

/**
 * Formats Double into Indian Rupee currency format (e.g. ₹ 1,25,450.00).
 */
fun Double.toInrCurrency(includeDecimals: Boolean = true): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    if (!includeDecimals) {
        formatter.maximumFractionDigits = 0
    }
    return formatter.format(this)
}

/**
 * Validates whether string is a valid NPCI UPI Virtual Payment Address (VPA).
 * Pattern: identifier@psp (e.g., rahul@unitedpay, merchant@sbi)
 */
fun String.isValidVpa(): Boolean {
    val vpaPattern = Regex("^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$")
    return this.matches(vpaPattern)
}
