package com.unitedpay.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.unitedpay.feature.auth.SimBindingScreen
// Feature Home & Hubs
import com.unitedpay.feature.home.AllServicesScreen
import com.unitedpay.feature.home.CardsScreen
import com.unitedpay.feature.home.HomeScreen

// Cards Domain
import com.unitedpay.feature.home.cards.AddCardScreen
import com.unitedpay.feature.home.cards.CardLimitsScreen
import com.unitedpay.feature.home.cards.ResetCardPinScreen

// Transfers Domain
import com.unitedpay.feature.home.services.transfers.AddMoneyScreen
import com.unitedpay.feature.home.services.transfers.AutopayScreen
import com.unitedpay.feature.home.services.transfers.BankTransferScreen
import com.unitedpay.feature.home.services.transfers.CheckBalanceScreen
import com.unitedpay.feature.home.services.transfers.DigitalRupeeScreen
import com.unitedpay.feature.home.services.transfers.RequestMoneyScreen
import com.unitedpay.feature.home.services.transfers.SelfTransferScreen
import com.unitedpay.feature.home.services.transfers.ToMobileScreen

// Recharge Domain
import com.unitedpay.feature.home.services.recharge.FastagScreen
import com.unitedpay.feature.home.services.recharge.MetroScreen
import com.unitedpay.feature.home.services.recharge.RechargeScreen

// Utilities Domain
import com.unitedpay.feature.home.services.utilities.BroadbandScreen
import com.unitedpay.feature.home.services.utilities.DthScreen
import com.unitedpay.feature.home.services.utilities.ElectricityScreen
import com.unitedpay.feature.home.services.utilities.LandlineScreen
import com.unitedpay.feature.home.services.utilities.MunicipalTaxScreen
import com.unitedpay.feature.home.services.utilities.PipedGasScreen
import com.unitedpay.feature.home.services.utilities.WaterBillScreen

// Financial Domain
import com.unitedpay.feature.home.services.financial.CreditCardScreen
import com.unitedpay.feature.home.services.financial.DigitalGoldScreen
import com.unitedpay.feature.home.services.financial.InsuranceScreen
import com.unitedpay.feature.home.services.financial.LoanEmiScreen
import com.unitedpay.feature.home.services.financial.MutualFundsScreen

// Promotions Domain
import com.unitedpay.feature.home.services.promotions.GiftCardsScreen
import com.unitedpay.feature.home.services.promotions.OffersScreen
import com.unitedpay.feature.home.services.promotions.ReferWinScreen
import com.unitedpay.feature.home.services.promotions.RewardsScreen

// Communications Domain
import com.unitedpay.feature.home.communications.ChatDetailScreen
import com.unitedpay.feature.home.communications.MessagesScreen
import com.unitedpay.feature.home.communications.NotificationsScreen

// Hookolu Multi-Service Domain Screens
import com.unitedpay.feature.home.services.banking.AepsScreen
import com.unitedpay.feature.home.services.banking.DmtScreen
import com.unitedpay.feature.home.services.banking.MicroAtmScreen
import com.unitedpay.feature.home.services.banking.BankAccountOpeningScreen
import com.unitedpay.feature.home.services.financial.LendingScreen
import com.unitedpay.feature.home.services.travel.TravelHubScreen
import com.unitedpay.feature.home.services.retailer.RetailerDashboardScreen
import com.unitedpay.feature.home.services.utilities.LpgCylinderScreen
import com.unitedpay.feature.home.services.utilities.EducationFeesScreen
import com.unitedpay.feature.home.services.utilities.SubscriptionsScreen

// Profile Domain
import com.unitedpay.feature.home.profile.BiometricLockScreen
import com.unitedpay.feature.home.profile.ChangeMpinScreen
import com.unitedpay.feature.home.profile.DisputeCenterScreen
import com.unitedpay.feature.home.profile.MyQrScreen
import com.unitedpay.feature.home.profile.SoundboxSettingsScreen
import com.unitedpay.feature.passbook.PassbookScreen
import com.unitedpay.feature.passbook.TransactionDetailScreen
import com.unitedpay.feature.payment.PaymentScreen
import com.unitedpay.feature.payment.scanner.QrScannerScreen

