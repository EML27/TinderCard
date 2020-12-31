package com.kpfu.tindercard.internal

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.kpfu.tindercard.Duration
import com.kpfu.tindercard.StackLayoutManager
import com.kpfu.tindercard.SwipeAnimationSettings
import kotlin.math.abs
import kotlin.math.max

class StackSnapHelper : SnapHelper() {
    private var velocityX = 0
    private var velocityY = 0

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        val manager = layoutManager as StackLayoutManager
        manager.findViewByPosition(manager.state.topPosition)?.let {
            val x = targetView.translationX.toInt()
            val y = targetView.translationY.toInt()
            if (x != 0 || y != 0) {
                val setting = manager.settings
                val horizontal = abs(x) / targetView.width
                val vertical = abs(y) / targetView.height
                val duration = Duration.fromVelocity(max(velocityX, velocityY))
                if (duration == Duration.FAST || setting.swipeThreshold < horizontal || setting.swipeThreshold < vertical) {
                    val state = manager.state
                    if (setting.directions.contains(state.getDirection())) {
                        state.targetPosition = state.topPosition + 1

                        val swipeAnimationSetting = SwipeAnimationSettings(
                            direction = setting.swipeAnimationSettings.direction,
                            duration = duration.duration,
                            interpolator = setting.swipeAnimationSettings.interpolator
                        )
                        manager.settings.swipeAnimationSettings = swipeAnimationSetting

                        velocityX = 0
                        velocityY = 0

                        val scroller = StackSmoothScroller(
                            StackSmoothScroller.ScrollType.MANUAL_SWIPE,
                            manager
                        )
                        scroller.targetPosition = manager.state.topPosition
                        manager.startSmoothScroll(scroller)
                    } else {
                        val scroller = StackSmoothScroller(
                            StackSmoothScroller.ScrollType.MANUAL_CANCEL,
                            manager
                        )
                        scroller.targetPosition = manager.state.topPosition
                        manager.startSmoothScroll(scroller)
                    }
                } else {
                    val scroller = StackSmoothScroller(
                        StackSmoothScroller.ScrollType.MANUAL_CANCEL,
                        manager
                    )
                    scroller.targetPosition = manager.state.topPosition
                    manager.startSmoothScroll(scroller)
                }
            }
        }
        return IntArray(2)
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        val manager = layoutManager as StackLayoutManager
        val view = manager.findViewByPosition(manager.state.topPosition)
        view?.let {
            val x = it.translationX.toInt()
            val y = it.translationY.toInt()
            if (x == 0 && y == 0) {
                return null
            }
        }
        return view
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager?,
        velocityX: Int,
        velocityY: Int
    ): Int {
        this.velocityX = abs(velocityX)
        this.velocityY = abs(velocityY)
        val manager = layoutManager as StackLayoutManager
        return manager.state.topPosition
    }
}
