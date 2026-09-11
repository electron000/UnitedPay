package com.unitedpay.core.model.mock

import com.unitedpay.core.model.*

/**
 * Canonical Single Source of Truth for all Mock / Test Environment Fintech Data.
 *
 * In accordance with enterprise fintech architecture:
 * 1. All mock fixtures are centralized here.
 * 2. API services (e.g. MockFintechApiClient) read from this repository.
 * 3. Screens and ViewModels never directly hardcode test values.
 * 4. When real Bank PSP, BBPS Switch, and NPCI SDKs are integrated, this file
 *    serves as the test fixture provider or can be cleanly decoupled.
 */
object UnitedMockData {

    // 1. Primary User Identity
    val userProfile = UserProfile(
        userId = "usr_arunjyoti_001",
        fullName = "Arunjyoti Changkakoty",
        phoneNumber = "+91 98765 43210",
        primaryVpa = "arunjyoti@unitedpay",
        qrCodePayload = "upi://pay?pa=arunjyoti@unitedpay&pn=Arunjyoti%20Changkakoty&cu=INR",
        isKycVerified = true,
        avatarUrl = null
    )

    // 2. Bank Accounts & Linked Cards
    val linkedBankAccounts = listOf(
        BankAccount(
            id = "acc_sbi_primary",
            bankName = "State Bank of India",
            ifscCode = "SBIN0000123",
            accountNumberMasked = "•••• 4821",
            accountType = "SAVINGS",
            isPrimary = true,
            balance = 24850.50
        ),
        BankAccount(
            id = "acc_hdfc_sec",
            bankName = "HDFC Bank",
            ifscCode = "HDFC0001245",
            accountNumberMasked = "•••• 9102",
            accountType = "SAVINGS",
            isPrimary = false,
            balance = 112400.00
        ),
        BankAccount(
            id = "acc_icici_curr",
            bankName = "ICICI Bank",
            ifscCode = "ICIC0000092",
            accountNumberMasked = "•••• 3341",
            accountType = "CURRENT",
            isPrimary = false,
            balance = 58230.00
        ),
        BankAccount(
            id = "acc_axis_sal",
            bankName = "Axis Bank",
            ifscCode = "UTIB0000045",
            accountNumberMasked = "•••• 5519",
            accountType = "SALARY",
            isPrimary = false,
            balance = 4120.00
        ),
        BankAccount(
            id = "acc_pnb_sav",
            bankName = "Punjab National Bank",
            ifscCode = "PUNB0002140",
            accountNumberMasked = "•••• 8812",
            accountType = "SAVINGS",
            isPrimary = false,
            balance = 15900.00
        )
    )

