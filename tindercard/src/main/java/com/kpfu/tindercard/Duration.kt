package com.kpfu.tindercard

enum class Duration(val duration: Int) {

    FAST(100),
    NORMAL(200),
    SLOW(500);

    companion object {
        fun fromVelocity(velocity: Int): Duration {
            return when {
                velocity < 1000 -> SLOW
                velocity < 5000 -> NORMAL
                else -> FAST
            }
        }
    }
}
