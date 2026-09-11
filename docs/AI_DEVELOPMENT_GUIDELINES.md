# UnitedPay — AI Development Guidelines & Architectural Rulebook

> **Document Status**: Enforceable Standard (Active)  
> **Target Audience**: AI Agents (Antigravity, Claude, Copilot, GPT), Staff Engineers, and Mobile Developers  
> **Ecosystem**: UnitedPay UPI 2.0 Android App, UnitedPay SDK Bridge, and Mock Banking Switch  

---

## 1. Core Operating Principles & AI Guardrails

All AI agents and contributing engineers operating on this codebase must strictly adhere to the following non-negotiable guardrails:

### 1.1 Phase 1 First (Read Before Modifying)
- **Zero Immediate Overwrites**: Never edit or overwrite source code files upon receiving a prompt without first inspecting related modules, interfaces, and design tokens.
- **Dependency Mapping**: Map dependencies across `:app`, `:core:designsystem`, `:core:model`, and `:feature:*` to preserve architectural separation.
- **Trace State Machines**: Before altering payment or verification flows, trace the complete state machine (Input -> Validation -> Repository -> Security Layer -> UI StateFlow).

### 1.2 Strict Guardrail Against Destructive AI Overwrites
- **No Truncated Snippets**: Never replace complete files with partial code containing placeholders like `// ... rest of code unchanged` or `/* existing methods */`.
- **Surgical Scoping**: Every edit must be surgical, preserving all existing functions, comments, docstrings, and financial audit metadata.
- **Zero Regressions**: Ensure changes in one module do not break dependencies in consumer feature modules.

### 1.3 Radical Simplification (The 1000-to-3 Line Rule)
- **Eliminate Over-Engineering**: Hunt for bespoke custom logic that spans hundreds of lines where Jetpack Compose standard primitives, Kotlin standard library utilities, or core design system components achieve the exact same result in 2–3 idiomatic lines.
- **No Duplicate Logic**: If a layout, button, card, or input field is needed, reuse the centralized component from `:core:designsystem` rather than rewriting custom composables.

### 1.4 Strict Zero-Emoji Mandate & Professional Vector Standard
- **Zero Unicode Emojis**: Absolutely **NO Unicode emojis** (e.g. smileys, cards, locks, phones, checkmarks) are permitted in Compose UI text, XML drawables, string resources, or error messages.
- **Vector Standard**: All visual indicators, badges, and icons must be implemented using:
  1. `com.unitedpay.core.designsystem.components.UnitedIcons` (`ImageVector`).
  2. Android Vector Drawables (`res/drawable/*.xml`).
  3. Consistent stroke weights (1.5dp–2.0dp) and brand palette colors.

---

## 2. Design Tokens & Theme Constitution

All visual elements must strictly derive from `com.unitedpay.core.designsystem.theme`. **Hardcoded hex colors (`Color(0xFF...)`) in feature modules are strictly forbidden.**

### 2.1 Color Palette Reference (`Color.kt`)

