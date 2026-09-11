package com.unitedpay.feature.home.communications

import androidx.compose.ui.graphics.Color

// =========================================================================
// DATA MODELS FOR UPI CHATS, MESSAGES & NOTIFICATIONS
// =========================================================================


sealed class ChatEntry {
    data class DateHeader(val date: String) : ChatEntry()
    data class TextMsg(
        val id: String,
        val text: String,
        val time: String,
        val isFromMe: Boolean
    ) : ChatEntry()
    data class PaymentSent(
        val id: String,
        val amount: String,
        val recipientName: String,
        val time: String,
        val status: String = "Paid",
        val utr: String,
        val bank: String = "State Bank of India •••• 8821"
    ) : ChatEntry()
    data class PaymentReceived(
        val id: String,
        val amount: String,
        val senderName: String,
        val time: String,
        val status: String = "Received",
        val utr: String,
        val bank: String = "State Bank of India •••• 8821"
    ) : ChatEntry()
    data class PaymentRequest(
        val id: String,
        val amount: String,
        val requesterName: String,
        val note: String,
        val time: String,
        var isPaid: Boolean = false,
        var isDeclined: Boolean = false
    ) : ChatEntry()
}

// Payment flow steps inside slide-up bottom sheet
enum class PayFlowStep {
    CONFIRM_AMOUNT,
    ENTER_PIN,
    SUCCESS
}

data class FintechNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val category: String,
    val iconType: String,
    val actionText: String? = null,
    val isRead: Boolean = false
)
