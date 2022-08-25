package com.example.ordme.ui.user.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.ui.restaurant.data.Restaurants
import com.example.ordme.ui.user.screen.RestaurantFragment
import kotlinx.android.synthetic.main.item_choose_restaurants.view.*

class MainUserAdapter(private val restaurantsList: ArrayList<Restaurants>):
    RecyclerView.Adapter<MainUserAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_choose_restaurants, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val restaurant: Restaurants = restaurantsList[position]

        holder.chooseRestaurants.setOnClickListener {

        }

        holder.bindView(restaurant)
    }

    override fun getItemCount(): Int = restaurantsList.size


    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {

        val chooseRestaurants = v.findViewById<ConstraintLayout>(R.id.chooseRestaurant)

        fun bindView(r: Restaurants) {
            v.nameRestaurant.text = r.nameRestaurant
            v.value.text = r.value
        }


    }
}