package com.unitedpay.core.model

/**
 * Domain model representing a UPI transaction.
 */
data class UpiTransaction(
    val id: String,
    val utrNumber: String,
    val payeeName: String,
    val payeeVpa: String,
    val payeeAvatarUrl: String? = null,
    val payerName: String = "Arunjyoti Changkakoty",
    val payerVpa: String = "arunjyoti@unitedpay",
    val amount: Double,
    val timestamp: Long,
    val status: PaymentStatus,
    val type: TransactionType,
    val bankName: String,
    val bankAccountNumberMasked: String,
    val note: String? = null
)
