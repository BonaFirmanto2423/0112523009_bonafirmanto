package com.example.kulinernav.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Palet hangat bernuansa kuliner (oranye cabai + hijau daun)
private val LightColors = lightColorScheme(
    primary = Color(0xFFB3401B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCF),
    onPrimaryContainer = Color(0xFF3A0B00),
    secondary = Color(0xFF4E6B3A),
    secondaryContainer = Color(0xFFD0F0B6),
    tertiary = Color(0xFFC08A00),
    background = Color(0xFFFFFBF8),
    surface = Color(0xFFFFFBF8)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB59C),
    onPrimary = Color(0xFF5E1700),
    primaryContainer = Color(0xFF8A2A06),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = Color(0xFFB4D49C),
    secondaryContainer = Color(0xFF37522A),
    tertiary = Color(0xFFF5BF48)
)

@Composable
fun KulinerNavTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
