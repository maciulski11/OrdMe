package com.example.ordme.ui.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Meal (
    val name: String? = null,
    var amount: Int? = null,
    var price: Double? = null,
    val uidMeal: String? = null,
    val uidRestaurant: String? = null
    ) : Parcelable {}