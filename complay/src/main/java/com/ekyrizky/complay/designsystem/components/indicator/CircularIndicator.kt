package com.ekyrizky.complay.designsystem.components.indicator

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ekyrizky.complay.designsystem.utils.BufferingSize

@Composable
internal fun CircularIndicator(
    size: BufferingSize,
    color: Color,
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier.size(size.toIndicatorSize()),
        color = color,
        strokeWidth = when (size) {
            BufferingSize.SMALL -> 2.dp
            BufferingSize.MEDIUM -> 3.dp
            BufferingSize.LARGE -> 4.dp
        }
    )
}