sealed class Screen(val route: String) {
    // App Launch Splash Screen
    object Splash : Screen("splash")

    // Bottom Navigation Tabs
    object Home : Screen("home")
    object Cards : Screen("cards")
    object QrScanner : Screen("qr_scanner")
    object Passbook : Screen("passbook")
    object Profile : Screen("profile")

    // Flow / Transaction Screens
    object SimBinding : Screen("sim_binding")
    object Payment : Screen("payment?vpa={vpa}&name={name}") {
        fun createRoute(vpa: String = "", name: String = "") = "payment?vpa=$vpa&name=$name"
    }

    // All Services Hub (20+ Features)
    object AllServices : Screen("all_services")

    // Quick Actions Screens
    object Offers : Screen("offers")
    object Rewards : Screen("rewards")
    object GiftCards : Screen("gift_cards")
    object ReferWin : Screen("refer_win")

    // Cards Hub Sub-flows
    object AddCard : Screen("add_card")
    object CardLimits : Screen("card_limits")
    object ResetCardPin : Screen("reset_card_pin")

    // Fintech Service Full Pages
    object Recharge : Screen("recharge")
    object Electricity : Screen("electricity")
    object Dth : Screen("dth")
    object CreditCard : Screen("credit_card_bill")
    object ToMobile : Screen("to_mobile")
    object BankTransfer : Screen("bank_transfer")
    object SelfTransfer : Screen("self_transfer")
    object AddMoney : Screen("add_money")
    object CheckBalance : Screen("check_balance")
    object Autopay : Screen("autopay")
    object DigitalRupee : Screen("digital_rupee")
    object RequestMoney : Screen("request_money")
    object Fastag : Screen("fastag")
    object PipedGas : Screen("piped_gas")
    object WaterBill : Screen("water_bill")
    object Broadband : Screen("broadband")
    object Landline : Screen("landline")
    object LoanEmi : Screen("loan_emi")
    object Insurance : Screen("insurance")
    object DigitalGold : Screen("digital_gold")
    object MutualFunds : Screen("mutual_funds")
    object Metro : Screen("metro")
    object MunicipalTax : Screen("municipal_tax")

    // Hookolu Multi-Service Hub Screens
    object Aeps : Screen("aeps")
    object Dmt : Screen("dmt")
    object MicroAtm : Screen("micro_atm")
    object BankAccountOpening : Screen("bank_account_opening")
    object Lending : Screen("lending")
    object TravelHub : Screen("travel_hub?initialTab={initialTab}") {
        fun createRoute(initialTab: String = "BUS") = "travel_hub?initialTab=$initialTab"
    }
    object RetailerDashboard : Screen("retailer_dashboard")
    object LpgCylinder : Screen("lpg_cylinder")
    object EducationFees : Screen("education_fees")
    object Subscriptions : Screen("subscriptions")

    // Profile Sub-Screens
    object MyQr : Screen("my_qr")
    object Biometrics : Screen("biometric_settings")
    object ChangeMpin : Screen("change_mpin")
    object Soundbox : Screen("soundbox_settings")
    object DisputeCenter : Screen("dispute_center")

    // UPI Messages & Notifications
    object Messages : Screen("messages")
    object ChatDetail : Screen("chat_detail?contactId={contactId}&contactName={contactName}&contactVpa={contactVpa}") {
        fun createRoute(contactId: String = "ramesh", contactName: String = "Ramesh Sharma", contactVpa: String = "ramesh@unitedpay") =
            "chat_detail?contactId=$contactId&contactName=$contactName&contactVpa=$contactVpa"
    }
    object Notifications : Screen("notifications")
    object TransactionDetail : Screen("transaction_detail?txnId={txnId}") {
        fun createRoute(txnId: String = "") = "transaction_detail?txnId=$txnId"
    }
}

