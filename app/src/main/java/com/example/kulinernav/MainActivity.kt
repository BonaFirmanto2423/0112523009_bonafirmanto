package com.example.kulinernav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kulinernav.data.settings.UserSettings
import com.example.kulinernav.navigation.KulinerNavHost
import com.example.kulinernav.ui.theme.KulinerNavTheme

/**
 * MainActivity hanya memasang tema dan NavHost.
 * Tema gelap/terang dibaca dari preference DataStore (dark_mode).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val settingsRepository = (application as KulinerApp).container.settingsRepository

        setContent {
            val settings: UserSettings? by settingsRepository.settings
                .collectAsStateWithLifecycle(initialValue = null)

            KulinerNavTheme(darkTheme = settings?.darkMode ?: isSystemInDarkTheme()) {
                KulinerNavHost()
            }
        }
    }
}
