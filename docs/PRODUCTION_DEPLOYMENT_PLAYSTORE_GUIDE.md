# UnitedPay — Production Deployment, Play Store Release & Banking Integration Master Guide

> **Classification**: FINTECH TIER-1 / PRODUCTION RUNBOOK  
> **Target Audience**: Technical Founders, Principal Architects, DevOps/SRE Leads, Mobile Leads, and Security Officers  
> **Scope**: Backend Microservices Deployment, CI/CD Pipelines, Google Play Store Publishing, Proprietary Banking SDK Integration, NPCI UPI 2.0 Switch Rails, Kafka Event Streaming, Redis Idempotency, and Clean Mock-to-Production Migration.

---

## 1. Executive Master Blueprint: From Codebase to Production

Moving a Tier-1 UPI fintech application from local development to production involves four synchronized tracks:

```
+---------------------------------------------------------------------------------------------------+
|                                 THE 4 PRODUCTION RELEASE TRACKS                                   |
|                                                                                                   |
|  [ TRACK 1: BACKEND DEPLOYMENT ]           [ TRACK 2: CI/CD PIPELINES ]                           |
|  - Microservices in /backend               - GitHub Actions for Backend (AWS EKS, Docker, Helm)   |
|  - Kafka Event Bus & Redis Redlock         - GitHub Actions for Android (AAB, Keystore, Play API) |
|  - PostgreSQL Aurora Double-Entry Ledger   - Automated Security & SAST Scanning (Trivy, Sonar)   |
|                                                                                                   |
|  [ TRACK 3: PLAY STORE PUBLISHING ]        [ TRACK 4: FINTECH & NPCI INTEGRATION ]                |
|  - Release Keystore & App Signing (PEPDK)  - Drop-in UnitedPaySDK.aar via Clean Architecture      |
|  - ProGuard/R8 Obfuscation & App Bundle    - Sponsor Bank (PSP) & NPCI UPI Switch Integration     |
|  - RBI/NPCI Financial Declarations         - Phasing Out Arunjyoti Changkakoty Mock Sandbox       |
+---------------------------------------------------------------------------------------------------+
```

---

## 2. Backend Architecture: Where Code Goes & How to Deploy

All backend code belongs in the root `backend/` directory of the repository. It is structured as a cloud-native microservices ecosystem using **Java 21 / Spring Boot 3.3** (or high-throughput **Go** for transaction-critical routing).

### 2.1 Backend Directory Topology (`backend/`)

```
backend/
├── pom.xml                                   # Root Maven / Gradle multi-module build definition
├── shared/
│   ├── domain-models/                        # Shared Java/Kotlin immutable records (PaymentRequest, TransactionEvent)
│   ├── security-common/                      # HMAC-SHA256 signing, AES-GCM crypto, JWT validation, Keystore bridge
│   └── proto/                                # Protocol Buffers / gRPC definitions for high-speed inter-service IPC
├── services/
│   ├── api-gateway/                          # Envoy / Spring Cloud Gateway: SSL termination, WAF, rate-limiting
│   ├── payment-orchestrator/                 # Distributed Saga State Machine coordinating payment flow
│   ├── auth-service/                         # SIM binding validation, telecom token verification, device attestation
│   ├── account-service/                      # VPA directory (Virtual Payment Address), bank account linking, IFSC lookup
│   ├── sdk-hub/                              # Merchant token generation, HMAC checksum validation, webhooks
│   ├── auto-reconciliation-worker/          # High-frequency daemon polling NPCI switch for pending transactions
│   └── notification-service/                 # Push notifications (FCM), SMS OTP, Soundbox voice triggers
├── deploy/
│   ├── docker/                               # Production multi-stage Dockerfiles for each service
│   ├── kubernetes/                           # Helm charts & K8s manifests (Deployments, Services, HPA, Ingress)
│   └── terraform/                            # Infrastructure as Code (AWS VPC, EKS, MSK Kafka, Aurora, ElastiCache)
```

### 2.2 Infrastructure Sizing & AWS Deployment Topology
Fintech regulations in India (RBI Master Direction on Storage of Payment System Data) mandate that all transaction logs, financial records, and cryptographic material **must reside 100% within Indian data centers (AWS ap-south-1 Mumbai or Hyderabad)**.

