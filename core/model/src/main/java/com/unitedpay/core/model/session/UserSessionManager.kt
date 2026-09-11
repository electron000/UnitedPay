package com.unitedpay.core.model.session

import android.content.Context
import android.content.SharedPreferences
import com.unitedpay.core.model.*
import com.unitedpay.core.model.mock.UnitedMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

/**
 * Enterprise Session & Account Profile Data representation.
 */
data class UserSessionData(
    val userId: String,
    val fullName: String,
    val phoneNumber: String,
    val primaryVpa: String,
    val simSlot: Int,
    val isOnboarded: Boolean,
    val isBiometricEnabled: Boolean,
    val bankAccounts: List<BankAccount>,
    val transactions: List<UpiTransaction>,
    val cardDetails: FintechCardDetails,
    val chatContacts: List<ChatContact> = emptyList(),
    val chatMessages: Map<String, List<ChatMessage>> = emptyMap(),
    val notifications: List<FintechNotificationItem> = emptyList(),
    val autopayMandates: List<AutopayMandate> = emptyList(),
    val digitalRupeeWallet: DigitalRupeeWallet? = null,
    val upiLiteBalance: Double = 0.0,
    val electricityBill: BbpsBillSummary? = null,
    val waterBill: BbpsBillSummary? = null,
    val dthBill: BbpsBillSummary? = null,
    val broadbandBill: BbpsBillSummary? = null,
    val pipedGasBill: BbpsBillSummary? = null,
    val landlineBill: BbpsBillSummary? = null,
    val municipalTax: BbpsBillSummary? = null,
    val fastagDetails: FastagDetails? = null,
    val metroCardDetails: MetroCardDetails? = null,
    val creditCardStatement: CreditCardStatement? = null,
    val loanEmiSummary: LoanEmiSummary? = null,
    val insurancePolicySummary: InsurancePolicySummary? = null,
    val scratchCards: List<ScratchCardReward> = emptyList(),
    val disputeTickets: List<DisputeTicket> = emptyList(),
    val upiPin: String? = null
) {
    val userProfile: UserProfile
        get() = UserProfile(
            userId = userId,
            fullName = fullName,
            phoneNumber = phoneNumber,
            primaryVpa = primaryVpa,
            qrCodePayload = "upi://pay?pa=$primaryVpa&pn=${fullName.replace(" ", "%20")}&cu=INR",
            isKycVerified = isOnboarded
        )
}

/**
 * Enterprise UserSessionManager managing UPI SIM Binding, Account Switching,
 * and Session Persistence across App Launches.
 *
 * SIM 1 (6002239926): Arunjyoti Changkakoty (Full rich mock ecosystem)
 * SIM 2 (9876543210): Fresh / Blank Account for new user onboarding
 */
object UserSessionManager {

    const val SIM_1_PHONE = "6002239926"
    const val SIM_2_PHONE = "9876543210"

    private const val PREFS_NAME = "united_pay_user_session"
    private const val KEY_ACTIVE_PHONE = "active_phone"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_BIOMETRIC_PREFIX = "biometric_"
    private const val KEY_ONBOARDED_PREFIX = "onboarded_"
    private const val KEY_SIM2_TXNS = "sim2_txns"
    private const val KEY_SIM2_CHATS = "sim2_chats"
    private const val KEY_SIM2_NOTIFS = "sim2_notifs"

    private var prefs: SharedPreferences? = null

    // In-memory store of account profiles
    private val accountsStore = mutableMapOf<String, UserSessionData>()

    private val _currentSession = MutableStateFlow<UserSessionData?>(null)
    val currentSession: StateFlow<UserSessionData?> = _currentSession.asStateFlow()

