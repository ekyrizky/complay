package com.ekyrizky.complay.ui.horizontal

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.ekyrizky.complay.designsystem.components.controller.ComplayPlayerControls
import com.ekyrizky.complay.designsystem.components.controller.ComplaySeekBar
import com.ekyrizky.complay.designsystem.utils.PlayerControllerActions
import com.ekyrizky.complay.designsystem.utils.PlayerControllerType
import com.ekyrizky.complay.model.PlayerEvent
import com.ekyrizky.complay.player.PlayerManager

@OptIn(UnstableApi::class)
@Composable
internal fun ComplayHOverlay(
    playerManager: PlayerManager?,
    onEvent: (PlayerEvent) -> Unit,
    modifier: Modifier = Modifier,
    controllerType: PlayerControllerType = PlayerControllerType.STANDARD,
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        if (playerManager != null) {
            val state = playerManager.playerState.collectAsState()
            val isPlaying = state.value.isPlaying
            val actions = when (controllerType) {
                PlayerControllerType.MINIMAL -> {
                    PlayerControllerActions.MinimalActions(
                        onPlay = { onEvent(PlayerEvent.Play) },
                        onPause = { onEvent(PlayerEvent.Pause) }
                    )
                }

                PlayerControllerType.STANDARD -> PlayerControllerActions.StandardActions(
                    onPlay = { onEvent(PlayerEvent.Play) },
                    onPause = { onEvent(PlayerEvent.Pause) },
                    onForward = { onEvent(PlayerEvent.SeekForward(10)) },
                    onBackward = { onEvent(PlayerEvent.SeekBackward(10)) }
                )

                PlayerControllerType.SKIP -> PlayerControllerActions.SkipActions(
                    onPlay = { onEvent(PlayerEvent.Play) },
                    onPause = { onEvent(PlayerEvent.Pause) },
                    onSkipNext = { onEvent(PlayerEvent.SkipNext) },
                    onSkipPrevious = { onEvent(PlayerEvent.SkipPrevious) }
                )
            }

            ComplayPlayerControls(
                type = controllerType,
                actions = actions,
                isPlaying = isPlaying,
                modifier = Modifier.align(Alignment.Center),
                tint = Color.White
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ComplaySeekBar(
                    positionMs = state.value.currentPosition,
                    durationMs = state.value.duration,
                    onSeekFinished = { target -> onEvent(PlayerEvent.SeekTo(target)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}