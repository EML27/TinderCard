package com.kpfu.tindercard.internal

import androidx.recyclerview.widget.RecyclerView
import com.kpfu.tindercard.StackLayoutManager
import kotlin.math.min

class StackDataObserver(private val recyclerView: RecyclerView) :
    RecyclerView.AdapterDataObserver() {

    override fun onChanged() {
        val manager = getLayoutManager()
        manager.state.topPosition = 0
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {

    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {

    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {

    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        val manager = getLayoutManager()
        val topPosition = manager.state.topPosition
        if (manager.itemCount == 0) {
            manager.state.topPosition = 0
        } else if (positionStart < topPosition) {
            val diff = topPosition - positionStart
            manager.state.topPosition = min(topPosition - diff, manager.itemCount - 1)
        }
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        val manager = getLayoutManager()
        manager.removeAllViews()
    }

    private fun getLayoutManager(): StackLayoutManager =
        recyclerView.layoutManager as StackLayoutManager
}
