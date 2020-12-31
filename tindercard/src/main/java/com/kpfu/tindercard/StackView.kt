package com.kpfu.tindercard

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.kpfu.tindercard.internal.StackDataObserver
import com.kpfu.tindercard.internal.StackSnapHelper

class StackView(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    RecyclerView(context, attrs, defStyle) {

    init {
        initialize()
    }

    private val observer = StackDataObserver(this)

    override fun setLayoutManager(layout: LayoutManager?) {
        if (layout is StackLayoutManager) {
            super.setLayoutManager(layout)
        } else {
            throw IllegalArgumentException("Illegal layout manager class")
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        if (layoutManager == null)
            layoutManager = StackLayoutManager(context)

        this.adapter?.unregisterAdapterDataObserver(observer)
        this.adapter?.onDetachedFromRecyclerView(this)

        adapter?.registerAdapterDataObserver(observer)

        super.setAdapter(adapter)
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        if (e?.action == MotionEvent.ACTION_DOWN) {
            val manager = layoutManager as StackLayoutManager
            manager.updateProportion(e.y)
        }
        return super.onInterceptTouchEvent(e)
    }

    fun swipe() {
        val manager = layoutManager as StackLayoutManager
        smoothScrollToPosition(manager.state.topPosition + 1)
    }

    fun rewind() {
        val manager = layoutManager as StackLayoutManager
        smoothScrollToPosition(manager.state.topPosition - 1)
    }

    private fun initialize() {
        StackSnapHelper().attachToRecyclerView(this)
        overScrollMode = OVER_SCROLL_NEVER
    }
}
