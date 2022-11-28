package com.example.ordme.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.ui.data.Meal
import kotlinx.android.synthetic.main.item_checkout.view.*

class CheckoutAdapter(var basketList: ArrayList<Meal> = arrayListOf()): RecyclerView.Adapter<CheckoutAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_checkout, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CheckoutAdapter.MyViewHolder, position: Int) {
        val meal: Meal = basketList[position]

        holder.bindView(meal)
    }

    override fun getItemCount(): Int = basketList.size

    inner class MyViewHolder(private var v: View): RecyclerView.ViewHolder(v) {

        fun bindView(meal: Meal) {
            val price = "%.2f".format(meal.price)

            v.nameMealTV.text = meal.name
            v.amountTV.text = meal.amount.toString()
            v.priceTV.text = price

        }
    }
}