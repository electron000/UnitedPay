# UNITED PAY — MASTER ARCHITECTURAL AUDIT & ENTERPRISE REFACTORING DIRECTIVE
### Authored by: Principal Mobile Fintech Architect (20+ Years Tier-1 Banking & UPI Systems)
### Target System: United Pay (`com.unitedpay.upi`) — Android Jetpack Compose Multi-Module Architecture

---

## MISSION STATEMENT & OBJECTIVE

You are acting as an elite **Principal Mobile Fintech Software Architect & Staff Android Engineer with 20+ years of production experience** leading mission-critical engineering at top-tier financial technology firms (Google Pay, Stripe, Revolut, Paytm, Cash App). 

Your objective is to conduct a **comprehensive, end-to-end architectural audit and structural refactoring** of the entire United Pay codebase. You must eliminate all architectural anti-patterns, dismantle monolithic "god files", rebuild the directory structure into a modular, clean feature-by-feature hierarchy, eliminate dead code, optimize Jetpack Compose recomposition performance, establish 100% theme consistency across Android Dark and Light modes, modernize the visual aesthetics with crisp, executive bank-grade styling (including reduced border radiuses), and strictly enforce all security and regulatory guidelines defined in the project's documentation (`docs/PRODUCTION_GUIDELINES.md` and `docs/DESIGN_SPECIFICATION.md`).

Every line of code you produce must be production-ready, crash-proof, type-safe, and capable of processing millions of concurrent high-value UPI transactions with zero unmonitored failures.

---

## 1. COMPREHENSIVE CODEBASE AUDIT & STRUCTURAL DECOMPOSITION

### 1.1 The Monolithic "God Files" Must Be Dismantled
The current codebase suffers from massive multi-thousand-line files that bundle disparate transactional domains, causing tight coupling, slow compilation times, high merge collision risks, and unmanageable state. You must decompose these files into dedicated, single-responsibility files (max 250–350 lines each) organized by domain:

1. **`DedicatedServicesScreens.kt` (~3,025 lines)**
   - Decompose into domain-driven sub-packages under `feature/home/src/main/java/com/unitedpay/feature/home/services/`:
     - `recharge/`: `MobileRechargeScreen.kt`, `FastagRechargeScreen.kt`, `MetroSmartCardScreen.kt`
     - `utilities/`: `ElectricityBillScreen.kt`, `DthCableScreen.kt`, `PipedGasScreen.kt`, `WaterTaxScreen.kt`, `BroadbandScreen.kt`
     - `transfers/`: `BankTransferScreen.kt`, `SelfTransferScreen.kt`, `UpiIdTransferScreen.kt`
     - `finance/`: `CreditCardPaymentScreen.kt`, `MutualFundsScreen.kt`, `DigitalGoldScreen.kt`, `InsuranceHubScreen.kt`
     - `lifestyle/`: `FlightBookingScreen.kt`, `TrainTicketScreen.kt`, `MovieTicketsScreen.kt`

2. **`MessagesAndNotificationsScreens.kt` (~2,104 lines)**
   - Decompose into `feature/home/src/main/java/com/unitedpay/feature/home/communications/`:
     - `messages/`: `MessagesScreen.kt`, `ConversationListItem.kt`, `ChatSearchBar.kt`
     - `chat/`: `ChatDetailScreen.kt`, `ChatBubbleComponents.kt`, `ChatTransactionCards.kt`
     - `panels/`: `ChatPaySlideUpPanel.kt`, `ChatRequestSlideUpPanel.kt`
     - `notifications/`: `NotificationsScreen.kt`, `NotificationItem.kt`, `PromoAlertCard.kt`

3. **`FintechServiceScreens.kt` (~1,382 lines)**
   - Decompose remaining auxiliary service views into their corresponding domain packages in `services/`.