    // 3. Transactions History
    val initialTransactions = listOf(
        UpiTransaction(
            id = "UP/2026/09/10/89324021",
            utrNumber = "429188021940",
            payeeName = "Ramesh Sharma",
            payeeVpa = "ramesh@unitedpay",
            payerName = "Arunjyoti Changkakoty",
            payerVpa = "arunjyoti@unitedpay",
            amount = 1200.00,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
            status = PaymentStatus.SUCCESS,
            type = TransactionType.DEBIT,
            bankName = "State Bank of India",
            bankAccountNumberMasked = "•••• 4821",
            note = "Dinner & Snacks"
        ),
        UpiTransaction(
            id = "UP/2026/09/10/58392014",
            utrNumber = "425918239012",
            payeeName = "Assam Power Distribution",
            payeeVpa = "apdcl.bill@sbi",
            payerName = "Arunjyoti Changkakoty",
            payerVpa = "arunjyoti@unitedpay",
            amount = 1450.00,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
            status = PaymentStatus.SUCCESS,
            type = TransactionType.DEBIT,
            bankName = "State Bank of India",
            bankAccountNumberMasked = "•••• 4821",
            note = "Electricity Bill - Guwahati"
        ),
        UpiTransaction(
            id = "UP/2026/09/10/37482910",
            utrNumber = "425902948123",
            payeeName = "Priya Baruah",
            payeeVpa = "priya@unitedpay",
            payerName = "Priya Baruah",
            payerVpa = "priya@unitedpay",
            amount = 500.00,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 180,
            status = PaymentStatus.SUCCESS,
            type = TransactionType.CREDIT,
            bankName = "State Bank of India",
            bankAccountNumberMasked = "•••• 4821",
            note = "Tea Garden gift"
        ),
        UpiTransaction(
            id = "UP/2026/09/09/98234102",
            utrNumber = "425890123456",
            payeeName = "Saraighat Fuel Station",
            payeeVpa = "fuel.iocl@icici",
            payerName = "Arunjyoti Changkakoty",
            payerVpa = "arunjyoti@unitedpay",
            amount = 2200.00,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
            status = PaymentStatus.SUCCESS,
            type = TransactionType.DEBIT,
            bankName = "State Bank of India",
            bankAccountNumberMasked = "•••• 4821",
            note = "Petrol Refill (Speed 97)"
        ),
        UpiTransaction(
            id = "UP/2026/09/08/11293847",
            utrNumber = "425789012345",
            payeeName = "Suresh Patel",
            payeeVpa = "suresh.patel@axis",
            payerName = "Arunjyoti Changkakoty",
            payerVpa = "arunjyoti@unitedpay",
            amount = 340.00,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48,
            status = PaymentStatus.SUCCESS,
            type = TransactionType.DEBIT,
            bankName = "State Bank of India",
            bankAccountNumberMasked = "•••• 4821",
            note = "Book purchase"
        ),
        UpiTransaction(
            id = "UP/2026/09/07/55481920",
            utrNumber = "425678901234",
            payeeName = "Cashback Reward",
            payeeVpa = "rewards@unitedpay",
            payerName = "United Pay Fintech",
            payerVpa = "rewards@unitedpay",
            amount = 50.00,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 72,
            status = PaymentStatus.SUCCESS,
            type = TransactionType.CREDIT,
            bankName = "State Bank of India",
            bankAccountNumberMasked = "•••• 4821",
            note = "APDCL Utility Bill Cashback"
        )
    )

    // 4. BBPS Utility Billers & Sample Invoices
    val electricityBillers = listOf(
        "Assam Power Distribution (APDCL)",
        "Tata Power - DDL",
        "BSES Rajdhani Power",
        "Adani Electricity Mumbai",
        "BESCOM - Bengaluru",
        "WBSEDCL - West Bengal"
    )

    val electricityBillSummary = BbpsBillSummary(
        billerId = "apdcl_01",
        billerName = "Assam Power Distribution (APDCL)",
        consumerNumber = "10394829",
        consumerName = "Arunjyoti Changkakoty",
        billCycle = "August 2026",
        dueDate = "15 Sep 2026",
        amountDue = 1450.00
    )

    val waterBillers = listOf(
        "Delhi Jal Board (DJB)",
        "Bangalore Water Supply (BWSSB)",
        "Hyderabad Metro Water (HMWSSB)",
        "Municipal Corp of Greater Mumbai",
        "Guwahati Jal Board"
    )

    val waterBillSummary = BbpsBillSummary(
        billerId = "djb_01",
        billerName = "Delhi Jal Board (DJB)",
        consumerNumber = "DJB-48190283",
        consumerName = "Arunjyoti Changkakoty",
        billCycle = "August 2026",
        dueDate = "20 Sep 2026",
        amountDue = 380.00
    )

    val dthBillers = listOf(
        "Tata Play (Tata Sky)",
        "Airtel Digital TV",
        "Dish TV India",
        "Sun Direct",
        "D2H (Videocon)"
    )

    val dthBillSummary = BbpsBillSummary(
        billerId = "tataplay_01",
        billerName = "Tata Play (Tata Sky)",
        consumerNumber = "1002948291",
        consumerName = "Arunjyoti Changkakoty",
        billCycle = "Monthly Active Pack",
        dueDate = "22 Sep 2026",
        amountDue = 499.00
    )

