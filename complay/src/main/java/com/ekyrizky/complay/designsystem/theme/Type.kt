package com.ekyrizky.complay.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class ComplayTypography(
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val labelMedium: TextStyle

) {
    companion object {
        @Composable
        fun defaultTypography(): ComplayTypography = ComplayTypography(
            titleLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 26.sp,
                letterSpacing = 0.sp
            ),

            titleMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 22.sp,
                letterSpacing = 0.sp,
            ),

            bodyLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 24.sp,
                letterSpacing = 0.sp
            ),

            bodyMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp,
                letterSpacing = 0.sp,
            ),

            labelMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.4.sp
            )
        )
    }
}

internal val LocalTypography = compositionLocalOf<ComplayTypography> {
    error("No typography provided! Make sure to wrap all usages in MealsTheme.")
}