4. **`ProfileSubScreens.kt` (~1,039 lines)**
   - Decompose into `feature/home/src/main/java/com/unitedpay/feature/home/profile/`:
     - `qr/`: `MyQrScreen.kt`, `QrGeneratorHelper.kt` (ZXing Level H, MediaStore Q-scoped storage)
     - `security/`: `BiometricsScreen.kt`, `ChangeMpinScreen.kt`
     - `preferences/`: `SoundboxLanguageScreen.kt`, `AppPreferencesScreen.kt`
     - `support/`: `NpciDisputeCenterScreen.kt`, `TransactionDetailSheet.kt`

5. **`CardsScreen.kt` & `OnboardingCardScreen.kt`**
   - Decompose into `feature/home/src/main/java/com/unitedpay/feature/home/cards/`:
     - `CardsHubScreen.kt`: Main cards management dashboard
     - `components/VirtualCard3D.kt`: Responsive virtual debit/credit card with independent rows for credentials (`VALID THRU`, `CVV`) and cardholder/network badge
     - `components/CardActionPills.kt`: Freeze, Show/Hide CVV, Limits, Reset PIN
     - `components/LinkedBankSection.kt`: Primary and secondary account tiles

---

### 1.2 Dead Code, Redundant Utilities & Legacy Dialog Elimination
Perform an aggressive tree-shake and cleanup across the repository:
1. **Remove `FintechFlowsDialogs.kt`**: All modal dialogs have been superseded by dedicated full-screen flows and multi-step slide-up bottom panels. Delete or archive this file and remove all orphan references.
2. **Eliminate Deprecated API Usages**:
   - Replace all `Icons.Filled.ArrowBack` with `Icons.AutoMirrored.Filled.ArrowBack`.
   - Replace all `Icons.Filled.Backspace` with `Icons.AutoMirrored.Filled.Backspace`.
   - Replace all `Icons.Filled.VolumeUp` with `Icons.AutoMirrored.Filled.VolumeUp`.
   - Replace `LocalLifecycleOwner` from `ui` package with `androidx.lifecycle.compose.LocalLifecycleOwner`.
3. **Remove Unused Imports & Compiler Warnings**: Strip all unreferenced imports, dead private functions, and uninstantiated mock data classes.

---

## 2. PROFESSIONAL FINTECH UI MODERNIZATION (EXECUTIVE TIER-1 LOOK)

### 2.1 Border Radius Discipline (Crisp, Executive Aesthetic)
Fintech applications handling enterprise funds cannot look like playful casual toys. Overly bubbly, excessive pill shapes (20dp–28dp) undermine perceived trust, authority, and bank-grade precision. You must systematically apply standard Tier-1 banking radii (inspired by Apple Pay, Stripe, Revolut, Chase Private Client):

| UI Element Type | Old Radius (Casual/Bubbly) | New Bank-Grade Radius (Executive) | Rationale |
| :--- | :--- | :--- | :--- |
| **Primary CTAs & Action Buttons** | `20dp` – `28dp` (Pills) | **`8.dp` – `10.dp`** | Delivers solid, grounded tactile authority |
| **Transaction & Service Cards** | `20dp` – `24dp` | **`12.dp` – `14.dp`** | Elegant, crisp content containers |
| **Input TextFields & Search Bars** | `20dp` – `24dp` | **`8.dp` – `10.dp`** | Clean alignment with standard system keyboards |
| **Modal Bottom Sheets (Top Corners)** | `24dp` – `32dp` | **`16.dp`** | Subtle modern sheet tuck, maximizing screen real estate |
| **Quick Action Badges & Filter Chips**| `16dp` – `20dp` | **`6.dp` – `8.dp`** | Compact, sharp data density |
| **Bank Account / Transaction Tiles** | `18dp` – `22dp` | **`10.dp` – `12.dp`** | Structured list rhythm with clean 1dp border strokes |
| **Virtual Debit/Credit Cards** | `22dp` | **`16.dp`** | Matches international ISO/IEC 7810 ID-1 card proportions |

*Note: Circular elements that represent user avatars (`CircleShape`) or floating center FABs remain circular as intended by system design.*

