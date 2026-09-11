# United Pay — Visual Design Specification & UI System

> **Brand Identity**: *United Pay ("UP")*  
> **Tagline**: *"North-East India's First UPI Payment App — Simple | Secure | Instant"*  
> **Target Experience**: Bank-grade security, instant transactions, pristine modern UI with frosted glassmorphism, royal cobalt blue gradients, and zero emojis.
> **Design References**: Directly conforming to user assets in `c:\Users\Acer\Desktop\UnitedPay\Imaged\`:
> - `Logo.jpg`: Official Shield ("U") + Concentric Track Monogram ("P").
> - `images.jpg`: "North-East India's First UPI App" launch celebration poster.
> - `Screenshot 2026-09-10 154655.png`: High-resolution dashboard top & quick action matrix.
> - `Screenshot 2026-09-10 154737.png`: Two-screen system ("YOUR CARD YOUR CONTROL" Onboarding & Full Dashboard with Bottom Nav).

---

## 1. Strict Zero-Emoji Mandate & Professional Vector Standard

### 1.1 Policy Definition
* **Zero Emojis Permitted**: Absolutely NO Unicode emojis (no smiling faces, lightning bolts, credit cards, phones, locks, boxes, or symbols) are allowed anywhere in the United Pay application, design system, layouts, documentation, or codebase.
* **Vector Iconography Standard**: Every single icon, badge, utility service, or action indicator must be implemented using:
  1. High-fidelity Compose `ImageVector` paths.
  2. Professional Android Vector Drawables (`<vector>` XML).
  3. Clean mathematical geometry and single-line / duotone strokes.
* **Icon Aesthetic**:
  - Thin to medium stroke width (1.5dp – 2dp).
  - Consistent bounding box (24x24dp standard, 28x28dp for prominent actions).
  - Precision rounded joins and clean geometric terminals.
  - Colors: Deep Royal Blue (`#1E50C8`), Pure White (`#FFFFFF`), Soft Gold (`#FFB800`), or Chartreuse Lime (`#C4D852`).

---

## 2. Color Palette & Design Tokens

The color palette is extracted directly from the reference screenshots and brand assets:

### 2.1 Primary & Gradient Palette

| Design Token | Hex Code | Description | Usage |
| :--- | :--- | :--- | :--- |
| `UnitedHeaderBlueDark` | `#1A4CB8` | Deep Cobalt Blue | Top gradient start (status bar & header) |
| `UnitedHeaderBlueLight` | `#2D6DEC` | Radiant Royal Blue | Top gradient end (header glow) |
| `UnitedLimeAccent` | `#C4D852` | Chartreuse / Lime Green | "Welcome Back!" greeting text, diagonal arrows |
| `UnitedLimePill` | `#ADC768` | Olive / Lime Soft Pill | "PAY TO CONTACT" quick action pill button |
| `UnitedMoneyBlue` | `#1B4DB8` | Vibrant Cobalt / Navy | "Money Control" 4 circular action buttons |
| `UnitedCardSheen` | `#8CAEE6` | Card Glass Sheen | 3D Card frosted surface glow |
| `UnitedMidnightNavy` | `#030D26` | Deep Midnight Navy | Dark mode, security sheet backdrops |
| `UnitedShieldCyan` | `#00A3FF` | Electric Cyan | Shield "U" gradient, active scanner reticle |
| `UnitedAccentGold` | `#FFB800` | Festive Gold | NPCI verified emblems, crowns, cashbacks |

### 2.2 Surface, Glassmorphism & Canvas Palette

| Design Token | Hex Code / Alpha | Purpose |
| :--- | :--- | :--- |
| `UnitedGlassCard` | `#FFFFFF` (92% opacity) | Floating "Quick Actions" frosted card |
| `UnitedCanvasLight` | `#F5F7FB` | Main dashboard content background |
| `UnitedBorderSubtle` | `#E2E8F0` | 1dp crisp card and divider stroke |
| `UnitedTextDark` | `#0F172A` | Primary typography ("Quick Actions", "Money Control") |
| `UnitedTextMuted` | `#64748B` | Secondary subtitles, timestamps, account masks |
| `UnitedCardBackground` | `#1D4ED8` | Primary 3D Virtual Card background gradient |

---

## 3. Typography System

