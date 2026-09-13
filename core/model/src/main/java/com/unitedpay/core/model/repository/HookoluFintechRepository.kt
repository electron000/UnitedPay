package com.unitedpay.core.model.repository

import com.unitedpay.core.model.*
import com.unitedpay.core.model.mock.UnitedMockData
import com.unitedpay.core.model.session.UserSessionManager
import kotlinx.coroutines.delay

/**
 * Enterprise Service Repositories for HookoluPay / United Pay Multi-Service Super-App.
 * Provides clean abstractions so that UI and ViewModels remain completely decoupled
 * from underlying data sources (Mock Sandboxes vs Production Core Banking / Switch APIs).
 */

// ==========================================
// 1. AEPS Repository
// ==========================================
interface AepsRepository {
    suspend fun getSupportedBanks(): List<AepsBank>
    suspend fun getRdServiceDevices(): List<RdServiceDevice>
    suspend fun getMiniStatement(aadhaar: String, iin: String): List<AepsStatementEntry>
    suspend fun performAepsTransaction(
        type: AepsTransactionType,
        aadhaar: String,
        bankIin: String,
        amount: Double
    ): AepsTransactionReceipt
}

class MockAepsRepository : AepsRepository {
    override suspend fun getSupportedBanks(): List<AepsBank> = UnitedMockData.aepsSupportedBanks

    override suspend fun getRdServiceDevices(): List<RdServiceDevice> = UnitedMockData.rdServiceDevices

    override suspend fun getMiniStatement(aadhaar: String, iin: String): List<AepsStatementEntry> {
        delay(600)
        return UnitedMockData.aepsStatementEntries
    }

    override suspend fun performAepsTransaction(
        type: AepsTransactionType,
        aadhaar: String,
        bankIin: String,
        amount: Double
    ): AepsTransactionReceipt {
        delay(1200)
        val bank = UnitedMockData.aepsSupportedBanks.firstOrNull { it.iin == bankIin }
            ?: UnitedMockData.aepsSupportedBanks.first()
        val activeBank = UserSessionManager.getCurrentBankAccounts().firstOrNull()
        val currentBalance = activeBank?.balance ?: 24850.50
        val remainingBalance = when (type) {
            AepsTransactionType.CASH_WITHDRAWAL, AepsTransactionType.AADHAAR_PAY -> (currentBalance - amount).coerceAtLeast(0.0)
            else -> currentBalance
        }
        val stanNum = (100000..999999).random().toString()
        val rrnNum = "429188" + (100000..999999).random()

        return AepsTransactionReceipt(
            rrn = rrnNum,
            stan = stanNum,
            txnType = type,
            aadhaarMasked = "•••• •••• " + aadhaar.takeLast(4),
            customerName = UserSessionManager.getCurrentProfile().fullName,
            bankName = bank.bankName,
            amount = amount,
            remainingBalance = remainingBalance
        )
    }
}

// ==========================================
// 2. DMT Repository
// ==========================================
interface DmtRepository {
    suspend fun getSenderProfile(phone: String): DmtSender
    suspend fun getBeneficiaries(): List<DmtBeneficiary>
    suspend fun addBeneficiary(name: String, accountNo: String, ifsc: String, bankName: String): DmtBeneficiary
    suspend fun sendMoney(beneficiaryId: String, amount: Double, mode: DmtTransferMode): DmtTransactionReceipt
}

class MockDmtRepository : DmtRepository {
    override suspend fun getSenderProfile(phone: String): DmtSender {
        delay(300)
        return UserSessionManager.getDmtSenderProfile()
    }

    override suspend fun getBeneficiaries(): List<DmtBeneficiary> = UserSessionManager.getDmtBeneficiaries()

    override suspend fun addBeneficiary(
        name: String,
        accountNo: String,
        ifsc: String,
        bankName: String
    ): DmtBeneficiary {
        delay(800)
        return UserSessionManager.addDmtBeneficiary(name, accountNo, ifsc, bankName)
    }

    override suspend fun sendMoney(
        beneficiaryId: String,
        amount: Double,
        mode: DmtTransferMode
    ): DmtTransactionReceipt {
        delay(1200)
        val bens = UserSessionManager.getDmtBeneficiaries()
        val ben = bens.firstOrNull { it.id == beneficiaryId } ?: bens.firstOrNull()
            ?: DmtBeneficiary("ben_default", "Beneficiary", "•••• 1234", "SBIN0001824", "State Bank of India")
        val fee = if (amount <= 1000) 5.0 else if (amount <= 5000) 10.0 else (amount * 0.004)
        val utr = "4259" + (10000000..99999999).random()

        val profile = UserSessionManager.getCurrentProfile()
        return DmtTransactionReceipt(
            utr = utr,
            senderName = profile.fullName,
            senderMobile = profile.phoneNumber,
            beneficiaryName = ben.name,
            accountNumberMasked = ben.maskedAccountNumber,
            bankName = ben.bankName,
            transferMode = mode,
            amount = amount,
            serviceFee = fee,
            totalDebited = amount + fee
        )
    }
}

