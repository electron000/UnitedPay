# United Pay — Complete Fintech End-to-End Master Plan

> **Scope**: End-to-End Fintech Ecosystem Architecture  
> **Components**: Native Android App + United Pay UPI SDK Integration + Resilient Backend Microservices + Enterprise Admin Operations Portal + Zero-Unmonitored-Failures Observability Engine.

---

## 1. Executive Blueprint: The 4 Ecosystem Pillars

United Pay is designed as an enterprise-grade fintech platform where every transaction is tracked from origin to settlement without silent drops, unhandled exceptions, or unmonitored failures.

```
+---------------------------------------------------------------------------------------+
|                                    1. CLIENT LAYER                                    |
|  +-------------------------------------------------+  +----------------------------+  |
|  |             United Pay Android App              |  |   United Pay Merchant Web  |  |
|  |  (Compose + MVI + Keystore + Crash Guard)       |  |   (Checkout Widget/Portal) |  |
|  +------------------------+------------------------+  +--------------+-------------+  |
|                           |                                          |                |
|                           v                                          v                |
|  +---------------------------------------------------------------------------------+  |
|  |                 United Pay UPI SDK Integration Layer (Client-Side)              |  |
|  |  - Secure Token Handshake  - Isolated Activity Sandbox  - Lifecycle Watchdog    |  |
|  |  - Re-Query Fallback       - Hardware-Backed Attestation                        |  |
|  +----------------------------------------+----------------------------------------+  |
+-------------------------------------------|-------------------------------------------+
                                            | HTTPS / TLS 1.3 + HMAC-SHA256
                                            v
+---------------------------------------------------------------------------------------+
|                           2. BACKEND API GATEWAY & ORCHESTRATION                      |
|  - Reverse Proxy (Envoy / AWS API Gateway) with Rate Limiting (Token Bucket)          |
|  - Distributed Tracing Injector: X-Trace-Id, X-Correlation-Id, X-Device-Fingerprint   |
|  - Idempotency Filter (X-Idempotency-Key validation via Redis Cluster)                |
+-------------------------------------------+-------------------------------------------+
                                            |
                                            v
+---------------------------------------------------------------------------------------+
|                              3. BACKEND MICROSERVICES LAYER                           |
|  +------------------------+  +------------------------+  +-------------------------+  |
|  | Payment Orchestrator   |  | SDK Token & Checksum   |  | Account & SIM Binding   |  |
|  | (Saga State Machine)   |  | Service                |  | Service                 |  |
|  +-----------+------------+  +-----------+------------+  +------------+------------+  |
|              |                           |                            |               |
|  +-----------+------------+  +-----------+------------+  +------------+------------+  |
|  | Fraud & Velocity Engine|  | Auto-Reconciliation    |  | Webhook & Soundbox      |  |
|  | (Real-Time ML Rules)   |  | & Settlement Service   |  | Notification Dispatcher |  |
|  +------------------------+  +------------------------+  +-------------------------+  |
|                                           |                                           |
|       +-----------------------------------+-----------------------------------+       |
|       | Apache Kafka (Order matching, Audit topics, Dead-Letter Queues (DLQ)) |       |
|       | Redis Cluster (Session cache, idempotency locks, velocity counters)   |       |
|       | PostgreSQL / Aurora (Double-Entry Financial Ledger, ACID Compliant)   |       |
|       +-----------------------------------+-----------------------------------+       |
+-------------------------------------------|-------------------------------------------+
                                            |
                         +------------------+------------------+
                         |                                     |
                         v                                     v
+------------------------------------+   +----------------------------------------------+
|     4. BANKING & NPCI RAILS        |   |      5. ENTERPRISE ADMIN OPERATIONS PORTAL   |
|  - Remitter Core Banking System    |   |  - Real-Time Live Ledger & TPS Monitor       |
|  - NPCI UPI Switch                 |   |  - Bank Gateway Health & Route Balancer      |
|  - Beneficiary Core Banking System |   |  - Dispute, Chargeback & Refund Center       |
|                                    |   |  - User/Merchant KYC & Device Audit Desk     |
|                                    |   |  - Fraud & Velocity Rule Configurator        |
+------------------------------------+   +----------------------------------------------+
```

---

## 2. United Pay UPI SDK Integration Architecture

The platform integrates the proprietary **United Pay UPI SDK** into both Frontend and Backend, maintaining strict decoupling so SDK internal updates never break the host application.

### 2.1 Backend SDK Integration (Token Generation & Checksum)
1. **Order Initiation (`POST /api/v1/sdk/order/create`)**:
   - The host backend accepts payment requests from internal or merchant clients.
   - Generates a cryptographically signed **Transaction Token** using the United Pay SDK secret key:
     $$\text{Checksum} = \text{HMAC-SHA256}(\text{MerchantId} + \text{OrderId} + \text{Amount} + \text{Timestamp}, \text{SecretKey})$$
   - Persists order in `ORDER_INITIATED` state in the PostgreSQL database before returning response to client.
