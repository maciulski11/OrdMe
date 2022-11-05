package com.example.ordme.ui.adapter

import android.annotation.SuppressLint
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.ui.data.Meal
import kotlinx.android.synthetic.main.fragment_meal.view.*
import kotlinx.android.synthetic.main.item_meal.view.*
import kotlinx.android.synthetic.main.item_meal.view.nameMealTV

class RestaurantAdapter(var mealsList: ArrayList<Meal>,
                        private val v: View
                        ): RecyclerView.Adapter<RestaurantAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val meal: Meal = mealsList[position]

        holder.addMeal.setOnClickListener {
            val bundle = Bundle()

            //TODO: Skończ to
//            bundle.putParcelable()
//            bundle.getParcelable("meal", Meal::class.java)
            bundle.putString(
                "uidMeal",
                mealsList[position].uidMeal
            )
            bundle.putString(
                "uidRestaurant",
                mealsList[position].uidRestaurant
            )
            bundle.putDouble(
                "price",
                mealsList[position].price!!.toDouble()
            )
            bundle.putDouble(
                "priceStart",
                mealsList[position].price!!.toDouble()
            )
            bundle.putInt(
                "amount",
                mealsList[position].amount!!.toInt()
            )

            v.findNavController().navigate(R.id.action_restaurantFragment_to_mealFragment, bundle)

            Log.d("RestaurantAdapter", "$bundle")

        }

        holder.bindView(meal)
    }

    override fun getItemCount(): Int = mealsList.size

    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {

        val addMeal = v.findViewById<ImageView>(R.id.addMealBT)!!

        fun bindView(m: Meal) {
            val price = "%.2f".format(m.price)

            v.nameMealTV.text = m.name
            v.priceTV.text = price
        }
    }
}