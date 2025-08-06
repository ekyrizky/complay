package com.ekyrizky.complay.player

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ekyrizky.complay.model.PlaybackState
import com.ekyrizky.complay.model.PlayerAnalytics
import com.ekyrizky.complay.model.PlayerConfig
import com.ekyrizky.complay.model.PlayerError
import com.ekyrizky.complay.model.PlayerEvent
import com.ekyrizky.complay.model.PlayerState
import com.ekyrizky.complay.model.Video
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerManager private constructor(
    private val context: Context,
    private val config: PlayerConfig,
    private val analytics: PlayerAnalytics? = null
) : LifecycleObserver {

    companion object {
        private const val TAG = "Complay Player Manager"

        fun create(
            context: Context,
            config: PlayerConfig = PlayerConfig(),
            analytics: PlayerAnalytics? = null
        ): PlayerManager {
            return PlayerManager(
                context.applicationContext,
                config,
                analytics
            )
        }
    }

    private var exoPlayer: ExoPlayer? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    val isPlaying: StateFlow<Boolean> = playerState.map { it.isPlaying }
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), false)

    val currentPosition: StateFlow<Long> = playerState.map { it.currentPosition }
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), 0L)

    val duration: StateFlow<Long> = playerState.map { it.duration }
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), 0L)

    private var positionUpdateJob: Job? = null
    private var currentVideo: Video? = null

    private val player: ExoPlayer by lazy {
        createExoPlayer()
    }

    fun createExoPlayer(): ExoPlayer {

        exoPlayer = ExoPlayer.Builder(context)
            .build()
            .also { player ->
                player.addListener(playerListener)
                exoPlayer = player
            }

        return exoPlayer!!
    }

    fun handleEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.Play -> play()
            is PlayerEvent.Pause -> pause()
            is PlayerEvent.SeekTo -> seekTo(event.position)
            is PlayerEvent.SetVolume -> setVolume(event.volume)
            is PlayerEvent.PrepareVideo -> prepareVideo(event.video)
            is PlayerEvent.Release -> release()
        }
    }


    fun prepareVideo(video: Video) {
        Log.d("ViPlusPlayerManager", "Preparing video: ${video.url}")

        try {
            updateState { copy(isLoading = true, error = null) }
            currentVideo = video

            val mediaItem = createMediaItem(video)
            player.apply {
                setMediaItem(mediaItem)
                prepare()
            }

            analytics?.onVideoStarted(video)
        } catch (e: Exception) {
            Log.e(TAG, "Error preparing video", e)
            handleError(PlayerError(-1, "Failed to prepare video", e))
        }
    }

    private fun createMediaItem(video: Video): MediaItem {
        val builder = MediaItem.Builder().setUri(video.url)

        if (video.headers.isNotEmpty()) {
            builder.setRequestMetadata(
                MediaItem.RequestMetadata.Builder()
                    .setExtras(Bundle().apply {
                        video.headers.forEach { (key, value) ->
                            putString(key, value)
                        }
                    }).build()
            )
        }

        if (video.isDrmProtected && !video.licenseUrl.isNullOrEmpty()) {
            val drmConfig = MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                .setLicenseUri(video.licenseUrl)
                .setMultiSession(true)
                .build()
            builder.setDrmConfiguration(drmConfig)
        }

        return builder.build()
    }

    fun play() {
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun seekTo(position: Long) {
        player.seekTo(position.coerceAtLeast(0))
    }

    fun setVolume(volume: Float) {
        player.volume = volume.coerceIn(0f, 1f)
    }

    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    fun getDuration(): Long = exoPlayer?.duration?.takeIf { it != C.TIME_UNSET } ?: 0L

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppPaused() {
        if (_playerState.value.isPlaying) {
            pause()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onAppDestroyed() {
        release()
    }

    fun release() {
        Log.d(TAG, "Releasing player")
        coroutineScope.cancel()

        stopPositionUpdates()

        exoPlayer?.let { player ->
            player.removeListener(playerListener)
            player.stop()
            player.clearMediaItems()
            player.release()
        }
        exoPlayer = null

        _playerState.value = PlayerState()
        currentVideo = null
    }

    private fun updateState(update: PlayerState.() -> PlayerState) {
        _playerState.value = update
    }

    private fun startPositionUpdates() {
        if (!config.enablePositionUpdates) return

        stopPositionUpdates()
        positionUpdateJob = coroutineScope.launch {
            while (isActive) {
                val position = getCurrentPosition()
                updateState { copy(currentPosition = position) }
                delay(config.positionUpdateInterval)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    private fun handleError(error: PlayerError) {
        Log.e(TAG, "Player error: ${error.message}", error.cause)
        updateState { copy(error = error, isLoading = false) }
        analytics?.onError(error)
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateState { copy(isPlaying = isPlaying) }

            if (isPlaying) {
                startPositionUpdates()
            } else {
                stopPositionUpdates()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            val state = when (playbackState) {
                Player.STATE_IDLE -> PlaybackState.IDLE
                Player.STATE_BUFFERING -> PlaybackState.BUFFERING
                Player.STATE_READY -> PlaybackState.READY
                Player.STATE_ENDED -> PlaybackState.ENDED
                else -> PlaybackState.IDLE
            }

            Log.d(TAG, "Playback state changed to: $state")
            val isLoading = playbackState == Player.STATE_BUFFERING
            val duration = if (playbackState == Player.STATE_READY) getDuration() else _playerState.value.duration

            updateState {
                copy(
                    playbackState = state,
                    isLoading = isLoading,
                    duration = duration,
                    error = null
                )
            }

            if (playbackState == Player.STATE_BUFFERING) {
                analytics?.onBuffering(getCurrentPosition())
            } else if (playbackState == Player.STATE_ENDED) {
                currentVideo?.let { video ->
                    analytics?.onVideoCompleted(video, getCurrentPosition())
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            val playerError = PlayerError(
                code = error.errorCode,
                message = error.message ?: "Unknown playback error",
                cause = error
            )
            handleError(playerError)
        }
    }
}