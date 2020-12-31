package com.kpfu.tindercard.internal

import androidx.recyclerview.widget.RecyclerView
import com.kpfu.tindercard.Direction
import kotlin.math.abs
import kotlin.math.min

class StackState {
    var status = Status.IDLE
    var width = 0
    var height = 0
    var dx = 0
    var dy = 0
    var topPosition = 0
    var targetPosition = RecyclerView.NO_POSITION
    var proportion = 0.0f

    enum class Status {
        IDLE, DRAGGING, REWIND_ANIMATION, AUTO_SWIPE_ANIMATION, AUTO_SWIPE_ANIMATED, MANUAL_SWIPE_ANIMATION, MANUAL_SWIPE_ANIMATED;

        fun isBusy() = this != IDLE
        fun isDragging() = this == DRAGGING
        fun isSwipeAnimating() = this == MANUAL_SWIPE_ANIMATION || this == AUTO_SWIPE_ANIMATION

        fun toAnimatedStatus(): Status {
            return when (this) {
                MANUAL_SWIPE_ANIMATION -> MANUAL_SWIPE_ANIMATED
                AUTO_SWIPE_ANIMATION -> AUTO_SWIPE_ANIMATED
                else -> IDLE
            }
        }
    }

    fun next(state: Status) {
        status = state
    }

    fun getDirection(): Direction {
        return if (abs(dy) < abs(dx)) {
            if (dx < 0.0f) {
                Direction.LEFT
            } else {
                Direction.RIGHT
            }
        } else {
            if (dy < 0.0f) {
                Direction.TOP
            } else {
                Direction.BOTTOM
            }
        }
    }

    fun getRatio(): Float {
        val absDx = abs(dx)
        val absDy = abs(dy)
        val ratio: Float
        ratio = if (absDx < absDy) {
            absDy / (height / 2.0f)
        } else {
            absDx / (width / 2.0f)
        }
        return min(ratio, 1.0f)
    }

    fun isSwipeCompleted(): Boolean {
        if (status.isSwipeAnimating()) {
            if (topPosition < targetPosition) {
                if (width < abs(dx) || height < abs(dy)) {
                    return true
                }
            }
        }
        return false
    }

    fun canScrollToPosition(position: Int, itemCount: Int): Boolean {
        if (position == topPosition) {
            return false
        }
        if (position < 0) {
            return false
        }
        if (itemCount < position) {
            return false
        }
        return !status.isBusy()
    }
}