    val broadbandBillers = listOf(
        "JioFiber Broadband",
        "Airtel Xstream Fiber",
        "ACT Fibernet",
        "BSNL Bharat Fiber",
        "Excitel Broadband"
    )

    val broadbandBillSummary = BbpsBillSummary(
        billerId = "act_01",
        billerName = "ACT Fibernet",
        consumerNumber = "ACT-84920194",
        consumerName = "Arunjyoti Changkakoty",
        billCycle = "Unlimited 150 Mbps Pack",
        dueDate = "18 Sep 2026",
        amountDue = 825.00
    )

    val pipedGasBillers = listOf(
        "Indraprastha Gas Limited (IGL)",
        "Mahanagar Gas (MGL)",
        "Adani Total Gas",
        "Gujarat Gas Limited",
        "Assam Gas Company Limited"
    )

    val pipedGasBillSummary = BbpsBillSummary(
        billerId = "igl_01",
        billerName = "Indraprastha Gas Limited (IGL)",
        consumerNumber = "IGL-99201948",
        consumerName = "Arunjyoti Changkakoty",
        billCycle = "Bi-Monthly PNG Consumption",
        dueDate = "25 Sep 2026",
        amountDue = 640.00
    )

    val landlineBillers = listOf(
        "BSNL Landline (Individual)",
        "Airtel Landline",
        "MTNL Delhi / Mumbai",
        "Tata Teleservices"
    )

    val landlineBillSummary = BbpsBillSummary(
        billerId = "bsnl_01",
        billerName = "BSNL Landline (Individual)",
        consumerNumber = "03612459102",
        consumerName = "Arunjyoti Changkakoty",
        billCycle = "August 2026",
        dueDate = "28 Sep 2026",
        amountDue = 499.00
    )

    val municipalTaxCorporations = listOf(
        "Municipal Corporation of Delhi (MCD)",
        "Bruhat Bengaluru Mahanagara Palike (BBMP)",
        "Greater Chennai Corporation",
        "Guwahati Municipal Corporation (GMC)"
    )

    val municipalTaxSummary = BbpsBillSummary(
        billerId = "gmc_01",
        billerName = "Guwahati Municipal Corporation (GMC)",
        consumerNumber = "PROP-8492019",
        consumerName = "Arunjyoti Changkakoty",
        billCycle = "FY 2026-27 Q2 Property Assessment",
        dueDate = "30 Sep 2026",
        amountDue = 2150.00
    )

    // 5. Mobile Recharge & Transit
    val mobileOperators = listOf("Jio", "Airtel", "Vodafone Idea (Vi)", "BSNL")

    val mobilePlans = listOf(
        MobilePlan("plan_299", 299.0, "28 Days", "1.5 GB / Day", category = "Hero Pack"),
        MobilePlan("plan_719", 719.0, "84 Days", "1.5 GB / Day", category = "Best Value"),
        MobilePlan("plan_2999", 2999.0, "365 Days", "2.5 GB / Day", category = "Annual 5G"),
        MobilePlan("plan_19", 19.0, "1 Day", "1 GB Data Booster", callBenefit = "Data Only Add-on", category = "Data Addon")
    )

    val fastagIssuers = listOf(
        "SBI FASTag",
        "ICICI Bank FASTag",
        "Paytm Payments Bank FASTag",
        "HDFC Bank FASTag",
        "Axis Bank FASTag"
    )

    val fastagDetails = FastagDetails(
        vehicleNumber = "AS-01-EF-4821",
        issuerBank = "SBI FASTag",
        customerName = "Arunjyoti Changkakoty",
        balance = 420.00
    )

    val metroAuthorities = listOf(
        "Delhi Metro (DMRC)",
        "Bangalore Metro (Namma Metro)",
        "Mumbai Metro",
        "Kolkata Metro",
        "Chennai Metro"
    )