    init {
        // Seed Arunjyoti Changkakoty's account (SIM 1) with all rich mock fixtures
        accountsStore[SIM_1_PHONE] = UserSessionData(
            userId = "usr_arunjyoti_001",
            fullName = "Arunjyoti Changkakoty",
            phoneNumber = "+91 $SIM_1_PHONE",
            primaryVpa = "arunjyoti@unitedpay",
            simSlot = 1,
            isOnboarded = true,
            isBiometricEnabled = true,
            bankAccounts = UnitedMockData.linkedBankAccounts,
            transactions = UnitedMockData.initialTransactions,
            cardDetails = UnitedMockData.cardDetails,
            chatContacts = UnitedMockData.chatContacts,
            chatMessages = mapOf("ramesh" to UnitedMockData.sampleChatMessages),
            notifications = UnitedMockData.notifications,
            autopayMandates = UnitedMockData.autopayMandates,
            digitalRupeeWallet = UnitedMockData.digitalRupeeWallet,
            upiLiteBalance = UnitedMockData.upiLiteBalance,
            electricityBill = UnitedMockData.electricityBillSummary,
            waterBill = UnitedMockData.waterBillSummary,
            dthBill = UnitedMockData.dthBillSummary,
            broadbandBill = UnitedMockData.broadbandBillSummary,
            pipedGasBill = UnitedMockData.pipedGasBillSummary,
            landlineBill = UnitedMockData.landlineBillSummary,
            municipalTax = UnitedMockData.municipalTaxSummary,
            fastagDetails = UnitedMockData.fastagDetails,
            metroCardDetails = UnitedMockData.metroCardDetails,
            creditCardStatement = UnitedMockData.creditCardStatement,
            loanEmiSummary = UnitedMockData.loanEmiSummary,
            insurancePolicySummary = UnitedMockData.insurancePolicySummary,
            scratchCards = UnitedMockData.scratchCards,
            disputeTickets = UnitedMockData.disputeTickets,
            upiPin = "123456"
        )

        // Seed SIM 2 as a completely fresh account (Blank until onboarding completed)
        accountsStore[SIM_2_PHONE] = UserSessionData(
            userId = "usr_new_002",
            fullName = "New User",
            phoneNumber = "+91 $SIM_2_PHONE",
            primaryVpa = "user98765@unitedpay",
            simSlot = 2,
            isOnboarded = false,
            isBiometricEnabled = false,
            bankAccounts = emptyList(),
            transactions = emptyList(),
            cardDetails = FintechCardDetails(
                cardNumberFormatted = "••••  ••••  ••••  ••••",
                validThru = "--/--",
                cvv = "•••",
                cardHolderName = "NEW USER",
                cardType = "Debit Card",
                dailyLimit = 25000.0,
                isOnlineActive = false,
                isContactlessActive = false
            ),
            chatContacts = emptyList(),
            chatMessages = emptyMap(),
            notifications = emptyList(),
            autopayMandates = emptyList(),
            digitalRupeeWallet = null,
            upiLiteBalance = 0.0,
            electricityBill = null,
            waterBill = null,
            dthBill = null,
            broadbandBill = null,
            pipedGasBill = null,
            landlineBill = null,
            municipalTax = null,
            fastagDetails = null,
            metroCardDetails = null,
            creditCardStatement = null,
            loanEmiSummary = null,
            insurancePolicySummary = null,
            scratchCards = emptyList(),
            disputeTickets = emptyList(),
            upiPin = null
        )

        // Default to Arunjyoti's account initially
        _currentSession.value = accountsStore[SIM_1_PHONE]
    }