| Style | Font Weight | Size | Line Height | Usage |
| :--- | :--- | :--- | :--- | :--- |
| `DisplayHero` | ExtraBold (800) | 32sp | 38sp | "YOUR CARD YOUR CONTROL" onboarding headline |
| `GreetingAccent` | SemiBold (600) | 13sp | 18sp | "Welcome Back!" lime green greeting |
| `UserTitle` | Bold (700) | 22sp | 28sp | User name in top header ("Vikas Morvadiya") |
| `SectionHeader` | Bold (700) | 16sp | 22sp | "Quick Actions", "Money Control" section titles |
| `ActionLabel` | Medium (500) | 11sp | 14sp | Service labels ("Crypto", "Recharge", "Electricity") |
| `MoneyControlLabel`| SemiBold (600) | 12sp | 16sp | Labels beneath circular buttons ("Pay Money") |
| `PillButtonText` | Bold (700) | 11sp | 14sp | "PAY TO CONTACT", "+ Add Your Card" text |
| `CardNumber` | SemiBold (600) | 15sp | 20sp | Monospace / Tabular card digits (`1234 5678 9000 0000`) |

---

## 4. UI Screen Architecture (Conforming to Screenshots)

### Screen A: Onboarding & Virtual Card Showcase (Screenshot 2 - Left)
* **Header**:
  - United Pay brand monogram ("UP") in top-left with neon lime accent.
* **Hero Section**:
  - Giant high-impact headline: **"YOUR CARD\nYOUR CONTROL"** in bold sans-serif dark navy.
  - Subtitle: *"Anytime, Anywhere"* in muted slate grey.
* **3D Perspective Card**:
  - Elevated floating debit/credit card rendered at a dynamic 15-degree isometric angle.
  - Subtle frosted shadow and organic 3D wave backdrop.
  - Card Details:
    - Chip icon and Contactless wave icon (`)))`).
    - Embossed 16-digit card number: `1234 5678 9000 0000`.
    - Cardholder Name: `VIKAS MORVADIYA`.
    - Valid Thru: `08/26`.
    - United Pay & RuPay / Mastercard branding emblem.
* **Primary Bottom CTA**:
  - Full-width pill button: **"GET STARTED"** in deep royal blue (`#1B4DB8`), launching the user into the main dashboard.

---

### Screen B: Main Glassmorphic Dashboard (Screenshot 1 & Screenshot 2 - Right)

#### Section 1: Top Atmospheric Gradient Header
* **Background Canvas**:
  - Smooth vertical gradient: `#1A4CB8` down to `#2D6DEC`, softening into a frosty haze transitioning to `#F5F7FB`.
* **Top Navigation Row**:
  - Left: 4-Square App Menu Icon (crisp rounded squares in white stroke).
  - Right:
    - Chat Bubble icon with unread badge dot.
    - Notification Bell icon with unread badge dot.
* **Greeting & Quick Contact Action**:
  - Left column:
    - Accent subtitle: *"Welcome Back!"* in lime green (`#C4D852`).
    - Primary title: *"Vikas Morvadiya"* in crisp bold white (`22sp`).
  - Right column:
    - Pill button: **"PAY TO CONTACT"** with soft lime/olive background (`#ADC768`), rounded 20dp, padding 12dp horizontal x 6dp vertical.

#### Section 2: Floating "Quick Actions" Card
* **Card Surface**:
  - Elevated floating rounded card with 24dp corner radius, white background (`#FFFFFF`), subtle 3dp soft drop shadow, 1dp subtle border (`#E2E8F0`).
* **Header**:
  - Bold title: **"Quick Actions"** (`15sp`, `#0F172A`).
* **2x4 Vector Icon Grid**:
  - Each item consists of a 28dp vector line icon in royal blue (`#1E50C8`), 6dp vertical gap, and a clean label (`11sp`, `#0F172A`):
  1. **Crypto / e-Rupee**: Stack of cylindrical coins with ₹ currency imprint. Label: *Crypto*.
  2. **Recharge**: Smartphone outline with speaker notch & screen line. Label: *Recharge*.
  3. **TV Cable**: Monitor / Television outline with stand. Label: *TV Cable*.
  4. **Electricity**: Lightbulb silhouette with interior lightning filament. Label: *Electricity*.
  5. **Offers**: Scalloped discount badge / coupon with star cutout. Label: *Offers*.
  6. **Gift Cards**: Rectangular voucher card wrapped in a gift ribbon cross. Label: *Gift Cards*.
  7. **Rewards**: 3D-styled gift box with a top ribbon bow. Label: *Rewards*.
  8. **More**: 4-square grid menu icon. Label: *More*.

#### Section 3: "Money Control" Action Row
* **Section Title**:
  - **"Money Control"** in bold navy (`16sp`, `#0F172A`).
