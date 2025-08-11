package com.ekyrizky.complay.model

interface PlayerAnalytics {
    fun onVideoStarted(video: Video)
    fun onVideoCompleted(video: Video, watchTime: Long)
    fun onError(error: PlayerError)
    fun onBuffering(position: Long)
}