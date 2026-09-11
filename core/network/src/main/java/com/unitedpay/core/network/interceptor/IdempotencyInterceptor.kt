package com.unitedpay.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID

/**
 * Ensures any mutating transaction request (POST / PUT to payment endpoints)
 * carries a distinct X-Idempotency-Key header to prevent duplicate debits.
 */
class IdempotencyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (originalRequest.method.equals("POST", ignoreCase = true) ||
            originalRequest.method.equals("PUT", ignoreCase = true)
        ) {
            val urlPath = originalRequest.url.encodedPath
            if (urlPath.contains("payment") || urlPath.contains("transfer") || urlPath.contains("recharge")) {
                val hasIdempotencyKey = originalRequest.header("X-Idempotency-Key") != null
                if (!hasIdempotencyKey) {
                    val requestWithKey = originalRequest.newBuilder()
                        .header("X-Idempotency-Key", UUID.randomUUID().toString())
                        .build()
                    return chain.proceed(requestWithKey)
                }
            }
        }

        return chain.proceed(originalRequest)
    }
}
