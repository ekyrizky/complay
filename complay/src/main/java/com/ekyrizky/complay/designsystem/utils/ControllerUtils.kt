package com.ekyrizky.complay.designsystem.utils

enum class PlaybackState { PLAY, PAUSE }

enum class PlayerControllerType {
    MINIMAL,
    STANDARD,
    SKIP
}

sealed class PlayerControllerActions {
    data class MinimalActions(
        val onPlayPause: (PlaybackState) -> Unit
    ) : PlayerControllerActions()

    data class StandardActions(
        val onPlayPause: (PlaybackState) -> Unit,
        val onForward: () -> Unit,
        val onRewind: () -> Unit
    ) : PlayerControllerActions()

    data class SkipActions(
        val onPlayPause: (PlaybackState) -> Unit,
        val onSkipNext: () -> Unit,
        val onSkipPrevious: () -> Unit
    ) : PlayerControllerActions()
}