    val metroCardDetails = MetroCardDetails(
        cardNumber = "940182740192",
        metroAuthority = "Delhi Metro (DMRC)",
        customerName = "Arunjyoti Changkakoty",
        balance = 180.00
    )

    // 6. Financial Products (Credit Card, Gold, Funds, EMI, Insurance)
    val creditCardStatement = CreditCardStatement(
        cardType = "RuPay Platinum",
        bankName = "HDFC BANK",
        cardNumberMasked = "4251  ••••  ••••  8812",
        cardHolderName = "ARUNJYOTI CHANGKAKOTY",
        expiry = "08/29",
        totalAmountDue = 18450.00,
        minimumAmountDue = 1850.00,
        dueDate = "20 Sep 2026"
    )

    val digitalGoldQuote = DigitalGoldQuote(
        buyPricePerGram = 7450.00,
        sellPricePerGram = 7320.00,
        userVaultGrams = 1.4500,
        userVaultValue = 10802.50
    )

    val mutualFunds = listOf(
        MutualFund("fund_uti", "UTI Nifty 50 Index Fund", "Large Cap • Index", "+16.8% p.a.", "Very Low", "₹500 / mo"),
        MutualFund("fund_pp", "Parag Parikh Flexi Cap Fund", "Flexi Cap • Global", "+21.4% p.a.", "Moderate", "₹1,000 / mo"),
        MutualFund("fund_mirae", "Mirae Asset Large & Midcap", "Equity Growth", "+19.2% p.a.", "Moderately High", "₹500 / mo"),
        MutualFund("fund_quant", "Quant ELSS Tax Saver Fund", "Tax Saving (80C)", "+26.5% p.a.", "High", "₹500 / mo")
    )

    val loanLenders = listOf(
        "Bajaj Finance Limited",
        "HDFC Bank Loans",
        "Tata Capital Financial Services",
        "Home Credit India",
        "IDFC FIRST Bank Loans",
        "L&T Finance"
    )

    val loanEmiSummary = LoanEmiSummary(
        loanAccountNo = "LAN-849201948",
        lenderName = "Bajaj Finance Limited",
        customerName = "Arunjyoti Changkakoty",
        dueDate = "05 Oct 2026",
        emiAmount = 8450.00
    )

    val insurers = listOf(
        "Life Insurance Corporation (LIC)",
        "HDFC Life Insurance",
        "ICICI Prudential Life",
        "Star Health & Allied Insurance",
        "Bajaj Allianz General Insurance"
    )

    val insurancePolicySummary = InsurancePolicySummary(
        policyNumber = "POL-992817203",
        insurerName = "Life Insurance Corporation (LIC)",
        policyHolderName = "Arunjyoti Changkakoty",
        gracePeriodClose = "30 Sep 2026",
        premiumDue = 12400.00
    )

    // 7. Promotions, Deals & Rewards
    val giftCardBrands = listOf(
        GiftCardBrandItem("Amazon Pay", "Shopping & Bills", "2% Cashback"),
        GiftCardBrandItem("Flipkart", "E-Commerce", "3% Off"),
        GiftCardBrandItem("Swiggy", "Food Delivery", "5% Off"),
        GiftCardBrandItem("Uber", "Rides & Transit", "4% Off"),
        GiftCardBrandItem("Myntra", "Fashion", "6% Off"),
        GiftCardBrandItem("BookMyShow", "Entertainment", "5% Off")
    )

    val giftCardDenominations = listOf("₹500", "₹1,000", "₹2,000", "₹5,000")

    val offerCategories = listOf("All Deals", "Food & Dining", "Travel", "Shopping", "Recharge")

