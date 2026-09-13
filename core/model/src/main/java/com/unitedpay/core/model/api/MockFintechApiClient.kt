package com.unitedpay.core.model.api

import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.model.*
import com.unitedpay.core.model.mock.UnitedMockData
import com.unitedpay.core.model.session.UserSessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Enterprise Mock Implementation of UnitedPayApiGateway.
 * Serves session-isolated fintech data from UserSessionManager:
 * - SIM 1 (6002239926 - Arunjyoti Changkakoty): Complete rich mock ecosystem
 * - SIM 2 (9876543210 - New User): Clean / Blank account with persistent sandbox storage
 */
object MockFintechApiClient : UnitedPayApiGateway {

    override val banking: UpiBankingApi = object : UpiBankingApi {
        override fun getUserProfile(): Flow<Resource<UserProfile>> = flow {
            emit(Resource.Loading)
            delay(50)
            emit(Resource.Success(UserSessionManager.getCurrentProfile()))
        }

        override fun getLinkedBankAccounts(): Flow<Resource<List<BankAccount>>> = flow {
            emit(Resource.Loading)
            delay(50)
            emit(Resource.Success(UserSessionManager.getCurrentBankAccounts()))
        }

        override fun checkBalance(accountId: String, mpin: CharArray): Flow<Resource<Double>> = flow {
            emit(Resource.Loading)
            delay(100)
            val accounts = UserSessionManager.getCurrentBankAccounts()
            val acc = accounts.find { it.id == accountId } ?: accounts.firstOrNull()
            emit(Resource.Success(acc?.balance ?: 10000.00))
        }

        override fun getAutopayMandates(): Flow<Resource<List<AutopayMandate>>> = flow {
            emit(Resource.Loading)
            delay(50)
            emit(Resource.Success(UserSessionManager.getCurrentAutopayMandates()))
        }

        override fun getDigitalRupeeWallet(): Flow<Resource<DigitalRupeeWallet>> = flow {
            emit(Resource.Loading)
            delay(50)
            val wallet = UserSessionManager.getCurrentDigitalRupeeWallet()
            if (wallet != null) {
                emit(Resource.Success(wallet))
            } else {
                emit(Resource.Error("No Digital Rupee wallet linked"))
            }
        }

        override fun getRecentTransactions(): Flow<Resource<List<UpiTransaction>>> = flow {
            emit(Resource.Loading)
            delay(50)
            emit(Resource.Success(TransactionRepository.transactions.value))
        }

        override fun getUpiLiteBalance(): Flow<Resource<Double>> = flow {
            emit(Resource.Loading)
            delay(50)
            emit(Resource.Success(UserSessionManager.getCurrentUpiLiteBalance()))
        }

        override fun getCardDetails(): Flow<Resource<FintechCardDetails>> = flow {
            emit(Resource.Loading)
            delay(50)
            emit(Resource.Success(UserSessionManager.getCurrentCardDetails()))
        }
    }