// ==========================================
// 3. Micro-ATM Repository
// ==========================================
interface MicroAtmRepository {
    suspend fun getTerminalDetails(): MicroAtmTerminal
    suspend fun executeOperation(operation: MicroAtmOperation, amount: Double): MicroAtmReceipt
}

class MockMicroAtmRepository : MicroAtmRepository {
    override suspend fun getTerminalDetails(): MicroAtmTerminal = UnitedMockData.microAtmTerminal

    override suspend fun executeOperation(operation: MicroAtmOperation, amount: Double): MicroAtmReceipt {
        delay(1500)
        val activeBank = UserSessionManager.getCurrentBankAccounts().firstOrNull()
        val currentBalance = activeBank?.balance ?: 0.0
        val last4 = activeBank?.accountNumberMasked?.takeLast(4) ?: "4821"
        val holder = UserSessionManager.getCurrentProfile().fullName.uppercase()

        return MicroAtmReceipt(
            terminalId = UnitedMockData.microAtmTerminal.terminalId,
            rrn = "42910" + (1000000..9999999).random(),
            cardScheme = "RuPay Debit Chip",
            cardLast4 = last4,
            cardHolderName = holder,
            operation = operation,
            amount = amount,
            availableBalance = (currentBalance - amount).coerceAtLeast(0.0)
        )
    }
}

// ==========================================
// 4. Partner Bank Account Opening Repository
// ==========================================
interface BankAccountOpeningRepository {
    suspend fun getPartnerBanks(): List<PartnerBank>
    suspend fun submitApplication(
        bankId: String,
        applicantName: String,
        panNumber: String,
        aadhaarNumber: String,
        mobileNumber: String
    ): BankApplicationSubmission
}

class MockBankAccountOpeningRepository : BankAccountOpeningRepository {
    override suspend fun getPartnerBanks(): List<PartnerBank> = UnitedMockData.partnerBanks

    override suspend fun submitApplication(
        bankId: String,
        applicantName: String,
        panNumber: String,
        aadhaarNumber: String,
        mobileNumber: String
    ): BankApplicationSubmission {
        delay(1000)
        val bank = UnitedMockData.partnerBanks.firstOrNull { it.id == bankId } ?: UnitedMockData.partnerBanks.first()
        return BankApplicationSubmission(
            applicationId = "APP-HKL-" + (10000..99999).random(),
            bankName = bank.bankName,
            applicantName = applicantName,
            panNumber = panNumber,
            aadhaarMasked = "•••• •••• " + aadhaarNumber.takeLast(4),
            mobileNumber = mobileNumber,
            scheduledVkycSlot = "Today between 03:00 PM - 05:00 PM"
        )
    }
}

// ==========================================
// 5. Embedded Digital Lending Repository
// ==========================================
interface LendingRepository {
    suspend fun getCreditScore(): Int
    suspend fun getLoanOffers(): List<LoanOffer>
    suspend fun applyLoan(offerId: String, amount: Double, tenureMonths: Int): LoanDisbursalReceipt
}

class MockLendingRepository : LendingRepository {
    override suspend fun getCreditScore(): Int =
        if (UserSessionManager.isSim1Active) UnitedMockData.userCreditScore else 0

    override suspend fun getLoanOffers(): List<LoanOffer> = UnitedMockData.loanOffers

    override suspend fun applyLoan(offerId: String, amount: Double, tenureMonths: Int): LoanDisbursalReceipt {
        delay(1500)
        val offer = UnitedMockData.loanOffers.firstOrNull { it.id == offerId } ?: UnitedMockData.loanOffers.first()
        val monthlyRate = offer.interestRatePerMonth / 100.0
        val emi = (amount * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths.toDouble())) /
                (Math.pow(1 + monthlyRate, tenureMonths.toDouble()) - 1)
        val procFee = amount * (offer.processingFeeRate / 100.0)

        val currentProfile = UserSessionManager.getCurrentProfile()
        val activeBank = UserSessionManager.getCurrentBankAccounts().firstOrNull()
        val bankTitle = activeBank?.let { "${it.bankName} (${it.accountNumberMasked})" } ?: "State Bank of India (•••• 4821)"

        return LoanDisbursalReceipt(
            loanId = "LOAN-HKL-" + (100000..999999).random(),
            loanTitle = offer.title,
            borrowerName = currentProfile.fullName,
            sanctionedAmount = amount,
            tenureMonths = tenureMonths,
            monthlyEmi = emi,
            processingFee = procFee,
            netDisbursedAmount = amount - procFee,
            disbursalAccount = bankTitle,
            disbursalDate = "Instant (Within 60 seconds)"
        )
    }
}

