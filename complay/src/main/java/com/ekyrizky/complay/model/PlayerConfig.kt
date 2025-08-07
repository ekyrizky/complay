package com.ekyrizky.complay.model

data class PlayerConfig(
    val enablePositionUpdates: Boolean = true,
    val positionUpdateInterval: Long = 100L,
    val enableDrmSupport: Boolean = true,
    val enableAnalytics: Boolean = false,
    val bufferConfiguration: BufferConfiguration? = null
)

data class BufferConfiguration(
    val minBufferMs: Int = 15_000,
    val maxBufferMs: Int = 50_000,
    val bufferForPlaybackMs: Int = 2_500,
    val bufferForPlaybackAfterRebufferMs: Int = 5_000
) {
    companion object {
        val LOW_LATENCY = BufferConfiguration(
            minBufferMs = 5_000,
            maxBufferMs = 15_000,
            bufferForPlaybackMs = 1_000,
            bufferForPlaybackAfterRebufferMs = 2_000
        )

        val HIGH_QUALITY = BufferConfiguration(
            minBufferMs = 30_000,
            maxBufferMs = 120_000,
            bufferForPlaybackMs = 5_000,
            bufferForPlaybackAfterRebufferMs = 10_000
        )

        val MOBILE_OPTIMIZED = BufferConfiguration(
            minBufferMs = 10_000,
            maxBufferMs = 30_000,
            bufferForPlaybackMs = 2_000,
            bufferForPlaybackAfterRebufferMs = 3_000
        )
    }
}