package com.kpfu.tindercard.internal

import android.view.animation.Interpolator
import com.kpfu.tindercard.Direction

interface AnimationSetting {
    val direction: Direction
    val duration: Int
    val interpolator: Interpolator
}