```
                                  PRODUCTION AWS CLUSTER (ap-south-1)

                   +---------------------------------------------------------------+
                   |                   AWS Route 53 (DNS + Anycast)                |
                   +-------------------------------+-------------------------------+
                                                   |
                                                   v
                   +---------------------------------------------------------------+
                   |          AWS WAF (Web Application Firewall + Anti-DDoS)       |
                   +-------------------------------+-------------------------------+
                                                   |
                                                   v
                   +---------------------------------------------------------------+
                   |          Application Load Balancer (ALB) - TLS 1.3 mTLS       |
                   +-------------------------------+-------------------------------+
                                                   |
                         +-------------------------+-------------------------+
                         |                                                   |
                         v                                                   v
     +---------------------------------------+   +---------------------------------------+
     |   AWS EKS Cluster (Private Subnet A)  |   |   AWS EKS Cluster (Private Subnet B)  |
     |   - api-gateway (Pods)                |   |   - api-gateway (Pods)                |
     |   - payment-orchestrator (Pods)       |   |   - payment-orchestrator (Pods)       |
     |   - auth-service (Pods)               |   |   - auth-service (Pods)               |
     |   - auto-reconciliation (Pods)        |   |   - auto-reconciliation (Pods)        |
     +-------------------+-------------------+   +-------------------+-------------------+
                         |                                           |
                         +---------------------+---------------------+
                                               |
         +-------------------------------------+-------------------------------------+
         |                                     |                                     |
         v                                     v                                     v
+------------------+                 +-------------------+                 +-------------------+
|  AWS MSK (Kafka) |                 | AWS ElastiCache   |                 | Amazon Aurora     |
|  - 3 Multi-AZ    |                 | (Redis Cluster)   |                 | PostgreSQL        |
|    Brokers       |                 | - Multi-AZ Primary|                 | - Multi-AZ ACID   |
|  - payment-events|                 |   + Read Replicas |                 |   Double-Entry    |
|  - DLQ Topics    |                 | - Redlock Mutex   |                 |   Financial DB    |
+------------------+                 +-------------------+                 +-------------------+
```

---

## 3. CI/CD Automation Pipelines (GitHub Actions)

Two production-grade CI/CD pipelines automate testing, containerization, security scanning, and deployment.

### 3.1 Backend Microservices CI/CD Pipeline
Saved at `.github/workflows/backend-ci-cd.yml`:

```yaml
name: Backend Microservices CI/CD

on:
  push:
    branches: [ main ]
    paths:
      - 'backend/**'
      - '.github/workflows/backend-ci-cd.yml'

env:
  AWS_REGION: ap-south-1
  ECR_REPOSITORY: unitedpay-backend
  EKS_CLUSTER_NAME: unitedpay-prod-cluster

jobs:
  test-and-security:
    name: Unit Tests & SAST Vulnerability Scan
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'maven'

      - name: Run Test Suites
        run: |
          cd backend
          mvn clean test --batch-mode

      - name: Run Trivy Security Vulnerability Scan
        uses: aquasecurity/trivy-action@master
        with:
          scan-type: 'fs'
          scan-ref: 'backend/'
          severity: 'CRITICAL,HIGH'
          exit-code: '1'

  build-and-deploy:
    name: Build Docker Images & Deploy to EKS
    needs: test-and-security
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Configure AWS Credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ env.AWS_REGION }}

      - name: Log in to Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v2

      - name: Build, Tag, and Push Docker Image
        env:
          ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
          IMAGE_TAG: ${{ github.sha }}
        run: |
          docker build -t $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG -f backend/deploy/docker/Dockerfile.payment-orchestrator backend/
          docker push $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG

      - name: Deploy to Kubernetes (EKS via Helm)
        run: |
          aws eks update-kubeconfig --region ${{ env.AWS_REGION }} --name ${{ env.EKS_CLUSTER_NAME }}
          helm upgrade --install payment-orchestrator backend/deploy/kubernetes/helm/payment-orchestrator             --set image.tag=${{ github.sha }}             --namespace production
```

### 3.2 Android Mobile CI/CD Pipeline (Play Store Release)
Saved at `.github/workflows/android-release.yml`:

