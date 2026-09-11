package com.unitedpay.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID

/**
 * Injects Authorization header, anti-replay nonces, and transaction timestamps.
 */
class AuthInterceptor(
    private val tokenProvider: () -> String?,
    private val deviceIdProvider: () -> String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
            .header("X-Timestamp", System.currentTimeMillis().toString())
            .header("X-Nonce", UUID.randomUUID().toString())
            .header("X-Device-Id", deviceIdProvider())

        val token = tokenProvider()
        if (!token.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(builder.build())
    }
}
