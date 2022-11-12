package com.example.ordme.ui.data

data class Basket (
    val uid: String? = null,
    var totalPrice: Double? = null,
    val meals: ArrayList<Meal>? = arrayListOf(),
)