    val offerDeals = listOf(
        OfferDealItem("Swiggy", "Flat ₹125 OFF on Gourmet & Dining", "UNITEDSWIGGY", "Valid on orders above ₹399 with United Pay UPI", "FLAT ₹125", "Expires in 3 days"),
        OfferDealItem("Zomato", "50% Instant Discount up to ₹100", "ZOMUNITED", "No minimum order requirement. Apply code at checkout", "50% OFF", "Expires today"),
        OfferDealItem("MakeMyTrip", "Flat ₹1,200 Cashback on Domestic Flights", "MMTUNITED", "Instant bank discount on United Pay RuPay Cards", "₹1,200 OFF", "Expires in 7 days"),
        OfferDealItem("Myntra", "Extra 15% OFF on Fashion & Lifestyle", "MYNTRAUP15", "Applicable on top of existing sales & coupons", "15% EXTRA", "Expires in 5 days"),
        OfferDealItem("BookMyShow", "Buy 1 Get 1 Free on Movie Tickets", "BMSBOGO", "Exclusive weekend privilege for United Pay users", "BOGO FREE", "Expires this Sunday"),
        OfferDealItem("Jio Telecom", "10% Cashback on 84-Day True 5G Plans", "JIOUPAY10", "Cashback credited directly to your bank account", "10% CASHBACK", "Expires in 12 days")
    )

    val scratchCards = listOf(
        ScratchCardReward(1, "Merchant Payment", "₹25 Cashback", false, 25.0),
        ScratchCardReward(2, "Electricity Bill", "₹100 Cashback", false, 100.0),
        ScratchCardReward(3, "Friend Referral", "₹50 Cashback", false, 50.0),
        ScratchCardReward(4, "Mobile Recharge", "₹15 Cashback", false, 15.0)
    )

    // 8. Autopay & Digital Rupee
    val autopayMandates = listOf(
        AutopayMandate("man_1", "Netflix Premium", "₹649 / month", "Next Debit: 24 Sep 2026 • SBI"),
        AutopayMandate("man_2", "UTI Nifty 50 Index Mutual Fund SIP", "₹2,500 / month", "Next Debit: 05 Oct 2026 • SBI"),
        AutopayMandate("man_3", "Spotify Individual Plan", "₹119 / month", "Next Debit: 12 Oct 2026 • SBI")
    )

    val digitalRupeeWallet = DigitalRupeeWallet(
        walletId = "rbi_cbdc_481029",
        balance = 2450.00
    )

    val upiLiteBalance: Double = 1850.00

    val cardDetails = FintechCardDetails(
        cardNumberFormatted = "1234  5678  9000  0000",
        validThru = "08/26",
        cvv = "782",
        cardHolderName = "ARUNJYOTI CHANGKAKOTY",
        cardType = "Mastercard Platinum",
        dailyLimit = 50000.0,
        isOnlineActive = true,
        isContactlessActive = true
    )

    val sampleBankBeneficiary = BankBeneficiary(
        accountNumber = "918273645109",
        confirmAccountNumber = "918273645109",
        ifscCode = "HDFC0001234",
        beneficiaryName = "Aditya Sharma",
        defaultAmount = "3000"
    )

    val samplePaymentRequest = PaymentRequestInfo(
        vpa = "rohit.verma@okaxis",
        defaultAmount = "1200",
        note = "Dinner split"
    )

