package com.example.ordme.ui.data

import android.os.Parcel
import android.os.Parcelable

data class Meal (
    val name: String? = null,
    var amount: Int? = null,
    var price: Double? = null,
    val uidMeal: String? = null,
    val uidRestaurant: String? = null
    ) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {

        dest.writeString(name)
        dest.writeInt(amount!!)
        dest.writeDouble(price!!)
        dest.writeString(uidMeal)
        dest.writeString(uidRestaurant)
    }

    companion object CREATOR : Parcelable.Creator<Meal> {
        override fun createFromParcel(parcel: Parcel): Meal {
            return Meal(parcel)
        }

        override fun newArray(size: Int): Array<Meal?> {
            return arrayOfNulls(size)
        }
    }
}