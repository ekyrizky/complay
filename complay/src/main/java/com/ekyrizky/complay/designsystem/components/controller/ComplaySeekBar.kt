package com.ekyrizky.complay.designsystem.components.controller

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ekyrizky.complay.designsystem.theme.ComplayTheme
import com.ekyrizky.complay.utils.toTimeString
import kotlin.math.abs
import kotlin.math.max

@Composable
fun ComplaySeekBar(
    positionMs: Long,
    durationMs: Long,
    modifier: Modifier = Modifier,
    onSeekFinished: (Long) -> Unit,
    onSeekChanged: ((Long) -> Unit)? = null,
    showTimeLabels: Boolean = true,
    thumbRadius: Dp = 8.dp,
    touchHeight: Dp = 36.dp,
    trackHeight: Dp = 3.dp,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = 0.35f),
    textColor: Color = Color.White
) {
    val clampedDurationMs = if (durationMs > 0) durationMs else 1L
    var isDragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableStateOf(0f) }

    val sliderValue by remember(positionMs, durationMs, isDragging, dragFraction) {
        derivedStateOf {
            if (isDragging) dragFraction
            else (positionMs.toFloat() / clampedDurationMs.toFloat()).coerceIn(0f, 1f)
        }
    }

    val density = LocalDensity.current
    val thumbRadiusPx = with(density) { thumbRadius.toPx() }
    val trackHeightPx = with(density) { trackHeight.toPx() }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(touchHeight)
                .padding(horizontal = 0.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onPress = { offset ->
                        val canvasWidth = size.width
                        val thumbCenterX = sliderValue * canvasWidth
                        val thumbHitSlop = max(thumbRadiusPx * 1.5f, with(density) { 16.dp.toPx() })
                        val nearThumb = abs(offset.x - thumbCenterX) <= thumbHitSlop
                        val targetFraction = (offset.x / canvasWidth).coerceIn(0f, 1f)

                        val released = tryAwaitRelease()
                        if (released && !nearThumb) {
                            onSeekFinished((targetFraction * clampedDurationMs).toLong())
                        }
                    })
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            val canvasWidth = size.width
                            val thumbCenterX = sliderValue * canvasWidth
                            val thumbHitSlop = max(thumbRadiusPx * 1.5f, with(density) { 16.dp.toPx() })
                            val nearThumb = abs(startOffset.x - thumbCenterX) <= thumbHitSlop
                            isDragging = true
                            if (nearThumb) {
                                dragFraction = (startOffset.x / canvasWidth).coerceIn(0f, 1f)
                                onSeekChanged?.invoke((dragFraction * clampedDurationMs).toLong())
                            }
                        },
                        onDrag = { change, dragAmount ->
                            val canvasWidth = size.width
                            dragFraction = ((change.position.x) / canvasWidth).coerceIn(0f, 1f)
                            onSeekChanged?.invoke((dragFraction * clampedDurationMs).toLong())
                            change.consumePositionChange()
                        },
                        onDragEnd = {
                            isDragging = false
                            onSeekFinished((dragFraction * clampedDurationMs).toLong())
                        }
                    )
                }
        ) {
            val width = size.width
            val centerY = size.height / 2f
            val cornerRadius = CornerRadius(trackHeightPx / 2f, trackHeightPx / 2f)

            drawRoundRect(
                color = inactiveColor,
                topLeft = Offset(0f, centerY - trackHeightPx / 2f),
                size = Size(width, trackHeightPx),
                cornerRadius = cornerRadius
            )

            val activeWidth = (sliderValue * width).coerceIn(0f, width)
            drawRoundRect(
                color = activeColor,
                topLeft = Offset(0f, centerY - trackHeightPx / 2f),
                size = Size(activeWidth, trackHeightPx),
                cornerRadius = cornerRadius
            )

            drawCircle(
                color = activeColor,
                center = Offset(activeWidth, centerY),
                radius = thumbRadiusPx
            )
        }

        if (showTimeLabels) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val leftTime = if (isDragging) (dragFraction * clampedDurationMs).toLong() else positionMs
                Text(
                    text = leftTime.toTimeString(),
                    style = ComplayTheme.typography.labelMedium,
                    color = textColor
                )
                Text(
                    text = durationMs.toTimeString(),
                    style = ComplayTheme.typography.labelMedium,
                    color = textColor
                )
            }
        }
    }
}

