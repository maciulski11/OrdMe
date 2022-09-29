package com.example.ordme.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.ui.data.Meal
import kotlinx.android.synthetic.main.item_meal.view.*
class RestaurantAdapter(private val mealsList: ArrayList<Meal>): RecyclerView.Adapter<RestaurantAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val meal: Meal = mealsList[position]

        holder.bindView(meal)
    }

    override fun getItemCount(): Int = mealsList.size

    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {

        val chooseMeal = v.findViewById<ConstraintLayout>(R.id.chooseMeal)!!
        val plus = v.findViewById<ImageView>(R.id.plusBT)!!
        val minus = v.findViewById<ImageView>(R.id.minusBT)!!
        val amount = v.findViewById<TextView>(R.id.amountTV)
        val price = v.findViewById<TextView>(R.id.priceTV)

        fun bindView(m: Meal) {
            v.nameMealTV.text = m.nameMeal
            v.priceTV.text = m.price
        }
    }
}