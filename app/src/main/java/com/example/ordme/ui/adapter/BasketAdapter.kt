package com.example.ordme.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.ui.data.Basket
import com.example.ordme.ui.data.Meal
import kotlinx.android.synthetic.main.item_basket.view.*

class BasketAdapter(private val basketList: ArrayList<Meal>): RecyclerView.Adapter<BasketAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_basket, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BasketAdapter.MyViewHolder, position: Int) {
        val meal: Meal = basketList[position]

        holder.bindView(meal)

    }

    override fun getItemCount(): Int = basketList.size

    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {

        fun bindView(meal: Meal){
            v.nameMealTV.text = meal.name
            v.amountTV.text = meal.amount.toString()
            v.priceTV.text = meal.price.toString()
        }
    }
}