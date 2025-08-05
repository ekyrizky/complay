package com.ekyrizky.complay.designsystem.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class BufferingSize {
    SMALL, MEDIUM, LARGE;

    internal fun toDotSize(): Dp = when (this) {
        SMALL -> 6.dp
        MEDIUM -> 8.dp
        LARGE -> 12.dp
    }

    internal fun toIndicatorSize(): Dp = when (this) {
        SMALL -> 32.dp
        MEDIUM -> 50.dp
        LARGE -> 64.dp
    }

    internal fun toCircleRadius(): Dp = when (this) {
        SMALL -> 16.dp
        MEDIUM -> 20.dp
        LARGE -> 24.dp
    }
}

sealed class BufferingType {
    object Circular : BufferingType()
    object DotChase : BufferingType()
    object RotatingDots : BufferingType()
}