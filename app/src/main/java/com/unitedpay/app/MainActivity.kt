package com.unitedpay.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.unitedpay.app.navigation.AppNavGraph
import com.unitedpay.core.designsystem.components.UnitedToastHost
import com.unitedpay.core.designsystem.theme.UnitedPayTheme
import com.unitedpay.core.model.session.UserSessionManager

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize UserSessionManager with persistence
        UserSessionManager.init(this)

        // Lock window status bar styling
        window.statusBarColor = Color.parseColor("#0078DF")
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            UnitedPayTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph()
                    UnitedToastHost()
                }
            }
        }
    }
}
