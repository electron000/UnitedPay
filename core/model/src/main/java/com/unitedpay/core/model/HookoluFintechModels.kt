package com.unitedpay.core.model

/**
 * Enterprise Domain Models for HookoluPay / United Pay Multi-Service Super-App.
 * Fully decoupled from UI to allow seamless transition between Mock sandboxes
 * and production Banking/NPCI/BBPS/AEPS Switch APIs.
 */

// ==========================================
// 1. AEPS (Aadhaar Enabled Payment System)
// ==========================================

enum class AepsTransactionType(val title: String) {
    CASH_WITHDRAWAL("Cash Withdrawal"),
    BALANCE_ENQUIRY("Balance Enquiry"),
    MINI_STATEMENT("Mini Statement"),
    AADHAAR_PAY("Aadhaar Pay")
}

data class AepsBank(
    val iin: String,
    val bankName: String,
    val isPopular: Boolean = false
)

data class AepsStatementEntry(
    val date: String,
    val narration: String,
    val txnType: String,
    val amount: Double,
    val balance: Double,
    val isCredit: Boolean
) {
    val formattedAmount: String get() = (if (isCredit) "+₹" else "-₹") + String.format("%.2f", amount)
    val formattedBalance: String get() = "Bal: ₹" + String.format("%.2f", balance)
}

data class RdServiceDevice(
    val id: String,
    val deviceName: String,
    val isConnected: Boolean,
    val qualityScore: Int
)

data class AepsTransactionReceipt(
    val rrn: String,
    val stan: String,
    val txnType: AepsTransactionType,
    val aadhaarMasked: String,
    val customerName: String,
    val bankName: String,
    val amount: Double,
    val remainingBalance: Double,
    val terminalId: String = "HKL-BC-781001",
    val status: String = "SUCCESS",
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedAmount: String get() = "₹${String.format("%.2f", amount)}"
    val formattedBalance: String get() = "₹${String.format("%.2f", remainingBalance)}"
}

// ==========================================
// 2. DMT (Domestic Money Transfer)
// ==========================================

enum class DmtTransferMode {
    IMPS, NEFT
}

data class DmtSender(
    val mobileNumber: String,
    val fullName: String,
    val kycTier: String = "Full KYC",
    val monthlyLimit: Double = 200000.0,
    val usedLimit: Double = 35000.0
) {
    val remainingLimit: Double get() = monthlyLimit - usedLimit
    val formattedMonthlyLimit: String get() = "₹${String.format("%.0f", monthlyLimit)}"
    val formattedRemainingLimit: String get() = "₹${String.format("%.0f", remainingLimit)}"
}

data class DmtBeneficiary(
    val id: String,
    val name: String,
    val accountNumber: String,
    val ifsc: String,
    val bankName: String,
    val isVerified: Boolean = true
) {
    val maskedAccountNumber: String get() = "•••• " + accountNumber.takeLast(4)
}

data class DmtTransactionReceipt(
    val utr: String,
    val senderName: String,
    val senderMobile: String,
    val beneficiaryName: String,
    val accountNumberMasked: String,
    val bankName: String,
    val transferMode: DmtTransferMode,
    val amount: Double,
    val serviceFee: Double,
    val totalDebited: Double,
    val status: String = "SUCCESS",
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedAmount: String get() = "₹${String.format("%.2f", amount)}"
    val formattedFee: String get() = "₹${String.format("%.2f", serviceFee)}"
    val formattedTotal: String get() = "₹${String.format("%.2f", totalDebited)}"
}

// ==========================================
// 3. Micro-ATM / mPOS
// ==========================================

data class MicroAtmTerminal(
    val terminalId: String,
    val serialNumber: String,
    val modelName: String,
    val batteryLevel: Int,
    val isPaired: Boolean,
    val connectionType: String = "Bluetooth 5.0"
)

enum class MicroAtmOperation(val title: String) {
    CASH_WITHDRAWAL("Cash Withdrawal"),
    BALANCE_ENQUIRY("Balance Enquiry"),
    CARD_SALE("Card Sale (mPOS)")
}

data class MicroAtmReceipt(
    val terminalId: String,
    val rrn: String,
    val cardScheme: String,
    val cardLast4: String,
    val cardHolderName: String,
    val operation: MicroAtmOperation,
    val amount: Double,
    val availableBalance: Double = 0.0,
    val status: String = "APPROVED",
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedAmount: String get() = "₹${String.format("%.2f", amount)}"
    val formattedBalance: String get() = "₹${String.format("%.2f", availableBalance)}"
}

// ==========================================
// 4. Partner Bank Account Opening Wizard
// ==========================================

data class PartnerBank(
    val id: String,
    val bankName: String,
    val accountType: String,
    val minInitialDeposit: Double,
    val interestRate: String,
    val features: List<String>
) {
    val formattedDeposit: String get() = "Min Deposit: ₹${minInitialDeposit.toInt()}"
}

enum class AccountOpeningStep(val stepNumber: Int, val title: String) {
    SELECT_BANK(1, "Select Partner Bank"),
    PAN_VERIFICATION(2, "PAN Verification"),
    AADHAAR_EKYC(3, "Aadhaar eKYC"),
    CONFIRMATION(4, "Video KYC & Account")
}

data class BankApplicationSubmission(
    val applicationId: String,
    val bankName: String,
    val applicantName: String,
    val panNumber: String,
    val aadhaarMasked: String,
    val mobileNumber: String,
    val scheduledVkycSlot: String,
    val status: String = "IN_PROGRESS"
)

