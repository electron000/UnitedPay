package com.unitedpay.core.security

import android.content.Context
import com.unitedpay.core.security.integrity.RootDetector
import com.unitedpay.core.security.keystore.KeystoreHelper
import java.util.Arrays

/**
 * High-level security manager unifying hardware Keystore, device attestation, and memory sanitization.
 */
class SecurityManager(
    private val context: Context,
    private val keystoreHelper: KeystoreHelper = KeystoreHelper(),
    private val rootDetector: RootDetector = RootDetector(context)
) {

    /**
     * Checks if the device is safe to process financial UPI transactions.
     */
    fun isEnvironmentSecure(): Boolean {
        return !rootDetector.isDeviceCompromised()
    }

    /**
     * Safely executes an operation using an MPIN CharArray, ensuring the array
     * is thoroughly overwritten with zeros immediately upon completion.
     */
    inline fun <R> withSanitizedPin(pin: CharArray, block: (CharArray) -> R): R {
        return try {
            block(pin)
        } finally {
            Arrays.fill(pin, '0')
        }
    }

    /**
     * Encrypts sensitive payload (e.g. auth tokens, bank account metadata).
     */
    fun encryptPayload(data: String): ByteArray {
        return keystoreHelper.encrypt(data.toByteArray(Charsets.UTF_8))
    }

    /**
     * Decrypts sensitive payload.
     */
    fun decryptPayload(encrypted: ByteArray): String {
        return String(keystoreHelper.decrypt(encrypted), Charsets.UTF_8)
    }
}
