package com.ekyrizky.complay.utils

fun Boolean?.orFalse() = this ?: false
fun Boolean?.orTrue() = this ?: true

fun Int?.orZero() = this ?: 0
fun Long?.orZero() = this ?: 0L

fun Long.toTimeString(): String {
	val totalSeconds = this / 1000
	val hours = totalSeconds / 3600
	val minutes = (totalSeconds % 3600) / 60
	val seconds = totalSeconds % 60
	return if (hours > 0) {
		String.format("%d:%02d:%02d", hours, minutes, seconds)
	} else {
		String.format("%02d:%02d", minutes, seconds)
	}
}