/**
 * Top-level Navigation Graph connecting all Bottom Navigation tabs, fintech service pages & profile sub-screens.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = if (com.unitedpay.core.model.session.UserSessionManager.isLoggedIn) Screen.Home.route else Screen.SimBinding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Instant fallback redirection if Screen.Splash.route is ever invoked (no animation)
        composable(Screen.Splash.route) {
            LaunchedEffect(Unit) {
                if (com.unitedpay.core.model.session.UserSessionManager.isLoggedIn) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.SimBinding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
        }
        // Tab 1: Home
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToScan = { navController.navigate(Screen.QrScanner.route) },
                onNavigateToPayment = { vpa -> navController.navigate(Screen.Payment.createRoute(vpa)) },
                onNavigateToPassbook = {
                    navController.navigate(Screen.Passbook.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToCards = {
                    navController.navigate(Screen.Cards.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToChangeMpin = { navController.navigate(Screen.ChangeMpin.route) },
                onNavigateToBiometrics = { navController.navigate(Screen.Biometrics.route) },
                onNavigateToSoundbox = { navController.navigate(Screen.Soundbox.route) },
                onNavigateToDisputeCenter = { navController.navigate(Screen.DisputeCenter.route) },
                onNavigateToMyQr = { navController.navigate(Screen.MyQr.route) },
                onLogout = {
                    navController.navigate(Screen.SimBinding.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToAllServices = { navController.navigate(Screen.AllServices.route) },
                onNavigateToRecharge = { navController.navigate(Screen.Recharge.route) },
                onNavigateToElectricity = { navController.navigate(Screen.Electricity.route) },
                onNavigateToDth = { navController.navigate(Screen.Dth.route) },
                onNavigateToCreditCard = { navController.navigate(Screen.CreditCard.route) },
                onNavigateToBankTransfer = { navController.navigate(Screen.BankTransfer.route) },
                onNavigateToSelfTransfer = { navController.navigate(Screen.SelfTransfer.route) },
                onNavigateToAddMoney = { navController.navigate(Screen.AddMoney.route) },
                onNavigateToCheckBalance = { navController.navigate(Screen.CheckBalance.route) },
                onNavigateToAutopay = { navController.navigate(Screen.Autopay.route) },
                onNavigateToDigitalRupee = { navController.navigate(Screen.DigitalRupee.route) },
                onNavigateToDigitalGold = { navController.navigate(Screen.DigitalGold.route) },
                onNavigateToRequestMoney = { navController.navigate(Screen.RequestMoney.route) },
                onNavigateToOffers = { navController.navigate(Screen.Offers.route) },
                onNavigateToRewards = { navController.navigate(Screen.Rewards.route) },
                onNavigateToGiftCards = { navController.navigate(Screen.GiftCards.route) },
                onNavigateToMessages = { navController.navigate(Screen.Messages.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToAeps = { _ -> navController.navigate(Screen.Aeps.route) },
                onNavigateToMicroAtm = { navController.navigate(Screen.MicroAtm.route) },
                onNavigateToDmt = { navController.navigate(Screen.Dmt.route) },
                onNavigateToBankAccountOpening = { navController.navigate(Screen.BankAccountOpening.route) },
                onNavigateToLending = { navController.navigate(Screen.Lending.route) },
                onNavigateToTravel = { tab -> navController.navigate(Screen.TravelHub.createRoute(tab)) },
                onNavigateToRetailerDashboard = { navController.navigate(Screen.RetailerDashboard.route) },
                onNavigateToLpgCylinder = { navController.navigate(Screen.LpgCylinder.route) },
                onNavigateToEducationFees = { navController.navigate(Screen.EducationFees.route) },
                onNavigateToSubscriptions = { navController.navigate(Screen.Subscriptions.route) },
                onNavigateToMutualFunds = { navController.navigate(Screen.MutualFunds.route) },
                onNavigateToInsurance = { navController.navigate(Screen.Insurance.route) },
                onNavigateToFastag = { navController.navigate(Screen.Fastag.route) },
                onNavigateToWaterBill = { navController.navigate(Screen.WaterBill.route) },
                onNavigateToReferEarn = { navController.navigate(Screen.ReferWin.route) },
                onNavigateToToMobile = { navController.navigate(Screen.ToMobile.route) }
            )
        }

        // Tab 2: Cards ("YOUR CARD YOUR CONTROL")
        composable(Screen.Cards.route) {
            CardsScreen(
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateScan = { navController.navigate(Screen.QrScanner.route) },
                onNavigateHistory = {
                    navController.navigate(Screen.Passbook.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateServices = {
                    navController.navigate(Screen.AllServices.route) {
                        launchSingleTop = true
                    }
                },
                onAddNewCard = { navController.navigate(Screen.AddCard.route) },
                onCardLimitsClick = { navController.navigate(Screen.CardLimits.route) },
                onResetPinClick = { navController.navigate(Screen.ResetCardPin.route) }
            )
        }

        // Tab 3: Center Scanner Action
        composable(Screen.QrScanner.route) {
            QrScannerScreen(
                onCloseClick = { navController.popBackStack() },
                onQrDetected = { qrData ->
                    var parsedVpa = qrData.trim()
                    var parsedName = ""
                    try {
                        if (parsedVpa.startsWith("upi://", ignoreCase = true)) {
                            val uri = android.net.Uri.parse(parsedVpa)
                            parsedVpa = uri.getQueryParameter("pa") ?: parsedVpa
                            parsedName = uri.getQueryParameter("pn") ?: ""
                        } else if (parsedVpa.contains("pa=")) {
                            val match = Regex("[?&]pa=([^&]+)").find(parsedVpa)
                            if (match != null) {
                                parsedVpa = java.net.URLDecoder.decode(match.groupValues[1], "UTF-8")
                            }
                            val nameMatch = Regex("[?&]pn=([^&]+)").find(qrData)
                            if (nameMatch != null) {
                                parsedName = java.net.URLDecoder.decode(nameMatch.groupValues[1], "UTF-8")
                            }
                        }
                    } catch (e: Exception) {
                        // Fallback
                    }

                    navController.navigate(Screen.Payment.createRoute(vpa = parsedVpa, name = parsedName)) {
                        popUpTo(Screen.QrScanner.route) { inclusive = true }
                    }
                }
            )
        }

        // Tab 4: History / Passbook
        composable(Screen.Passbook.route) {
            PassbookScreen(
                onBackClick = { navController.popBackStack() },
                onTransactionClick = { txnId ->
                    navController.navigate(Screen.TransactionDetail.createRoute(txnId))
                },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateCards = {
                    navController.navigate(Screen.Cards.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateScan = { navController.navigate(Screen.QrScanner.route) },
                onNavigateServices = {
                    navController.navigate(Screen.AllServices.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Profile Sub-Screens (Direct access from Profile Drawer)
        composable(Screen.ChangeMpin.route) {
            ChangeMpinScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.Biometrics.route) {
            BiometricLockScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.Soundbox.route) {
            SoundboxSettingsScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.DisputeCenter.route) {
            DisputeCenterScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.MyQr.route) {
            MyQrScreen(onBackClick = { navController.popBackStack() })
        }

        // Tab 5: "More" Services Hub Page (20+ features)
        composable(Screen.AllServices.route) {
            AllServicesScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateCards = {
                    navController.navigate(Screen.Cards.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateScan = { navController.navigate(Screen.QrScanner.route) },
                onNavigateHistory = {
                    navController.navigate(Screen.Passbook.route) {
                        launchSingleTop = true
                    }
                },
                onServiceClick = { svcId ->
                    when (svcId) {
                        "recharge" -> navController.navigate(Screen.Recharge.route)
                        "electricity" -> navController.navigate(Screen.Electricity.route)
                        "dth" -> navController.navigate(Screen.Dth.route)
                        "credit_card_bill", "pay_later" -> navController.navigate(Screen.CreditCard.route)
                        "bank_transfer" -> navController.navigate(Screen.BankTransfer.route)
                        "self_transfer" -> navController.navigate(Screen.SelfTransfer.route)
                        "add_money" -> navController.navigate(Screen.AddMoney.route)
                        "check_balance" -> navController.navigate(Screen.CheckBalance.route)
                        "autopay" -> navController.navigate(Screen.Autopay.route)
                        "digital_rupee" -> navController.navigate(Screen.DigitalRupee.route)
                        "request_money" -> navController.navigate(Screen.RequestMoney.route)
                        "fastag" -> navController.navigate(Screen.Fastag.route)
                        "gas" -> navController.navigate(Screen.PipedGas.route)
                        "water" -> navController.navigate(Screen.WaterBill.route)
                        "broadband" -> navController.navigate(Screen.Broadband.route)
                        "landline" -> navController.navigate(Screen.Landline.route)
                        "loan_emi" -> navController.navigate(Screen.LoanEmi.route)
                        "insurance" -> navController.navigate(Screen.Insurance.route)
                        "digital_gold" -> navController.navigate(Screen.DigitalGold.route)
                        "mutual_funds", "stocks" -> navController.navigate(Screen.MutualFunds.route)
                        "metro", "movie" -> navController.navigate(Screen.Metro.route)
                        "bus" -> navController.navigate(Screen.TravelHub.createRoute("BUS"))
                        "flight" -> navController.navigate(Screen.TravelHub.createRoute("FLIGHT"))
                        "train" -> navController.navigate(Screen.TravelHub.createRoute("TRAIN"))
                        "hotels" -> navController.navigate(Screen.TravelHub.createRoute("HOTEL"))
                        "aeps", "aeps_cash_withdrawal", "aeps_balance", "aeps_statement" -> navController.navigate(Screen.Aeps.route)
                        "micro_atm", "mpos" -> navController.navigate(Screen.MicroAtm.route)
                        "dmt", "money_transfer" -> navController.navigate(Screen.Dmt.route)
                        "bank_account", "account_opening" -> navController.navigate(Screen.BankAccountOpening.route)
                        "lending", "loans", "vyapar_credit" -> navController.navigate(Screen.Lending.route)
                        "retailer", "merchant_hub", "commission" -> navController.navigate(Screen.RetailerDashboard.route)
                        "cylinder", "lpg" -> navController.navigate(Screen.LpgCylinder.route)
                        "education", "fees" -> navController.navigate(Screen.EducationFees.route)
                        "subscriptions", "ott" -> navController.navigate(Screen.Subscriptions.route)
                        "municipal_tax" -> navController.navigate(Screen.MunicipalTax.route)
                        "offers" -> navController.navigate(Screen.Offers.route)
                        "rewards" -> navController.navigate(Screen.Rewards.route)
                        "refer" -> navController.navigate(Screen.ReferWin.route)
                        else -> navController.navigate(Screen.Recharge.route)
                    }
                }
            )
        }

        // Dedicated Quick Actions Pages
        composable(Screen.Offers.route) {
            OffersScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Rewards.route) {
            RewardsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.GiftCards.route) {
            GiftCardsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.ReferWin.route) {
            ReferWinScreen(onBackClick = { navController.popBackStack() })
        }

        // Dedicated Cards Sub-flows
        composable(Screen.AddCard.route) {
            AddCardScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.CardLimits.route) {
            CardLimitsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.ResetCardPin.route) {
            ResetCardPinScreen(onBackClick = { navController.popBackStack() })
        }

        // Dedicated Hub Services
        composable(Screen.Fastag.route) {
            FastagScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.PipedGas.route) {
            PipedGasScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.WaterBill.route) {
            WaterBillScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Broadband.route) {
            BroadbandScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Landline.route) {
            LandlineScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.LoanEmi.route) {
            LoanEmiScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Insurance.route) {
            InsuranceScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.DigitalGold.route) {
            DigitalGoldScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.MutualFunds.route) {
            MutualFundsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Metro.route) {
            MetroScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.MunicipalTax.route) {
            MunicipalTaxScreen(onBackClick = { navController.popBackStack() })
        }

        // Dedicated Fintech Action Pages
        composable(Screen.Recharge.route) {
            RechargeScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Electricity.route) {
            ElectricityScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Dth.route) {
            DthScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.CreditCard.route) {
            CreditCardScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.BankTransfer.route) {
            BankTransferScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.SelfTransfer.route) {
            SelfTransferScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.AddMoney.route) {
            AddMoneyScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.CheckBalance.route) {
            CheckBalanceScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Autopay.route) {
            AutopayScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.DigitalRupee.route) {
            DigitalRupeeScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.RequestMoney.route) {
            RequestMoneyScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToMyQr = { navController.navigate(Screen.MyQr.route) }
            )
        }

        // Dedicated Profile Pages
        composable(Screen.MyQr.route) {
            MyQrScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Biometrics.route) {
            BiometricLockScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.ChangeMpin.route) {
            ChangeMpinScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Soundbox.route) {
            SoundboxSettingsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.DisputeCenter.route) {
            DisputeCenterScreen(onBackClick = { navController.popBackStack() })
        }

        // Sim Binding / Auth
        composable(Screen.SimBinding.route) {
            SimBindingScreen(
                onBindingSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SimBinding.route) { inclusive = true }
                    }
                }
            )
        }

        // To Mobile / Pay to Contact (Paytm & GPay Standard)
        composable(Screen.ToMobile.route) {
            ToMobileScreen(
                onBackClick = { navController.popBackStack() },
                onSelectContact = { name, vpa ->
                    navController.navigate(Screen.Payment.createRoute(vpa = vpa, name = name))
                },
                onNavigateToQrScan = {
                    navController.navigate(Screen.QrScanner.route)
                }
            )
        }

        // Payment Screen
        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("vpa") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val vpa = backStackEntry.arguments?.getString("vpa") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""

            PaymentScreen(
                payeeVpa = vpa,
                payeeName = name,
                onBackClick = { navController.popBackStack() },
                onPaymentSuccess = { txnId ->
                    navController.navigate(Screen.Passbook.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        // UPI Messages & Chats Hub
        composable(Screen.Messages.route) {
            MessagesScreen(
                onBackClick = { navController.popBackStack() },
                onOpenChat = { id, name, vpa ->
                    navController.navigate(Screen.ChatDetail.createRoute(id, name, vpa))
                },
                onNewPayment = { navController.navigate(Screen.Payment.createRoute()) }
            )
        }

        // UPI Chat Detail Screen (Google Pay Style)
        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(
                navArgument("contactId") { type = NavType.StringType; defaultValue = "ramesh" },
                navArgument("contactName") { type = NavType.StringType; defaultValue = "Ramesh Sharma" },
                navArgument("contactVpa") { type = NavType.StringType; defaultValue = "ramesh@unitedpay" }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId") ?: "ramesh"
            val contactName = backStackEntry.arguments?.getString("contactName") ?: "Ramesh Sharma"
            val contactVpa = backStackEntry.arguments?.getString("contactVpa") ?: "ramesh@unitedpay"
            ChatDetailScreen(
                contactId = contactId,
                contactName = contactName,
                contactVpa = contactVpa,
                onBackClick = { navController.popBackStack() },
                onNavigateToTransactionDetail = { txnId ->
                    navController.navigate(Screen.TransactionDetail.createRoute(txnId))
                }
            )
        }

        // Notifications & Alerts Screen
        composable(Screen.Notifications.route) {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToElectricity = { navController.navigate(Screen.Electricity.route) },
                onNavigateToRewards = { navController.navigate(Screen.Rewards.route) }
            )
        }

        // GPay-Style Transaction Detail & Share Screen
        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(
                navArgument("txnId") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val txnId = backStackEntry.arguments?.getString("txnId") ?: ""
            TransactionDetailScreen(
                transactionId = txnId,
                onBackClick = { navController.popBackStack() },
                onPayAgain = { txn ->
                    navController.navigate(Screen.Payment.createRoute(vpa = txn.payeeVpa, name = txn.payeeName))
                }
            )
        }

        // ==========================================
        // Hookolu Multi-Service Dedicated Screens
        // ==========================================
        composable(Screen.Aeps.route) {
            AepsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Dmt.route) {
            DmtScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.MicroAtm.route) {
            MicroAtmScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.BankAccountOpening.route) {
            BankAccountOpeningScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Lending.route) {
            LendingScreen(onBackClick = { navController.popBackStack() })
        }

        composable(
            route = Screen.TravelHub.route,
            arguments = listOf(
                navArgument("initialTab") {
                    type = NavType.StringType
                    defaultValue = "BUS"
                }
            )
        ) { backStackEntry ->
            val initialTab = backStackEntry.arguments?.getString("initialTab") ?: "BUS"
            TravelHubScreen(
                initialTab = initialTab,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.RetailerDashboard.route) {
            RetailerDashboardScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.LpgCylinder.route) {
            LpgCylinderScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.EducationFees.route) {
            EducationFeesScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Subscriptions.route) {
            SubscriptionsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
