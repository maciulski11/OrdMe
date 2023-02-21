package com.example.ordme.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.data.model.Restaurant
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.ImageView
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ordme.data.model.Meal
import kotlinx.android.synthetic.main.item_choose_restaurants.view.*

class ChooseRestaurantAdapter(
    private val context: Context,
    var restaurantList: ArrayList<Restaurant>,
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
        val restaurant: Restaurant = restaurantList[position]

        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        val image = holder.itemView.findViewById<ImageView>(R.id.imageProducts)
        Glide.with(context)
            .load(restaurant.image_photo)
            .apply(RequestOptions().override(screenWidth, 650))
//            .override(900, 900)
            .into(image)

        val icon = holder.itemView.findViewById<ImageView>(R.id.icon)
        Glide.with(holder.itemView)
            .load(restaurant.icon)
            .override(180,180)
            .circleCrop()
            .into(icon)

        holder.chooseRestaurant.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(
                "uidRestaurant",
                restaurant.uid
            )
            v.findNavController()
                .navigate(R.id.action_mainUserFragment_to_restaurantFragment, bundle)

        }

        holder.bindView(restaurant)

    }

    override fun getItemCount(): Int = restaurantList.size

    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {

        val chooseRestaurant = v.findViewById<ConstraintLayout>(R.id.chooseRestaurant)!!

        fun bindView(r: Restaurant) {
            v.nameRestaurant.text = r.nameRestaurant
            v.value.text = r.value
        }
    }
}