    /**
     * Initializes persistence with Android SharedPreferences.
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val activePhone = prefs?.getString(KEY_ACTIVE_PHONE, SIM_1_PHONE) ?: SIM_1_PHONE
        val isLoggedIn = prefs?.getBoolean(KEY_IS_LOGGED_IN, true) ?: true

        // Restore biometric & onboarding flags
        val sim1Bio = prefs?.getBoolean(KEY_BIOMETRIC_PREFIX + SIM_1_PHONE, true) ?: true
        val sim2Bio = prefs?.getBoolean(KEY_BIOMETRIC_PREFIX + SIM_2_PHONE, false) ?: false
        val sim2Onboarded = prefs?.getBoolean(KEY_ONBOARDED_PREFIX + SIM_2_PHONE, false) ?: false

        accountsStore[SIM_1_PHONE] = accountsStore[SIM_1_PHONE]!!.copy(isBiometricEnabled = sim1Bio)

        if (sim2Onboarded) {
            val existingSim2 = accountsStore[SIM_2_PHONE]!!
            // Restore persistent SIM 2 transactions
            val savedTxns = restoreSim2Transactions()
            val savedContacts = restoreSim2Contacts()
            val savedNotifs = restoreSim2Notifications()

            accountsStore[SIM_2_PHONE] = existingSim2.copy(
                isOnboarded = true,
                isBiometricEnabled = sim2Bio,
                bankAccounts = if (existingSim2.bankAccounts.isEmpty()) listOf(
                    BankAccount(
                        id = "acc_sim2_sbi",
                        bankName = "State Bank of India",
                        ifscCode = "SBIN0001824",
                        accountNumberMasked = "•••• 9821",
                        accountType = "SAVINGS",
                        isPrimary = true,
                        balance = 10000.00
                    )
                ) else existingSim2.bankAccounts,
                transactions = savedTxns,
                chatContacts = savedContacts,
                notifications = savedNotifs
            )
        }

        if (isLoggedIn) {
            val normalizedPhone = if (activePhone.contains(SIM_1_PHONE)) SIM_1_PHONE else SIM_2_PHONE
            _currentSession.value = accountsStore[normalizedPhone]
        } else {
            _currentSession.value = null
        }
    }

    val isLoggedIn: Boolean
        get() = _currentSession.value != null

    val isCurrentBiometricEnabled: Boolean
        get() = _currentSession.value?.isBiometricEnabled == true

    fun isUserOnboarded(phoneNumber: String): Boolean {
        val key = if (phoneNumber.contains(SIM_1_PHONE)) SIM_1_PHONE else SIM_2_PHONE
        return accountsStore[key]?.isOnboarded == true
    }

    fun isBiometricEnabledForUser(phoneNumber: String): Boolean {
        val key = if (phoneNumber.contains(SIM_1_PHONE)) SIM_1_PHONE else SIM_2_PHONE
        return accountsStore[key]?.isBiometricEnabled == true
    }

    /**
     * Authenticate and activate user session.
     */
    fun login(phoneNumber: String, simSlot: Int): UserSessionData {
        val key = if (phoneNumber.contains(SIM_1_PHONE)) SIM_1_PHONE else SIM_2_PHONE
        val account = accountsStore[key] ?: accountsStore[SIM_1_PHONE]!!
        val updated = account.copy(simSlot = simSlot)
        accountsStore[key] = updated
        _currentSession.value = updated

        prefs?.edit()
            ?.putString(KEY_ACTIVE_PHONE, key)
            ?.putBoolean(KEY_IS_LOGGED_IN, true)
            ?.apply()

        return updated
    }

    /**
     * Logs out active user. Session is terminated; redirects to SIM Selection.
     */
    fun logout() {
        _currentSession.value = null
        prefs?.edit()
            ?.putBoolean(KEY_IS_LOGGED_IN, false)
            ?.apply()
    }

