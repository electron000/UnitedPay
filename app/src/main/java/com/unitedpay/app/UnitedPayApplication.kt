package com.unitedpay.app

import android.app.Application
import com.unitedpay.core.security.SecurityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UnitedPayApplication : Application() {

    lateinit var securityManager: SecurityManager
        private set

    override fun onCreate() {
        super.onCreate()

        securityManager = SecurityManager(applicationContext)

        // Asynchronously check environment on IO dispatcher to prevent main thread blocking / ANR
        CoroutineScope(Dispatchers.IO).launch {
            try {
                securityManager.isEnvironmentSecure()
            } catch (_: Exception) {
                // Ignore in dev/emulator environment
            }
        }
    }
}
