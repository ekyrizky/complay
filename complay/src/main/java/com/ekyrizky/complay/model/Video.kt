package com.ekyrizky.complay.model

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val id: String,
    val url: String,
    val title: String = "",
    val description: String = "",
    val thumbnailUrl: String = "",
    val duration: Long = 0L,
    val isDrmProtected: Boolean = false,
    val licenseUrl: String = "",
    val headers: Map<String, String> = emptyMap()
)