    /**
     * Completes 1st-Time Onboarding for SIM 2 (Bank discovery, Debit Card & 6-Digit PIN).
     */
    fun completeSim2Onboarding(
        bankName: String,
        maskedAccount: String,
        ifscCode: String,
        cardLast6: String,
        cardExpiry: String,
        upiPin: String,
        enableBiometric: Boolean
    ): UserSessionData {
        val newAccount = BankAccount(
            id = "acc_sim2_${System.currentTimeMillis()}",
            bankName = bankName,
            ifscCode = ifscCode,
            accountNumberMasked = maskedAccount,
            accountType = "SAVINGS",
            isPrimary = true,
            balance = 10000.00
        )

        val newCard = FintechCardDetails(
            cardNumberFormatted = "••••  ••••  ••••  $cardLast6",
            validThru = cardExpiry,
            cvv = "321",
            cardHolderName = "NEW USER",
            cardType = "$bankName Debit Card",
            dailyLimit = 25000.0,
            isOnlineActive = true,
            isContactlessActive = true
        )

        val updatedSession = UserSessionData(
            userId = "usr_new_${System.currentTimeMillis()}",
            fullName = "New User",
            phoneNumber = "+91 $SIM_2_PHONE",
            primaryVpa = "user98765@unitedpay",
            simSlot = 2,
            isOnboarded = true,
            isBiometricEnabled = enableBiometric,
            bankAccounts = listOf(newAccount),
            transactions = emptyList(), // Blank initial passbook!
            cardDetails = newCard,
            chatContacts = emptyList(),
            chatMessages = emptyMap(),
            notifications = emptyList(),
            autopayMandates = emptyList(),
            digitalRupeeWallet = null,
            upiLiteBalance = 0.0,
            electricityBill = null,
            waterBill = null,
            dthBill = null,
            broadbandBill = null,
            pipedGasBill = null,
            landlineBill = null,
            municipalTax = null,
            fastagDetails = null,
            metroCardDetails = null,
            creditCardStatement = null,
            loanEmiSummary = null,
            insurancePolicySummary = null,
            scratchCards = emptyList(),
            disputeTickets = emptyList(),
            upiPin = upiPin
        )

        accountsStore[SIM_2_PHONE] = updatedSession
        _currentSession.value = updatedSession

        prefs?.edit()
            ?.putString(KEY_ACTIVE_PHONE, SIM_2_PHONE)
            ?.putBoolean(KEY_IS_LOGGED_IN, true)
            ?.putBoolean(KEY_ONBOARDED_PREFIX + SIM_2_PHONE, true)
            ?.putBoolean(KEY_BIOMETRIC_PREFIX + SIM_2_PHONE, enableBiometric)
            ?.apply()

        return updatedSession
    }

    /**
     * Updates biometric preference for a specific phone number.
     */
    fun setBiometricEnabled(phoneNumber: String, enabled: Boolean) {
        val key = if (phoneNumber.contains(SIM_1_PHONE)) SIM_1_PHONE else SIM_2_PHONE
        val existing = accountsStore[key] ?: return
        val updated = existing.copy(isBiometricEnabled = enabled)
        accountsStore[key] = updated

        if (_currentSession.value?.phoneNumber?.contains(key) == true) {
            _currentSession.value = updated
        }

        prefs?.edit()
            ?.putBoolean(KEY_BIOMETRIC_PREFIX + key, enabled)
            ?.apply()
    }

    /**
     * Appends a new transaction to the active user's session.
     * Persists in SharedPreferences if the active user is SIM 2.
     */
    fun addTransaction(transaction: UpiTransaction) {
        val current = _currentSession.value ?: return
        val key = if (current.phoneNumber.contains(SIM_1_PHONE)) SIM_1_PHONE else SIM_2_PHONE
        val updatedTxns = listOf(transaction) + current.transactions
        val updated = current.copy(transactions = updatedTxns)
        accountsStore[key] = updated
        _currentSession.value = updated

        if (key == SIM_2_PHONE) {
            persistSim2Transactions(updatedTxns)
        }
    }

    /**
     * Appends a new chat message to the active user's session.
     */
    fun addChatMessage(contactId: String, message: ChatMessage) {
        val current = _currentSession.value ?: return
        val key = if (current.phoneNumber.contains(SIM_1_PHONE)) SIM_1_PHONE else SIM_2_PHONE
        val currentMessages = current.chatMessages[contactId] ?: emptyList()
        val updatedMessages = currentMessages + message
        val newMap = current.chatMessages.toMutableMap().apply { put(contactId, updatedMessages) }
        val updated = current.copy(chatMessages = newMap)
        accountsStore[key] = updated
        _currentSession.value = updated
    }

