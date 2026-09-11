package com.unitedpay.core.security.integrity

import android.content.Context
import android.os.Build
import com.scottyab.rootbeer.RootBeer
import java.io.File

/**
 * Validates device integrity, detecting Root, Magisk, Test-Keys, and dangerous binaries.
 * Safe for emulator and physical device without hanging main thread.
 */
class RootDetector(private val context: Context) {

    private val rootBeer by lazy { RootBeer(context) }

    fun isDeviceCompromised(): Boolean {
        return isRooted() || isRunningOnEmulator() || hasTestKeys()
    }

    fun isRooted(): Boolean {
        return try {
            rootBeer.isRooted || checkSuBinary()
        } catch (_: Exception) {
            false
        }
    }

    private fun checkSuBinary(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in paths) {
            try {
                if (File(path).exists()) return true
            } catch (_: Exception) {
                // Ignore permission or file check exceptions
            }
        }
        return false
    }

    private fun hasTestKeys(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun isRunningOnEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.PRODUCT.contains("sdk_gphone64_arm64"))
    }
}
