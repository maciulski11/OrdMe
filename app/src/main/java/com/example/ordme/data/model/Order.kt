package com.example.ordme.data.model

import com.google.firebase.Timestamp


data class Order(
    var uid: String? = null,
    val meals: ArrayList<Meal>? = arrayListOf(),
    val full_name: String? = null,
    var price: Double? = null,
    val information: String? = null,
    val time: String? = null,
    val acceptOrder: Boolean? = null
)