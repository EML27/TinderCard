package com.kpfu.demo

data class Food(
    val id: Int = counter++,
    val name: String = namesList.random(),
    val price: String = (Math.random() * 100).toInt().toString(),
    val url: String = "https://source.unsplash.com/collection/3758248/900x1600"
)

private var counter = 0

val namesList = listOf(
    "\"Good morning\" Breakfast",
    "\"Happy day\" Lunch",
    "\"Elegant evening\" Dinner",
    "Sweet start of the morning",
    "Deluxe Pleasure",
    "Elegant lunch",
)