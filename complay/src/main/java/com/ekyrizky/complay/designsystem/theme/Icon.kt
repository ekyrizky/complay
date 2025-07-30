package com.ekyrizky.complay.designsystem.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import com.ekyrizky.complay.R

@Immutable
data class ComplayIcons(
    @DrawableRes val play: Int,
    @DrawableRes val pause: Int,
    @DrawableRes val stop: Int,
    @DrawableRes val skipNext: Int,
    @DrawableRes val skipPrevious: Int,
    @DrawableRes val autoPlay: Int,
    @DrawableRes val fastForward: Int,
    @DrawableRes val fastRewind: Int,
    @DrawableRes val forward10: Int,
    @DrawableRes val rewind10: Int,
    @DrawableRes val replay: Int,
    @DrawableRes val volumeUp: Int,
    @DrawableRes val volumeDown: Int,
    @DrawableRes val volumeOff: Int,
    @DrawableRes val fullscreen: Int,
    @DrawableRes val fullscreenExit: Int,
    @DrawableRes val settings: Int,
    @DrawableRes val pip: Int,
    @DrawableRes val quality: Int,
    @DrawableRes val subtitle: Int,
    @DrawableRes val speed: Int,
    @DrawableRes val more: Int,
    @DrawableRes val share: Int,
    @DrawableRes val like: Int,
    @DrawableRes val dislike: Int
) {
    companion object {
        @Composable
        fun defaultIcons(): ComplayIcons = ComplayIcons(
            play = R.drawable.ic_play,
            pause = R.drawable.ic_pause,
            stop = R.drawable.ic_stop,
            skipNext = R.drawable.ic_skip_next,
            skipPrevious = R.drawable.ic_skip_previous,
            autoPlay = R.drawable.ic_auto_play,
            fastForward = R.drawable.ic_fast_forward,
            fastRewind = R.drawable.ic_fast_rewind,
            forward10 = R.drawable.ic_forward_10,
            rewind10 = R.drawable.ic_replay_10,
            replay = R.drawable.ic_replay,
            volumeUp = R.drawable.ic_volume_up,
            volumeDown = R.drawable.ic_volume_down,
            volumeOff = R.drawable.ic_volume_off,
            fullscreen = R.drawable.ic_fullscreen,
            fullscreenExit = R.drawable.ic_fullscreen_exit,
            settings = R.drawable.ic_settings,
            pip = R.drawable.ic_pip,
            quality = R.drawable.ic_tune,
            subtitle = R.drawable.ic_subtitle,
            speed = R.drawable.ic_speed,
            more = R.drawable.ic_more,
            share = R.drawable.ic_share,
            like = R.drawable.ic_like,
            dislike = R.drawable.ic_dislike
        )
    }
}

internal val LocalIcons = compositionLocalOf<ComplayIcons> {
    error("No icons provided! Make sure to wrap all usages in ComplayTheme.")
}