* **4 Elevated Circular Buttons**:
  - Arranged horizontally with equal spacing:
  1. **Pay Money**:
     - 60dp diameter circular button in deep cobalt blue (`#1B4DB8`).
     - Vector Icon: Diagonal top-right arrow `↗` in lime green (`#C4D852`).
     - Label: *Pay Money* (centered below).
  2. **Request Money**:
     - 60dp diameter circular button in deep cobalt blue (`#1B4DB8`).
     - Vector Icon: Diagonal bottom-left arrow `↙` in lime green (`#C4D852`).
     - Label: *Request Money* (centered below).
  3. **Add Money**:
     - 60dp diameter circular button in deep cobalt blue (`#1B4DB8`).
     - Vector Icon: Plus sign `+` in lime green (`#C4D852`).
     - Label: *Add Money* (centered below).
  4. **Check Balance**:
     - 60dp diameter circular button in deep cobalt blue (`#1B4DB8`).
     - Vector Icon: Bank / Treasury temple pillars in lime green (`#C4D852`).
     - Label: *Check Balance* (centered below).
* **Carousel Pager Dots**:
  - 3 pagination dots below the buttons (`• • •`), indicating additional swipeable action pages.

#### Section 4: Virtual Card & Bank Account Showcase
* **"Add Your Card" Floating Strip**:
  - White rounded card with soft border:
    - Left: Isometric 3D Bank & Vault vector illustration.
    - Right: Blue pill action button: **"+ Add Your Card"** (`#1B4DB8` background, white bold text).
* **3D Peeking Card**:
  - Styled perspective credit/debit card peeking out beneath the promo strip:
    - Gradient sheen: Cobalt to frosty light blue.
    - Card digits: `1234 5678 9000 0000`.
    - Cardholder: `VIKAS MORVADIYA`, Expiry: `08/26`.
    - RuPay / Mastercard dual circle emblem.

#### Section 5: North-East Regional Identity Strip
* Celebratory banner incorporating `images.jpg`:
  - Tagline: *"North-East India's 1st UPI App — Simple | Secure | Instant"*.
  - Gold crown accents and shield brand logo.

#### Section 6: Bottom Navigation Bar
* **Surface**:
  - Crisp white docked bottom bar with rounded top corners (16dp), top border (`#E2E8F0`), and soft elevation.
* **5 Navigation Items**:
  1. **Home**: Modern house icon with active black indicator & label.
  2. **Cards**: Stacked credit cards icon & label.
  3. **Center Scanner Action (Elevated FAB)**:
     - Prominent 52dp circular elevated button in royal blue (`#1B4DB8`) with glowing cyan rim.
     - Icon: Camera viewfinder / QR scan reticle brackets `[ - ]`.
     - Direct one-tap shortcut to live CameraX QR scanner.
  4. **History**: Ledger / transaction receipt icon & label.
  5. **Profile**: Person outline avatar icon & label.

---

## 5. Vector Iconography Implementation Specifications

To adhere strictly to the **Zero-Emoji Policy**, the following custom Compose `ImageVector` objects and vector drawables are registered in `:core:designsystem`:

```kotlin
object UnitedIcons {
    // Quick Actions
    val Crypto: ImageVector        // Coin cylinder stack with rupee symbol
    val Recharge: ImageVector      // Mobile device outline
    val TvCable: ImageVector       // Monitor outline with stand
    val Electricity: ImageVector   // Lightbulb with lightning bolt
    val Offers: ImageVector        // Perforated discount coupon badge
    val GiftCards: ImageVector     // Card with ribbon cross
    val Rewards: ImageVector       // Gift box with ribbon bow
    val More: ImageVector          // 4-item grid

    // Money Control
    val ArrowTopRight: ImageVector // Pay Money diagonal arrow
    val ArrowBottomLeft: ImageVector // Request Money diagonal arrow
    val Plus: ImageVector          // Add Money plus
    val BankTemple: ImageVector    // Check Balance bank pillars

    // Top Bar & Nav
    val GridMenu: ImageVector      // 4 rounded squares
    val ChatBubble: ImageVector    // Message bubble
    val Bell: ImageVector          // Notification bell
    val ScanReticle: ImageVector   // [ - ] Viewfinder
    val CardStack: ImageVector     // Payment cards
    val Receipt: ImageVector       // Transaction history ledger
}
```

---

## 6. Verification & Quality Acceptance Criteria

1. **Zero Unicode Emojis**: Grep search across all source code, XMLs, and assets confirms 0 occurrences of emojis.
2. **Visual Fidelity**: The home screen reflects the exact layout, colors, and components of `Screenshot 2026-09-10 154655.png` and `Screenshot 2026-09-10 154737.png`.
3. **Interactive Functionality**:
   - Tapping the Center Scanner FAB opens CameraX QR scanner.
   - Tapping "Pay Money" or "PAY TO CONTACT" opens UPI transfer screen.
   - Tapping "Check Balance" opens Passbook / Balance sheet.
   - Tapping "GET STARTED" on the Onboarding screen transitions smoothly to the Dashboard.
   - Bottom navigation allows switching between Home, Cards, History (Passbook), and Profile.