```kotlin
package com.unitedpay.core.designsystem.theme

// Primary Brand Identity (Derived from official United Pay 'U' crest)
val UnitedMoneyBlue       = Color(0xFF0078DF) // Core brand blue (#0078DF)
val UnitedRoyalBlue       = Color(0xFF0078DF) // Uniform platform blue
val UnitedDeepBlue        = Color(0xFF0242D6) // Gradient base
val UnitedShieldCyan      = Color(0xFF00A0E2) // Gradient crest glow
val UnitedMidnightNavy    = Color(0xFF030D26) // Dark backgrounds & modal overlays
val UnitedObsidian        = Color(0xFF121620) // Deep container borders

// Atmospheric & Greeting Accents
val UnitedLimeAccent      = Color(0xFFC4D852) // Welcome Back greeting text & action arrows
val UnitedLimePill        = Color(0xFFADC768) // PAY TO CONTACT button fill
val UnitedAccentGold      = Color(0xFFFFB800) // Cashbacks, rewards & NPCI badges
val UnitedGoldGlow        = Color(0xFFFFE27D) // Shimmer highlights

// Surfaces, Canvas & Glassmorphism
val UnitedWhite           = Color(0xFFFFFFFF) // Pure white card surfaces
val UnitedCanvasLight     = Color(0xFFF5F7FB) // Main dashboard scroll canvas
val UnitedBackgroundLight = Color(0xFFF4F7FC) // Screen background
val UnitedBorderLight     = Color(0xFFE2E8F0) // 1dp hairline border
val UnitedSurfaceSubtle   = Color(0xFFEDF2F9) // Form input background

// Semantic Typography
val UnitedTextPrimary     = Color(0xFF0F172A) // Headlines, titles, balances
val UnitedTextSecondary   = Color(0xFF64748B) // Subtitles, hints, timestamps

// Financial Semantics
val UnitedSuccess         = Color(0xFF00C853) // Completed transactions, verified badges
val UnitedSuccessContainer= Color(0xFFE8F8EE) // Success badge pill background
val UnitedPending         = Color(0xFFFF9100) // Processing & bank switch timeouts
val UnitedPendingContainer= Color(0xFFFFF4E5) // Warning/Pending background
val UnitedError           = Color(0xFFD50000) // Failed transactions, invalid fields
val UnitedErrorContainer  = Color(0xFFFDEAEA) // Error alert background
```

### 2.2 Gradients & Glassmorphism

```kotlin
// Header Atmospheric Gradient
val UnitedHeaderGradient = Brush.verticalGradient(
    colors = listOf(UnitedMoneyBlue, Color(0xFF0090E6), UnitedShieldCyan)
)

// Glassmorphism Brushes
val UnitedGlassSurfaceBrush = Brush.verticalGradient(
    colors = listOf(Color.White.copy(alpha = 0.94f), Color.White.copy(alpha = 0.88f))
)
val UnitedGlassBorderBrush = Brush.verticalGradient(
    colors = listOf(Color.White.copy(alpha = 0.95f), Color.White.copy(alpha = 0.40f))
)
```

---

## 3. Mobile Responsiveness & Viewport Inset Defense

To guarantee seamless rendering on devices ranging from compact 360dp widths to tall edge-to-edge displays:

### 3.1 Defensive Screen Padding Rules
1. **No Side-Attaching Buttons**: Every interactive CTA button must have at least 16dp horizontal padding (`Modifier.padding(horizontal = 16dp)`). Full-width elements must never clip or stick to physical screen edges.
2. **System Bars Insets**:
   - Headers and top-level screens must apply `Modifier.statusBarsPadding()`.
   - Bottom bars, footers, and keypad panels must apply `Modifier.navigationBarsPadding()`.
3. **IME (Keyboard) Defense**:
   - Input screens must account for the software keyboard using `Modifier.imePadding()`.

### 3.2 Standard Responsive Layout Template
When building screens with inputs and bottom action buttons, follow the **Scrollable Body + Pinned Footer** pattern:

```kotlin
@Composable
fun UnitedFormScreen(onProceed: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UnitedBackgroundLight)
            .statusBarsPadding()
    ) {
        UnitedTopAppBar(title = "Payment Details", onBackClick = { /* navigate back */ })

        // 1. Scrollable Upper Content (Never gets clipped on small screens)
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UnitedTextField(value = text, onValueChange = { text = it }, label = "Beneficiary VPA")
            // Additional form fields...
        }

        // 2. Pinned Bottom CTA (Always visible above navigation bar & keyboard)
        UnitedPinnedBottomBar {
            UnitedPrimaryButton(
                text = "CONTINUE",
                onClick = onProceed,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```

---

## 4. Reusable Component Catalog (`:core:designsystem`)

Never write duplicate buttons, inputs, cards, or PIN sheets. Always import and reuse the official components:

