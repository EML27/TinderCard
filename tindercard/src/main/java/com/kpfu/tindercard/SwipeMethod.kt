package com.kpfu.tindercard

enum class SwipeMethod {
    AUTO_AND_MANUAL,
    AUTO,
    MANUAL,
    NONE;

    fun swipeableAutomatically() = (this == AUTO || this == AUTO_AND_MANUAL)
    fun swipeableManually() = (this == MANUAL || this == AUTO_AND_MANUAL)
    fun swipeable() = swipeableAutomatically() || swipeableManually()
}
