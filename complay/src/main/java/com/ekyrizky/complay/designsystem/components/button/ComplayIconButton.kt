package com.ekyrizky.complay.designsystem.components.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ekyrizky.complay.designsystem.theme.ComplayTheme

@Composable
fun ComplayIconButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    iconSize: Dp = 24.dp,
    buttonSize: Dp = 24.dp,
    text: String? = null,
    textStyle: TextStyle = ComplayTheme.typography.labelMedium,
    textColor: Color = Color.Unspecified,
    textPadding: Dp = 2.dp
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(buttonSize)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(iconSize)
            )
        }

        text?.let {
            Text(
                text = it,
                style = textStyle,
                color = textColor,
                modifier = Modifier.padding(top = textPadding)
            )
        }
    }
}

// Previews
@Preview(name = "Icon Only", showBackground = true)
@Composable
private fun ComplayIconButtonPreview() {
    ComplayTheme {
        ComplayIconButton(
            icon = ComplayTheme.icons.play,
            contentDescription = "Play",
            onClick = {},
        )
    }
}

@Preview(name = "With Text", showBackground = true)
@Composable
private fun ComplayIconButtonWithTextPreview() {
    ComplayTheme {
        ComplayIconButton(
            icon = ComplayTheme.icons.like,
            contentDescription = "Like",
            onClick = {},
            text = "Like",
        )
    }
}