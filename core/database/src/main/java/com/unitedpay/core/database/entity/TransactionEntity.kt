package com.unitedpay.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.unitedpay.core.model.PaymentStatus
import com.unitedpay.core.model.TransactionType
import com.unitedpay.core.model.UpiTransaction

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val utrNumber: String,
    val payeeName: String,
    val payeeVpa: String,
    val payeeAvatarUrl: String?,
    val amount: Double,
    val timestamp: Long,
    val status: String,
    val type: String,
    val bankName: String,
    val bankAccountNumberMasked: String,
    val note: String?
) {
    fun toDomain(): UpiTransaction = UpiTransaction(
        id = id,
        utrNumber = utrNumber,
        payeeName = payeeName,
        payeeVpa = payeeVpa,
        payeeAvatarUrl = payeeAvatarUrl,
        amount = amount,
        timestamp = timestamp,
        status = PaymentStatus.valueOf(status),
        type = TransactionType.valueOf(type),
        bankName = bankName,
        bankAccountNumberMasked = bankAccountNumberMasked,
        note = note
    )

    companion object {
        fun fromDomain(domain: UpiTransaction): TransactionEntity = TransactionEntity(
            id = domain.id,
            utrNumber = domain.utrNumber,
            payeeName = domain.payeeName,
            payeeVpa = domain.payeeVpa,
            payeeAvatarUrl = domain.payeeAvatarUrl,
            amount = domain.amount,
            timestamp = domain.timestamp,
            status = domain.status.name,
            type = domain.type.name,
            bankName = domain.bankName,
            bankAccountNumberMasked = domain.bankAccountNumberMasked,
            note = domain.note
        )
    }
}
