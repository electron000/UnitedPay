# UnitedPay — North-East India's First UPI 2.0 Fintech Platform

[![Android](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026--35)-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%202.1.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20(BOM%202025.02.00)-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Security](https://img.shields.io/badge/Security-OWASP%20MASVS%20L2%20%7C%20NPCI%20UPI%202.0-0078DF?style=for-the-badge&logo=shield&logoColor=white)](https://mas.owasp.org)
[![Architecture](https://img.shields.io/badge/Architecture-12--Module%20Clean%20MAD-005BB5?style=for-the-badge&logo=google&logoColor=white)](https://developer.android.com/topic/architecture)

> **Simple | Secure | Instant**  
> An enterprise-grade, high-throughput UPI 2.0 digital banking and inclusion ecosystem engineered for North-East India and pan-India fintech scale.  
> Comprises a 100% native Android application (Jetpack Compose), an immutable financial design system, a dual-SIM deterministic sandbox engine, hardware-bound biometric authentication, an encrypted local datastore, and a resilient microservices backend with enterprise administration capabilities.

---

## Table of Contents

1. [Executive Summary & Platform Highlights](#1-executive-summary--platform-highlights)
2. [Complete Technology Stack & SDK Specifications](#2-complete-technology-stack--sdk-specifications)
3. [Repository Structure & Modular Architecture](#3-repository-structure--modular-architecture)
4. [Core Architectural Modules Deep-Dive](#4-core-architectural-modules-deep-dive)
5. [Feature Suites & Complete Screen Catalog (58 Screens)](#5-feature-suites--complete-screen-catalog-58-screens)
6. [Security, Integrity & Compliance Blueprint](#6-security-integrity--compliance-blueprint)
7. [Deterministic Dual-SIM Testing Sandbox](#7-deterministic-dual-sim-testing-sandbox)
8. [Design System & Zero-Emoji Policy](#8-design-system--zero-emoji-policy)
9. [Developer Quickstart & Deployment Guide](#9-developer-quickstart--deployment-guide)
10. [Documentation Index & References](#10-documentation-index--references)

---

## 1. Executive Summary & Platform Highlights

UnitedPay is engineered from the ground up to solve digital payments, financial inclusion, and merchant banking in North-East India while maintaining full compliance with National Payments Corporation of India (NPCI) UPI 2.0 and RBI Master Directions.

- **100% True Native Android (Kotlin)**: Zero webview bridges or hybrid wrappers; direct low-level access to the camera hardware (CameraX), Android StrongBox KeyStore TEE, and biometric sensors.
- **58 Production Screens Across 12 Gradle Modules**: Full end-to-end user journeys for peer-to-peer payments, Bharat Bill Payment System (BBPS), transit, financial wealth (24K digital gold, mutual funds, insurance, lending), and banking inclusion (AePS, Micro-ATM, DMT, digital bank account opening).
- **Hardware-Gated Biometrics on Launch**: Integrated `BiometricPrompt` requiring enrolled fingerprints or 3D facial unlock before sensitive screens or app launch can be accessed.
- **In-App Smart Soundbox**: Eliminates merchant dependency on external audio hardware by broadcasting payment confirmations in **Assamese**, **Bengali**, **Hindi**, and **English** using native Android TTS engines.
- **Zero-Emoji Professional UI**: Every icon, badge, and graphic indicator is custom-rendered in Jetpack Compose vector geometry — strictly conforming to bank-grade aesthetic standards.

---

## 2. Complete Technology Stack & SDK Specifications

### Application Target Configuration (`app/build.gradle.kts`)
| Parameter | Value | Description |
| :--- | :--- | :--- |
| **Application ID** | `com.unitedpay.upi` (Debug: `com.unitedpay.upi.debug`) | Unique package namespace |
| **Minimum SDK** | `26` (Android 8.0 Oreo) | Supports 95%+ active Android devices |
| **Target SDK** | `35` (Android 15 Vanilla Ice Cream) | Compliance with latest Google Play requirements |
| **Compile SDK** | `35` | Android 15 toolchain |
| **JVM Target** | `Java 17` (`VERSION_17`) | Modern JVM bytecode optimization |
| **Build Optimization** | `isMinifyEnabled = true`, `isShrinkResources = true` | ProGuard / R8 code obfuscation & resource stripping |

### Technology Stack & Library Catalog (`gradle/libs.versions.toml`)
| Category | Library / Framework | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Build System** | Android Gradle Plugin (AGP) | `8.13.2` | Gradle build automation |
| **Language** | Kotlin | `2.1.10` | 100% first-class idiomatic codebase |
| **Compiler Plugin** | Kotlin KSP | `2.1.10-1.0.29` | High-speed annotation processing for Room |
| **UI Toolkit** | Jetpack Compose BOM | `2025.02.00` | Declarative, reactive UI architecture |
| **Design System** | Material 3 | `1.3.1` | Google Material Design 3 tokens & themes |
| **Navigation** | Navigation Compose | `2.8.7` | Type-safe single-activity navigation graph |
| **Asynchrony** | Kotlinx Coroutines & Flow | `1.10.1` | Reactive state management (`StateFlow`) |
| **Local Database** | AndroidX Room | `2.6.1` | Type-safe SQLite object mapping |
| **DB Encryption** | SQLCipher for Android | `4.5.4` | Bank-grade AES-256 database file encryption |
| **Networking** | Square Retrofit | `2.11.0` | REST API client interface |
| **HTTP Engine** | Square OkHttp & Logging | `4.12.0` | HTTP/2 & TLS 1.3 network transport |
| **Biometrics** | AndroidX Biometric | `1.1.0` | Hardware fingerprint & facial authentication |
| **Integrity** | RootBeer | `0.1.0` | Device compromise & root detection |
| **Computer Vision** | Google ML Kit Barcode Scanning | `17.3.0` | High-speed QR & Barcode parsing |
| **Camera** | AndroidX CameraX (Core, Camera2, View) | `1.4.1` | Ultra-low latency camera capture pipeline |
| **QR Generation** | ZXing Core | `3.5.3` | Dynamic personal & merchant UPI QR encoding |

---

## 3. Repository Structure & Modular Architecture

UnitedPay follows Google's recommended **Modern Android Development (MAD)** clean modular structure:

```
UnitedPay/
├── app/                                  # Application Shell, Manifest & Navigation Root
│   ├── src/main/java/com/unitedpay/app/
│   │   ├── MainActivity.kt               # Single Activity host with Biometric Lock Gate
│   │   ├── UnitedPayApplication.kt       # Application class & crash guard installation
│   │   └── navigation/
│   │       └── AppNavGraph.kt            # Global 40+ route navigation graph
│   └── src/main/AndroidManifest.xml      # Telephony, Biometric, Camera, UPI deep links
│
├── core/                                 # Shared Foundation Modules
│   ├── common/                           # Result monad (Resource<T>), Dispatchers, Telemetry
│   ├── designsystem/                     # UnitedPay theme, Color tokens, 50+ vector icons
│   ├── security/                         # Keystore AES-256 GCM, Root detector, FLAG_SECURE
│   ├── network/                          # OkHttp TLS 1.3, SSL Pinning, Idempotency interceptor
│   ├── database/                         # SQLCipher Room database (unitedpay_encrypted.db)
│   └── model/                            # Domain entities, Hookolu models, UserSessionManager
│
├── feature/                              # Decoupled Feature Modules
│   ├── auth/                             # Splash screen, 7-step SIM binding & device onboarding
│   ├── home/                             # Home dashboard, 12 sections, and 51 domain screens
│   ├── payment/                          # CameraX QR scanner, payment flow, NPCI MPIN verification
│   ├── passbook/                         # Ledger history, transaction detail & branded receipt sharing
│   └── soundbox/                         # Multi-lingual TTS payment announcer (Hindi/Eng/As/Bn)
│
├── backend/                              # Enterprise Microservices (Node.js/Go/Java)
│   ├── services/                         # Auth, UPI Switch Gateway, BBPS, Ledger, Notification
│   └── deploy/                           # Docker, Kubernetes, and Helm deployment charts
│
├── admin-portal/                         # Enterprise Web Operations Portal (React/TypeScript)
│   └── src/                              # Real-time transaction ledger, dispute center, merchant KYC
│
└── docs/                                 # Authoritative Engineering Specifications
    ├── AI_DEVELOPMENT_GUIDELINES.md      # Rules of engagement for AI agents and developers
    ├── ARCHITECTURE.md                   # Distributed systems & mobile module blueprint
    ├── DESIGN_SPECIFICATION.md           # Visual design tokens & responsive screen templates
    ├── FINTECH_END_TO_END_MASTER_PLAN.md # 4-pillar fintech architecture plan
    ├── NON_TECHNICAL_EXECUTIVE_SUMMARY.md# Plain-English guide for founders and investors
    ├── PRODUCTION_DEPLOYMENT_PLAYSTORE_GUIDE.md # Play Store launch checklist & compliance
    ├── PRODUCTION_GUIDELINES.md          # Zero-crash policy, exception handling & security rules
    └── TECHNICAL_ARCHITECTURE_DEEP_DIVE.md# Cryptography, memory scrubbing & thread model
```

### Codebase Statistics
- **Total Gradle Modules**: `12`
- **Total Kotlin Source Files**: `120`
- **Total Lines of Kotlin Code**: `35,315`
- **Total Production Screens / Sheets**: `58`

---

## 4. Core Architectural Modules Deep-Dive

### `core:model` (12 Kotlin Files | 3,498 Lines)
- **`UserSessionManager.kt` (994 lines)**: Centralized in-memory and `SharedPreferences` state engine managing dual-SIM identities, active bank accounts, transactions, wallet balance, and persistent retailer metrics.
- **`HookoluFintechModels.kt` (380 lines)**: Data models for AePS (cash withdrawal, balance enquiry, mini statement), DMT (domestic money transfer), Micro-ATM terminals, digital savings account opening wizards, digital lending offers, travel bookings (ASTC buses, flights, IRCTC trains), and customer Khata entries.
- **`FintechModels.kt` (242 lines)**: Data models for BBPS utilities, FASTag, Metro cards, credit card statements, 24K digital gold quotes, mutual funds, insurance policies, gamified scratch cards, and dispute tickets.
- **`BankAccount.kt` & `UpiTransaction.kt`**: NPCI-compliant bank account representation and transaction ledgers with payment statuses (`SUCCESS`, `PENDING`, `FAILED`, `REFUNDED`).

### `core:designsystem` (17 Kotlin Files | 5,010 Lines)
- **`Color.kt`**: Strict brand color palette:
  - `UnitedMoneyBlue = Color(0xFF0078DF)` (Official signature brand blue)
  - `UnitedDeepBlue = Color(0xFF0242D6)`, `UnitedShieldCyan = Color(0xFF00A0E2)`
  - `UnitedMidnightNavy = Color(0xFF030D26)`, `UnitedCanvasLight = Color(0xFFF5F7FB)`
  - Gradients: `UnitedHeaderGradient`, `UnitedShieldGradient`, `UnitedGlassSurfaceBrush`
- **Components**:
  - `UnitedGlassCard`: Frosted glass container with specular hairline border.
  - `UnitedToast` / `UnitedToastHost`: Animated non-blocking toast pill.
  - `UnitedNpciMpinSheet`: NPCI 6-digit MPIN modal sheet with scrambled keypad.
  - `UnitedBottomBar`: 5-tab floating dock with elevated QR scan FAB.
  - `UnitedCenteredAmountField`: Centered currency input with quick chip row (+₹100, +₹500).
  - `ReceiptShareHelper`: Native 1080x1620 high-resolution bitmap receipt generator.
  - `UnitedIcons.kt` (1,618 lines): 50+ custom zero-emoji Compose vector drawables.

### `core:security` (6 Kotlin Files | 367 Lines)
- **`BiometricAuthHelper.kt`**: AndroidX `BiometricPrompt` controller targeting hardware sensors with fallback handling.
- **`KeystoreHelper.kt`**: Hardware-backed key generation inside Android KeyStore (`AES/GCM/NoPadding`, 256-bit key size, 128-bit authentication tag, randomized 12-byte IV).
- **`RootDetector.kt`**: Multi-phase integrity validation checking `RootBeer`, 9 `su` binary paths (`/system/bin/su`, etc.), test-keys, and emulator signatures.
- **`FlagSecureHelper.kt`**: Toggles `WindowManager.LayoutParams.FLAG_SECURE` to prevent screenshots, screen recordings, and recent app thumbnail leaks.
- **`SecurityManager.kt`**: Includes `withSanitizedPin(pin: CharArray)` memory scrubbing which immediately clears sensitive MPIN character arrays from RAM.
- **`FintechCrashGuard.kt`**: Catches uncaught exceptions and flushes encrypted stack traces to internal storage without exposing stack traces to the user.

### `core:network` (5 Kotlin Files | 244 Lines)
- **`SslPinningFactory.kt`**: SHA-256 SPKI certificate pinning for `api.unitedpay.in` preventing Man-in-the-Middle (MITM) attacks.
- **`IdempotencyInterceptor.kt`**: Attaches a unique `X-Idempotency-Key: <UUID>` header to all payment mutations (`POST`, `PUT`), preventing double debits on network retries.
- **`AuthInterceptor.kt`**: Injects cryptographic headers: `X-Timestamp`, `X-Nonce`, `X-Device-Id`, and Bearer tokens.
- **`UnitedPaySdkManager.kt`**: Pluggable SDK client bridge for connecting the mobile UI to core banking switches.

### `core:database` (3 Kotlin Files | 145 Lines)
- **`UnitedPayDatabase.kt`**: Room database (`unitedpay_encrypted.db`) encrypted via **SQLCipher** (`net.zetetic:android-database-sqlcipher:4.5.4`).
- **`TransactionDao.kt`**: Reactive queries returning `Flow<List<TransactionEntity>>`, single transaction lookup, and bulk upsert operations.

### `core:common` (4 Kotlin Files | 134 Lines)
- **`Resource.kt`**: Sealed interface representing UI async states: `Success<T>`, `Error`, and `Loading`.
- **`CurrencyExtensions.kt`**: Indian Rupee currency formatting (`Double.toInrCurrency()`) and NPCI VPA regex validation (`String.isValidVpa()`).
- **`TransactionTelemetry.kt`**: Distributed tracing engine generating trace UUIDs and tracking lifecycle state transitions without logging PII.

---

## 5. Feature Suites & Complete Screen Catalog (58 Screens)

### Feature Summary Table
| Feature Module | Dedicated Screens | Components / Dialogs | ViewModels | Key Capabilities |
| :--- | :---: | :---: | :---: | :--- |
| **`feature:home`** | **51** | **7** | **1** | Dashboard, UPI Transfers, BBPS Bills, Wealth, AePS, Travel, Retailer |
| **`feature:auth`** | **2** | **0** | **0** | Animated Splash & 7-step NPCI SIM binding and device onboarding |
| **`feature:payment`** | **2** | **1** | **1** | CameraX live QR scanner & recipient amount payment flow |
| **`feature:passbook`** | **2** | **0** | **1** | Financial ledger, search/filter, and branded receipt sharing |
| **`feature:soundbox`**| **1** *(Settings in home)* | **0** | **0** | 4-language Text-to-Speech payment voice announcement engine |
| **TOTAL** | **58** | **8** | **3** | **Complete end-to-end fintech surface** |

---

### Complete Screen-by-Screen Catalog

#### 1. Application Launch & Security Gates
1. **`MainActivity.kt` (BiometricLockGate)**: Full-screen biometric gate checking fingerprint or 3D face scan before revealing the application to an existing user.
2. **`SplashScreen.kt`**: Startup animation featuring brand emblem ("U" & "P") with smooth wordmark reveal.

#### 2. Onboarding & Authentication (`feature:auth`)
3. **`SimBindingScreen.kt` (7-step wizard)**:
   - *Step 1: SIM Selection* (Choose SIM 1 Jio 5G or SIM 2 Airtel 4G).
   - *Step 2: Encrypted Outbound SMS Verification* (3-phase network handshake).
   - *Step 3: Bank Discovery* (Discovers accounts across SBI, HDFC, ICICI, Axis, etc.).
   - *Step 4: Debit Card Verification* (Last 6 digits + MM/YY expiry + bank OTP).
   - *Step 5: Set 6-Digit UPI MPIN* (Dual-entry confirmation keypad).
   - *Step 6: Biometric Setup* (Enrolls hardware fingerprint for instant login).
   - *Step 7: Onboarding Success* (Direct transition into the dashboard).

#### 3. Core Dashboard & Navigation Hubs (`feature:home`)
4. **`HomeScreen.kt`**: Master financial dashboard with 12 modular sections, active balance, quick pay matrix, RuPay Platinum showcase, and profile drawer.
5. **`AllServicesScreen.kt`**: Comprehensive marketplace hub categorizing all 20+ services with brand search.
6. **`CardsScreen.kt`**: Virtual RuPay Platinum card management, transaction limit toggles, and card freeze/unfreeze controls.

#### 4. UPI Money Transfers (`services/transfers/`)
7. **`ToMobileScreen.kt`**: Transfer money to phone contacts or UPI IDs with recent contacts avatar grid (BJ, KK, RB), live search, and 123/ABC keypad switcher.
8. **`RequestMoneyScreen.kt`**: UPI Collect interface with keyboard toggle, UPI handle chips (`@unitedpay`, `@okhdfcbank`, `@oksbi`), recent avatars, and slide-up collect request sheet.
9. **`CheckBalanceScreen.kt`**: Multi-bank account and RuPay credit card hub; inline balance reveal after 6-digit NPCI MPIN authentication.
10. **`CheckBalanceSheet.kt`**: Reusable modal sheet component for checking available balance anywhere across the app.
11. **`BankTransferScreen.kt`**: Direct IMPS/NEFT transfers using Account Number, Confirm Account Number, and IFSC code.
12. **`SelfTransferScreen.kt`**: Inter-account funds movement between the user's own linked savings/current accounts.
13. **`AddMoneyScreen.kt`**: Top-up UPI Lite and prepaid wallet from primary bank with quick-select amount chips (₹500, ₹1,000, ₹2,000).
14. **`AutopayScreen.kt`**: Recurring UPI mandate management — view active mandates, pause/resume, and view execution history.
15. **`DigitalRupeeScreen.kt`**: Official RBI Central Bank Digital Currency (CBDC / e₹) wallet for loading, redeeming, and transferring digital tokens.

#### 5. Recharge & Transit (`services/recharge/`)
16. **`MobileRechargeScreen.kt`**: Prepaid mobile recharge and postpaid bill payment across Jio, Airtel, Vi, and BSNL with browse plans.
17. **`FastagRechargeScreen.kt`**: National Electronic Toll Collection (NETC) FASTag recharge by vehicle registration number.
18. **`MetroCardScreen.kt`**: Metro smart card recharge and transit line passes.

#### 6. BBPS Utilities (`services/utilities/`)
19. **`ElectricityBillScreen.kt`**: State electricity board bill fetching and payment (e.g., APDCL) by consumer number.
20. **`DthCableScreen.kt`**: DTH satellite TV recharge (Tata Play, Airtel Digital TV, Dish TV, Sun Direct).
21. **`BroadbandScreen.kt`**: High-speed fiber internet bill payment (Airtel Xstream, JioFiber, ACT Fibernet, BSNL).
22. **`LpgCylinderScreen.kt`**: LPG cooking gas cylinder refill booking (Indane, Bharat Gas, HP Gas).
23. **`WaterBillScreen.kt`**: Municipal water supply utility bill payment by consumer connection ID.
24. **`PipedGasScreen.kt`**: Piped Natural Gas (PNG) utility bill payment (Adani Total Gas, IGL, MGL).
25. **`LandlineScreen.kt`**: Fixed-line telephone bill payment (BSNL, MTNL, Airtel).
26. **`EducationFeesScreen.kt`**: School, college, and university tuition fee payment under BBPS.
27. **`MunicipalTaxScreen.kt`**: Urban property tax payment for municipal corporations.
28. **`SubscriptionsScreen.kt`**: OTT streaming subscriptions and entertainment gift passes (Hotstar, SonyLIV, Zee5).

#### 7. Financial Services & Wealth (`services/financial/`)
29. **`CreditCardPaymentScreen.kt`**: Credit card bill summary presentation (total due, minimum due) and instant UPI settlement.
30. **`DigitalGoldScreen.kt`**: 24K 99.9% pure MMTC-PAMP certified digital gold buying, selling, live market rate charts, and vault holdings.
31. **`InsuranceScreen.kt`**: Life, health, and motor insurance policy premium discovery and renewal.
32. **`LendingScreen.kt`**: Embedded merchant lending portal with credit score check, loan offer comparison, and instant disbursal.
33. **`LoanEmiScreen.kt`**: Loan EMI repayment for banks and NBFC lenders (Bajaj Finance, HDFC, Home Credit).
34. **`MutualFundsScreen.kt`**: Mutual fund investments discovery, SIP calculator, and top-performing funds tracking.

#### 8. Promotions, Rewards & Referral (`services/promotions/`)
35. **`OffersScreen.kt`**: Curated cashback deals, merchant promotional vouchers, and coupon redemption.
36. **`RewardsScreen.kt`**: Gamified rewards hub featuring interactive scratch cards and United Coins balance.
37. **`GiftCardsScreen.kt`**: Purchase digital gift cards for top brands (Amazon, Flipkart, Swiggy, Myntra).
38. **`ReferWinScreen.kt`**: Refer & Earn dashboard with session-isolated referral codes, one-tap copy, WhatsApp/native share sheets, and earning milestones.

#### 9. Banking & Financial Inclusion (`services/banking/`)
39. **`AepsScreen.kt`**: NPCI Aadhaar Enabled Payment System (Cash Withdrawal, Balance Enquiry, Mini Statement) via biometric fingerprint RD service integration.
40. **`BankAccountOpeningScreen.kt`**: 4-step digital savings account opening wizard with partner banks (AU Small Finance Bank, Equitas SFB, SBI CSP) featuring PAN & Aadhaar e-KYC.
41. **`DmtScreen.kt`**: Domestic Money Transfer (DMT) for BC agents supporting IMPS/NEFT remittances, penny-drop beneficiary validation, and RBI monthly limit tracking.
42. **`MicroAtmScreen.kt`**: Micro-ATM and mPOS hardware terminal interface for Bluetooth EMV chip card cash withdrawals and card sales.

#### 10. Travel & Transit Suite (`services/travel/`)
43. **`TravelHubScreen.kt`**: Multi-modal travel booking hub with 4 modules: Bus (ASTC & interstate routes), Flights (Guwahati/NE airports), Trains (IRCTC integration), and Hotels.

#### 11. Retailer & BC Merchant Operations (`services/retailer/`)
44. **`RetailerDashboardScreen.kt`**: BC agent operations dashboard with daily business volumes, commission slab ledger, instant bank wallet settlement, and digital customer Khata.

#### 12. Profile, Security & Grievances (`profile/`)
45. **`BiometricLockScreen.kt`**: App security lock settings for biometric fingerprint/face authentication, lock timeout intervals, and high-value payment prompts.
46. **`ChangeMpinScreen.kt`**: Secure 6-digit UPI MPIN reset and modification flow with keypad, bank card validation, and NPCI confirmation.
47. **`DisputeCenterScreen.kt`**: NPCI grievance, chargeback, and transaction dispute filing and ticket tracking center.
48. **`MyQrScreen.kt`**: Branded dynamic personal UPI QR code display with save-to-gallery, native image sharing, and VPA copying.
49. **`SoundboxSettingsScreen.kt`**: Configuration screen for Virtual Smart Soundbox audio language (Hindi, English, Assamese, Bengali) with voice preview.

#### 13. Card Management Sub-Flows (`cards/`)
50. **`AddCardScreen.kt`**: Form to link new credit/debit cards with card number, holder name, expiry, CVV verification, and refundable ₹2 penny-drop authentication.
51. **`CardLimitsScreen.kt`**: Fine-grained transaction limit configuration (ATM daily limit, POS swipe limit, online ecommerce limit, and international usage toggle).
52. **`ResetCardPinScreen.kt`**: Card PIN reset interface requiring CVV validation and 4-digit PIN confirmation.

#### 14. Conversational Payments & Communications (`communications/`)
53. **`MessagesScreen.kt`**: Conversational UPI inbox listing recent chat threads, peer payments, and transaction history filters.
54. **`ChatDetailScreen.kt` (1,510 lines)**: 1-on-1 UPI conversational payment chat supporting messaging, inline payment requests, instant payments, NPCI MPIN verification, balance check, and receipt sharing.
55. **`NotificationsScreen.kt`**: Categorized notifications center (Transactions, Offers, Alerts, System) with deep-link action buttons.

#### 15. Real-Time Payment Flow (`feature:payment`)
56. **`QrScannerScreen.kt`**: High-speed QR scanner built on CameraX and Google ML Kit Barcode Scanning with flashlight toggle, gallery picker, and UPI deep-link URI parser.
57. **`PaymentScreen.kt`**: Full payment execution screen with recipient header, centered currency input, payment remarks, debit bank account picker, slide-up NPCI MPIN verification sheet, animated radar progress dialog, and full transaction success confirmation with UTR.

#### 16. Financial Passbook & Receipts (`feature:passbook`)
58. **`PassbookScreen.kt` & `TransactionDetailScreen.kt`**: Digital passbook displaying transaction history with status indicators (debit/credit), filter tabs (All, Paid, Received), copyable UTR, bank metadata, "Pay Again" action, and native Android share intent with bitmap receipt generation.

---

## 6. Security, Integrity & Compliance Blueprint

UnitedPay implements a defense-in-depth security model conforming to **OWASP MASVS Level 2** and **NPCI UPI Procedural Guidelines**:

```
┌─────────────────────────────────────────────────────────────┐
│                    SECURITY ARCHITECTURE                    │
├──────────────────────────────┬──────────────────────────────┤
│ Application Layer            │ Device Integrity Layer       │
│ • FLAG_SECURE Protection     │ • RootBeer su Detection      │
│ • Zero Clipboard PII Leaks   │ • Emulator Signature Check   │
│ • Memory MPIN Scrubbing      │ • Test-Keys Validation       │
├──────────────────────────────┼──────────────────────────────┤
│ Cryptographic Enclave        │ Network Transport Layer      │
│ • Android KeyStore (TEE)     │ • TLS 1.3 Strict Cipher      │
│ • AES-256-GCM Hardware Keys  │ • SPKI Certificate Pinning   │
│ • Randomized 12-byte IVs     │ • X-Idempotency-Key UUID     │
└──────────────────────────────┴──────────────────────────────┘
```

1. **Biometric Authentication Gate**: The application uses AndroidX `BiometricPrompt` with `BIOMETRIC_STRONG` (ultrasonic fingerprint / 3D face unlock). If enabled, the app locks the UI immediately upon launch or resumption.
2. **Hardware KeyStore AES-256-GCM**: Cryptographic secrets and session tokens are encrypted using hardware-backed keys stored in the Android KeyStore StrongBox. Keys never enter application heap memory.
3. **In-Memory MPIN Scrubbing**: Sensitive PIN inputs are handled strictly as `CharArray` and instantly overwritten with zeros (`'0'`) in a `finally` block using `SecurityManager.withSanitizedPin()`, preventing memory scraping.
4. **Screenshot & Recording Prevention**: `WindowManager.LayoutParams.FLAG_SECURE` is enforced on sensitive activities to prevent external screen recording, screenshot capture, and recent apps thumbnail exposure.
5. **SSL Certificate Pinning & Replay Defense**:
   - Primary SPKI Pin: `sha256/k20YW5tYRZp6l7K98z9YkL/2qM3X1Vw+J8w9Xz2X6Y4=`
   - Backup SPKI Pin: `sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=`
   - Requests include `X-Timestamp`, `X-Nonce`, and `X-Device-Id` to invalidate replay attacks.
6. **Encrypted Local Storage**: The local SQLite database (`unitedpay_encrypted.db`) is fully encrypted using **SQLCipher 4.5.4** with a 256-bit passphrase.

---

## 7. Deterministic Dual-SIM Testing Sandbox

To enable complete end-to-end testing without waiting months for live NPCI banking switch credentials, UnitedPay features a built-in deterministic sandbox in `UserSessionManager.kt`:

### Profile 1: Arunjyoti Changkakoty (SIM 1 — Primary Pre-Seeded Ecosystem)
- **Phone Number**: `+91 6002239926` (`SIM_1_PHONE`)
- **Primary UPI ID**: `arunjyoti@unitedpay`
- **Biometric App Lock**: Enabled by default (triggers fingerprint prompt on launch)
- **Pre-Linked Bank Accounts**:
  - State Bank of India (`•••• 4821`, Primary Savings, Balance: ₹24,850.50)
  - HDFC Bank (`•••• 9012`, Secondary Savings, Balance: ₹1,12,400.00)
- **Linked RuPay Credit Cards**:
  - HDFC Tata Neu Infinity RuPay (`•••• 7712`, Available Limit: ₹1,85,000.00)
  - ICICI Coral RuPay Credit Card (`•••• 4410`, Available Limit: ₹72,000.00)
- **Prepaid & CBDC Wallets**:
  - Hookolu RuPay Prepaid Wallet: `₹14,250.00`
  - RBI Digital Rupee (e₹) Wallet: `e₹ 1,500.00`
- **Pre-Seeded Ledgers**: Rich transaction history (Swiggy, Electricity, Peer transfers), 16+ recent chat contacts (BJ, KK, RB, etc.), active autopay mandates, and rewards.

### Profile 2: Clean New User (SIM 2 — Dynamic Onboarding Sandbox)
- **Phone Number**: `+91 9876543210` (`SIM_2_PHONE`)
- **Initial State**: Unonboarded / clean state.
- **Onboarding Flow**: Selecting SIM 2 launches the 7-step `SimBindingScreen` wizard (SMS verification, bank selection, debit card verification, 6-digit MPIN creation, biometric setup).
- **Disk Persistence**: Any onboarding, transactions, Khata entries, or profile updates performed on SIM 2 are persisted across app restarts via `SharedPreferences` keys (`sim2_txns`, `sim2_chats`, `sim2_wallet_balance`, etc.).

---

## 8. Design System & Zero-Emoji Policy

UnitedPay strictly enforces a **Zero-Emoji Policy**:
- Absolutely **zero Unicode emojis** (no smiley faces, cartoon lightning bolts, or money bags) are permitted in code, layouts, or strings.
- All 50+ visual indicators are custom-built Compose `ImageVector` paths in `UnitedIcons.kt`.
- **Responsive Inset Defense**:
  - Headers apply `Modifier.statusBarsPadding()`.
  - Footers and navigation bars apply `Modifier.navigationBarsPadding()`.
  - Form inputs apply `Modifier.imePadding()` to guarantee the software keyboard never obscures input fields.
  - Interactive CTA buttons maintain a minimum 16dp horizontal screen edge padding.

---

## 9. Developer Quickstart & Deployment Guide

### Prerequisites
- **Android Studio**: Ladybug (2024.2.1) or newer
- **JDK**: Java 17 (bundled inside Android Studio at `C:\Program Files\Android\Android Studio\jbr`)
- **Android Device / Emulator**: Running Android 8.0 (API 26) or higher with USB Debugging enabled

### Method 1: Terminal Build & Deployment (Fastest)

```powershell
# 1. Verify connected device
adb devices

# 2. Compile the Debug APK using Java 17
cmd.exe /c "set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr&& gradlew.bat assembleDebug"

# 3. Stream-install the APK to your connected smartphone
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 4. Launch the application
adb shell am start -n com.unitedpay/com.unitedpay.app.MainActivity

# 5. Monitor real-time logs
adb logcat -s UnitedPay:V UserSessionManager:V BiometricPrompt:V
```

### Method 2: Android Studio GUI Flow
1. Open `c:\Users\Acer\Desktop\UnitedPay` in Android Studio.
2. Click **Sync Project with Gradle Files** (elephant icon) and wait for indexing.
3. Select your physical phone (e.g. `Realme 3 Pro / a9708b36`) from the device dropdown.
4. Press **Shift + F10** or click the green **Run** arrow.

---

## 10. Documentation Index & References

For in-depth architectural and operational specifications, refer to the authoritative documentation located in the `docs/` folder:

| Document | Purpose & Target Audience |
| :--- | :--- |
| [**`AI_DEVELOPMENT_GUIDELINES.md`**](docs/AI_DEVELOPMENT_GUIDELINES.md) | Enforceable architectural rules, token conventions, and instructions for AI agents and staff engineers. |
| [**`ARCHITECTURE.md`**](docs/ARCHITECTURE.md) | Distributed backend architecture, microservices blueprint, and mobile modular structure. |
| [**`DESIGN_SPECIFICATION.md`**](docs/DESIGN_SPECIFICATION.md) | Design token constitution, color swatches, typography scales, and responsive layout templates. |
| [**`FINTECH_END_TO_END_MASTER_PLAN.md`**](docs/FINTECH_END_TO_END_MASTER_PLAN.md) | 4-pillar fintech master plan covering client, SDK, backend switch, and admin portal. |
| [**`NON_TECHNICAL_EXECUTIVE_SUMMARY.md`**](docs/NON_TECHNICAL_EXECUTIVE_SUMMARY.md) | Plain-English technology guide for founders, investors, and business leaders. |
| [**`PRODUCTION_DEPLOYMENT_PLAYSTORE_GUIDE.md`**](docs/PRODUCTION_DEPLOYMENT_PLAYSTORE_GUIDE.md) | Google Play Store release checklist, keystore signing, and regulatory compliance guidelines. |
| [**`PRODUCTION_GUIDELINES.md`**](docs/PRODUCTION_GUIDELINES.md) | Security standards, crash guards, and exception handling protocols. |
| [**`TECHNICAL_ARCHITECTURE_DEEP_DIVE.md`**](docs/TECHNICAL_ARCHITECTURE_DEEP_DIVE.md) | Deep technical breakdown of KeyStore TEE, Biometrics, and SQLCipher database encryption. |

---

*UnitedPay — Engineered with Pride for North-East India & Beyond.*
