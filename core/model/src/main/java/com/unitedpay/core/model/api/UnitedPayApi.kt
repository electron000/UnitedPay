package com.unitedpay.core.model.api

/**
 * Enterprise API Provider Entry Point for UnitedPay.
 *
 * Current Mode: Configured to MockFintechApiClient (reading from UnitedMockData).
 * Future Mode: When backend microservices / NPCI SDK / BBPS switch arrive,
 * simply assign `client = RealFintechApiClient` (or inject via Hilt/Koin).
 * Zero UI screen modifications required.
 */
object UnitedPayApi {
    var client: UnitedPayApiGateway = MockFintechApiClient
}
