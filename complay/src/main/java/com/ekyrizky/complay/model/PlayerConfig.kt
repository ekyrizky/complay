package com.ekyrizky.complay.model

data class PlayerConfig(
    val enablePositionUpdates: Boolean = true,
    val positionUpdateInterval: Long = 100L,
    val enableDrmSupport: Boolean = true,
    val enableAnalytics: Boolean = false,
)