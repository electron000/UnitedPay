package com.unitedpay.core.network.di

import com.unitedpay.core.network.interceptor.AuthInterceptor
import com.unitedpay.core.network.interceptor.IdempotencyInterceptor
import com.unitedpay.core.network.security.SslPinningFactory
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private const val BASE_URL = "https://api.unitedpay.in/"

    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        idempotencyInterceptor: IdempotencyInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .certificatePinner(SslPinningFactory.createCertificatePinner())
            .addInterceptor(authInterceptor)
            .addInterceptor(idempotencyInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            .retryOnConnectionFailure(true)
            .build()
    }

    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
