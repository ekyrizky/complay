package com.ekyrizky.complay.designsystem.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ekyrizky.complay.designsystem.theme.ComplayTheme
import com.ekyrizky.complay.designsystem.utils.LikeDislikeColors
import com.ekyrizky.complay.designsystem.utils.LikeDislikeState

@Composable
fun ComplayLikeDislikeButtons(
    state: LikeDislikeState,
    onStateChanged: (LikeDislikeState) -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    spacing: Dp = 16.dp,
    colors: LikeDislikeColors = LikeDislikeColors.default()
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Like Button
        ComplayIconButton(
            icon = ComplayTheme.icons.like,
            contentDescription = "Like",
            iconSize = iconSize,
            onClick = {
                onStateChanged(
                    if (state == LikeDislikeState.LIKED) LikeDislikeState.NEUTRAL
                    else LikeDislikeState.LIKED
                )
            },
            tint = if (state == LikeDislikeState.LIKED) colors.likeActiveColor else colors.defaultColor
        )

        // Dislike Button
        ComplayIconButton(
            icon = ComplayTheme.icons.dislike,
            contentDescription = "dislike",
            iconSize = iconSize,
            onClick = {
                onStateChanged(
                    if (state == LikeDislikeState.DISLIKED) LikeDislikeState.NEUTRAL
                    else LikeDislikeState.DISLIKED
                )
            },
            tint = if (state == LikeDislikeState.DISLIKED) colors.dislikeActiveColor else colors.defaultColor
        )
    }
}

@Preview(name = "Neutral State", showBackground = true)
@Composable
private fun LikeDislikeButtonsNeutralPreview() {
    ComplayTheme {
        ComplayLikeDislikeButtons(
            state = LikeDislikeState.NEUTRAL,
            onStateChanged = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Liked State", showBackground = true)
@Composable
private fun LikeDislikeButtonsLikedPreview() {
    ComplayTheme {
        ComplayLikeDislikeButtons(
            state = LikeDislikeState.LIKED,
            onStateChanged = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Disliked State", showBackground = true)
@Composable
private fun LikeDislikeButtonsDislikedPreview() {
    ComplayTheme {
        ComplayLikeDislikeButtons(
            state = LikeDislikeState.DISLIKED,
            onStateChanged = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Custom Colors", showBackground = true)
@Composable
private fun LikeDislikeButtonsCustomPreview() {
    ComplayTheme {
        ComplayLikeDislikeButtons(
            state = LikeDislikeState.LIKED,
            onStateChanged = {},
            colors = LikeDislikeColors(
                defaultColor = Color.LightGray,
                likeActiveColor = Color(0xFF4CAF50),
                dislikeActiveColor = Color(0xFFF44336)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}