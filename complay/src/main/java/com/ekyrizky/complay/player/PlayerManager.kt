package com.ekyrizky.complay.player

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@UnstableApi
internal class PlayerManager private constructor(
    private val context: Context,
    private val config: PlayerConfig,
    private val analytics: PlayerAnalytics? = null
) : DefaultLifecycleObserver {

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
    private var trackSelector: DefaultTrackSelector? = null
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private var positionUpdateJob: Job? = null
    private var currentVideo: Video? = null
    private var resumeOnForeground: Boolean = false

    private fun getOrCreatePlayer(): ExoPlayer {
        return exoPlayer ?: createExoPlayer()
    }

    private val renderersFactory: DefaultRenderersFactory =
        DefaultRenderersFactory(context).setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)

    fun createExoPlayer(): ExoPlayer {
        if (exoPlayer != null) return exoPlayer!!

        val loadControl = config.bufferConfiguration?.let { bufferConfig ->
            DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    bufferConfig.minBufferMs,
                    bufferConfig.maxBufferMs,
                    bufferConfig.bufferForPlaybackMs,
                    bufferConfig.bufferForPlaybackAfterRebufferMs
                )
                .build()
        }

        val selector = DefaultTrackSelector(context)
        trackSelector = selector

        return ExoPlayer.Builder(context)
            .setTrackSelector(selector)
            .setRenderersFactory(renderersFactory)
            .apply {
                loadControl?.let { setLoadControl(it) }
            }
            .build()
            .also { player ->
                player.addListener(playerListener)
                player.setWakeMode(C.WAKE_MODE_LOCAL)
                exoPlayer = player
            }
    }

    fun onEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.Play -> play()
            is PlayerEvent.Pause -> pause()
            is PlayerEvent.SeekTo -> seekTo(event.position)
            is PlayerEvent.SeekForward -> seekForward(event.seconds)
            is PlayerEvent.SeekBackward -> seekBackward(event.seconds)
            is PlayerEvent.SkipNext -> skipNext()
            is PlayerEvent.SkipPrevious -> skipPrevious()
            is PlayerEvent.SetVolume -> setVolume(event.volume)
            is PlayerEvent.PrepareVideo -> prepareVideo(event.video)
            is PlayerEvent.Release -> release()
        }
    }

    private fun prepareVideo(video: Video) {
        Log.d(TAG, "Preparing video: ${video.url}")

        try {
            updateState { currentState -> currentState.copy(isLoading = true, error = null) }
            currentVideo = video

            val mediaItem = createMediaItem(video)
            val player = getOrCreatePlayer()

            val httpFactory = DefaultHttpDataSource.Factory().apply {
                if (video.headers.isNotEmpty()) {
                    setDefaultRequestProperties(video.headers)
                }
            }

            val mediaSource = if (video.url.endsWith(".mpd", ignoreCase = true)) {
                DashMediaSource.Factory(httpFactory).createMediaSource(mediaItem)
            } else {
                ProgressiveMediaSource.Factory(httpFactory).createMediaSource(mediaItem)
            }

            player.setMediaSource(mediaSource)
            player.prepare()

            if (config.enableAnalytics) {
                analytics?.onVideoStarted(video)
            }
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

        if (config.enableDrmSupport && video.isDrmProtected && video.licenseUrl.isNotEmpty()) {
            val drmConfig = MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                .setLicenseUri(video.licenseUrl)
                .setMultiSession(true)
                .build()
            builder.setDrmConfiguration(drmConfig)
        }

        return builder.build()
    }

    private fun play() {
        exoPlayer?.play()
    }

    private fun pause() {
        exoPlayer?.pause()
    }

    private fun seekTo(position: Long) {
        exoPlayer?.seekTo(position.coerceAtLeast(0))
    }

    private fun seekForward(seconds: Long) {
        exoPlayer?.let { player ->
            val currentPosition = player.currentPosition
            val targetPosition = (currentPosition + seconds * 1000L)
                .coerceAtMost(getDuration())
            seekTo(targetPosition)
        }
    }

    private fun seekBackward(seconds: Long) {
        exoPlayer?.let { player ->
            val currentPosition = player.currentPosition
            val targetPosition = (currentPosition - seconds * 1000L)
                .coerceAtLeast(0L)
            seekTo(targetPosition)
        }
    }

    private fun skipNext() {}
    private fun skipPrevious() {}

    private fun setVolume(volume: Float) {
        exoPlayer?.volume = volume.coerceIn(0f, 1f)
    }

    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    fun getDuration(): Long = exoPlayer?.duration?.takeIf { it != C.TIME_UNSET } ?: 0L

    fun getCurrentTracks(): Tracks? {
        return exoPlayer?.currentTracks
    }

    fun selectTrack(trackType: Int, groupIndex: Int, trackIndex: Int) {
        val selector = trackSelector ?: return
        val player = exoPlayer ?: return
        val matchingGroups = player.currentTracks.groups.filter { it.type == trackType }
        if (groupIndex < 0 || groupIndex >= matchingGroups.size) return
        val group = matchingGroups[groupIndex]
        val trackCount = group.length
        if (trackIndex < 0 || trackIndex >= trackCount) return
        val override = TrackSelectionOverride(group.mediaTrackGroup, listOf(trackIndex))
        val params = selector.parameters
            .buildUpon()
            .clearOverridesOfType(trackType)
            .addOverride(override)
            .build()
        selector.parameters = params
    }

    fun clearTrackSelection(trackType: Int) {
        val selector = trackSelector ?: return
        val params = selector.parameters
            .buildUpon()
            .clearOverridesOfType(trackType)
            .build()
        selector.parameters = params
    }

    override fun onPause(owner: LifecycleOwner) {
        resumeOnForeground = _playerState.value.isPlaying
        if (resumeOnForeground) pause()
    }

    override fun onResume(owner: LifecycleOwner) {
        if (resumeOnForeground) {
            play()
        }
        resumeOnForeground = false
    }

    override fun onDestroy(owner: LifecycleOwner) {
        release()
    }

    private fun release() {
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

    private fun updateState(update: (PlayerState) -> PlayerState) {
        _playerState.update(update)
    }

    private fun startPositionUpdates() {
        if (!config.enablePositionUpdates) return

        stopPositionUpdates()
        positionUpdateJob = coroutineScope.launch {
            while (isActive && exoPlayer != null) {
                val position = getCurrentPosition()
                updateState { currentState -> currentState.copy(currentPosition = position) }
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
        updateState { currentState -> currentState.copy(error = error, isLoading = false) }
        if (config.enableAnalytics) {
            analytics?.onError(error)
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateState { currentState -> currentState.copy(isPlaying = isPlaying) }

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

            updateState { currentState ->
                currentState.copy(
                    playbackState = state,
                    isLoading = isLoading,
                    duration = duration,
                    error = null
                )
            }

            if (playbackState == Player.STATE_BUFFERING) {
                if (config.enableAnalytics) {
                    analytics?.onBuffering(getCurrentPosition())
                }
            } else if (playbackState == Player.STATE_ENDED) {
                currentVideo?.let { video ->
                    if (config.enableAnalytics) {
                        analytics?.onVideoCompleted(video, getCurrentPosition())
                    }
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