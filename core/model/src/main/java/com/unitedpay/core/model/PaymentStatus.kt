package com.unitedpay.core.model

enum class PaymentStatus {
    SUCCESS,
    PENDING,
    FAILED,
    REFUNDED
}

enum class TransactionType {
    DEBIT,
    CREDIT
}