### 4.1 Button Hierarchy
| Component | Function Signature | Usage Guidelines |
| :--- | :--- | :--- |
| `UnitedPrimaryButton` | `text: String, onClick: () -> Unit, enabled: Boolean = true` | Primary 52dp call-to-action button in `UnitedMoneyBlue` with 8dp rounded corners and non-shrinking height. |
| `UnitedSecondaryButton` | `text: String, onClick: () -> Unit, enabled: Boolean = true` | Outlined 52dp secondary button with 1.5dp border in `UnitedMoneyBlue`. |
| `UnitedChip` | `text: String, isSelected: Boolean, onClick: () -> Unit` | Amount selection pills (+₹100, +₹500, +₹1,000) with 6dp corner radius. |

### 4.2 Form & Navigation Primitives
| Component | Key Features | Usage Guidelines |
| :--- | :--- | :--- |
| `UnitedTextField` | Fixed height, single-line enforcement, ellipsis on overflow, subtle background. | Standard text, phone, account number, and amount inputs. |
| `UnitedSearchField` | 50dp fixed height, search icon, 1-tap clear button, single-line. | Contact search, IFSC lookup, and transaction filtering. |
| `UnitedTopAppBar` | Explicit dark back arrow tint (immune to OS dark mode), centered/start title. | Standard top bar across all secondary and flow screens. |
| `UnitedPinnedBottomBar` | Pinned footer with 8dp shadow, 0.5dp border, `navigationBarsPadding` + `imePadding`. | Container for CTAs and submit buttons across all forms. |
| `UnitedGlassCard` | Frosted glassmorphism background, subtle blue ambient drop shadow, 24dp radius. | Elevated dashboard widgets and quick-action containers. |
| `UnitedNpciMpinSheet` | Scrambled keypad, secure `CharArray` submission, 6-digit strict dots. | NPCI UPI MPIN authentication bottom sheet. |

---

## 5. Universal Toast Notification Policy

All user alerts, feedback, network errors, and copy confirmations must strictly use the unified **UnitedToast** component.

### 5.1 Rules
1. **Never use Android raw Toasts**: `Toast.makeText(...)` is strictly forbidden.
2. **Never use black system notification banners**: Alerts must follow the brand design system.
3. **Bottom Placement**: Toasts slide up smoothly from the bottom, centered above the navigation bar.

### 5.2 Toast API Reference

```kotlin
import com.unitedpay.core.designsystem.components.UnitedToast

// Success Notification (e.g. UPI ID copied, transaction receipt downloaded)
UnitedToast.success("UPI ID copied to clipboard")

// Error Notification (e.g. Invalid VPA, Insufficient balance)
UnitedToast.error("Invalid UPI ID. Please check and retry.")

// Warning Notification (e.g. Daily limit threshold reached)
UnitedToast.warning("Approaching daily UPI limit of ₹1,00,000")

// Information Notification (e.g. Auto-read bank OTP)
UnitedToast.info("Bank OTP auto-detected")
```

---

## 6. Financial Integrity, Security & Validation

### 6.1 Screen & Memory Protection
- **`FLAG_SECURE`**: Must be enabled on all activities or window layers hosting MPIN entry, debit card details, or bank credentials to prevent screenshots and screen recording:
  ```kotlin
  window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
  ```
- **In-Memory MPIN Sanitization**:
  - MPINs must never be stored in immutable `String` objects.
  - Capture MPINs as `CharArray` and zero them out immediately after hashing/submitting:
    ```kotlin
    java.util.Arrays.fill(mpinChars, '0')
    ```

### 6.2 Strict Field Validations
- **UPI MPIN**: Strictly 6 numeric digits (`pin.length == 6 && pin.all { it.isDigit() }`). Never use 4 digits.
- **UPI VPA / ID**: Validated via strict regex:
  ```kotlin
  val VPA_REGEX = Regex("^[a-zA-Z0-9.\-_]{2,256}@[a-zA-Z]{2,64}$")
  ```
- **Debit Card**: Last 6 digits validated via numeric length; Expiry MM/YY validated with MM between 01–12 and YY >= current year.
- **Monetary Amounts**: Calculations and balances must strictly use `BigDecimal` or fixed-point formatting. Never use `Float` or `Double` for financial arithmetic to avoid IEEE 754 precision errors.

