package com.ekyrizky.complay.ui.horizontal

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.util.UnstableApi
import com.ekyrizky.complay.designsystem.components.controller.ComplayPlayerControls
import com.ekyrizky.complay.designsystem.utils.PlaybackState
import com.ekyrizky.complay.designsystem.utils.PlayerControllerActions
import com.ekyrizky.complay.designsystem.utils.PlayerControllerType
import com.ekyrizky.complay.player.PlayerManager

@OptIn(UnstableApi::class)
@Composable
internal fun ComplayHOverlay(
    playerManager: PlayerManager?,
    onPlayPause: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    val isPlaying by playerManager?.isPlaying?.collectAsState(initial = false)
        ?: remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        if (playerManager != null) {
            ComplayPlayerControls(
                type = PlayerControllerType.MINIMAL,
                actions = PlayerControllerActions.MinimalActions(
                    onPlayPause = { onPlayPause(isPlaying) }
                ),
                state = if (isPlaying) PlaybackState.PLAY else PlaybackState.PAUSE,
                modifier = Modifier
                    .align(Alignment.Center),
                tint = Color.White
            )
        }
    }
}