package com.ekyrizky.complay.model

data class PlayerError(
    val code: Int,
    val message: String,
    val cause: Throwable? = null
)