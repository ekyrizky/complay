package com.ekyrizky.complay.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier

@Composable
fun ComplayTheme(
    colors: ComplayColors = ComplayColors.defaultColors(),
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(
        LocalColors provides colors,
    ) {
        Box(
            modifier = Modifier.background(colors.background)
        ) {
            content()
        }
    }
}

object ComplayTheme {
    val colors: ComplayColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current
}