    // 9. Chat Contacts & Messages
    val chatContacts = listOf(
        ChatContact(
            id = "ramesh",
            name = "Ramesh Sharma",
            vpa = "ramesh@unitedpay",
            avatarHexColor = 0xFF1E88E5,
            lastSnippet = "Paid ₹450",
            lastTime = "Yesterday",
            unreadCount = 0,
            isPayment = true,
            isMerchant = false,
            isRequest = false
        ),
        ChatContact(
            id = "priya",
            name = "Priya Patel",
            vpa = "priya.patel@unitedpay",
            avatarHexColor = 0xFF8E24AA,
            lastSnippet = "Requested ₹350 for Grocery",
            lastTime = "11:20 AM",
            unreadCount = 1,
            isPayment = false,
            isMerchant = false,
            isRequest = true
        ),
        ChatContact(
            id = "amazon",
            name = "Amazon India",
            vpa = "amazonpay@unitedpay",
            avatarHexColor = 0xFFFF9900,
            lastSnippet = "Refund of ₹899 processed",
            lastTime = "Yesterday",
            unreadCount = 0,
            isPayment = true,
            isMerchant = true,
            isRequest = false
        ),
        ChatContact(
            id = "swiggy",
            name = "Swiggy UPI",
            vpa = "swiggy@unitedpay",
            avatarHexColor = 0xFFFC8019,
            lastSnippet = "Paid ₹349 • Dinner order",
            lastTime = "08 Sep",
            unreadCount = 0,
            isPayment = true,
            isMerchant = true,
            isRequest = false
        ),
        ChatContact(
            id = "rohit",
            name = "Rohit Verma",
            vpa = "rohit.v@unitedpay",
            avatarHexColor = 0xFF00897B,
            lastSnippet = "Received ₹1,200 from Rohit",
            lastTime = "07 Sep",
            unreadCount = 0,
            isPayment = true,
            isMerchant = false,
            isRequest = false
        ),
        ChatContact(
            id = "chaipoint",
            name = "Chai Point",
            vpa = "chaipoint@unitedpay",
            avatarHexColor = 0xFFE53935,
            lastSnippet = "Paid ₹80 • Cutting chai",
            lastTime = "06 Sep",
            unreadCount = 0,
            isPayment = true,
            isMerchant = true,
            isRequest = false
        ),
        ChatContact(
            id = "ananya",
            name = "Ananya Roy",
            vpa = "ananya.roy@unitedpay",
            avatarHexColor = 0xFFD81B60,
            lastSnippet = "Thanks for the treat!",
            lastTime = "05 Sep",
            unreadCount = 0,
            isPayment = false,
            isMerchant = false,
            isRequest = false
        ),
        ChatContact(
            id = "apdcl",
            name = "APDCL Assam Power",
            vpa = "apdcl.bill@unitedpay",
            avatarHexColor = 0xFF039BE5,
            lastSnippet = "Bill due reminder: ₹1,420",
            lastTime = "04 Sep",
            unreadCount = 0,
            isPayment = false,
            isMerchant = true,
            isRequest = true
        )
    )

    val sampleChatMessages = listOf(
        ChatMessage(
            id = "header_prev",
            text = "Yesterday, 09 Sep 2026",
            time = "",
            isFromMe = false,
            type = "DATE_HEADER"
        ),
        ChatMessage(
            id = "tx_101",
            text = "Paid",
            time = "02:45 PM",
            isFromMe = true,
            type = "PAYMENT_SENT",
            amount = "450",
            utr = "429104829104",
            bankMasked = "State Bank of India •••• 8821"
        ),
        ChatMessage(
            id = "m_1",
            text = "Hey Arunjyoti, thanks for splitting the lunch bill! Did you need anything else from the store?",
            time = "02:46 PM",
            isFromMe = false,
            type = "TEXT"
        ),
        ChatMessage(
            id = "m_2",
            text = "No, all good Ramesh! Cheers",
            time = "02:47 PM",
            isFromMe = true,
            type = "TEXT"
        ),
        ChatMessage(
            id = "m_header",
            text = "Today",
            time = "",
            isFromMe = false,
            type = "DATE_HEADER"
        ),
        ChatMessage(
            id = "req_101",
            text = "Payment Request",
            time = "11:15 AM",
            isFromMe = false,
            type = "PAYMENT_REQUEST",
            amount = "250",
            note = "Evening snacks & coffee",
            isPaid = false
        ),
        ChatMessage(
            id = "rx_102",
            text = "Received",
            time = "01:10 PM",
            isFromMe = false,
            type = "PAYMENT_RECEIVED",
            amount = "1,200",
            utr = "429188021940",
            bankMasked = "State Bank of India •••• 8821"
        )
    )

