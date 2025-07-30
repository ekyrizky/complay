package com.ekyrizky.complay.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ComplayColors(
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val primary: Color,
    val onPrimary: Color,
    val error: Color,
) {
    companion object {
        @Composable
        fun defaultColors(): ComplayColors = ComplayColors(
            background = Color(0x80FFFFFF),
            surface = Color(0xFFF2F2F7),

            onSurface = Color(0xFF000000),
            onSurfaceVariant = Color(0xFF8E8E93),

            primary = Color(0xFFFF0000),
            onPrimary = Color(0xFFFFFFFF),

            error = Color(0xFFFF453A)
        )
    }
}

internal val LocalColors = compositionLocalOf<ComplayColors> {
    error("No colors provided! Make sure to wrap all usages in ComplayTheme.")
}