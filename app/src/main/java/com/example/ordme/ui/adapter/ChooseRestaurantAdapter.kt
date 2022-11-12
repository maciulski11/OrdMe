package com.example.ordme.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.ui.data.Restaurant
import android.content.Context
import android.os.Bundle
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.item_choose_restaurants.view.*

class ChooseRestaurantAdapter(
    private val context: Context,
    var restaurantsList: ArrayList<Restaurant>,
    private val v: View
) :
    RecyclerView.Adapter<ChooseRestaurantAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_choose_restaurants, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val restaurant: Restaurant = restaurantsList[position]

        holder.chooseRestaurant.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(
                "uidRestaurant",
                restaurantsList[position].uid
            )
            v.findNavController()
                .navigate(R.id.action_mainUserFragment_to_restaurantFragment, bundle)

        }

        holder.bindView(restaurant)

    }

    override fun getItemCount(): Int = restaurantsList.size

    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {

        val chooseRestaurant = v.findViewById<ConstraintLayout>(R.id.chooseRestaurant)!!

        fun bindView(r: Restaurant) {
            v.nameRestaurant.text = r.nameRestaurant
            v.value.text = r.value
        }
    }
}