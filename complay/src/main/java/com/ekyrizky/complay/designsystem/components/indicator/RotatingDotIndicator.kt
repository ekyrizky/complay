package com.ekyrizky.complay.designsystem.components.indicator

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.ekyrizky.complay.designsystem.utils.BufferingSize
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun RotatingDotsIndicator(
    size: BufferingSize,
    color: Color,
    modifier: Modifier = Modifier
) {
    val dotCount = 4
    val dotSize = size.toDotSize()
    val circleRadius = size.toCircleRadius()
    val animationDuration = 2000
    val infiniteTransition = rememberInfiniteTransition()
    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(
        modifier = modifier.size(circleRadius * 2)
    ) {
        val centerX = this.size.width / 2
        val centerY = this.size.height / 2

        val angleStep = 360f / dotCount
        repeat(dotCount) { i ->
            val angle = Math.toRadians((i * angleStep + rotation.value).toDouble())
            val x = centerX + circleRadius.toPx() * cos(angle).toFloat()
            val y = centerY + circleRadius.toPx() * sin(angle).toFloat()
            drawCircle(
                color = color,
                radius = dotSize.toPx() / 2,
                center = Offset(x, y)
            )
        }
    }
}