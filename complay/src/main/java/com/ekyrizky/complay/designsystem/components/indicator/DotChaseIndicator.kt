package com.ekyrizky.complay.designsystem.components.indicator

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.ekyrizky.complay.designsystem.utils.BufferingSize

@Composable
internal fun DotChaseIndicator(
    size: BufferingSize,
    color: Color,
    modifier: Modifier = Modifier
) {
    val dotCount = 3
    val dotSize = size.toDotSize()
    val animationDuration = 1000
    val infiniteTransition = rememberInfiniteTransition()
    val delayStep = animationDuration / dotCount

    Row(modifier = modifier) {
        repeat(dotCount) { index ->
            val delay = index * delayStep
            val scale = infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = animationDuration
                        0.3f at delay
                        1f at delay + (animationDuration / 2)
                        0.3f at delay + animationDuration
                    },
                    repeatMode = RepeatMode.Restart
                )
            )

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
                    .clip(CircleShape)
                    .background(color)
            )
            if (index < dotCount - 1) Spacer(modifier = Modifier.width(dotSize / 2))
        }
    }
}