// ==========================================
// 6. Travel & Transit Repository
// ==========================================
interface TravelRepository {
    suspend fun getBusTrips(): List<BusTrip>
    suspend fun getFlightTrips(): List<FlightTrip>
    suspend fun getTrainTrips(): List<TrainTrip>
    suspend fun getHotels(): List<HotelProperty>
    suspend fun bookTicket(
        travelType: String,
        title: String,
        details: String,
        date: String,
        passengerName: String,
        seat: String,
        fare: Double
    ): TravelBookingReceipt
}

class MockTravelRepository : TravelRepository {
    override suspend fun getBusTrips(): List<BusTrip> = UnitedMockData.busTrips

    override suspend fun getFlightTrips(): List<FlightTrip> = UnitedMockData.flightTrips

    override suspend fun getTrainTrips(): List<TrainTrip> = UnitedMockData.trainTrips

    override suspend fun getHotels(): List<HotelProperty> = UnitedMockData.hotelProperties

    override suspend fun bookTicket(
        travelType: String,
        title: String,
        details: String,
        date: String,
        passengerName: String,
        seat: String,
        fare: Double
    ): TravelBookingReceipt {
        delay(1200)
        val pnr = when (travelType) {
            "TRAIN" -> "4529" + (100000..999999).random()
            "FLIGHT" -> "6E" + (1000..9999).random()
            else -> "HKL" + (100000..999999).random()
        }
        return TravelBookingReceipt(
            bookingId = "TRV-" + (10000..99999).random(),
            travelType = travelType,
            title = title,
            routeOrDetails = details,
            travelDate = date,
            passengerName = passengerName,
            seatOrRoom = seat,
            totalFare = fare,
            pnr = pnr
        )
    }
}

// ==========================================
// 7. Retailer & BC Operations Repository
// ==========================================
interface RetailerRepository {
    suspend fun getMetrics(): RetailerMetrics
    suspend fun getCommissionSlabs(): List<RetailerCommissionSlab>
    suspend fun getKhataEntries(): List<CustomerKhataEntry>
    suspend fun addKhataEntry(name: String, phone: String, amount: Double, isDebit: Boolean, note: String): CustomerKhataEntry
    suspend fun settleToBank(amount: Double): SettlementReceipt
}

class MockRetailerRepository : RetailerRepository {
    override suspend fun getMetrics(): RetailerMetrics = UserSessionManager.getRetailerMetrics()

    override suspend fun getCommissionSlabs(): List<RetailerCommissionSlab> = UnitedMockData.retailerCommissionSlabs

    override suspend fun getKhataEntries(): List<CustomerKhataEntry> = UserSessionManager.getKhataEntries()

    override suspend fun addKhataEntry(
        name: String,
        phone: String,
        amount: Double,
        isDebit: Boolean,
        note: String
    ): CustomerKhataEntry {
        delay(400)
        return UserSessionManager.addKhataEntry(name, phone, amount, isDebit, note)
    }

    override suspend fun settleToBank(amount: Double): SettlementReceipt {
        delay(1200)
        return UserSessionManager.settleRetailerToBank(amount)
    }
}

// ==========================================
// 8. Extra Utility Repository (LPG, Education, Subscriptions)
// ==========================================
interface ExtraUtilityRepository {
    suspend fun getLpgDetails(): LpgBookingDetails
    suspend fun bookLpgCylinder(consumerId: String, provider: String): LpgBookingDetails
    suspend fun getEducationFeeDetails(): EducationFeeDetails
    suspend fun getSubscriptionPlans(): List<SubscriptionPlan>
}

class MockExtraUtilityRepository : ExtraUtilityRepository {
    override suspend fun getLpgDetails(): LpgBookingDetails = UnitedMockData.lpgDetails

    override suspend fun bookLpgCylinder(consumerId: String, provider: String): LpgBookingDetails {
        delay(1000)
        return UnitedMockData.lpgDetails
    }

    override suspend fun getEducationFeeDetails(): EducationFeeDetails = UnitedMockData.educationFeeDetails

    override suspend fun getSubscriptionPlans(): List<SubscriptionPlan> = UnitedMockData.subscriptionPlans
}

/**
 * Global Service Provider Locator
 * Allows changing from Mock to Production SDK implementations with a single line change.
 */
object HookoluServices {
    var aeps: AepsRepository = MockAepsRepository()
    var dmt: DmtRepository = MockDmtRepository()
    var microAtm: MicroAtmRepository = MockMicroAtmRepository()
    var bankOpening: BankAccountOpeningRepository = MockBankAccountOpeningRepository()
    var lending: LendingRepository = MockLendingRepository()
    var travel: TravelRepository = MockTravelRepository()
    var retailer: RetailerRepository = MockRetailerRepository()
    var utility: ExtraUtilityRepository = MockExtraUtilityRepository()
}
