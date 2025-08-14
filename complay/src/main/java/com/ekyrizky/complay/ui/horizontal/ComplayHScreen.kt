package com.ekyrizky.complay.ui.horizontal

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import com.ekyrizky.complay.model.PlaybackState
import com.ekyrizky.complay.model.PlayerEvent
import com.ekyrizky.complay.model.Video
import com.ekyrizky.complay.player.PlayerManager
import com.ekyrizky.complay.ui.ComplayPlayerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
internal fun ComplayHScreen(
    video: Video,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var overlayVisible by remember { mutableStateOf(true) }
    var isFullscreen by remember { mutableStateOf(false) }
    var isLocked by remember { mutableStateOf(false) }
    var hideJob by remember { mutableStateOf<Job?>(null) }

    val playerManager = remember { PlayerManager.create(context) }
    val playerState by playerManager.playerState.collectAsState()

    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(playerManager)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(playerManager)
            playerManager.onEvent(PlayerEvent.Release)
        }
    }

    fun showOverlayWithTimeout() {
        overlayVisible = true
        hideJob?.cancel()
        hideJob = scope.launch {
            delay(5000)
            overlayVisible = false
        }
    }

    val activity = context as? Activity

    LaunchedEffect(isFullscreen) {
        activity?.requestedOrientation = if (isFullscreen) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    val configuration = LocalConfiguration.current
    LaunchedEffect(configuration.orientation) {
        isFullscreen = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isFullscreen) Modifier.fillMaxSize() else Modifier.aspectRatio(16f / 9f))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (overlayVisible) {
                            overlayVisible = false
                            hideJob?.cancel()
                        } else {
                            showOverlayWithTimeout()
                        }
                    }
                )
            }
    ) {
        ComplayPlayerView(
            video = video,
            playerManager = playerManager,
            onPlayerReady = { player -> player.onEvent(PlayerEvent.Play) }
        )

        AnimatedVisibility(
            visible = overlayVisible && playerState.playbackState != PlaybackState.BUFFERING && !playerState.isLoading,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            ComplayHOverlay(
                playerManager = playerManager,
                onEvent = {
                    playerManager.onEvent(it)
                    showOverlayWithTimeout()
                },
                modifier = Modifier.fillMaxSize(),
                isFullscreen = isFullscreen,
                onToggleFullscreen = { isFullscreen = !isFullscreen },
                isLocked = isLocked,
                onToggleLock = { isLocked = !isLocked }
            )
        }

        AnimatedVisibility(
            visible = playerState.playbackState == PlaybackState.BUFFERING || playerState.isLoading,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(52.dp),
                color = Color.White,
                strokeWidth = 4.dp
            )
        }
    }
}