---

### 2.2 Theme Consistency, Dark Mode Inversion & Color Tokens
Ensure 100% adherence to design tokens from `docs/DESIGN_SPECIFICATION.md`:
1. **Zero White-on-White Text Collisions**:
   - Every `TextField`, `OutlinedTextField`, and `BasicTextField` MUST explicitly configure `colors`:
     ```kotlin
     colors = OutlinedTextFieldDefaults.colors(
         focusedTextColor = UnitedTextPrimary,
         unfocusedTextColor = UnitedTextPrimary,
         cursorColor = UnitedMoneyBlue,
         focusedBorderColor = UnitedMoneyBlue,
         unfocusedBorderColor = UnitedBorderLight,
         focusedContainerColor = UnitedSurfaceSubtle,
         unfocusedContainerColor = UnitedSurfaceSubtle
     )
     ```
   - Never rely on default Material 3 `onSurface` for text inputs when running on Android Dark Mode against light card containers.
2. **Zero Dark Surface Modals**:
   - All `ModalBottomSheet` composables MUST explicitly set `containerColor = UnitedWhite` (or appropriate surface token) with `dragHandle = { BottomSheetDefaults.DragHandle(color = UnitedBorderLight) }`.
3. **No Hex Inlining**:
   - Replace raw hexadecimal values with semantic design tokens defined in `:core:designsystem:theme`:
     - Primary Brand: `UnitedMoneyBlue` (`#1B4DB8`), `UnitedHeaderBlueDark` (`#1A4CB8`), `UnitedHeaderBlueLight` (`#2D6DEC`)
     - Backgrounds: `UnitedCanvasLight` (`#F5F7FB`), `UnitedWhite` (`#FFFFFF`), `UnitedSurfaceSubtle` (`#F8FAFC`)
     - Typography: `UnitedTextPrimary` (`#0F172A`), `UnitedTextSecondary` (`#64748B`)
     - Accents: `UnitedSuccess` (`#16A34A`), `UnitedLimeAccent` (`#C4D852`), `UnitedAccentGold` (`#FFB800`)
4. **Strict Zero-Emoji Mandate**:
   - Verify that NO Unicode emojis exist in buttons, headers, cards, or notifications. All visual indicators must use crisp Compose `ImageVector` or Android Vector XML assets.

---

## 3. RESPONSIVENESS & ADAPTIVE LAYOUT ARCHITECTURE

### 3.1 Card Credentials & Information Density
In all debit, credit, and gift card components (e.g. `VirtualCard3D`, `AddCardScreen`, `CardPromotionSection`):
1. **Multi-Row Hierarchy**:
   - Credentials (`VALID THRU` and `CVV`) must reside in their own dedicated horizontal row below the 16-digit card number.
   - `CARD HOLDER` name must occupy the bottom-left row with `Modifier.weight(1f, fill = false)`, `maxLines = 1`, and `overflow = TextOverflow.Ellipsis`.
   - The Payment Network badge (Mastercard / RuPay / Visa) must anchor the bottom-right corner.
   - Never place 4 unconstrained columns in a single row.
2. **Adaptive Screen Width Scaling**:
   - Support screen densities from compact 320dp widths up to 600dp+ foldables/tablets without clipping or wrapping digits.
   - Use `weight`, flex layouts, or auto-scaling text modifiers for monospace card digits (`1234  5678  9000  0000`).

---

## 4. NAVIGATION, STATE HYGIENE & CONNECTIVITY INTEGRITY

### 4.1 Navigation Graph Hygiene (`AppNavGraph.kt`)
1. **Type-Safe Argument Passing**:
   - Ensure all routes accept strongly typed or properly encoded navigation arguments (e.g., `payment?vpa={vpa}&name={name}`).
   - URL-encode any special characters or whitespace in query parameters to prevent URI parsing exceptions.
