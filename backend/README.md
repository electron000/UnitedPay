# United Pay — Backend Microservices Architecture

> **Stack**: Java 21 / Spring Boot 3.3 / Spring Cloud / Apache Kafka / Redis Cluster / PostgreSQL (Aurora) / Docker / Kubernetes.

---

## 1. Microservice Responsibilities

```
backend/
├── services/
│   ├── payment-orchestrator/          # Distributed Saga orchestrating payment initiation & settlement
│   ├── sdk-hub/                       # Token generation, HMAC Checksum signing, & Webhook processing
│   ├── auth-service/                  # SIM Binding, SMS User Consent verification, JWT Session issuing
│   ├── account-service/               # Bank account linking, balance inquiry, NPCI VPA directory
│   ├── reconciliation-worker/         # Event-driven & Cron worker resolving timeout/pending transactions
│   └── notification-service/          # Push notifications, SMS OTP, and Soundbox voice playback triggers
└── shared/
    ├── domain-events/                 # Shared Kafka event schemas (PaymentInitiated, PaymentSettled, etc.)
    └── security-common/               # HMAC verification, JWT parser, Keystore interfaces
```

---

## 2. Zero-Unmonitored-Failures SLA

* **Idempotency Guarantee**: Every request to `/api/v1/sdk/order/create` and `/api/v1/payment/*` enforces Redis-backed distributed locks on `X-Idempotency-Key`.
* **Dead-Letter Queue (DLQ)**: Failed webhook ingestions write to Kafka `PAYMENT_DLQ` with automatic backoff retry.
* **Auto-Reconciliation Daemon**: Scans `SWITCH_PENDING` transactions older than 60 seconds every 30 seconds to poll NPCI and auto-settle or reverse balances.
