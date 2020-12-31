package com.kpfu.tindercard

import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import com.kpfu.tindercard.internal.AnimationSetting

data class RewindAnimationSettings(
    override val direction: Direction = Direction.RIGHT,
    override val duration: Int = Duration.NORMAL.duration,
    override val interpolator: Interpolator = AccelerateInterpolator()
) : AnimationSetting