2. **Backstack Lifecycle & Memory Management**:
   - Top-level bottom bar tab destinations (`Home`, `Cards`, `QrScanner`, `Passbook`, `Profile`) must utilize `popUpTo(Screen.Home.route)` with `saveState = true`, `launchSingleTop = true`, and `restoreState = true` to prevent memory leaks and redundant backstack creation.
   - Flow completion screens (e.g., payment success) must pop the intermediate PIN and confirmation steps so pressing Android System Back never re-prompts for authorization.

### 4.2 Compose Recomposition & Performance
1. **State Hoisting & Immutability**:
   - Annotate all UI State data classes with `@Immutable` or `@Stable` to allow Compose compiler smart skipping.
   - Wrap heavy computations (QR generation, cryptographic hashing, transaction filtering) in `remember(keys)` or offload to ViewModels on `Dispatchers.Default`.
2. **Lazy List Optimization**:
   - Every `LazyColumn` and `LazyRow` must provide a unique, stable `key = { item.id }` in `items()` calls to prevent full list recomposition during state mutations.
   - Use `derivedStateOf` for scroll position tracking and sticky headers.

---

## 5. SECURITY & REGULATORY DIRECTIVES (MANDATORY FROM PROJECT GUIDELINES)

Strictly integrate and maintain the security rules documented in `docs/PRODUCTION_GUIDELINES.md`:

1. **Screen Capture Protection (`FLAG_SECURE`)**:
   - Enforce `WindowManager.LayoutParams.FLAG_SECURE` on all activities/screens displaying sensitive data (MPIN entry, CVVs, balance inquiry, QR display).
2. **Memory Hygiene & Zero Plaintext PII**:
   - MPINs, Passwords, and OTPs MUST NEVER be stored in immutable `String` objects.
   - Use `CharArray` and immediately sanitize the buffer with `java.util.Arrays.fill(pinArray, '0')` inside a `finally` block once submitted.
   - Strip all sensitive data (phone numbers, account numbers, amounts) from logs. Ensure no `Log.d` or `println` outputs exist in release builds.
3. **Real ISO-Compliant UPI QR Code Engine**:
   - Maintain the ZXing `QRCodeWriter` engine generating authentic `upi://pay?pa=...&pn=...&cu=INR` QR matrices with **Level H (30%) error correction** to guarantee scannability even when adorned with the central brand shield emblem.
   - All gallery export logic must comply with Android 10+ (Q) MediaStore scoped storage (`Pictures/UnitedPay`).
4. **Offline Resilience & Idempotency**:
   - All transaction and payment requests must generate a cryptographically secure `UUIDv4` idempotency key (`X-Idempotency-Key`) to prevent double-debits on network drops.
   - Attach a `CoroutineExceptionHandler` to all ViewModel coroutines to catch unexpected network timeouts or switch drops without crashing the UI.

---

## 6. EXECUTION & VERIFICATION WORKFLOW

When executing this prompt:
1. **Step 1 — Code Inventory & Architectural Plan**: Review all source files in `:feature:home`, `:feature:payment`, `:core:designsystem`, and `:app`, listing every target decomposition and package reorganization.
2. **Step 2 — Package & File Restructuring**: Move and decompose the giant files into their respective sub-packages, updating build dependencies and import statements cleanly.
3. **Step 3 — UI Token & Border Radius Refactoring**: Apply the executive radii (`8.dp` buttons, `12.dp` cards, `16.dp` sheets) and ensure 100% theme color consistency.
4. **Step 4 — Build & Static Analysis**: Run `./gradlew :app:assembleDebug` with zero warnings, zero deprecations, and zero syntax errors.
5. **Step 5 — Physical Device Deployment & Live Verification**: Install the APK onto the connected test device via ADB, capture high-resolution screenshots of the refreshed screens (Home, Cards, Chat Detail, Services), and verify responsiveness across all device dimensions.

---
*Follow these instructions with absolute fidelity. Produce clean, modular, idiomatic Kotlin code that represents the pinnacle of modern Android fintech engineering.*
