package com.ekyrizky.complay.ui

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.ekyrizky.complay.model.Video
import com.ekyrizky.complay.player.PlayerManager

@OptIn(UnstableApi::class)
@Composable
internal fun ComplayPlayerView(
    video: Video,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    val playerManager = remember(video.id) { PlayerManager.create(context) }
    val exoPlayer = remember(video.id) { playerManager.createExoPlayer() }

    LaunchedEffect(video.id) {
        Log.d("ComplayPlayerView", "Preparing video: ${video.id}")
        playerManager.prepareVideo(video)
        playerManager.play()
    }

    DisposableEffect(video.id) {
        onDispose {
            Log.d("ComplayPlayerView", "Disposing video: ${video.id}")
            playerManager.release()
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            PlayerView(ctx).apply {
                useController = false
                player = exoPlayer
            }
        }
    )
}