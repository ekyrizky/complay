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
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.TrackGroupArray
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@UnstableApi
class PlayerManager private constructor(
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

    val isPlaying: StateFlow<Boolean> = _playerState.map { it.isPlaying }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

    val currentPosition: StateFlow<Long> = playerState.map { it.currentPosition }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = 0L
    )

    val duration: StateFlow<Long> = _playerState.map { it.duration }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = 0L
    )

    private var positionUpdateJob: Job? = null
    private var currentVideo: Video? = null

    private fun getOrCreatePlayer(): ExoPlayer {
        return exoPlayer ?: createExoPlayer()
    }

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
            .apply {
                loadControl?.let { setLoadControl(it) }
            }
            .build()
            .also { player ->
                player.addListener(playerListener)
                exoPlayer = player
            }
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
        Log.d(TAG, "Preparing video: ${video.url}")

        try {
            updateState { currentState -> currentState.copy(isLoading = true, error = null) }
            currentVideo = video

            val mediaItem = createMediaItem(video)
            val player = getOrCreatePlayer()
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

        if (video.isDrmProtected && video.licenseUrl.isNotEmpty()) {
            val drmConfig = MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                .setLicenseUri(video.licenseUrl)
                .setMultiSession(true)
                .build()
            builder.setDrmConfiguration(drmConfig)
        }

        return builder.build()
    }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position.coerceAtLeast(0))
    }

    fun setVolume(volume: Float) {
        exoPlayer?.volume = volume.coerceIn(0f, 1f)
    }

    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    fun getDuration(): Long = exoPlayer?.duration?.takeIf { it != C.TIME_UNSET } ?: 0L

    fun getCurrentTracks(): Tracks? {
        return exoPlayer?.currentTracks
    }

    fun getTrackGroups(): TrackGroupArray? {
        return exoPlayer?.currentTrackGroups
    }

    fun selectTrack(trackType: Int, groupIndex: Int, trackIndex: Int) {
        val selector = trackSelector ?: return
        val player = exoPlayer ?: return
        val groups = player.currentTrackGroups
        if (groupIndex < 0 || groupIndex >= groups.length) return
        val group = groups.get(groupIndex)
        if (trackIndex < 0 || trackIndex >= group.length) return
        val override = TrackSelectionOverride(group, listOf(trackIndex))
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
        if (_playerState.value.isPlaying) {
            pause()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
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
        analytics?.onError(error)
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