package com.ekyrizky.complay.designsystem.components.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ekyrizky.complay.designsystem.theme.ComplayTheme

@Composable
fun OverlayButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomEnd,
    padding: Dp = 16.dp,
    shape: Shape = RoundedCornerShape(50),
    backgroundColor: Color = ComplayTheme.colors.surface,
    contentColor: Color = ComplayTheme.colors.onSurface
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = alignment
    ) {
        Button(
            onClick = onClick,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor
            ),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(text = text)
        }
    }
}

@Preview(name = "Overlay Button", showBackground = true)
@Composable
fun OverlayButtonPreview() {
    ComplayTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            OverlayButton(
                text = "Skip Ad",
                onClick = { },
                alignment = Alignment.BottomEnd,
                padding = 24.dp
            )
        }
    }
}