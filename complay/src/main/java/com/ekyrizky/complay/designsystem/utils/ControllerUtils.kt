package com.ekyrizky.complay.designsystem.utils

enum class PlayerControllerType {
    MINIMAL,
    STANDARD,
    SKIP
}

sealed class PlayerControllerActions {
    data class MinimalActions(
        val onPlay: () -> Unit,
        val onPause: () -> Unit
    ) : PlayerControllerActions()

    data class StandardActions(
        val onPlay: () -> Unit,
        val onPause: () -> Unit,
        val onForward: () -> Unit,
        val onBackward: () -> Unit
    ) : PlayerControllerActions()

    data class SkipActions(
        val onPlay: () -> Unit,
        val onPause: () -> Unit,
        val onSkipNext: () -> Unit,
        val onSkipPrevious: () -> Unit
    ) : PlayerControllerActions()
}