package com.kpfu.tindercard

import android.view.View

interface StackListener {
    fun onCardDrag(direction: Direction, ratio: Float)
    fun onCardSwipe(direction: Direction)
    fun onCardRewound()
    fun onCardCancel()
    fun onCardAppear(view: View, position: Int)
    fun onCardDisappear(view: View, position: Int)

    companion object {

        val DEFAULT = object : StackListener {
            override fun onCardDrag(direction: Direction, ratio: Float) {}

            override fun onCardSwipe(direction: Direction) {}

            override fun onCardRewound() {}

            override fun onCardCancel() {}

            override fun onCardAppear(view: View, position: Int) {}

            override fun onCardDisappear(view: View, position: Int) {}
        }
    }
}
