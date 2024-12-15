package com.vraj.spendwise.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

private val lightColorScheme = lightColorScheme(
    primary = Color.White,
    primaryContainer = Color.Black,
    onPrimary = RegularBlue,
    secondary = RegularGreen,
    onSecondary = Color.White,
    background = LightGray,
    onBackground = RegularBlue
)

private val darkColorScheme = darkColorScheme(
    primary = DarkBlue,
    primaryContainer = Color.White,
    onPrimary = Color.White,
    secondary = DarkGreen,
    onSecondary = Color.White,
    background = LightGray,
    onBackground = DarkBlue
)

@Composable
fun SpendWiseTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    val isSystemInDarkTheme = isSystemInDarkTheme()
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = (if (isSystemInDarkTheme) darkColorScheme else lightColorScheme)
                .primary.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                // Show status bar
                show(WindowInsetsCompat.Type.statusBars())
                // Accepts status bar as part of UI. If false passed, content might go behind status bar
                WindowCompat.setDecorFitsSystemWindows(window, true)
                // Set True to have dark text in status bar (false for white text)
                isAppearanceLightStatusBars = !isSystemInDarkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme) darkColorScheme else lightColorScheme,
        typography = PoppinsTypography,
        shapes = shapes,
        content = content
    )
}