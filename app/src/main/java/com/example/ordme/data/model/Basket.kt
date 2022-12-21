package com.example.ordme.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Basket (
    var uid: String? = null,
    val meals: ArrayList<Meal>? = arrayListOf(),
    val information: String? = null,
    var totalPrice: Double? = null
) : Parcelable {}