    override val bbps: BbpsBillApi = object : BbpsBillApi {
        override fun getElectricityBillers(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(80)
            emit(Resource.Success(UnitedMockData.electricityBillers))
        }

        override fun getElectricityBill(): Flow<Resource<BbpsBillSummary>> = flow {
            emit(Resource.Loading)
            delay(120)
            val bill = UserSessionManager.getCurrentElectricityBill()
            if (bill != null) {
                emit(Resource.Success(bill))
            } else {
                emit(Resource.Error("No pending electricity bill"))
            }
        }

        override fun getWaterBillers(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(80)
            emit(Resource.Success(UnitedMockData.waterBillers))
        }

        override fun getWaterBill(): Flow<Resource<BbpsBillSummary>> = flow {
            emit(Resource.Loading)
            delay(120)
            val bill = UserSessionManager.getCurrentWaterBill()
            if (bill != null) {
                emit(Resource.Success(bill))
            } else {
                emit(Resource.Error("No pending water bill"))
            }
        }

        override fun getDthBillers(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(80)
            emit(Resource.Success(UnitedMockData.dthBillers))
        }

        override fun getDthBill(): Flow<Resource<BbpsBillSummary>> = flow {
            emit(Resource.Loading)
            delay(120)
            val bill = UserSessionManager.getCurrentDthBill()
            if (bill != null) {
                emit(Resource.Success(bill))
            } else {
                emit(Resource.Error("No pending DTH bill"))
            }
        }

        override fun getBroadbandBillers(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(80)
            emit(Resource.Success(UnitedMockData.broadbandBillers))
        }

        override fun getBroadbandBill(): Flow<Resource<BbpsBillSummary>> = flow {
            emit(Resource.Loading)
            delay(120)
            val bill = UserSessionManager.getCurrentBroadbandBill()
            if (bill != null) {
                emit(Resource.Success(bill))
            } else {
                emit(Resource.Error("No pending broadband bill"))
            }
        }

        override fun getPipedGasBillers(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(80)
            emit(Resource.Success(UnitedMockData.pipedGasBillers))
        }

        override fun getPipedGasBill(): Flow<Resource<BbpsBillSummary>> = flow {
            emit(Resource.Loading)
            delay(120)
            val bill = UserSessionManager.getCurrentPipedGasBill()
            if (bill != null) {
                emit(Resource.Success(bill))
            } else {
                emit(Resource.Error("No pending piped gas bill"))
            }
        }

        override fun getLandlineBillers(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(80)
            emit(Resource.Success(UnitedMockData.landlineBillers))
        }

        override fun getLandlineBill(): Flow<Resource<BbpsBillSummary>> = flow {
            emit(Resource.Loading)
            delay(120)
            val bill = UserSessionManager.getCurrentLandlineBill()
            if (bill != null) {
                emit(Resource.Success(bill))
            } else {
                emit(Resource.Error("No pending landline bill"))
            }
        }

        override fun getMunicipalTaxCorporations(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(80)
            emit(Resource.Success(UnitedMockData.municipalTaxCorporations))
        }

        override fun getMunicipalTax(): Flow<Resource<BbpsBillSummary>> = flow {
            emit(Resource.Loading)
            delay(120)
            val bill = UserSessionManager.getCurrentMunicipalTax()
            if (bill != null) {
                emit(Resource.Success(bill))
            } else {
                emit(Resource.Error("No pending municipal tax"))
            }
        }
    }

    override val recharge: RechargeTransitApi = object : RechargeTransitApi {
        override fun getMobileOperators(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(80)
            emit(Resource.Success(UnitedMockData.mobileOperators))
        }

        override fun getMobilePlans(): Flow<Resource<List<MobilePlan>>> = flow {
            emit(Resource.Loading)
            delay(100)
            emit(Resource.Success(UnitedMockData.mobilePlans))
        }

        override fun getFastagIssuers(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(80)
            emit(Resource.Success(UnitedMockData.fastagIssuers))
        }

        override fun getFastagDetails(): Flow<Resource<FastagDetails>> = flow {
            emit(Resource.Loading)
            delay(120)
            val fastag = UserSessionManager.getCurrentFastagDetails()
            if (fastag != null) {
                emit(Resource.Success(fastag))
            } else {
                emit(Resource.Error("No FASTag linked"))
            }
        }

        override fun getMetroAuthorities(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(80)
            emit(Resource.Success(UnitedMockData.metroAuthorities))
        }

        override fun getMetroCardDetails(): Flow<Resource<MetroCardDetails>> = flow {
            emit(Resource.Loading)
            delay(120)
            val metro = UserSessionManager.getCurrentMetroCardDetails()
            if (metro != null) {
                emit(Resource.Success(metro))
            } else {
                emit(Resource.Error("No metro card linked"))
            }
        }
    }