```yaml
name: Android Production Play Store Release

on:
  push:
    tags:
      - 'v*'

jobs:
  build-release-bundle:
    name: Build Signed Android App Bundle (AAB)
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'gradle'

      - name: Decode Production Keystore
        run: |
          echo "${{ secrets.PROD_RELEASE_KEYSTORE_BASE64 }}" | base64 --decode > app/release-keystore.jks

      - name: Build Production Bundle with R8 Obfuscation
        env:
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
        run: |
          chmod +x gradlew
          ./gradlew bundleProdRelease --stacktrace

      - name: Publish to Google Play Closed Testing Track
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_CONSOLE_SERVICE_ACCOUNT_JSON }}
          packageName: com.unitedpay
          releaseFiles: app/build/outputs/bundle/prodRelease/app-prod-release.aab
          track: internal
          status: completed
          mappingFile: app/build/outputs/mapping/prodRelease/mapping.txt
```

---

## 4. Google Play Store Release: Step-by-Step Production Runbook

Deploying a financial payment application to Google Play is significantly more rigorous than regular consumer apps. Google mandates compliance with both standard Play Policies and strict **Financial Services Guidelines**.

### 4.1 Prerequisites & Organizational Account Setup
1. **Google Play Developer Account (Organization)**:
   - Must be registered under the corporate entity (e.g., *UnitedPay Technologies Pvt. Ltd.*).
   - Requires a valid **DUNS Number** (Dun & Bradstreet) and verified corporate incorporation certificate (MCA Certificate in India).
   - Personal developer accounts are **not permitted** to publish banking or financial applications.
2. **RBI & NPCI TPAP License Disclosure**:
   - Google Play requires uploading proof of authorization as a Third-Party Application Provider (TPAP) or a formal partnership agreement with an RBI-licensed Sponsor Bank (e.g., ICICI Bank, Axis Bank, State Bank of India).

### 4.2 Production Cryptographic Signing Configuration
Never sign production builds with debug keys. 

1. **Generate Production Upload Keystore**:
   ```bash
   keytool -genkeypair -v -keystore release-upload-key.jks      -alias unitedpay-upload-alias      -keyalg RSA -keysize 4096      -validity 10000      -storetype JKS
   ```
2. **Google Play App Signing (Mandatory)**:
   - Google Play enforces **Play App Signing**. You upload your `.aab` signed with your *Upload Key*. Google validates it, strips the upload signature, and signs it with the permanent *App Signing Key* stored in Google's secure HSM vault.
3. **Gradle Signing Configuration (`app/build.gradle.kts`)**:
   ```kotlin
   signingConfigs {
       create("release") {
           storeFile = file(System.getenv("KEYSTORE_PATH") ?: "release-keystore.jks")
           storePassword = System.getenv("KEYSTORE_PASSWORD")
           keyAlias = System.getenv("KEY_ALIAS")
           keyPassword = System.getenv("KEY_PASSWORD")
           enableV1Signing = true
           enableV2Signing = true
           enableV3Signing = true
           enableV4Signing = true
       }
   }
   buildTypes {
       release {
           isMinifyEnabled = true
           isShrinkResources = true
           proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
           signingConfig = signingConfigs.getByName("release")
           isDebuggable = false
       }
   }
   ```

### 4.3 Mandatory Google Play Declarations

#### 1. Financial Services Declaration Form
- State that UnitedPay operates as a **UPI Third-Party Application Provider (TPAP)**.
- Declare the Partner/Sponsor Bank (e.g., "In technical partnership with ICICI Bank / Axis Bank for UPI clearing services").
- Provide customer dispute resolution contacts and grievance officer information.

#### 2. Data Safety Form (Accurate Compliance)
| Data Category | Collected? | Shared with 3rd Parties? | Handled In-Transit | Purpose |
| :--- | :--- | :--- | :--- | :--- |
| **Financial Info** (Transactions) | Yes | Yes (Only with NPCI & Sponsor Bank) | Encrypted (TLS 1.3) | App functionality, fraud prevention |
| **User Identifiers** (Phone, Name) | Yes | Yes (Bank KYC verification) | Encrypted (TLS 1.3) | Account management, authentication |
| **Location** (Approximate/Precise) | Yes | No | Encrypted (TLS 1.3) | Fraud prevention, geofencing (NPCI requirement) |
| **Camera Access** | No data stored | No | Processed live on device | QR code scanning only |
| **Biometric Credentials** | Never collected | Never shared | Stays in Android StrongBox TEE | Biometric unlock (app never reads raw prints) |

