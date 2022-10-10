package com.example.ordme.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.ui.data.Basket
import kotlinx.android.synthetic.main.item_basket.view.*

class BasketAdapter(private val basketList: ArrayList<Basket>): RecyclerView.Adapter<BasketAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_basket, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BasketAdapter.MyViewHolder, position: Int) {
        val basket: Basket = basketList[position]

        holder.bindView(basket)

    }

    override fun getItemCount(): Int = basketList.size

    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {

        fun bindView(b: Basket){
            v.nameMealTV.text = b.nameMeal
            v.amountTV.text = b.amountMeal
            v.priceTV.text = b.priceMeal
        }
    }
}