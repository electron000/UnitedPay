# United Pay — System Architecture & Modular Blueprint

This document details the high-scale distributed architecture, mobile multi-module architecture, SDK integration pipeline, and operational governance for **United Pay**, modeled on the proven engineering foundations of enterprise payment platforms (e.g., Paytm, PhonePe).

---

## 1. High-Level Distributed Systems Architecture

United Pay connects mobile clients to core banking systems, NPCI UPI switches, the United Pay Payment SDK, and merchant partners via a resilient microservices backend:

```
+-------------------------------------------------------------------------+
|                       United Pay Android Mobile App                     |
|  (Clean Architecture + Jetpack Compose + OkHttp TLS 1.3 + Keystore TEE) |
+------------------------------------+------------------------------------+
                                     |
                                     | HTTPS + HMAC Request Signature
                                     v
+-------------------------------------------------------------------------+
|                  API Gateway & Reverse Proxy (AWS / Envoy)              |
|  - SSL Termination, Rate Limiting (Token Bucket), WAF (DDoS Mitigation) |
|  - Trace Injector (X-Trace-Id, X-Correlation-Id) & Device Attestation   |
+------------------------------------+------------------------------------+
                                     |
                                     v
+-------------------------------------------------------------------------+
|                     Distributed Backend Microservices                   |
|                  (Java Spring Boot, Go High-Throughput Core)            |
|                                                                         |
|  +--------------------+  +--------------------+  +-------------------+  |
|  | Auth & SIM Binding |  |  Payment Processor |  | Account & Balance |  |
|  |      Service       |  |      Service       |  |      Service      |  |
|  +--------------------+  +--------------------+  +-------------------+  |
|  +--------------------+  +--------------------+  +-------------------+  |
|  | United Pay SDK Hub |  | Fraud & Risk Engine|  | Passbook & Ledger |  |
|  | (Token & Checksum) |  |  (ML Real-time)   |  |      Service      |  |
|  +--------------------+  +--------------------+  +-------------------+  |
|  +--------------------+  +--------------------+  +-------------------+  |
|  | Auto-Reconciliation|  |   Soundbox & Voice |  | Admin Operations  |  |
|  |  & Settlement Cron |  |    Notification    |  | Backend Service   |  |
|  +--------------------+  +--------------------+  +-------------------+  |
+------------------------------------+------------------------------------+
                                     |
                                     v
+-------------------------------------------------------------------------+
|                       Data, Cache & Event Streaming                     |
|  - Distributed Streaming: Apache Kafka (Order matching & Audit events)  |
|  - Dead-Letter Queue (DLQ): Kafka PAYMENT_DLQ for unhandled edge cases  |
|  - Ultra-Low Latency Caching: Redis Cluster (Idempotency & Sessions)    |
|  - Financial Master DB: Amazon Aurora PostgreSQL (Double-Entry Ledger)  |
+------------------------------------+------------------------------------+
                                     |
            +------------------------+------------------------+
            |                                                 |
            v                                                 v
+---------------------------------------+   +------------------------------------+
|     United Pay UPI Payment SDK Rail   |   |   Enterprise Admin Portal (Web)    |
|  - Token Signature & Verification     |   |  - Real-Time Live Ledger & TPS     |
|  - NPCI UPI Switch Handshake          |   |  - Bank Switch Health Dashboard    |
|  - Core Banking Remitter/Beneficiary  |   |  - Dispute & Refund Center         |
+---------------------------------------+   +------------------------------------+
```

---

## 2. Android Multi-Module Hierarchy

```
UnitedPay/
├── app/                                  # Application Root
│   ├── build.gradle.kts
│   └── src/main/java/com/unitedpay/app/
│       ├── UnitedPayApplication.kt       # Global crash handler & DI wiring
│       ├── MainActivity.kt               # Single-activity host & FLAG_SECURE
│       └── navigation/AppNavGraph.kt     # Global navigation graph
│
├── core/                                 # Core Shared Libraries
│   ├── common/                           # Result, Dispatchers, Telemetry (TraceId)
│   ├── designsystem/                     # United Pay Theme, Colors, Shield Components
│   ├── security/                         # Keystore, RootBeer, Biometrics, FlagSecure
│   ├── network/                          # Retrofit, OkHttp, SSL Pinning, SDK Bridge
│   ├── database/                         # Room + SQLCipher Encrypted DB
│   └── model/                            # Domain Entities (Transaction, Account, User)
│
├── feature/                              # Decoupled Feature Modules
│   ├── onboarding/                       # SIM Binding, SMS Consent, Phone Registration
│   ├── auth/                             # MPIN Keypad, Biometric Lock
│   ├── home/                             # Dashboard, Quick Pay 4-Grid, Regional Hub
│   ├── payment/                          # CameraX Scanner, SDK Runner, Amount Input
│   ├── passbook/                         # Transaction Ledger, PDF Receipt Generator
│   └── soundbox/                         # In-App Voice Announcement Synthesizer
│
└── admin-portal/                         # Enterprise Admin Management Web App
    ├── src/components/                   # Live Ledger, Uptime Monitor, Dispute Center
    └── src/services/                     # Admin Backend REST & WebSocket Client
```

---

## 3. Zero-Crash & Failure Observability Pipeline

1. **Global Crash Interceptor**:
   - `Thread.setDefaultUncaughtExceptionHandler` catches uncaught crashes, dumps breadcrumbs to an encrypted local crash log, and safely exits without corrupting local databases.
2. **End-to-End Tracing (`X-Trace-Id`)**:
   - Every transaction attaches a continuous correlation ID spanning client, gateway, microservices, SDK calls, and Kafka events.
3. **Automated Reconciliation**:
   - Any transaction pending beyond 60 seconds is polled via background cron against the SDK/NPCI status API, ensuring zero stranded funds.
4. **Dead-Letter Queue (DLQ)**:
   - Webhook failures automatically push to Kafka DLQ for retries with exponential backoff and jitter.

---

## 4. Admin Operations Portal Architecture

* **Tech Stack**: React 18 / Next.js / Tailwind CSS / WebSocket Live Feed.
* **Key Features**:
  1. Real-time Transaction Ledger with 1-click timeline inspection.
  2. Live Bank Switch health dashboard (tracking SBI, HDFC, ICICI UPI uptime).
  3. Dispute & Chargeback resolution center with instant refund trigger.
  4. Multi-Factor Authentication (MFA) and immutable RBAC audit trail.
