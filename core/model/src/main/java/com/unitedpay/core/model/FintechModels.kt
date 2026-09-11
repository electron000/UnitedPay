package com.unitedpay.core.model

/**
 * Enterprise domain models for UnitedPay fintech platform.
 * Fully decoupled from specific UI screens to support clean API and SDK integration.
 */

// BBPS (Bharat Bill Payment System)
data class BbpsBiller(
    val id: String,
    val name: String,
    val category: String,
    val iconType: String = "utility"
)

data class BbpsBillSummary(
    val billerId: String,
    val billerName: String,
    val consumerNumber: String,
    val consumerName: String,
    val billCycle: String,
    val dueDate: String,
    val amountDue: Double,
    val formattedAmount: String = "₹${String.format("%.2f", amountDue)}"
)

// Recharge & Transit
data class MobilePlan(
    val id: String,
    val price: Double,
    val validity: String,
    val dataAllowance: String,
    val callBenefit: String = "Truly Unlimited Calls",
    val additionalPerks: String = "100 SMS/day • UnitedPay 5% Cashback",
    val category: String = "Popular"
) {
    val formattedPrice: String get() = "₹${price.toInt()}"
}

data class FastagDetails(
    val vehicleNumber: String,
    val issuerBank: String,
    val customerName: String,
    val balance: Double,
    val status: String = "ACTIVE"
) {
    val formattedBalance: String get() = "₹${String.format("%.2f", balance)}"
}

data class MetroCardDetails(
    val cardNumber: String,
    val metroAuthority: String,
    val customerName: String,
    val balance: Double
) {
    val formattedBalance: String get() = "₹${String.format("%.2f", balance)}"
}

// Financial Services & Investments
data class CreditCardStatement(
    val cardType: String,
    val bankName: String,
    val cardNumberMasked: String,
    val cardHolderName: String,
    val expiry: String,
    val totalAmountDue: Double,
    val minimumAmountDue: Double,
    val dueDate: String,
    val availableLimit: Double = 150000.0,
    val totalLimit: Double = 200000.0
) {
    val formattedTotalDue: String get() = "₹${String.format("%.2f", totalAmountDue)}"
    val formattedMinDue: String get() = "₹${String.format("%.2f", minimumAmountDue)}"
}

data class DigitalGoldQuote(
    val buyPricePerGram: Double,
    val sellPricePerGram: Double,
    val userVaultGrams: Double,
    val userVaultValue: Double,
    val metalPurity: String = "24K 99.99% Pure Gold",
    val partner: String = "MMTC-PAMP"
) {
    val formattedBuyPrice: String get() = "₹${String.format("%.2f", buyPricePerGram)}"
    val formattedVaultValue: String get() = "₹${String.format("%.2f", userVaultValue)}"
}

data class MutualFund(
    val id: String,
    val name: String,
    val category: String,
    val threeYearReturn: String,
    val riskLevel: String,
    val minSip: String,
    val nav: Double = 84.50
)

data class LoanEmiSummary(
    val loanAccountNo: String,
    val lenderName: String,
    val customerName: String,
    val dueDate: String,
    val emiAmount: Double
) {
    val formattedAmount: String get() = "₹${String.format("%.2f", emiAmount)}"
}

data class InsurancePolicySummary(
    val policyNumber: String,
    val insurerName: String,
    val policyHolderName: String,
    val gracePeriodClose: String,
    val premiumDue: Double
) {
    val formattedPremium: String get() = "₹${String.format("%.2f", premiumDue)}"
}

// Promotions, Deals & Rewards
data class GiftCardBrandItem(
    val name: String,
    val category: String,
    val discountPercent: String
)

data class OfferDealItem(
    val brand: String,
    val title: String,
    val code: String,
    val desc: String,
    val discountPill: String,
    val validity: String
)

data class ScratchCardReward(
    val id: Int,
    val title: String,
    val rewardText: String,
    val isScratched: Boolean = false,
    val amount: Double = 0.0
)

// Autopay & Digital Currency
data class AutopayMandate(
    val id: String,
    val serviceName: String,
    val amountText: String,
    val nextDebitText: String,
    val frequency: String = "Monthly",
    val bankName: String = "State Bank of India"
)

data class DigitalRupeeWallet(
    val walletId: String,
    val balance: Double,
    val status: String = "ACTIVE",
    val issuingEntity: String = "Reserve Bank of India"
) {
    val formattedBalance: String get() = "e₹ ${String.format("%.2f", balance)}"
}

// Communication & Alerts
data class ChatContact(
    val id: String,
    val name: String,
    val vpa: String,
    val lastSnippet: String,
    val lastTime: String,
    val unreadCount: Int = 0,
    val isPayment: Boolean = false,
    val isMerchant: Boolean = false,
    val isRequest: Boolean = false,
    val avatarHexColor: Long = 0xFF1E88E5
)

data class ChatMessage(
    val id: String,
    val text: String,
    val time: String,
    val isFromMe: Boolean,
    val type: String = "TEXT", // "TEXT", "PAYMENT_REQUEST", "PAYMENT_RECEIVED", "DATE_HEADER", "PAYMENT_SENT"
    val amount: String? = null,
    val note: String? = null,
    val utr: String? = null,
    val bankMasked: String? = null,
    val isPaid: Boolean = false
)

data class FintechNotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val category: String,
    val iconType: String,
    val actionText: String? = null
)

data class DisputeTicket(
    val id: String,
    val transactionRef: String,
    val payeeName: String,
    val utr: String,
    val amount: Double,
    val status: String,
    val bank: String = "SBI",
    val resolutionNotice: String = "Ombudsman Scheme SLA: T+1 business days"
)

data class SoundboxConfig(
    val deviceId: String,
    val selectedLanguage: String,
    val volumeLevel: Int = 8,
    val isConnected: Boolean = true,
    val availableLanguages: List<Pair<String, String>>
)

data class FintechCardDetails(
    val cardNumberFormatted: String = "1234  5678  9000  0000",
    val validThru: String = "08/26",
    val cvv: String = "782",
    val cardHolderName: String = "ARUNJYOTI CHANGKAKOTY",
    val cardType: String = "Mastercard Platinum",
    val dailyLimit: Double = 50000.0,
    val isOnlineActive: Boolean = true,
    val isContactlessActive: Boolean = true
)

data class BankBeneficiary(
    val accountNumber: String = "918273645109",
    val confirmAccountNumber: String = "918273645109",
    val ifscCode: String = "HDFC0001234",
    val beneficiaryName: String = "Aditya Sharma",
    val defaultAmount: String = "3000"
)

data class PaymentRequestInfo(
    val vpa: String = "rohit.verma@okaxis",
    val defaultAmount: String = "1200",
    val note: String = "Dinner split"
)

