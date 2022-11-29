package com.example.ordme.data.model

import android.os.Parcelable
import com.example.ordme.data.model.Addition
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Meal (
    val name: String? = null,
    var amount: Int? = null,
    var price: Double? = null,
    var uid: String? = null,
    val uidMeal: String? = null,
    val uidRestaurant: String? = null,
    var additions: @RawValue ArrayList<Addition>? = arrayListOf()
    ) : Parcelable {}