    override val financial: FinancialServicesApi = object : FinancialServicesApi {
        override fun getCreditCardSummary(): Flow<Resource<CreditCardStatement>> = flow {
            emit(Resource.Loading)
            delay(100)
            val cc = UserSessionManager.getCurrentCreditCardStatement()
            if (cc != null) {
                emit(Resource.Success(cc))
            } else {
                emit(Resource.Error("No credit card linked"))
            }
        }

        override fun getDigitalGoldQuote(): Flow<Resource<DigitalGoldQuote>> = flow {
            emit(Resource.Loading)
            delay(100)
            val grams = UserSessionManager.getDigitalGoldGrams()
            val baseQuote = UnitedMockData.digitalGoldQuote
            val vaultVal = grams * baseQuote.buyPricePerGram
            val quote = baseQuote.copy(
                userVaultGrams = grams,
                userVaultValue = vaultVal
            )
            emit(Resource.Success(quote))
        }

        override fun getMutualFunds(): Flow<Resource<List<MutualFund>>> = flow {
            emit(Resource.Loading)
            delay(100)
            emit(Resource.Success(UnitedMockData.mutualFunds))
        }

        override fun getLoanLenders(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(80)
            emit(Resource.Success(UnitedMockData.loanLenders))
        }

        override fun getLoanEmiDetails(): Flow<Resource<LoanEmiSummary>> = flow {
            emit(Resource.Loading)
            delay(120)
            val loan = UserSessionManager.getCurrentLoanEmiSummary()
            if (loan != null) {
                emit(Resource.Success(loan))
            } else {
                emit(Resource.Error("No active loan account"))
            }
        }

        override fun getInsurers(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(80)
            emit(Resource.Success(UnitedMockData.insurers))
        }

        override fun getInsuranceDetails(): Flow<Resource<InsurancePolicySummary>> = flow {
            emit(Resource.Loading)
            delay(120)
            val ins = UserSessionManager.getCurrentInsurancePolicySummary()
            if (ins != null) {
                emit(Resource.Success(ins))
            } else {
                emit(Resource.Error("No active insurance policy"))
            }
        }
    }

    override val promotions: PromotionsRewardsApi = object : PromotionsRewardsApi {
        override fun getBrandGiftCards(): Flow<Resource<List<GiftCardBrandItem>>> = flow {
            emit(Resource.Loading)
            delay(100)
            emit(Resource.Success(UnitedMockData.giftCardBrands))
        }

        override fun getGiftCardDenominations(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(50)
            emit(Resource.Success(UnitedMockData.giftCardDenominations))
        }

        override fun getOfferCategories(): Flow<Resource<List<String>>> = flow {
            emit(Resource.Loading)
            delay(50)
            emit(Resource.Success(UnitedMockData.offerCategories))
        }

        override fun getOfferDeals(): Flow<Resource<List<OfferDealItem>>> = flow {
            emit(Resource.Loading)
            delay(100)
            emit(Resource.Success(UnitedMockData.offerDeals))
        }

        override fun getScratchCards(): Flow<Resource<List<ScratchCardReward>>> = flow {
            emit(Resource.Loading)
            delay(100)
            emit(Resource.Success(UserSessionManager.getCurrentScratchCards()))
        }
    }

    override val communications: CommunicationSupportApi = object : CommunicationSupportApi {
        override fun getChatContacts(): Flow<Resource<List<ChatContact>>> = flow {
            emit(Resource.Loading)
            delay(100)
            emit(Resource.Success(UserSessionManager.getCurrentChatContacts()))
        }

        override fun getChatMessages(contactId: String): Flow<Resource<List<ChatMessage>>> = flow {
            emit(Resource.Loading)
            delay(100)
            emit(Resource.Success(UserSessionManager.getCurrentChatMessages(contactId)))
        }

        override fun getNotifications(): Flow<Resource<List<FintechNotificationItem>>> = flow {
            emit(Resource.Loading)
            delay(100)
            emit(Resource.Success(UserSessionManager.getCurrentNotifications()))
        }

        override fun getDisputes(): Flow<Resource<List<DisputeTicket>>> = flow {
            emit(Resource.Loading)
            delay(100)
            emit(Resource.Success(UserSessionManager.getCurrentDisputeTickets()))
        }

        override fun getSoundboxConfig(): Flow<Resource<SoundboxConfig>> = flow {
            emit(Resource.Loading)
            delay(100)
            emit(Resource.Success(UnitedMockData.soundboxConfig))
        }
    }
}
