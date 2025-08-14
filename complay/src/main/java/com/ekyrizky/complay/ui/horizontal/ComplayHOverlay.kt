package com.ekyrizky.complay.ui.horizontal

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.ekyrizky.complay.designsystem.components.button.ComplayIconButton
import com.ekyrizky.complay.designsystem.components.controller.ComplayPlayerControls
import com.ekyrizky.complay.designsystem.components.controller.ComplaySeekBar
import com.ekyrizky.complay.designsystem.theme.ComplayTheme
import com.ekyrizky.complay.designsystem.utils.PlayerControllerActions
import com.ekyrizky.complay.designsystem.utils.PlayerControllerType
import com.ekyrizky.complay.model.PlayerEvent
import com.ekyrizky.complay.player.PlayerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
internal fun ComplayHOverlay(
    playerManager: PlayerManager?,
    onEvent: (PlayerEvent) -> Unit,
    modifier: Modifier = Modifier,
    controllerType: PlayerControllerType = PlayerControllerType.STANDARD,
    isFullscreen: Boolean = false,
    onToggleFullscreen: () -> Unit = {},
    isLocked: Boolean = false,
    onToggleLock: () -> Unit = {}
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        if (playerManager != null) {
            val state = playerManager.playerState.collectAsState()
            val isPlaying = state.value.isPlaying
            val lockLabel = if (isLocked) "Locked" else "Unlocked"
            var showLockLabel by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
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

            if (!isLocked) {
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
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    ComplaySeekBar(
                        positionMs = state.value.currentPosition,
                        durationMs = state.value.duration,
                        onSeekFinished = { target -> onEvent(PlayerEvent.SeekTo(target)) }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        ComplayIconButton(
                            icon = if (isFullscreen) ComplayTheme.icons.fullscreenExit else ComplayTheme.icons.fullscreen,
                            contentDescription = if (isFullscreen) "Exit fullscreen" else "Enter fullscreen",
                            onClick = onToggleFullscreen,
                            tint = Color.White,
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }
            }

            ComplayIconButton(
                icon = if (isLocked) ComplayTheme.icons.lock else ComplayTheme.icons.unlock,
                contentDescription = if (isLocked) "Lock" else "UnLock",
                onClick = {
                    onToggleLock()
                    showLockLabel = true
                    scope.launch {
                        delay(2000)
                        showLockLabel = false
                    }
                },
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            )

            if (showLockLabel) {
                Text(
                    text = lockLabel,
                    color = Color.White,
                    style = ComplayTheme.typography.labelMedium,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .background(Color.Black.copy(alpha = 0.3f))
                        .padding(4.dp)
                )
            }
        }
    }
}