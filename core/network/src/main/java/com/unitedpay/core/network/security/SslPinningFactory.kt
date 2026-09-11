package com.unitedpay.core.network.security

import okhttp3.CertificatePinner

object SslPinningFactory {

    private const val API_DOMAIN = "api.unitedpay.in"

    /**
     * Creates OkHttp CertificatePinner pinning against primary and backup SPKI hashes.
     */
    fun createCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            .add(API_DOMAIN, "sha256/k20YW5tYRZp6l7K98z9YkL/2qM3X1Vw+J8w9Xz2X6Y4=") // Primary
            .add(API_DOMAIN, "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=") // Backup
            .build()
    }
}