2. **Webhook Callback Receiver (`POST /api/v1/sdk/webhook`)**:
   - Asynchronous payment completion notifications from the UPI rail are ingested.
   - Validates webhook signature against SDK public certificate to prevent spoofed callbacks.
   - Enqueues event into Kafka topic `SDK_PAYMENT_CALLBACK` for idempotent consumer processing.

### 2.2 Android App SDK Integration (Client Bridge)
The SDK is isolated inside a dedicated module `:core:sdk` to prevent classloader collisions and guarantee crash isolation:

```kotlin
interface UnitedPaySdkBridge {
    fun initialize(merchantId: String, environment: SdkEnvironment)
    fun startUpiTransaction(
        activity: Activity,
        token: String,
        orderId: String,
        amount: Double,
        callback: SdkTransactionCallback
    )
}

interface SdkTransactionCallback {
    fun onSuccess(transactionResult: SdkTransactionResult)
    fun onFailure(errorCode: String, errorMessage: String, details: Map<String, Any>?)
    fun onCancelled(reason: String)
}
```

* **Crash Isolation**: The SDK invocation is wrapped inside a custom Android activity contract that intercepts any unhandled exceptions or SDK native crashes and translates them into actionable domain errors without terminating the host app process.
* **Auto-Re-query Fallback**: If the SDK callback drops due to OS process termination in low-memory situations, the app's `SyncWorker` triggers an automated server-side order inquiry (`GET /api/v1/sdk/order/{orderId}/status`) upon next app wake-up.

---

## 3. Zero-Crash & Zero-Unmonitored-Failures Observability Engine

Fintech applications cannot tolerate silent payment drops, unhandled exceptions, or unrecorded timeouts. Every event must be traceable and self-healing.

### 3.1 Strict Transaction State Machine

Every transaction progresses through an immutable, observable state machine:

```
[INITIATED] ──────────────> [TOKEN_GENERATED]
     │                                │
     ▼ (Token Gen Failure)            ▼
 [ABORTED]                     [SDK_INVOKED]
                                      │
                                      ▼
                               [SWITCH_PENDING] (Awaiting NPCI / Bank Switch)
                                      │
        +-----------------------------+-----------------------------+
        │                             │                             │
        ▼                             ▼                             ▼
    [SETTLED]                [FAILED_DECLINED]             [TIMEOUT_PENDING]
        │                             │                             │
        │ (Dispute/Refund)            │                             ▼ (Auto-Reconciliation)
        ▼                             │                    [AUTO_REQUERY_POLL]
   [REFUNDED]                         │                             │
                                      │             +---------------+---------------+
                                      │             ▼                               ▼
                                      +─────> [REVERSED_TO_USER]               [SETTLED]
```

### 3.2 Automated Monitoring & Resilience Protocols

1. **Distributed Correlation Tracking**:
   - Every user touchpoint generates a unique `X-Trace-Id` (UUIDv4) and `X-Correlation-Id`.
   - Propagated through:
     `Android App` $\rightarrow$ `API Gateway` $\rightarrow$ `Payment Microservice` $\rightarrow$ `SDK Rail` $\rightarrow$ `Kafka Topics` $\rightarrow$ `Database Audit Log`.
2. **Dead-Letter Queue (DLQ) & Auto-Retry**:
   - If a payment notification or bank callback fails processing, it is pushed to Kafka DLQ (`PAYMENT_DLQ`).
   - Consumer attempts retry with **exponential backoff + jitter** (1s, 2s, 5s, 10s, 30s).
   - If all retries fail, an automated high-priority alert is dispatched to the Admin Panel Operations Desk.
3. **Automated Reconciliation Worker (Cron / Event-Driven)**:
   - A background worker scans for any transaction in `SWITCH_PENDING` or `TIMEOUT_PENDING` older than **60 seconds**.
   - Invokes NPCI UPI switch status API and United Pay SDK status endpoint.
   - Automatically settles or reverses the user balance, guaranteeing zero stranded money.
4. **Mobile Global Crash Guard**:
   - `Thread.setDefaultUncaughtExceptionHandler` captures any catastrophic crash, logs full device breadcrumbs, non-PII stack trace, and pending transaction ID to secure offline storage before safe exit.
   - Sentry / Firebase Crashlytics telemetry with high-priority fintech alerting.

---

## 4. Enterprise Admin Operations Portal

The Admin Panel provides real-time governance, auditability, and emergency controls for United Pay operations.

### 4.1 Core Admin Functional Modules

