package com.vraj.spendwise.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

private val AppColorScheme = darkColorScheme(
    primary = Color.White,
    primaryContainer = Color.Black,
    onPrimary = BlueText,
    secondary = BaseGreen,
    background = LightGray
)

@Composable
fun SpendWiseTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AppColorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                // Show status bar
                show(WindowInsetsCompat.Type.statusBars())
                // Accepts status bar as part of UI. If false passed, content might go behind status bar
                WindowCompat.setDecorFitsSystemWindows(window, true)
                // Set True to have dark text in status bar (false for white text)
                isAppearanceLightStatusBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = PoppinsTypography,
        shapes = shapes,
        content = content
    )
}