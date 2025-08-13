package com.ekyrizky.complay.model

internal sealed interface PlayerEvent {
    object Play : PlayerEvent
    object Pause : PlayerEvent
    data class SeekTo(val position: Long) : PlayerEvent
    data class SeekForward(val seconds: Long) : PlayerEvent
    data class SeekBackward(val seconds: Long) : PlayerEvent
    data class SetVolume(val volume: Float) : PlayerEvent
    data class PrepareVideo(val video: Video) : PlayerEvent
    object Release : PlayerEvent
}