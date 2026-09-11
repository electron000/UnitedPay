# United Pay — Enterprise Admin Operations Portal

> **Purpose**: Centralized Command Center for Real-Time UPI Transaction Observability, Bank Gateway Health, Dispute Resolution, and Fraud Control.

---

## 1. Core Dashboards

1. **Live Transaction Operations Monitor (`/dashboard/live-ledger`)**:
   - Real-time TPS (Transactions Per Second) graph.
   - Success, Failure, and Timeout percentage gauges.
   - Live streaming transaction feed via WebSocket (`/ws/v1/admin/transactions`).
   - Deep search by UTR, Order ID, Mobile Number, or VPA.

2. **Bank Gateway Uptime & Health (`/dashboard/gateways`)**:
   - Status of NPCI UPI switch and major remitter banks (SBI, HDFC, ICICI, PNB, Assam Gramin Vikash Bank).
   - Real-time latency tracking (p50, p95, p99 in ms).
   - Automated and manual traffic re-routing during bank downtime.

3. **Dispute & Refund Desk (`/dashboard/disputes`)**:
   - NPCI UDIR (Unified Dispute & Issue Resolution) ticket tracking.
   - 1-click verified refund trigger with dual-admin approval for high-value disputes.

4. **Fraud & Risk Rule Engine (`/dashboard/risk`)**:
   - Velocity thresholds (max transfer velocity per user / per merchant).
   - High-value anomaly flagging (>₹50,000 to unverified VPAs).
   - Device and SIM change watchlists.

5. **Financial Auto-Reconciliation (`/dashboard/reconciliation`)**:
   - Daily end-of-day bank statement vs internal double-entry ledger matching.
   - Variance detection and automated settlement batch sign-off.

---

## 2. Tech Stack & Security

* **Framework**: React 18 / Next.js / TypeScript / Tailwind CSS.
* **State & Networking**: TanStack Query + WebSocket client + Axios with Bearer tokens.
* **Authentication**: Multi-Factor Authentication (TOTP / Hardware FIDO2) with strict RBAC:
  * `SUPER_ADMIN`, `FINANCE_OFFICER`, `SUPPORT_AGENT`, `RISK_ANALYST`.
* **Auditability**: Every admin click, search, export, and override is permanently logged to an immutable audit store.
