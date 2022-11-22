package com.example.ordme.ui.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Basket (
    val uid: String? = null,
    val meals: ArrayList<Meal>? = arrayListOf(),
    var totalPrice: Double? = null
) : Parcelable {}