#### 3. High-Risk Permission Justifications
- **`READ_PHONE_STATE` & `SEND_SMS`**:
  - Google Play scans for these permissions aggressively.
  - You must submit a video demonstration proving that SMS and phone state are required **strictly for NPCI UPI Device Binding**, without which the user cannot transact.

### 4.4 The Release Tracks: 20-Tester Policy to Production
For all new developer accounts created after November 2023, Google mandates:
1. **Internal Testing**: Up to 100 internal engineers test the build immediately without Google review.
2. **Closed Testing Track (Mandatory 20 Testers for 14 Days)**:
   - You must recruit at least 20 opted-in testers.
   - The testers must remain opted-in continuously for **14 consecutive days**.
   - Regular testing activity must be recorded.
3. **Application for Production Access**:
   - After 14 days, you fill out a questionnaire explaining your testing feedback and quality assurance results.
   - Google approves production access within 3 to 7 business days.
4. **Production Staged Rollout**:
   - Day 1: Release to 10% of users.
   - Day 2: Expand to 25% if crash-free sessions > 99.8%.
   - Day 3: Expand to 50%.
   - Day 4+: 100% full rollout.

---

## 5. The Fintech Core: NPCI UPI Switch & Bank API Architecture

To understand how money moves in production, here is the architecture connecting UnitedPay to India's banking rails.

```
                                  NPCI UPI 2.0 TRANSACTION FLOW

  [ UnitedPay App ]
         |
         | 1. Initiate Pay (VPA, Amount, X-Idempotency-Key)
         v
  [ UnitedPay Backend (Payment Orchestrator) ]
         |
         | 2. Fetch Account & Route
         v
  [ Sponsor Bank / PSP Bank (e.g., ICICI / Axis / SBI) ]
         |
         | 3. Encrypted ISO 8583 / XML Request (ReqPay)
         v
  [ NPCI Central UPI Switch ]
         |
         +-----------------------------+-----------------------------+
         |                                                           |
         | 4. Debit Request (ReqDebit)                               | 6. Credit Request (ReqCredit)
         v                                                           v
  [ Remitter Bank (Payer) ]                                   [ Beneficiary Bank (Payee) ]
  - Verify 6-digit MPIN via Common Lib (CL)                   - Credit Payee A/C
  - Debit Account Balance                                     - Return Success (RespCredit)
  - Return Success (RespDebit)                                       |
         |                                                           |
         +-----------------------------+-----------------------------+
                                       |
                                       | 7. Return Final Settlement (RespPay)
                                       v
                     [ NPCI Central UPI Switch ]
                                       |
                                       v
                 [ Sponsor Bank / PSP Bank Switch ]
                                       |
                                       v
               [ UnitedPay Backend (Kafka Event Bus) ]
                                       |
                       +---------------+---------------+
                       |                               |
                       v                               v
             [ Push Notification ]             [ Soundbox Voice ]
             "Paid INR 500 to Store"           "Payment Received: 500 Rupees"
```

### 5.1 Roles of Key Financial Entities
1. **UnitedPay (TPAP — Third-Party Application Provider)**:
   - Provides the consumer-facing mobile apps (Android & iOS), UX, merchant tools, soundbox, and local transaction ledger.
2. **Sponsor Bank / PSP Bank (e.g., Axis, ICICI, HDFC, SBI)**:
   - Holds the direct banking gateway license with NPCI.
   - Signs requests with bank-level Hardware Security Modules (HSM).
   - Operates the UPI handle provider (e.g., `@unitedpay` or `@axisbank`).
3. **NPCI (National Payments Corporation of India)**:
   - India's central clearinghouse switch. Routes transactions between the Remitter Bank (sending funds) and Beneficiary Bank (receiving funds).
4. **NPCI Common Library (CL)**:
   - A secure, sandboxed library provided directly by NPCI.
   - It renders an un-interceptable PIN pad to capture the customer's 6-digit bank MPIN, encrypts it using the Sponsor Bank's public key inside the device hardware, and passes the ciphertext directly to the bank.

---

## 6. Microservices & Distributed Event-Driven Architecture

### 6.1 Apache Kafka Event Topology
Every state change in UnitedPay is an immutable domain event streamed via Apache Kafka.

