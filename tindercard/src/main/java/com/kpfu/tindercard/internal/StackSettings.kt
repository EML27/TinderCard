package com.kpfu.tindercard.internal

import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.kpfu.tindercard.*

data class StackSettings(
    var stackForm: StackForm = StackForm.NONE,
    var visibleCount: Int = 3,
    var translationInterval: Float = 8.0f,
    var scaleInterval: Float = 0.95f,
    var swipeThreshold: Float = 0.3f,
    var maxDegree: Float = 20f,
    var directions: List<Direction> = Direction.HORIZONTAL,
    var horizontalScrollable: Boolean = true,
    var verticalScrollable: Boolean = true,
    var swipeMethod: SwipeMethod = SwipeMethod.AUTO_AND_MANUAL,
    var swipeAnimationSettings: SwipeAnimationSettings = SwipeAnimationSettings(),
    var rewindAnimationSettings: RewindAnimationSettings = RewindAnimationSettings(),
    var overlayInterpolator: Interpolator = LinearInterpolator()
)