### 6.3 PII Masking in Logs & UI
- Bank Account Numbers: Mask all but the last 4 digits (`•••• 4821`).
- Card Numbers: Mask all but the last 4 digits (`•••• •••• •••• 0000`).
- Phone Numbers: Mask the middle digits (`+91 98•••• 1234`).

---

## 7. Mock Data Engine & Future SDK Integration Architecture

### 7.1 Single Test Identity: Arunjyoti Changkakoty
Because the live UnitedPay Core Banking & Switch SDK is in progress, the application operates in a fully functional **Deterministic Sandbox Mode**.
All features, transactions, balances, and cards must link exclusively to **Arunjyoti Changkakoty**:

| Parameter | Sandbox Specification | Source of Truth |
| :--- | :--- | :--- |
| **Full Name** | Arunjyoti Changkakoty | `UserSessionManager.currentSession` |
| **Phone Number** | `+91 6002239926` (`SIM_1_PHONE`) | `UserSessionManager.SIM_1_PHONE` |
| **Primary VPA** | `arunjyoti@unitedpay` | `UserSessionManager.currentSession.primaryVpa` |
| **Primary Bank** | State Bank of India (A/C: `•••• 4821`, IFSC: `SBIN0001234`) | `UnitedMockData.linkedBankAccounts[0]` |
| **Secondary Bank**| HDFC Bank (A/C: `•••• 9012`, IFSC: `HDFC0005678`) | `UnitedMockData.linkedBankAccounts[1]` |
| **Wallet Balance**| Dynamic persistent balance | Updated on debit/credit via `UserSessionManager` |
| **Passbook Ledger**| Synchronized transaction history | Appended dynamically on payment completion |
| **Soundbox Engine**| Real-time multi-lingual speech output | Triggered on successful transaction settlement |

### 7.2 Zero-Friction SDK Pluggability Architecture
The application is strictly decoupled using Clean Architecture interfaces:

```
┌────────────────────────────────────────┐
│     Presentation Layer (Compose)       │
│  Home, Payment, Passbook, Soundbox     │
└───────────────────┬────────────────────┘
                    │ Observes StateFlow<UiState>
┌───────────────────▼────────────────────┐
│      Domain Layer / ViewModels         │
│  PaymentViewModel, PassbookViewModel   │
└───────────────────┬────────────────────┘
                    │ Depends on Interface
┌───────────────────▼────────────────────┐
│      PaymentRepository (Interface)      │
│  transferUpi(), getBalance(), etc.     │
└─────────┬────────────────────┬─────────┘
          │                    │
┌─────────▼────────┐  ┌────────▼──────────────┐
│ Current Sandbox  │  │ Future Live SDK       │
│ MockFintechClient│  │ UnitedPaySdkManager   │
│ (Arunjyoti Mock) │  │ (AAR / REST Switch)   │
└──────────────────┘  └───────────────────────┘
```

When the live `UnitedPaySDK.aar` is introduced:
1. Implement `UnitedPaySdkPaymentRepositoryImpl` conforming to `PaymentRepository`.
2. Update the Hilt / Koin dependency injection module to bind `PaymentRepository` to the SDK implementation.
3. **Zero UI code or Composable screens will require modification.**

---

## 8. Verification & Quality Acceptance Checklist

Before submitting code, verify:
- [ ] No hardcoded hex colors outside `:core:designsystem:theme`.
- [ ] No raw Unicode emojis present in UI or strings.
- [ ] All interactive buttons have 16dp horizontal padding and never touch screen borders.
- [ ] Upper content is scrollable and bottom CTAs/keypads remain pinned and fully visible.
- [ ] Notifications use `UnitedToast` sliding up from the bottom.
- [ ] MPIN is strictly 6 numeric digits.
- [ ] Sandbox operations reflect seamlessly against user **Arunjyoti Changkakoty**.
- [ ] Clean compilation with `./gradlew assembleDebug`.
