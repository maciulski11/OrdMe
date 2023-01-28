package com.example.ordme.ui.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ordme.R
import com.example.ordme.data.model.Meal
import com.example.ordme.data.model.Restaurant
import kotlinx.android.synthetic.main.item_choose_restaurants.view.*
import kotlinx.android.synthetic.main.item_search_view.view.*


class SearchViewAdapter(var restaurantList: List<Restaurant>, private val v: View)
    : RecyclerView.Adapter<SearchViewAdapter.MyViewHolder>() {

    private var restaurants: ArrayList<Restaurant> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun update(restaurants: List<Restaurant>) {

        this.restaurants.addAll(restaurants)

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(restaurantList: List<Restaurant>){
        this.restaurantList = restaurantList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_view, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val restaurant: Restaurant = restaurantList[position]

        val icon = holder.itemView.findViewById<ImageView>(R.id.iconSV)
        Glide.with(holder.itemView)
            .load(restaurant.icon)
            .override(100,100)
            .circleCrop()
            .into(icon)

        holder.chooseRestaurant.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(
                "uidRestaurant",
                restaurantList[position].uid
            )
            v.findNavController()
                .navigate(R.id.action_searchViewFragment2_to_restaurantFragment, bundle)

        }

        holder.bindView(restaurant)
    }

    override fun getItemCount(): Int = restaurantList.size

    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {

        val chooseRestaurant = v.findViewById<ConstraintLayout>(R.id.clickRestaurant)!!

        fun bindView(r: Restaurant) {
            v.nameTV.text = r.nameRestaurant!!
        }
    }
}