    // 10. Notifications
    val notifications = listOf(
        FintechNotificationItem(
            id = "n_1",
            title = "Cashback Credited",
            message = "₹50 cashback credited to your UnitedPay balance for APDCL electricity bill payment.",
            time = "10m ago",
            category = "Today",
            iconType = "cashback"
        ),
        FintechNotificationItem(
            id = "n_2",
            title = "Electricity Bill Due Tomorrow",
            message = "APDCL Consumer #10394829 bill of ₹1,420 is due on 11 Sep. Avoid late surcharge.",
            time = "2h ago",
            category = "Today",
            iconType = "bill",
            actionText = "Pay Now"
        ),
        FintechNotificationItem(
            id = "n_3",
            title = "Security Alert: New Device Login",
            message = "Successful biometric login on Realme 3 Pro (Android 10) from Guwahati, Assam. If this wasn't you, lock your account.",
            time = "Yesterday 4:15 PM",
            category = "Yesterday",
            iconType = "security"
        ),
        FintechNotificationItem(
            id = "n_4",
            title = "Autopay SIP Mandate Scheduled",
            message = "Monthly SIP mandate of ₹2,500 for Nippon India Large Cap Fund is scheduled for 15 Sep.",
            time = "Yesterday 11:30 AM",
            category = "Yesterday",
            iconType = "autopay"
        ),
        FintechNotificationItem(
            id = "n_5",
            title = "Reward Scratch Cards Ready",
            message = "You've earned 2 new scratch cards from recent merchant transactions! Tap to scratch.",
            time = "07 Sep",
            category = "Earlier",
            iconType = "reward",
            actionText = "View Rewards"
        ),
        FintechNotificationItem(
            id = "n_6",
            title = "Credit Card Bill Generated",
            message = "HDFC Bank Millennia Card statement generated. Total due: ₹14,850. Due date: 25 Sep.",
            time = "05 Sep",
            category = "Earlier",
            iconType = "card"
        )
    )

    // 11. Disputes & Soundbox
    val disputeTickets = listOf(
        DisputeTicket(
            id = "DSP-2026-8941",
            transactionRef = "UP/2026/09/08/98102",
            payeeName = "Saraighat Fuel Station",
            utr = "425109887123",
            amount = 1500.00,
            status = "Successful",
            bank = "SBI"
        )
    )

    val soundboxLanguages = listOf(
        Pair("Assamese & English", "ইউনাইটেড পে'ত ১০০ টকা লাভ হ'ল"),
        Pair("Hindi & English", "यूनाइटेड पे पर १०० रुपये प्राप्त हुए"),
        Pair("English Only", "Received 100 rupees on United Pay"),
        Pair("Bengali & English", "ইউনাইটেড পে-তে ১০০ টাকা পাওয়া গেছে"),
        Pair("Marathi & English", "युनायटेड पे वर १०० रुपये मिळाले"),
        Pair("Gujarati & English", "યુનાઇટેડ પે પર ૧૦૦ રૂપિયા મળ્યા"),
        Pair("Tamil & English", "யுனைடெட் பேயில் 100 ரூபாய் பெறப்பட்டது"),
        Pair("Telugu & English", "యునైటెڈ పే లో 100 రూపాయలు అందాయి"),
        Pair("Kannada & English", "ಯುನೈಟೆಡ್ ಪೇ ನಲ್ಲಿ 100 ರೂಪಾಯಿ ಬಂದಿದೆ"),
        Pair("Malayalam & English", "യുണൈറ്റഡ് പേയിൽ 100 രൂപ ലഭിച്ചു")
    )

    val soundboxConfig = SoundboxConfig(
        deviceId = "SB-GUW-88219",
        selectedLanguage = "Assamese & English",
        volumeLevel = 8,
        isConnected = true,
        availableLanguages = soundboxLanguages
    )
}
