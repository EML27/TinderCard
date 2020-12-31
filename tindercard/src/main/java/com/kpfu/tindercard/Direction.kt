package com.kpfu.tindercard

enum class Direction {
    LEFT, RIGHT, TOP, BOTTOM;

    companion object {
        val HORIZONTAL = arrayListOf(LEFT, RIGHT)
        val VERTICAL = arrayListOf(TOP, BOTTOM)
        val FREEDOM = values().toList()
    }
}
