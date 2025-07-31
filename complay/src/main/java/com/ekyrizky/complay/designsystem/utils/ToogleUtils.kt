package com.ekyrizky.complay.designsystem.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ekyrizky.complay.designsystem.theme.ComplayTheme

enum class LikeDislikeState {
    NEUTRAL, LIKED, DISLIKED
}

data class LikeDislikeColors(
    val defaultColor: Color,
    val likeActiveColor: Color,
    val dislikeActiveColor: Color
) {
    companion object {
        @Composable
        fun default() = LikeDislikeColors(
            defaultColor = ComplayTheme.colors.onSurfaceVariant,
            likeActiveColor = ComplayTheme.colors.primary,
            dislikeActiveColor = ComplayTheme.colors.error
        )
    }
}