    /**
     * Appends a notification to the active user's session.
     */
    fun addNotification(notification: FintechNotificationItem) {
        val current = _currentSession.value ?: return
        val key = if (current.phoneNumber.contains(SIM_1_PHONE)) SIM_1_PHONE else SIM_2_PHONE
        val updatedNotifs = listOf(notification) + current.notifications
        val updated = current.copy(notifications = updatedNotifs)
        accountsStore[key] = updated
        _currentSession.value = updated

        if (key == SIM_2_PHONE) {
            persistSim2Notifications(updatedNotifs)
        }
    }

    // Getters for active user session data
    fun getCurrentProfile(): UserProfile {
        return _currentSession.value?.userProfile ?: accountsStore[SIM_1_PHONE]!!.userProfile
    }

    fun getCurrentBankAccounts(): List<BankAccount> {
        return _currentSession.value?.bankAccounts ?: accountsStore[SIM_1_PHONE]!!.bankAccounts
    }

    fun getCurrentCardDetails(): FintechCardDetails {
        return _currentSession.value?.cardDetails ?: accountsStore[SIM_1_PHONE]!!.cardDetails
    }

    fun getCurrentTransactions(): List<UpiTransaction> {
        return _currentSession.value?.transactions ?: accountsStore[SIM_1_PHONE]!!.transactions
    }

    fun getCurrentChatContacts(): List<ChatContact> {
        return _currentSession.value?.chatContacts ?: accountsStore[SIM_1_PHONE]!!.chatContacts
    }

    fun getCurrentChatMessages(contactId: String): List<ChatMessage> {
        val session = _currentSession.value ?: accountsStore[SIM_1_PHONE]!!
        return session.chatMessages[contactId] ?: emptyList()
    }

    fun getCurrentNotifications(): List<FintechNotificationItem> {
        return _currentSession.value?.notifications ?: accountsStore[SIM_1_PHONE]!!.notifications
    }

    fun getCurrentAutopayMandates(): List<AutopayMandate> {
        return _currentSession.value?.autopayMandates ?: accountsStore[SIM_1_PHONE]!!.autopayMandates
    }

    fun getCurrentDigitalRupeeWallet(): DigitalRupeeWallet? {
        return _currentSession.value?.digitalRupeeWallet
    }

    fun getCurrentUpiLiteBalance(): Double {
        return _currentSession.value?.upiLiteBalance ?: 0.0
    }

    fun getCurrentElectricityBill(): BbpsBillSummary? {
        return _currentSession.value?.electricityBill
    }

    fun getCurrentWaterBill(): BbpsBillSummary? {
        return _currentSession.value?.waterBill
    }

    fun getCurrentDthBill(): BbpsBillSummary? {
        return _currentSession.value?.dthBill
    }

    fun getCurrentBroadbandBill(): BbpsBillSummary? {
        return _currentSession.value?.broadbandBill
    }

    fun getCurrentPipedGasBill(): BbpsBillSummary? {
        return _currentSession.value?.pipedGasBill
    }

    fun getCurrentLandlineBill(): BbpsBillSummary? {
        return _currentSession.value?.landlineBill
    }

    fun getCurrentMunicipalTax(): BbpsBillSummary? {
        return _currentSession.value?.municipalTax
    }

    fun getCurrentFastagDetails(): FastagDetails? {
        return _currentSession.value?.fastagDetails
    }

    fun getCurrentMetroCardDetails(): MetroCardDetails? {
        return _currentSession.value?.metroCardDetails
    }

    fun getCurrentCreditCardStatement(): CreditCardStatement? {
        return _currentSession.value?.creditCardStatement
    }

    fun getCurrentLoanEmiSummary(): LoanEmiSummary? {
        return _currentSession.value?.loanEmiSummary
    }