| Module Name | Purpose & Key Features |
| :--- | :--- |
| **1. Live Operations & TPS Dashboard** | Real-time transaction velocity, live TPS (Transactions Per Second), success/failure ratio graphs, gateway response latencies, and regional North-East transaction heatmaps. |
| **2. Transaction Ledger & Deep Search** | Instant lookup by UTR (12 digits), Order ID, Payee/Payer VPA, phone number, or date range. Shows complete end-to-end trace timeline from client tap to bank switch acknowledgment. |
| **3. Bank Gateway Health & Smart Routing** | Live uptime and error-rate monitoring for all linked bank switches (SBI, HDFC, ICICI, Assam Gramin Vikash Bank). Allows admins to throttle or reroute traffic away from degraded bank switches. |
| **4. Dispute & Chargeback Resolution Center** | Triages customer grievances, raises NPCI UDIR (Unified Dispute & Issue Resolution) queries, and allows authorized finance admins to trigger 1-click verified reversals/refunds. |
| **5. KYC & SIM Binding Audit Desk** | Inspects user onboarding logs, SIM card carrier details, hardware device fingerprint hashes, and compliance verification status. |
| **6. Fraud & Risk Rule Engine** | Configures dynamic fraud thresholds: flags transactions exceeding velocity limits (e.g. >₹1,00,000 within 10 minutes to new VPAs), geofencing anomalies, or consecutive failed MPIN attempts. |
| **7. Financial Reconciliation & Settlement** | Automated cross-matching of daily NPCI settlement raw files with internal double-entry ledger. Flags variances for accounting sign-off. |

### 4.2 Security & Role-Based Access Control (RBAC)

* **Multi-Factor Authentication (MFA)**: Mandatory hardware FIDO2 key or TOTP for all admin portal logins.
* **Granular Permissions**:
  * `SUPER_ADMIN`: Full system configuration, policy toggles, role management.
  * `FINANCE_OFFICER`: Reconciliation sign-off, refund approvals, ledger exports.
  * `SUPPORT_AGENT`: Read-only transaction search, customer dispute logging (PII masked).
  * `RISK_ANALYST`: Fraud rule configuration, AML anomaly inspection.
* **Immutable Audit Trail**: Every click, search, refund, or rule change in the admin panel writes an immutable log to an append-only AWS S3 Glacier / Elasticsearch store.

---

## 5. End-to-End Payment Flow Sequence Diagram

```
User (Android App)         Host Backend               United Pay SDK Rail            NPCI / Bank Switch
       │                         │                             │                              │
       │ 1. Tap "Pay" (₹500)     │                             │                              │
       ├────────────────────────>│                             │                              │
       │                         │ 2. Create Order & Checksum  │                              │
       │                         ├────────────────────────────>│                              │
       │                         │<────────────────────────────┤                              │
       │                         │    Token + OrderId          │                              │
       │<────────────────────────┤                             │                              │
       │    Txn Token            │                             │                              │
       │                         │                             │                              │
       │ 3. Launch UnitedPaySdkBridge.startUpiTransaction(...) │                              │
       ├──────────────────────────────────────────────────────>│                              │
       │                         │                             │ 4. Prompt NPCI MPIN Sheet    │
       │                         │                             ├─────────────────────────────>│
       │                         │                             │<─────────────────────────────┤
       │                         │                             │    MPIN Authorized           │
       │                         │                             │                              │
       │                         │                             │ 5. Execute Debit/Credit Rail │
       │                         │                             ├─────────────────────────────>│
       │                         │                             │<─────────────────────────────┤
       │                         │                             │    Settlement Confirmed      │
       │                         │ 6. Webhook (Signed Result)  │                              │
       │                         │<────────────────────────────┤                              │
       │                         │                             │                              │
       │ 7. SDK Success Callback │ 8. Verify & Record Ledger   │                              │
       │<──────────────────────────────────────────────────────┤                              │
       │                         ├─────────────────┐           │                              │
       │                         │ Update DB &     │           │                              │
       │                         │ Kafka Event     │           │                              │
       │                         │<────────────────┘           │                              │
       │ 9. UI Confetti & Soundbox                             │                              │
       │    Voice Announcement                                 │                              │
       v                                                       v                              v
```

---

## 6. Implementation Roadmap & Milestones

1. **Phase 1: SDK Abstraction & Android Bridge**:
   - Implement `UnitedPaySdkManager` inside `:core:network` or `:core:sdk`.
   - Setup `TransactionTelemetry` with `X-Trace-Id` propagation and global exception guards.
2. **Phase 2: Backend Orchestration & Webhooks**:
   - Build token issuance and checksum verification service (`/api/v1/sdk/order/*`).
   - Implement webhook receiver with HMAC signature verification and Kafka event emission.
3. **Phase 3: Admin Operations Portal**:
   - Scaffold React/TypeScript admin dashboard (`admin-portal/`).
   - Build live transaction feed, gateway uptime monitor, and dispute management center.
4. **Phase 4: Resilience & Auto-Reconciliation**:
   - Deploy background reconciliation cron worker to auto-resolve timeout payments.
   - Configure Sentry/OpenTelemetry monitoring and alert webhooks.
