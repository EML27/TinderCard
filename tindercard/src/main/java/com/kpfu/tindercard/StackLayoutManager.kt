package com.kpfu.tindercard

import android.content.Context
import android.graphics.PointF
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import androidx.recyclerview.widget.RecyclerView
import com.kpfu.tindercard.internal.DisplayUtil
import com.kpfu.tindercard.internal.StackSettings
import com.kpfu.tindercard.internal.StackSmoothScroller
import com.kpfu.tindercard.internal.StackState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.min

class StackLayoutManager(
    private val context: Context,
) :
    RecyclerView.LayoutManager(),
    RecyclerView.SmoothScroller.ScrollVectorProvider {

    val settings = StackSettings()
    val state = StackState()

    //listeners
    var onCardDragListener: (direction: Direction, ratio: Float) -> Unit = { _, _ -> }
    var onCardSwipeListener: (direction: Direction) -> Unit = { _ -> }
    var onCardRewoundListener: () -> Unit = {}
    var onCardCancelListener: () -> Unit = {}
    var onCardAppearListener: (view: View, position: Int) -> Unit = { _, _ -> }
    var onCardDisappearListener: (view: View, position: Int) -> Unit = { _, _ -> }
    //no more listeners(((

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? = null

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        recycler?.let { update(it) }
        if (state?.didStructureChange() == true) {
            val topView = getTopView()
            topView?.let { onCardAppearListener(it, this.state.topPosition) }
        }
    }

    override fun canScrollHorizontally(): Boolean =
        settings.swipeMethod.swipeable() && settings.horizontalScrollable

    override fun canScrollVertically(): Boolean =
        settings.swipeMethod.swipeable() && settings.verticalScrollable

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        s: RecyclerView.State?
    ): Int {
        if (state.topPosition == itemCount)
            return 0

        when (state.status) {
            StackState.Status.IDLE -> {
                if (settings.swipeMethod.swipeableManually()) {
                    state.dx -= dx
                    update(recycler)
                    return dx
                }
            }
            StackState.Status.DRAGGING -> {
                if (settings.swipeMethod.swipeableManually()) {
                    state.dx -= dx
                    update(recycler)
                    return dx
                }
            }
            StackState.Status.REWIND_ANIMATION -> {
                state.dx -= dx
                update(recycler)
                return dx
            }
            StackState.Status.AUTO_SWIPE_ANIMATION -> {
                if (settings.swipeMethod.swipeableAutomatically()) {
                    state.dx -= dx
                    update(recycler)
                    return dx
                }
            }
            StackState.Status.MANUAL_SWIPE_ANIMATION -> {
                if (settings.swipeMethod.swipeableManually()) {
                    state.dx -= dx
                    update(recycler)
                    return dx
                }
            }
            else -> {
            }
        }
        return 0
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        s: RecyclerView.State?
    ): Int {
        if (state.topPosition == itemCount)
            return 0

        when (state.status) {
            StackState.Status.IDLE -> {
                if (settings.swipeMethod.swipeableManually()) {
                    state.dy -= dy
                    update(recycler)
                    return dy
                }
            }
            StackState.Status.DRAGGING -> {
                if (settings.swipeMethod.swipeableManually()) {
                    state.dy -= dy
                    update(recycler)
                    return dy
                }
            }
            StackState.Status.REWIND_ANIMATION -> {
                state.dy -= dy
                update(recycler)
                return dy
            }
            StackState.Status.AUTO_SWIPE_ANIMATION -> {
                if (settings.swipeMethod.swipeableAutomatically()) {
                    state.dy -= dy
                    update(recycler)
                    return dy
                }
            }
            StackState.Status.MANUAL_SWIPE_ANIMATION -> {
                if (settings.swipeMethod.swipeableManually()) {
                    state.dy -= dy
                    update(recycler)
                    return dy
                }
            }
            else -> {
            }
        }
        return 0
    }

    override fun onScrollStateChanged(s: Int) {
        when (s) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                if (state.targetPosition == RecyclerView.NO_POSITION) {
                    state.next(StackState.Status.IDLE)
                    state.targetPosition = RecyclerView.NO_POSITION
                } else if (state.topPosition == state.targetPosition) {
                    state.next(StackState.Status.IDLE)
                    state.targetPosition = RecyclerView.NO_POSITION
                } else {
                    if (state.topPosition < state.targetPosition) {
                        smoothScrollToNext(state.targetPosition)
                    } else {
                        smoothScrollToPrevious(state.targetPosition)
                    }
                }
            }
            RecyclerView.SCROLL_STATE_DRAGGING -> {
                if (settings.swipeMethod.swipeableManually()) {
                    state.next(StackState.Status.DRAGGING)
                }
            }
        }
    }

    override fun scrollToPosition(position: Int) {
        if (settings.swipeMethod.swipeableAutomatically()) {
            if (state.canScrollToPosition(position, itemCount)) {
                state.topPosition = position
                requestLayout()
            }
        }
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        s: RecyclerView.State?,
        position: Int
    ) {
        if (settings.swipeMethod.swipeableAutomatically()) {
            if (state.canScrollToPosition(position, itemCount)) {
                smoothScrollToPosition(position)
            }
        }
    }

    private fun smoothScrollToPosition(position: Int) {
        if (state.topPosition < position) {
            smoothScrollToNext(position)
        } else {
            smoothScrollToPrevious(position)
        }
    }

    private fun update(recycler: RecyclerView.Recycler?) {
        val recyclerView = recycler ?: return
        state.width = width
        state.height = height

        if (state.isSwipeCompleted()) {
            getTopView()?.let { removeAndRecycleView(it, recyclerView) }

            val direction = state.getDirection()

            state.next(state.status.toAnimatedStatus())
            state.topPosition++
            state.dx = 0
            state.dy = 0
            if (state.topPosition == state.targetPosition) {
                state.targetPosition = RecyclerView.NO_POSITION
            }

            //TODO check this
            MainScope().launch(Dispatchers.Main) {
                onCardSwipeListener(direction)
                val topView = getTopView()
                topView?.let { onCardAppearListener(it, state.topPosition) }
            }
        }

        detachAndScrapAttachedViews(recyclerView)

        val parentTop = paddingTop
        val parentLeft = paddingLeft
        val parentRight = width - paddingLeft
        val parentBottom = height - paddingBottom
        for (i in state.topPosition..min(state.topPosition + settings.visibleCount, itemCount)) {
            val child = recyclerView.getViewForPosition(i)
            addView(child, 0)
            measureChildWithMargins(child, 0, 0)
            layoutDecoratedWithMargins(child, parentLeft, parentTop, parentRight, parentBottom)

            resetTranslation(child)
            resetScale(child)
            resetRotation(child)
            resetOverlay(child)

            if (i == state.topPosition) {
                updateTranslation(child)
                resetScale(child)
                updateRotation(child)
                updateOverlay(child)
            } else {
                val currentIndex = i - state.topPosition
                updateTranslation(child, currentIndex)
                updateScale(child, currentIndex)
                resetRotation(child)
                resetOverlay(child)
            }
        }

        if (state.status.isDragging()) {
            onCardDragListener(state.getDirection(), state.getRatio())
        }
    }

    fun getTopView() = findViewByPosition(state.topPosition)

    private fun resetTranslation(view: View) {
        view.translationX = 0f
        view.translationY = 0f
    }

    private fun resetScale(view: View) {
        view.scaleX = 1f
        view.scaleY = 1f
    }

    private fun resetRotation(view: View) {
        view.rotation = 0f
    }

    private fun resetOverlay(view: View) {
        view.findViewById<View>(R.id.left_overlay).alpha = 0f
        view.findViewById<View>(R.id.right_overlay).alpha = 0f
        view.findViewById<View>(R.id.top_overlay).alpha = 0f
        view.findViewById<View>(R.id.bottom_overlay).alpha = 0f
    }

    private fun updateTranslation(view: View) {
        view.translationX = state.dx.toFloat()
        view.translationY = state.dy.toFloat()
    }

    private fun updateTranslation(view: View, index: Int) {
        val nextIndex = index - 1
        val translationPx = DisplayUtil.dpToPx(context, settings.translationInterval)
        val currentTranslation = index * translationPx
        val nextTranslation = nextIndex * translationPx
        val targetTranslation =
            currentTranslation - (currentTranslation - nextTranslation) * state.getRatio()
        when (settings.stackForm) {
            StackForm.NONE -> {
            }
            StackForm.TOP -> view.translationY = -targetTranslation
            StackForm.TOP_LEFT -> {
                view.translationY = -targetTranslation
                view.translationX = -targetTranslation
            }
            StackForm.TOP_RIGHT -> {
                view.translationX = targetTranslation
                view.translationY = -targetTranslation
            }
            StackForm.BOTTOM -> view.translationY = targetTranslation
            StackForm.BOTTOM_LEFT -> {
                view.translationY = targetTranslation
                view.translationX = -targetTranslation
            }
            StackForm.BOTTOM_RIGHT -> {
                view.translationY = targetTranslation
                view.translationX = targetTranslation
            }
            StackForm.LEFT -> {
                view.translationX = -targetTranslation
            }
            StackForm.RIGHT -> {
                view.translationX = targetTranslation
            }
        }
    }

    private fun updateScale(view: View, index: Int) {
        val nextIndex = index - 1
        val currentScale = 1f - index * (1f - settings.scaleInterval)
        val nextScale = 1f - nextIndex * (1f - settings.scaleInterval)
        val targetScale = currentScale + (nextScale - currentScale) * state.getRatio()

        //TODO check this
        view.scaleX = targetScale
        view.scaleY = targetScale
    }

    private fun updateRotation(view: View) {
        val deg = state.dx * settings.maxDegree / width * state.proportion
        view.rotation = deg
    }

    private fun updateOverlay(view: View) {
        resetOverlay(view)
        val direction = state.getDirection()
        val alpha = settings.overlayInterpolator.getInterpolation(state.getRatio())
        when (direction) {
            Direction.LEFT -> view.findViewById<View>(R.id.left_overlay).alpha = alpha
            Direction.RIGHT -> view.findViewById<View>(R.id.right_overlay).alpha = alpha
            Direction.BOTTOM -> view.findViewById<View>(R.id.bottom_overlay).alpha = alpha
            Direction.TOP -> view.findViewById<View>(R.id.top_overlay).alpha = alpha
        }
    }

    private fun smoothScrollToNext(position: Int) {
        state.proportion = 0f
        state.targetPosition = position
        val scroller = StackSmoothScroller(StackSmoothScroller.ScrollType.AUTO_SWIPE, this)
        scroller.targetPosition = state.topPosition
        startSmoothScroll(scroller)
    }

    private fun smoothScrollToPrevious(position: Int) {
        val topView = getTopView()
        topView?.let { onCardDisappearListener(it, state.topPosition) }

        state.proportion = 0f
        state.targetPosition = position
        state.topPosition--
        val scroller = StackSmoothScroller(StackSmoothScroller.ScrollType.AUTO_REWIND, this)
        scroller.targetPosition = state.topPosition
        startSmoothScroll(scroller)
    }

    fun updateProportion(y: Float) {
        if (state.topPosition < itemCount) {
            val view = findViewByPosition(state.topPosition)
            view?.let {
                val half = height / 2
                state.proportion = -(y - half - view.top) / half
            }
        }
    }

    fun setStackFrom(stackFrom: StackForm) {
        settings.stackForm = stackFrom
    }

    fun setVisibleCount(visibleCount: Int) {
        require(visibleCount >= 1) { "VisibleCount must be greater than 0." }
        settings.visibleCount = visibleCount
    }

    fun setTranslationInterval(translationInterval: Float) {
        require(translationInterval >= 0.0f) { "TranslationInterval must be greater than or equal 0.0f" }
        settings.translationInterval = translationInterval
    }

    fun setScaleInterval(scaleInterval: Float) {
        require(scaleInterval >= 0.0f) { "ScaleInterval must be greater than or equal 0.0f." }
        settings.scaleInterval = scaleInterval
    }

    fun setSwipeThreshold(swipeThreshold: Float) {
        require(!(swipeThreshold < 0.0f || 1.0f < swipeThreshold)) { "SwipeThreshold must be 0.0f to 1.0f." }
        settings.swipeThreshold = swipeThreshold
    }

    fun setMaxDegree(maxDegree: Float) {
        require(!(maxDegree < -360.0f || 360.0f < maxDegree)) { "MaxDegree must be -360.0f to 360.0f" }
        settings.maxDegree = maxDegree
    }

    fun setDirections(directions: List<Direction>) {
        settings.directions = directions
    }

    fun setCanScrollHorizontal(canScrollHorizontal: Boolean) {
        settings.horizontalScrollable = canScrollHorizontal
    }

    fun setCanScrollVertical(canScrollVertical: Boolean) {
        settings.verticalScrollable = canScrollVertical
    }

    fun setSwipeMethod(swipeMethod: SwipeMethod) {
        settings.swipeMethod = swipeMethod
    }

    fun setSwipeAnimationSetting(swipeAnimationSetting: SwipeAnimationSettings) {
        settings.swipeAnimationSettings = swipeAnimationSetting
    }

    fun setRewindAnimationSetting(rewindAnimationSetting: RewindAnimationSettings) {
        settings.rewindAnimationSettings = rewindAnimationSetting
    }

    fun setOverlayInterpolator(overlayInterpolator: Interpolator) {
        settings.overlayInterpolator = overlayInterpolator
    }
}