    fun getCurrentInsurancePolicySummary(): InsurancePolicySummary? {
        return _currentSession.value?.insurancePolicySummary
    }

    fun getCurrentScratchCards(): List<ScratchCardReward> {
        return _currentSession.value?.scratchCards ?: emptyList()
    }

    fun getCurrentDisputeTickets(): List<DisputeTicket> {
        return _currentSession.value?.disputeTickets ?: emptyList()
    }

    // JSON Persistence helpers for SIM 2 dynamic sandbox data
    private fun persistSim2Transactions(txns: List<UpiTransaction>) {
        try {
            val jsonArray = JSONArray()
            for (t in txns) {
                val obj = JSONObject().apply {
                    put("id", t.id)
                    put("utrNumber", t.utrNumber)
                    put("payeeName", t.payeeName)
                    put("payeeVpa", t.payeeVpa)
                    put("payerName", t.payerName)
                    put("payerVpa", t.payerVpa)
                    put("amount", t.amount)
                    put("timestamp", t.timestamp)
                    put("status", t.status.name)
                    put("type", t.type.name)
                    put("bankName", t.bankName)
                    put("bankAccountNumberMasked", t.bankAccountNumberMasked)
                    put("note", t.note)
                }
                jsonArray.put(obj)
            }
            prefs?.edit()?.putString(KEY_SIM2_TXNS, jsonArray.toString())?.apply()
        } catch (_: Exception) {}
    }

    private fun restoreSim2Transactions(): List<UpiTransaction> {
        val jsonStr = prefs?.getString(KEY_SIM2_TXNS, null) ?: return emptyList()
        val list = mutableListOf<UpiTransaction>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    UpiTransaction(
                        id = obj.getString("id"),
                        utrNumber = obj.getString("utrNumber"),
                        payeeName = obj.getString("payeeName"),
                        payeeVpa = obj.getString("payeeVpa"),
                        payerName = obj.optString("payerName", "New User"),
                        payerVpa = obj.optString("payerVpa", "user98765@unitedpay"),
                        amount = obj.getDouble("amount"),
                        timestamp = obj.getLong("timestamp"),
                        status = try { PaymentStatus.valueOf(obj.getString("status")) } catch (_: Exception) { PaymentStatus.SUCCESS },
                        type = try { TransactionType.valueOf(obj.getString("type")) } catch (_: Exception) { TransactionType.DEBIT },
                        bankName = obj.optString("bankName", "State Bank of India"),
                        bankAccountNumberMasked = obj.optString("bankAccountNumberMasked", "•••• 9821"),
                        note = obj.optString("note", "")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    private fun persistSim2Notifications(notifs: List<FintechNotificationItem>) {
        try {
            val jsonArray = JSONArray()
            for (n in notifs) {
                val obj = JSONObject().apply {
                    put("id", n.id)
                    put("title", n.title)
                    put("message", n.message)
                    put("time", n.time)
                    put("category", n.category)
                    put("iconType", n.iconType)
                    if (n.actionText != null) put("actionText", n.actionText)
                }
                jsonArray.put(obj)
            }
            prefs?.edit()?.putString(KEY_SIM2_NOTIFS, jsonArray.toString())?.apply()
        } catch (_: Exception) {}
    }

    private fun restoreSim2Notifications(): List<FintechNotificationItem> {
        val jsonStr = prefs?.getString(KEY_SIM2_NOTIFS, null) ?: return emptyList()
        val list = mutableListOf<FintechNotificationItem>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    FintechNotificationItem(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        message = obj.getString("message"),
                        time = obj.getString("time"),
                        category = obj.getString("category"),
                        iconType = obj.getString("iconType"),
                        actionText = if (obj.has("actionText")) obj.getString("actionText") else null
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    private fun restoreSim2Contacts(): List<ChatContact> {
        // Can be expanded as user adds new contacts dynamically
        return emptyList()
    }
}
