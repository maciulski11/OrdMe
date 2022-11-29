package com.example.ordme.data.model

import com.google.firebase.Timestamp

data class Order (
    val uidMeal : String? = null,
    val nameMeal: String? = null,
    val amount: Int? = null,
    val price: Double? = null,
    val timestamp: Timestamp? = null
)