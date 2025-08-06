package com.ekyrizky.complay.model

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val isLoading: Boolean = false,
    val error: PlayerError? = null
)

enum class PlaybackState {
    IDLE, BUFFERING, READY, ENDED
}