```
KAFKA TOPIC TOPOLOGY

├── payment.initiated          # Emitted when user submits payment sheet
├── payment.switch.authorized  # Emitted when Sponsor Bank confirms NPCI receipt
├── payment.settled            # Emitted on final 200 OK credit confirmation
├── payment.failed             # Emitted on debit failure, timeout, or insufficient funds
├── payment.dlq                # Dead-Letter Queue for unhandled network/payload anomalies
├── soundbox.broadcast         # Dispatched to IoT soundboxes and mobile text-to-speech
└── audit.compliance-trail     # Permanent write-once-read-many (WORM) regulatory log
```

### 6.2 Redis Distributed Mutex (Zero-Double-Spend Guarantee)
To guarantee that a merchant or recipient is never paid twice even if a user frantically double-taps the pay button:

```java
// Spring Boot 3.3 / Java 21: Distributed Idempotency Guard
@Service
public class IdempotencyGuard {

    @Autowired
    private RedissonClient redissonClient;

    public <T> T executeWithLock(String idempotencyKey, Duration lockDuration, Supplier<T> operation) {
        String lockKey = "lock:idempotency:" + idempotencyKey;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // Wait up to 500ms to acquire; lease lock for 15 seconds
            boolean acquired = lock.tryLock(500, lockDuration.toMillis(), TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new DuplicateTransactionException("Transaction is already being processed: " + idempotencyKey);
            }
            return operation.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentSystemException("Lock acquisition interrupted", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
```

### 6.3 The Distributed Saga Pattern (Payment Orchestrator)
Transactions span multiple independent banking systems where traditional ACID database locks cannot be held. The `payment-orchestrator` executes an **Orchestration-based Saga**:

```
[START] -> Step 1: Create PENDING Ledger Record
                |
                v
           Step 2: Reserve Daily Limit in Redis
                |
                v
           Step 3: Dispatch ReqPay to Sponsor Bank Switch
                |
       +--------+--------+
       |                 | (Success)
(Failure / Timeout)      v
       |           Step 4: Confirm Remitter Debit
       |                 |
       |                 v
       |           Step 5: Confirm Beneficiary Credit
       |                 |
       |                 v
       |           Step 6: Mark SETTLED & Release Mutex
       |                 |
       |                 v
       |           [COMPLETE]
       |
       v (Compensating Transactions)
  Compensate 1: Poll Auto-Reconciliation Daemon
  Compensate 2: If debited without credit -> Trigger NPCI Auto-Reversal
  Compensate 3: Release Daily Limit in Redis
  Compensate 4: Mark Ledger as FAILED / REVERSED
       |
       v
  [TERMINATE]
```

### 6.4 Auto-Reconciliation Worker Daemon
In financial networks, timeouts happen (e.g., cellular towers drop mid-handshake).
- The `auto-reconciliation-worker` runs every 30 seconds.
- It scans Aurora PostgreSQL for any transaction with status `SWITCH_PENDING` older than 60 seconds.
- It calls the NPCI Query API (`ReqHbt` / `ReqChkTxn`) via the Sponsor Bank to determine the true state on the bank's central book.
- It either marks the payment `SUCCESS` and notifies the merchant, or executes a compensating reversal.

---

## 7. The Migration Strategy: Phasing Out Mock Data Cleanly

In our current development phase, the application runs on a deterministic mock engine centered around primary test user **Arunjyoti Changkakoty** (`usr_arunjyoti_001`, `9876543210`).

Because UnitedPay was built from Day 1 using **Clean Architecture and Interface Segregation**, transitioning to live production requires **ZERO changes to the Jetpack Compose UI screens**.

### 7.1 Gradle Build Flavors Architecture
Configure build variants in `app/build.gradle.kts`:

```kotlin
android {
    ...
    flavorDimensions += "environment"
    productFlavors {
        create("mock") {
            dimension = "environment"
            applicationIdSuffix = ".mock"
            versionNameSuffix = "-mock"
        }
        create("prod") {
            dimension = "environment"
            // Clean production package: com.unitedpay
        }
    }
}
```

### 7.2 Dagger/Hilt Dependency Injection Switching

The domain layers (`HomeScreen`, `PaymentScreen`, `PassbookScreen`, `ProfileScreen`) only know about abstract interfaces:

