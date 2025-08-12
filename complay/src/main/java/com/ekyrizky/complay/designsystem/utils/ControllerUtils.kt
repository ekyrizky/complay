package com.ekyrizky.complay.designsystem.utils

enum class PlaybackState { PLAY, PAUSE }

enum class PlayerControllerType {
    MINIMAL,
    STANDARD,
    SKIP
}

sealed class PlayerControllerActions {
    data class MinimalActions(
        val onPlayPause: () -> Unit
    ) : PlayerControllerActions()

    data class StandardActions(
        val onPlayPause: () -> Unit,
        val onForward: () -> Unit,
        val onRewind: () -> Unit
    ) : PlayerControllerActions()

    data class SkipActions(
        val onPlayPause: () -> Unit,
        val onSkipNext: () -> Unit,
        val onSkipPrevious: () -> Unit
    ) : PlayerControllerActions()
}