// ==========================================
// 5. Embedded Digital Lending & Credit Hub
// ==========================================

data class LoanOffer(
    val id: String,
    val title: String,
    val category: String,
    val maxAmount: Double,
    val interestRatePerMonth: Double,
    val processingFeeRate: Double,
    val minTenureMonths: Int,
    val maxTenureMonths: Int,
    val isPreApproved: Boolean = true
) {
    val formattedMaxAmount: String get() = "₹${String.format("%.0f", maxAmount)}"
}

data class LoanDisbursalReceipt(
    val loanId: String,
    val loanTitle: String,
    val borrowerName: String,
    val sanctionedAmount: Double,
    val tenureMonths: Int,
    val monthlyEmi: Double,
    val processingFee: Double,
    val netDisbursedAmount: Double,
    val disbursalAccount: String,
    val disbursalDate: String,
    val status: String = "DISBURSED_SUCCESSFULLY"
) {
    val formattedSanctioned: String get() = "₹${String.format("%.2f", sanctionedAmount)}"
    val formattedEmi: String get() = "₹${String.format("%.2f", monthlyEmi)}"
    val formattedNet: String get() = "₹${String.format("%.2f", netDisbursedAmount)}"
}

// ==========================================
// 6. Travel & Transit Suite
// ==========================================

data class BusTrip(
    val id: String,
    val operatorName: String,
    val busType: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val sourceCity: String,
    val destinationCity: String,
    val fare: Double,
    val availableSeats: Int,
    val boardingPoint: String
) {
    val formattedFare: String get() = "₹${fare.toInt()}"
}

data class FlightTrip(
    val id: String,
    val airlineName: String,
    val flightNumber: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val sourceAirport: String,
    val destAirport: String,
    val fare: Double
) {
    val formattedFare: String get() = "₹${fare.toInt()}"
}

data class TrainTrip(
    val trainNumber: String,
    val trainName: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val sourceStation: String,
    val destStation: String,
    val classesAvailable: List<String>
)

data class HotelProperty(
    val id: String,
    val name: String,
    val city: String,
    val locationSnippet: String,
    val rating: Double,
    val pricePerNight: Double,
    val amenities: List<String>
) {
    val formattedPrice: String get() = "₹${pricePerNight.toInt()}/night"
}

data class TravelBookingReceipt(
    val bookingId: String,
    val travelType: String, // "BUS", "FLIGHT", "TRAIN", "HOTEL"
    val title: String,
    val routeOrDetails: String,
    val travelDate: String,
    val passengerName: String,
    val seatOrRoom: String,
    val totalFare: Double,
    val pnr: String,
    val status: String = "CONFIRMED",
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedFare: String get() = "₹${String.format("%.2f", totalFare)}"
}

// ==========================================
// 7. Retailer & BC Merchant Operations Hub
// ==========================================

data class RetailerMetrics(
    val agentId: String,
    val merchantName: String,
    val todayTxnCount: Int,
    val todayVolume: Double,
    val todayCommission: Double,
    val walletBalance: Double,
    val primarySettlementBank: String,
    val primarySettlementAccountMasked: String
) {
    val formattedVolume: String get() = "₹${String.format("%.2f", todayVolume)}"
    val formattedCommission: String get() = "₹${String.format("%.2f", todayCommission)}"
    val formattedWallet: String get() = "₹${String.format("%.2f", walletBalance)}"
}

data class RetailerCommissionSlab(
    val serviceName: String,
    val commissionRule: String,
    val todayEarned: Double
) {
    val formattedEarned: String get() = "₹${String.format("%.2f", todayEarned)}"
}

data class CustomerKhataEntry(
    val id: String,
    val customerName: String,
    val phoneNumber: String,
    val balanceAmount: Double,
    val isDebit: Boolean, // true = customer owes merchant
    val lastTxnDate: String,
    val lastNote: String
) {
    val formattedBalance: String get() = "₹${String.format("%.2f", balanceAmount)}"
}

data class SettlementReceipt(
    val settlementId: String,
    val utr: String,
    val amount: Double,
    val toBank: String,
    val toAccountMasked: String,
    val fee: Double = 0.0,
    val status: String = "SETTLED",
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedAmount: String get() = "₹${String.format("%.2f", amount)}"
}

// ==========================================
// 8. Extra Utility Bills (LPG, Education, Subscriptions)
// ==========================================

data class LpgBookingDetails(
    val consumerId: String,
    val providerName: String,
    val consumerName: String,
    val cylinderSize: String = "14.2 KG Domestic",
    val price: Double,
    val subsidyEstimated: Double,
    val deliveryAddress: String
) {
    val formattedPrice: String get() = "₹${String.format("%.2f", price)}"
    val formattedSubsidy: String get() = "₹${String.format("%.2f", subsidyEstimated)}"
}

data class EducationFeeDetails(
    val instituteId: String,
    val instituteName: String,
    val studentName: String,
    val rollNumber: String,
    val academicTerm: String,
    val totalFee: Double,
    val dueDate: String
) {
    val formattedFee: String get() = "₹${String.format("%.2f", totalFee)}"
}

data class SubscriptionPlan(
    val providerId: String,
    val providerName: String,
    val planTitle: String,
    val validity: String,
    val price: Double,
    val billingCycle: String
) {
    val formattedPrice: String get() = "₹${String.format("%.2f", price)}"
}
