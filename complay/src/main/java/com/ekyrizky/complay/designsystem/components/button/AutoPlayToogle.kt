package com.ekyrizky.complay.designsystem.components.button

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ekyrizky.complay.designsystem.theme.ComplayColors
import com.ekyrizky.complay.designsystem.theme.ComplayIcons

@Composable
fun AutoPlayToggle(
    modifier: Modifier = Modifier,
    isAutoPlayEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    icons: ComplayIcons = ComplayIcons.defaultIcons(),
    colors: ComplayColors = ComplayColors.defaultColors()
) {
    val transition = updateTransition(targetState = isAutoPlayEnabled, label = "autoplay_toggle")

    val thumbOffset by transition.animateDp(label = "thumb_offset") { enabled ->
        if (enabled) 36.dp else 0.dp
    }

    val iconRes = if (isAutoPlayEnabled) icons.play else icons.pause

    Box(
        modifier = modifier
            .wrapContentSize()
            .clickable { onToggle(!isAutoPlayEnabled) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(width = 64.dp, height = 24.dp)
                .background(
                    color = colors.onSurfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(32.dp)
                .background(color = colors.surface, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = colors.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AutoPlayToggleOnPreview() {
    var isAutoPlayEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AutoPlayToggle(
            isAutoPlayEnabled = isAutoPlayEnabled,
            onToggle = { isAutoPlayEnabled = it },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AutoPlayToggleOffPreview() {
    var isAutoPlayEnabled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AutoPlayToggle(
            isAutoPlayEnabled = isAutoPlayEnabled,
            onToggle = { isAutoPlayEnabled = it },
            modifier = Modifier.padding(16.dp)
        )
    }
}
