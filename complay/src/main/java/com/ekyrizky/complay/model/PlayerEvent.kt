package com.ekyrizky.complay.model

sealed class PlayerEvent {
    object Play : PlayerEvent()
    object Pause : PlayerEvent()
    data class SeekTo(val position: Long) : PlayerEvent()
    data class SetVolume(val volume: Float) : PlayerEvent()
    data class PrepareVideo(val video: Video) : PlayerEvent()
    object Release : PlayerEvent()
}