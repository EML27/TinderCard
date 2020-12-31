package com.kpfu.tindercard.internal

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kpfu.tindercard.Direction
import com.kpfu.tindercard.StackLayoutManager

class StackSmoothScroller(private val type: ScrollType, private val manager: StackLayoutManager) :
    RecyclerView.SmoothScroller() {

    enum class ScrollType {
        AUTO_SWIPE,
        AUTO_REWIND,
        MANUAL_SWIPE,
        MANUAL_CANCEL
    }

    override fun onSeekTargetStep(dx: Int, dy: Int, state: RecyclerView.State, action: Action) {
        if (type == ScrollType.AUTO_REWIND) {
            val setting = manager.settings.rewindAnimationSettings
            action.update(
                -getDx(setting),
                -getDy(setting),
                setting.duration,
                setting.interpolator
            )
        }
    }

    override fun onTargetFound(targetView: View, state: RecyclerView.State, action: Action) {
        val x = targetView.translationX
        val y = targetView.translationY

        when (type) {
            ScrollType.AUTO_SWIPE -> {
                val setting = manager.settings.swipeAnimationSettings
                action.update(
                    -getDx(setting),
                    -getDy(setting),
                    setting.duration,
                    setting.interpolator
                )
            }
            ScrollType.MANUAL_SWIPE -> {
                val dx = -x * 10
                val dy = -y * 10
                val setting = manager.settings.swipeAnimationSettings
                action.update(dx.toInt(), dy.toInt(), setting.duration, setting.interpolator)
            }
            ScrollType.AUTO_REWIND, ScrollType.MANUAL_CANCEL -> {
                val setting = manager.settings.rewindAnimationSettings
                action.update(
                    x.toInt(),
                    y.toInt(),
                    setting.duration,
                    setting.interpolator
                )
            }
        }
    }

    override fun onStart() {
        val listener = manager.listener
        val state = manager.state
        when (type) {
            ScrollType.AUTO_SWIPE -> {
                state.next(StackState.Status.AUTO_SWIPE_ANIMATION)
                manager.getTopView()
                    ?.let { listener.onCardDisappear(it, manager.state.topPosition) }
            }
            ScrollType.MANUAL_SWIPE -> {
                state.next(StackState.Status.MANUAL_SWIPE_ANIMATION)
                manager.getTopView()
                    ?.let { listener.onCardDisappear(it, manager.state.topPosition) }
            }
            ScrollType.AUTO_REWIND, ScrollType.MANUAL_CANCEL -> {
                state.next(StackState.Status.REWIND_ANIMATION)
            }
        }
    }

    override fun onStop() {
        val listener = manager.listener
        when (type) {
            ScrollType.AUTO_REWIND -> {
                listener.onCardRewound()
                manager.getTopView()?.let { listener.onCardAppear(it, manager.state.topPosition) }
            }
            ScrollType.MANUAL_CANCEL -> {
                listener.onCardCancel()
            }
            else -> {
            }
        }
    }

    private fun getDx(setting: AnimationSetting): Int {
        val state = manager.state
        return when (setting.direction) {
            Direction.LEFT -> -state.width * 2
            Direction.RIGHT -> state.width * 2
            else -> 0
        }
    }

    private fun getDy(setting: AnimationSetting): Int {
        val state = manager.state

        return when (setting.direction) {
            Direction.TOP -> -state.height * 2
            Direction.BOTTOM -> state.height * 2
            else -> state.height / 4
        }
    }
}
