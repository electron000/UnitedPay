package com.unitedpay.core.security.view

import android.app.Activity
import android.view.WindowManager

object FlagSecureHelper {

    /**
     * Prevents screen recording, screenshots, and recents thumbnail leakage.
     */
    fun protectScreen(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    /**
     * Clears FLAG_SECURE if screen transitions to a non-sensitive public flow.
     */
    fun unprotectScreen(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}
