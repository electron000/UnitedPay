# United Pay — Production Engineering & Security Guidelines for AI & Developers

> **Classification**: FINTECH TIER-1 / RESTRICTED  
> **Applicability**: All code, architectural decisions, SDK integrations, and AI code generation for United Pay.  
> **Mandate**: Zero security compromises, strict NPCI UPI & RBI regulatory compliance, OWASP MASVS Level 2 adherence, zero unmonitored transaction failures, and crash-proof software architecture.

---

## 1. Zero-Trust Security Directives (MANDATORY)

Any code failing any directive in this section MUST NOT be written, merged, or proposed.

### 1.1 Screen Capture Protection (`FLAG_SECURE`)
* **Rule**: All sensitive screens (MPIN entry, payment confirmation, QR display, bank balance sheet, card entry) MUST enforce Android's `WindowManager.LayoutParams.FLAG_SECURE`.
* **Behavior**: Blocks screenshots, screen recordings, remote desktop viewing, and blacks out the thumbnail in Android's recent apps switcher.

### 1.2 Cryptographic Key Management (Hardware Keystore & StrongBox)
* **Rule**: Symmetric and asymmetric keys MUST NEVER reside in plaintext in memory, shared preferences, SQLite, or hardcoded strings.
* **Storage**: Keys must be generated and stored inside the `AndroidKeyStore` provider backed by a Hardware Security Module (HSM) / Trusted Execution Environment (TEE) or StrongBox Keymaster.
* **Algorithm Standard**:
  * Symmetric: `AES/GCM/NoPadding` with 256-bit keys and random 12-byte IVs.
  * Asymmetric: `RSA/ECB/OAEPWithSHA-256AndMGF1Padding` (minimum 2048-bit, preferred 4096-bit) or `EC` (P-256).

### 1.3 Memory Hygiene & Zero Plaintext PII
* **Rule**: MPINs, Passwords, CVVs, and OTPs MUST NEVER be stored in immutable `java.lang.String`.
* **Standard**:
  * Use `CharArray` or primitive byte buffers.
  * Immediately zero out the array after use in a `finally` block:
    ```kotlin
    val mpin: CharArray = getMpinInput()
    try {
        cryptoManager.encryptAndSend(mpin)
    } finally {
        java.util.Arrays.fill(mpin, '0')
    }
    ```
* **Logging Ban**:
  * NEVER write `Log.d`, `Log.i`, `println`, or crash reports containing phone numbers, VPA handles, account numbers, tokens, or amounts.
  * In release builds, R8/ProGuard MUST strip all Android logging calls completely.

### 1.4 Transport Security & Anti-MitM (SSL Pinning & Replay Defense)
* **TLS Policy**: TLS 1.3 only (TLS 1.2 minimum). Cleartext HTTP (`http://`) is strictly prohibited in `network_security_config.xml`.
* **Certificate Pinning**: OkHttp `CertificatePinner` MUST pin against both primary and backup leaf/intermediate SHA-256 SPKI public key hashes.
* **Request Signing & Anti-Replay**:
  * Every transactional network call must include:
    1. `X-Timestamp`: Unix epoch milliseconds (validated within a ±30s server window).
    2. `X-Nonce`: Cryptographically secure random UUIDv4.
    3. `X-Signature`: HMAC-SHA256 of (`HTTP_METHOD + PATH + TIMESTAMP + NONCE + HASH(BODY)`), signed with the device session key.
    4. `X-Trace-Id`: UUIDv4 propagated across the entire transaction lifecycle for end-to-end tracing.
* **Idempotency**:
  * All debit, transfer, and recharge calls must send `X-Idempotency-Key` (UUIDv4) to guarantee zero double-charging even if network drops during transit.

---

## 2. United Pay Payment SDK Integration Directives

1. **Decoupled Bridge Architecture**:
   - The native United Pay Payment SDK must be encapsulated inside `:core:network` or a dedicated `:core:sdk` module.
   - UI layers and ViewModels MUST NEVER invoke SDK native classes directly; all calls go through a `UnitedPaySdkBridge` interface.
2. **Crash-Proof Activity Execution**:
   - SDK launches must be registered through Android's `ActivityResultLauncher` contract.
   - Any SDK internal crashes or unhandled runtime exceptions must be caught by a dedicated wrapper and mapped into an `SdkResult.Failure` without crashing the host application.
3. **Server-Side Token Verification**:
   - The app must NEVER generate or sign payment orders locally with private merchant secret keys.
   - Order tokens must be generated on the secure backend via `/api/v1/sdk/order/create` and passed to the SDK.
4. **Lifecycle Watchdog**:
   - If the user switches apps or the OS kills the process during payment, the app's `SyncWorker` must automatically query the order status upon restart to ensure the UI stays synchronized with backend bank records.

---

## 3. Zero-Unmonitored-Failures & Crash Resilience Directives

Fintech applications cannot tolerate silent payment drops or unrecorded timeouts. Every event must be traceable and self-healing.

1. **Global Uncaught Exception Handler**:
   - `UnitedPayApplication` must register a `Thread.setDefaultUncaughtExceptionHandler` that captures unhandled crashes, flushes pending transaction states to encrypted local storage, and logs breadcrumbs to Sentry / OpenTelemetry before graceful termination.
2. **Coroutine Exception Handling**:
   - Every ViewModel coroutine must attach a `CoroutineExceptionHandler`:
     ```kotlin
     val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
         telemetry.logError(traceId, "CoroutinesFailure", throwable)
         _uiState.update { it.copy(isLoading = false, error = throwable.localizedMessage) }
     }
     ```
3. **Transaction State Machine Telemetry**:
   - Every state transition (`INITIATED` -> `SDK_PROCESSING` -> `SETTLED` / `FAILED`) must emit an event with `X-Trace-Id`.
   - No transaction may remain in `PROCESSING` indefinitely; a background worker scans transactions older than 60s and runs an automated status re-query.
4. **Kafka Dead-Letter Queue (DLQ)**:
   - Backend webhook handlers that fail processing must route payloads to Kafka DLQ (`PAYMENT_DLQ`) with exponential retry backoff and jitter.

---

## 4. Admin Operations Portal Security Standards

1. **Multi-Factor Authentication (MFA)**:
   - Mandatory hardware FIDO2 or TOTP 2FA for all administrative logins.
2. **Strict Role-Based Access Control (RBAC)**:
   - Least privilege access principle: support agents see masked account numbers and cannot trigger refunds. Only authorized finance officers can authorize ledger reversals.
3. **Immutable Audit Logs**:
   - Every search query, export, configuration change, or manual action taken in the admin portal must write an immutable audit log to write-once-read-many (WORM) storage.
4. **Rate Limiting & IP Whitelisting**:
   - Admin API endpoints must be locked to corporate VPN / static IP gateways with strict rate limiting.
