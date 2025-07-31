package com.ekyrizky.complay.designsystem.components.controller

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ekyrizky.complay.designsystem.theme.ComplayTheme
import com.ekyrizky.complay.designsystem.utils.PlaybackState
import com.ekyrizky.complay.designsystem.utils.PlayerControllerActions
import com.ekyrizky.complay.designsystem.utils.PlayerControllerType

@Composable
fun ComplayPlayerControls(
    type: PlayerControllerType,
    actions: PlayerControllerActions,
    state: PlaybackState,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    iconSize: Dp = 24.dp,
    buttonSize: Dp = 48.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Button
        when (type) {
            PlayerControllerType.MINIMAL -> { /* No left button */ }
            PlayerControllerType.STANDARD -> {
                val standardActions = actions as PlayerControllerActions.StandardActions
                PlayerControlButton(
                    icon = ComplayTheme.icons.rewind10,
                    contentDescription = "Rewind 10 seconds",
                    onClick = standardActions.onRewind,
                    tint = tint,
                    iconSize = iconSize,
                    buttonSize = buttonSize
                )
            }
            PlayerControllerType.SKIP -> {
                val skipActions = actions as PlayerControllerActions.SkipActions
                PlayerControlButton(
                    icon = ComplayTheme.icons.skipPrevious,
                    contentDescription = "Previous Track",
                    onClick = skipActions.onSkipPrevious,
                    tint = tint,
                    iconSize = iconSize,
                    buttonSize = buttonSize
                )
            }
        }

        // Play/Pause Button (common to all)
        val onPlayPause = when (actions) {
            is PlayerControllerActions.MinimalActions -> actions.onPlayPause
            is PlayerControllerActions.StandardActions -> actions.onPlayPause
            is PlayerControllerActions.SkipActions -> actions.onPlayPause
        }

        PlayerControlButton(
            icon = if (state == PlaybackState.PLAY) ComplayTheme.icons.pause else ComplayTheme.icons.play,
            contentDescription = if (state == PlaybackState.PLAY) "Pause" else "Play",
            onClick = { onPlayPause(if (state == PlaybackState.PLAY) PlaybackState.PAUSE else PlaybackState.PLAY) },
            tint = tint,
            iconSize = iconSize,
            buttonSize = buttonSize
        )

        // Right Button
        when (type) {
            PlayerControllerType.MINIMAL -> { /* No right button */ }
            PlayerControllerType.STANDARD -> {
                val standardActions = actions as PlayerControllerActions.StandardActions
                PlayerControlButton(
                    icon = ComplayTheme.icons.forward10,
                    contentDescription = "Forward 10 seconds",
                    onClick = standardActions.onForward,
                    tint = tint,
                    iconSize = iconSize,
                    buttonSize = buttonSize
                )
            }
            PlayerControllerType.SKIP -> {
                val skipActions = actions as PlayerControllerActions.SkipActions
                PlayerControlButton(
                    icon = ComplayTheme.icons.skipNext,
                    contentDescription = "Next Track",
                    onClick = skipActions.onSkipNext,
                    tint = tint,
                    iconSize = iconSize,
                    buttonSize = buttonSize
                )
            }
        }
    }
}

@Preview(name = "Minimal - Play")
@Composable
private fun MinimalPreview() {
    ComplayTheme {
        ComplayPlayerControls(
            type = PlayerControllerType.MINIMAL,
            actions = PlayerControllerActions.MinimalActions(
                onPlayPause = {}
            ),
            state = PlaybackState.PLAY,
            modifier = Modifier.width(100.dp)
        )
    }
}

@Preview(name = "Standard - Pause")
@Composable
private fun StandardPreview() {
    ComplayTheme {
        ComplayPlayerControls(
            type = PlayerControllerType.STANDARD,
            actions = PlayerControllerActions.StandardActions(
                onPlayPause = {},
                onForward = {},
                onRewind = {}
            ),
            state = PlaybackState.PAUSE,
            modifier = Modifier.width(250.dp)
        )
    }
}

@Preview(name = "Skip - Play")
@Composable
private fun SkipPreview() {
    ComplayTheme {
        ComplayPlayerControls(
            type = PlayerControllerType.SKIP,
            actions = PlayerControllerActions.SkipActions(
                onPlayPause = {},
                onSkipNext = {},
                onSkipPrevious = {}
            ),
            state = PlaybackState.PLAY,
            modifier = Modifier.width(250.dp)
        )
    }
}