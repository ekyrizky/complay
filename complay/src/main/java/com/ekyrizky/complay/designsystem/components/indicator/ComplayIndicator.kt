package com.ekyrizky.complay.designsystem.components.indicator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ekyrizky.complay.designsystem.theme.ComplayTheme
import com.ekyrizky.complay.designsystem.utils.BufferingSize
import com.ekyrizky.complay.designsystem.utils.BufferingType

@Composable
fun ComplayIndicator(
    type: BufferingType = BufferingType.Circular,
    size: BufferingSize = BufferingSize.MEDIUM,
    color: Color = ComplayTheme.colors.primary,
    modifier: Modifier = Modifier,
    isCentered: Boolean = true
) {
    val boxModifier = if (isCentered) modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)
    else modifier

    Box(modifier = boxModifier) {
        when (type) {
            BufferingType.Circular -> CircularIndicator(size, color)
            BufferingType.DotChase -> DotChaseIndicator(size, color)
            BufferingType.RotatingDots -> RotatingDotsIndicator(size, color)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun LoadingIndicatorPreview() {
    ComplayTheme {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ComplayIndicator(
                    type = BufferingType.Circular,
                    size = BufferingSize.SMALL,
                    isCentered = false
                )
                ComplayIndicator(
                    type = BufferingType.Circular,
                    size = BufferingSize.MEDIUM,
                    isCentered = false
                )
                ComplayIndicator(
                    type = BufferingType.Circular,
                    size = BufferingSize.LARGE,
                    isCentered = false
                )
            }

            // Dot chase indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ComplayIndicator(
                    type = BufferingType.DotChase,
                    size = BufferingSize.SMALL,
                    isCentered = false
                )
                ComplayIndicator(
                    type = BufferingType.DotChase,
                    size = BufferingSize.MEDIUM,
                    isCentered = false
                )
                ComplayIndicator(
                    type = BufferingType.DotChase,
                    size = BufferingSize.LARGE,
                    isCentered = false
                )
            }

            // Rotating dots indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ComplayIndicator(
                    type = BufferingType.RotatingDots,
                    size = BufferingSize.SMALL,
                    isCentered = false
                )
                ComplayIndicator(
                    type = BufferingType.RotatingDots,
                    size = BufferingSize.MEDIUM,
                    isCentered = false
                )
                ComplayIndicator(
                    type = BufferingType.RotatingDots,
                    size = BufferingSize.LARGE,
                    isCentered = false
                )
            }
        }
    }
}