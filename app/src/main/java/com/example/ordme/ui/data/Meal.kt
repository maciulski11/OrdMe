package com.example.ordme.ui.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Meal (
    val name: String? = null,
    var amount: Int? = null,
    var price: Double? = null,
    val priceStart: Double? = null,
    var uid: String? = null,
    val uidMeal: String? = null,
    val uidRestaurant: String? = null,
    val additions: @RawValue ArrayList<Addition>? = arrayListOf()
    ) : Parcelable {}