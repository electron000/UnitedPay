# UnitedPay — North-East India's First UPI 2.0 Payment App

> **Simple | Secure | Instant**  
> An enterprise-grade, high-throughput UPI payment ecosystem engineered to fintech security standards (OWASP MASVS Level 2, NPCI UPI 2.0).  
> Covers Android Mobile App, Design System, Mock Sandbox Engine, UnitedPay SDK Bridge, and Enterprise Microservices.

---

## Table of Contents
1. [Beginner Quick-Start: Inspecting UI on Physical Mobile via USB Debugging](#1-beginner-quick-start-inspecting-ui-on-physical-mobile-via-usb-debugging)
   - [Method A: Terminal / Command Line Flow (Fastest)](#method-a-terminal--command-line-flow-fastest)
   - [Method B: Android Studio GUI Flow](#method-b-android-studio-gui-flow)
2. [Tech Stack & Architecture](#2-tech-stack--architecture)
3. [Folder & Directory Tour (What Each Module Holds)](#3-folder--directory-tour-what-each-module-holds)
4. [Mock Data Engine: Testing with Arunjyoti Changkakoty](#4-mock-data-engine-testing-with-arunjyoti-changkakoty)
5. [Future UnitedPay SDK & Backend Integration Pipeline](#5-future-unitedpay-sdk--backend-integration-pipeline)
6. [Architectural & AI Development Rulebooks](#6-architectural--ai-development-rulebooks)

---

## 1. Beginner Quick-Start: Inspecting UI on Physical Mobile via USB Debugging

If you are a beginner Android developer, follow these exact steps to compile the code, deploy it to your physical smartphone via USB, and immediately see your UI changes.

### Step 0: One-Time Phone Setup (Enable USB Debugging)
1. On your Android phone, go to **Settings** -> **About Phone**.
2. Tap **Build Number** 7 times rapidly until a toast says: *"You are now a developer!"*.
3. Go back to **Settings** -> **System / Additional Settings** -> **Developer Options**.
4. Toggle **USB Debugging** to **ON**.
5. Connect your phone to your PC via a USB data cable. When prompted on your phone, check *"Always allow from this computer"* and tap **Allow**.

---

### Method A: Terminal / Command Line Flow (Fastest)

Open PowerShell or Command Prompt inside the project root (`c:\Users\Acer\Desktop\UnitedPay`):

#### 1. Verify Connected Device
```powershell
adb devices
```
*Expected output*:
```text
List of devices attached
a9708b36    device
```

#### 2. Compile Debug APK
Set your Java 17 path (bundled inside Android Studio) and compile:
```powershell
cmd /c "set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr&& gradlew.bat assembleDebug"
```
*Tip: When it finishes, you will see `BUILD SUCCESSFUL`.*

#### 3. Install APK to Device
Stream-install the APK with replacement flag (`-r`):
```powershell
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
*Expected output*: `Success`.

#### 4. Launch UnitedPay on Phone
Launch the main activity directly from your terminal:
```powershell
adb shell am start -n com.unitedpay/com.unitedpay.app.MainActivity
```

#### 5. View Real-Time Device Logs
Filter logs to monitor payment flows, toasts, and session state:
```powershell
adb logcat -s UnitedPay:V UserSessionManager:V AndroidRuntime:E
```

---

### Method B: Android Studio GUI Flow

1. **Launch Android Studio**: Open the folder `c:\Users\Acer\Desktop\UnitedPay`.
2. **Sync Project with Gradle Files**: Click the elephant icon (`Sync Project with Gradle Files`) in the top-right toolbar. Wait until the background indexing completes.
3. **Select Your Physical Phone**: In the top toolbar, open the device dropdown (next to the green Run button). Your connected phone (e.g. `Realme 3 Pro / a9708b36`) will appear under **Running Devices**. Select it.
4. **Run the App**: Click the green **Run** arrow button or press `Shift + F10`. Android Studio will automatically build, install, and launch UnitedPay on your phone.
5. **Inspect Live Logs**: Open the **Logcat** tab at the bottom toolbar. Type `package:mine` in the filter bar to see all application events.

---

## 2. Tech Stack & Architecture

UnitedPay is built using Google's recommended Modern Android Development (MAD) architecture:

```
┌───────────────────────────────────────────────────────────┐
│              UI / Presentation Layer                      │
│     Jetpack Compose + Material 3 + Navigation Compose     │
└─────────────────────────────┬─────────────────────────────┘
                              │ Exposes StateFlow<UiState>
┌─────────────────────────────▼─────────────────────────────┐
│                 ViewModel / Domain Layer                  │
│       Kotlin Coroutines + StateFlow + Clean Use Cases     │
└─────────────────────────────┬─────────────────────────────┘
                              │ Interacts via Repositories
┌─────────────────────────────▼─────────────────────────────┐
│                   Data & Network Layer                    │
│   Mock Engine (Active)  │  UnitedPay SDK Bridge (Future)  │
│   SQLCipher Encrypted DB│  OkHttp TLS 1.3 + Cert Pinning  │
└───────────────────────────────────────────────────────────┘
```

| Technology | Purpose |
| :--- | :--- |
| **Kotlin 1.9+** | 100% first-class idiomatic language. |
| **Jetpack Compose** | Declarative, reactive UI toolkit eliminating XML layouts. |
| **Material 3** | Latest design tokens, dynamic color support, and surface elevation. |
| **Navigation Compose** | Single-Activity, multi-screen type-safe navigation graph. |
| **Kotlin Coroutines & Flow** | Asynchronous operations, reactive state streams (`StateFlow`). |
| **CameraX & ML Kit** | Real-time QR Code scanning and barcode detection. |
| **Room + SQLCipher** | Bank-grade AES-256 encrypted local database. |
| **Android Keystore (AES-GCM)** | Hardware-backed cryptographic key generation. |
| **Text-to-Speech (TTS)** | Multi-lingual soundbox audio verification engine. |

---

## 3. Folder & Directory Tour (What Each Module Holds)

The repository uses a high-performance **modular Gradle architecture**:

```
UnitedPay/
├── app/                                  # Application Entry & Navigation Host
│   ├── src/main/java/com/unitedpay/app/
│   │   ├── MainActivity.kt               # Single Activity host with Toast overlay
│   │   ├── UnitedPayApplication.kt       # Application class & crash guard
│   │   └── navigation/
│   │       ├── AppNavGraph.kt            # Global navigation graph and route definitions
│   │       └── Screen.kt                 # Sealed classes defining all screen destinations
│   └── AndroidManifest.xml               # Permissions, hardware features, UPI schemes
│
├── core/                                 # Shared Foundation & System Libraries
│   ├── common/                           # Result<T> monads, Coroutine dispatchers, Telemetry
│   ├── designsystem/                     # Official Design System & UI Constitution
│   │   ├── theme/                        # Color.kt (Design tokens), Theme.kt, Type.kt
│   │   └── components/                   # Reusable UI primitives:
│   │       ├── UnitedCommonComponents.kt # UnitedPrimaryButton, UnitedTextField, UnitedPinnedBottomBar
│   │       ├── UnitedToast.kt            # Global bottom toast notification host
│   │       ├── UnitedGlassCard.kt        # Frosted glass card with 1dp border
│   │       ├── UnitedNpciMpinSheet.kt    # NPCI 6-Digit secure MPIN bottom sheet
│   │       └── UnitedIcons.kt            # Professional Compose vector icons (Zero emojis)
│   ├── security/                         # Keystore AES-GCM, Root detector, FLAG_SECURE
│   ├── network/                          # OkHttp TLS 1.3, SSL Pinning, UnitedPaySdkManager bridge
│   ├── database/                         # SQLCipher Encrypted Room database
│   └── model/                            # Shared Domain Entities & Sandbox Mock Data:
│       ├── UpiTransaction.kt             # Transaction data models & status enums
│       ├── BankAccount.kt                # Linked bank accounts & card models
│       ├── session/
│       │   └── UserSessionManager.kt     # In-memory & SharedPreferences session engine
│       └── mock/
│           └── UnitedMockData.kt         # Comprehensive mock fixtures for Arunjyoti Changkakoty
│
├── feature/                              # Decoupled Feature Modules
│   ├── auth/                             # SIM Binding, SMS OTP verification, Phone registration
│   ├── home/                             # Home Dashboard & User Profile
│   │   ├── ui/HomeScreen.kt              # Atmospheric header, Quick Actions matrix, Money Control
│   │   ├── profile/ProfileScreen.kt      # Account settings & UPI IDs
│   │   └── profile/ChangeMpinScreen.kt   # 5-step NPCI MPIN reset flow
│   ├── payment/                          # UPI Payment Flow
│   │   ├── scanner/QrScannerScreen.kt    # CameraX live QR scanner
│   │   ├── SendMoneyScreen.kt            # Amount entry, Bank selector, MPIN trigger
│   │   └── PaymentSuccessScreen.kt       # Verified payment receipt & share intent
│   ├── passbook/                         # Passbook & Financial Ledger
│   │   └── PassbookScreen.kt             # Transaction history, search, category filters
│   └── soundbox/                         # Voice Announcement Engine
│       └── SoundboxAnnouncementService.kt# Regional audio announcements (Assamese, Bengali, English)
│
└── docs/                                 # Architecture Specifications & Rulebooks
    ├── AI_DEVELOPMENT_GUIDELINES.md      # Enforceable UI, token, security, and AI rules
    ├── DESIGN_SPECIFICATION.md           # Visual design tokens & screenshot alignments
    ├── PRODUCTION_GUIDELINES.md          # Security standards, crash guards, and SDK policies
    └── ARCHITECTURE.md                   # Distributed backend & mobile module blueprint
```

---

## 4. Mock Data Engine: Testing with Arunjyoti Changkakoty

Because the physical UnitedPay Core Banking Switch is under development, all modules operate against a centralized **Deterministic Sandbox Engine**.

### Primary Test Identity
- **Full Name**: `Arunjyoti Changkakoty`
- **Registered Phone**: `+91 6002239926` (`UserSessionManager.SIM_1_PHONE`)
- **Primary UPI ID (VPA)**: `arunjyoti@unitedpay`
- **Primary Linked Bank**: State Bank of India (`•••• 4821`, IFSC: `SBIN0001234`)
- **Secondary Linked Bank**: HDFC Bank (`•••• 9012`, IFSC: `HDFC0005678`)
- **Virtual Debit Card**: `•••• •••• •••• 4821` (Valid Thru: `08/28`)

### Dynamic State Persistence
Every transaction you perform in the app (Send Money, Add Money, Bill Pay) directly mutates `UserSessionManager`:
1. **Wallet & Account Balance**: Updates in real-time on the Dashboard.
2. **Passbook Ledger**: Instantly appends the new transaction receipt to History.
3. **Soundbox Audio**: Speaks the confirmation in your selected regional language.

---

## 5. Future UnitedPay SDK & Backend Integration Pipeline

How does the mobile app connect with the backend and how will the SDK plug in?

```
┌────────────────────────────────────────┐
│     Mobile UI (Jetpack Compose)        │
│  HomeScreen, SendMoneyScreen, etc.     │
└───────────────────┬────────────────────┘
                    │ Observes StateFlow
┌───────────────────▼────────────────────┐
│          Feature ViewModel             │
│  PaymentViewModel, PassbookViewModel   │
└───────────────────┬────────────────────┘
                    │ Calls Domain Interface
┌───────────────────▼────────────────────┐
│      PaymentRepository (Interface)      │
│  transferUpi(), getBalance(), etc.     │
└─────────┬────────────────────┬─────────┘
          │                    │
┌─────────▼────────┐  ┌────────▼────────────────┐
│ Current Mock     │  │ Future Live Integration │
│ MockFintechClient│  │ UnitedPaySdkManager     │
│ (Arunjyoti Mock) │  │ (AAR / Switch REST API) │
└──────────────────┘  └─────────────────────────┘
```

### Zero-Friction SDK Plug-In Process:
When the live UnitedPay Core Switch SDK is released:
1. Copy `UnitedPaySDK.aar` into `:core:network/libs/`.
2. Implement `UnitedPaySdkPaymentRepositoryImpl` wrapping `UnitedPaySdkManager`.
3. In your dependency injection layer, bind `PaymentRepository` to the new implementation.
4. **No UI Composable, Screen, or Navigation Graph requires any change.**

---

## 6. Architectural & AI Development Rulebooks

Before contributing code or working with AI agents, consult the authoritative documentation:
- [AI Development Guidelines & Architectural Rulebook](docs/AI_DEVELOPMENT_GUIDELINES.md) *(Mandatory reading for AI & devs)*
- [Visual Design Specification](docs/DESIGN_SPECIFICATION.md)
- [Production Security & Scalability Guidelines](docs/PRODUCTION_GUIDELINES.md)
- [Distributed & Mobile Architecture](docs/ARCHITECTURE.md)
