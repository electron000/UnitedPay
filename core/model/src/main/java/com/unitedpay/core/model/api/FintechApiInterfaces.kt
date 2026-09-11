package com.unitedpay.core.model.api

import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Enterprise API Service interfaces for UnitedPay platform.
 * Fully mirrors future microservices / Bank PSP / BBPS Switch / NPCI Common Library endpoints.
 */

interface UpiBankingApi {
    fun getUserProfile(): Flow<Resource<UserProfile>>
    fun getLinkedBankAccounts(): Flow<Resource<List<BankAccount>>>
    fun checkBalance(accountId: String, mpin: CharArray): Flow<Resource<Double>>
    fun getAutopayMandates(): Flow<Resource<List<AutopayMandate>>>
    fun getDigitalRupeeWallet(): Flow<Resource<DigitalRupeeWallet>>
    fun getRecentTransactions(): Flow<Resource<List<UpiTransaction>>>
    fun getUpiLiteBalance(): Flow<Resource<Double>>
    fun getCardDetails(): Flow<Resource<FintechCardDetails>>
}

interface BbpsBillApi {
    fun getElectricityBillers(): Flow<Resource<List<String>>>
    fun getElectricityBill(): Flow<Resource<BbpsBillSummary>>
    fun getWaterBillers(): Flow<Resource<List<String>>>
    fun getWaterBill(): Flow<Resource<BbpsBillSummary>>
    fun getDthBillers(): Flow<Resource<List<String>>>
    fun getDthBill(): Flow<Resource<BbpsBillSummary>>
    fun getBroadbandBillers(): Flow<Resource<List<String>>>
    fun getBroadbandBill(): Flow<Resource<BbpsBillSummary>>
    fun getPipedGasBillers(): Flow<Resource<List<String>>>
    fun getPipedGasBill(): Flow<Resource<BbpsBillSummary>>
    fun getLandlineBillers(): Flow<Resource<List<String>>>
    fun getLandlineBill(): Flow<Resource<BbpsBillSummary>>
    fun getMunicipalTaxCorporations(): Flow<Resource<List<String>>>
    fun getMunicipalTax(): Flow<Resource<BbpsBillSummary>>
}

interface RechargeTransitApi {
    fun getMobileOperators(): Flow<Resource<List<String>>>
    fun getMobilePlans(): Flow<Resource<List<MobilePlan>>>
    fun getFastagIssuers(): Flow<Resource<List<String>>>
    fun getFastagDetails(): Flow<Resource<FastagDetails>>
    fun getMetroAuthorities(): Flow<Resource<List<String>>>
    fun getMetroCardDetails(): Flow<Resource<MetroCardDetails>>
}

interface FinancialServicesApi {
    fun getCreditCardSummary(): Flow<Resource<CreditCardStatement>>
    fun getDigitalGoldQuote(): Flow<Resource<DigitalGoldQuote>>
    fun getMutualFunds(): Flow<Resource<List<MutualFund>>>
    fun getLoanLenders(): Flow<Resource<List<String>>>
    fun getLoanEmiDetails(): Flow<Resource<LoanEmiSummary>>
    fun getInsurers(): Flow<Resource<List<String>>>
    fun getInsuranceDetails(): Flow<Resource<InsurancePolicySummary>>
}

interface PromotionsRewardsApi {
    fun getBrandGiftCards(): Flow<Resource<List<GiftCardBrandItem>>>
    fun getGiftCardDenominations(): Flow<Resource<List<String>>>
    fun getOfferCategories(): Flow<Resource<List<String>>>
    fun getOfferDeals(): Flow<Resource<List<OfferDealItem>>>
    fun getScratchCards(): Flow<Resource<List<ScratchCardReward>>>
}

interface CommunicationSupportApi {
    fun getChatContacts(): Flow<Resource<List<ChatContact>>>
    fun getChatMessages(contactId: String): Flow<Resource<List<ChatMessage>>>
    fun getNotifications(): Flow<Resource<List<FintechNotificationItem>>>
    fun getDisputes(): Flow<Resource<List<DisputeTicket>>>
    fun getSoundboxConfig(): Flow<Resource<SoundboxConfig>>
}

interface UnitedPayApiGateway {
    val banking: UpiBankingApi
    val bbps: BbpsBillApi
    val recharge: RechargeTransitApi
    val financial: FinancialServicesApi
    val promotions: PromotionsRewardsApi
    val communications: CommunicationSupportApi
}