```
                  DOMAIN INTERFACE BOUNDARY
                  
    [ UI / ViewModels (HomeScreen, PaymentScreen) ]
                           |
                           v
              interface TransactionRepository
                           |
             +-------------+-------------+
             |                           |
             v                           v
   MockDataModule             ProductionDataModule
   (Active Today)             (When Switch is Live)
         |                           |
         v                           v
   [ UnitedMockData ]         [ UnitedPaySDK.aar ]
   [ UserSessionManager ]     [ OkHttp / Retrofit ]
   [ Arunjyoti Persona ]      [ Production Core Banking ]
```

#### The Mock Module (`MockDataModule.kt`)
Used during development and QA:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object MockDataModule {
    @Provides
    @Singleton
    fun provideTransactionRepository(
        userSessionManager: UserSessionManager
    ): TransactionRepository {
        return MockTransactionRepository(userSessionManager)
    }
}
```

#### The Production Module (`ProductionDataModule.kt`)
Used in `prodRelease`:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object ProductionDataModule {
    @Provides
    @Singleton
    fun provideTransactionRepository(
        sdkManager: UnitedPaySdkManager,
        apiClient: UnitedPayApi
    ): TransactionRepository {
        return ProductionTransactionRepository(sdkManager, apiClient)
    }
}
```

### 7.3 What Happens to the Test Persona?
- When building `prodRelease`, the compiler builds against the `prod` flavor.
- The `MockDataModule` is excluded from compilation.
- The Arunjyoti Changkakoty mock sandbox code remains safely in the `mock` source tree or test fixtures, completely stripped out of the production binary by R8 compiler dead-code elimination.
- Not a single line of Compose layout, styling, or navigation logic is rewritten.

---

## 8. Master Production Readiness Checklist

Before submitting the `.aab` to Google Play Store and enabling live switch traffic, verify every item:

### Client / Mobile (Android)
- [ ] Build variant set to `prodRelease`.
- [ ] Android App Bundle (`.aab`) generated with R8 Full Mode shrinking and obfuscation.
- [ ] `isDebuggable = false` enforced in `build.gradle.kts`.
- [ ] Production keystore generated with 4096-bit RSA and stored in secure CI/CD secrets.
- [ ] Google Play App Signing enrolled via Play Console.
- [ ] `FLAG_SECURE` verified active on all payment, MPIN, and balance views.
- [ ] In-memory `CharArray` zeroization verified for MPIN entries.
- [ ] SSL Pinning active against production backend SHA-256 public key hashes.
- [ ] All development mock data excluded from the production compile graph.
- [ ] Mandatory 20 testers completed 14 continuous days on the Closed Testing track.

### Backend & Cloud (AWS ap-south-1)
- [ ] VPC configured with public/private subnets across 3 Availability Zones.
- [ ] AWS EKS running Spring Boot 3.3 microservices with auto-scaling (HPA).
- [ ] Apache Kafka (MSK) cluster configured with replication factor of 3 and active DLQ consumers.
- [ ] Redis Cluster configured with Multi-AZ failover and Redlock idempotency locks active.
- [ ] Amazon Aurora PostgreSQL configured with encrypted double-entry ledger tables.
- [ ] Auto-reconciliation worker daemon running and polling `SWITCH_PENDING` transactions.
- [ ] CI/CD pipelines configured with Trivy vulnerability scans and automated deployments.
- [ ] All customer data and transaction logs residing exclusively within domestic Indian data centers.

### Compliance & Regulatory
- [ ] Third-Party Application Provider (TPAP) agreement finalized with Sponsor Bank.
- [ ] NPCI technical compliance and Common Library (CL) MPIN integration audit passed.
- [ ] CERT-In empaneled auditor security assessment and VAPT certification completed.
- [ ] Google Play Financial Services Declaration and Data Safety disclosures submitted and approved.

---

## 9. Summary & Execution Directive

By following this master guide:
1. Your **backend microservices** are deployed safely on AWS India with high-availability Kafka and Redis infrastructure.
2. Your **CI/CD pipelines** automate testing, security scanning, and deployment without manual error.
3. Your **Android app** is packaged, signed, and published to Google Play following the exact financial declarations and testing tracks required by Google.
4. Your **fintech switch rails** move real money safely using the Distributed Saga pattern and auto-reconciliation.
5. Your **mock sandbox** is gracefully retired without having to rewrite or destabilize a single screen in the mobile app.
