package com.example.ordme.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.ui.data.Meal
import com.example.ordme.ui.screen.BasketViewModel
import kotlinx.android.synthetic.main.item_basket.view.*

class BasketAdapter(var basketList: ArrayList<Meal>, val context: Context): RecyclerView.Adapter<BasketAdapter.MyViewHolder>() {

    private var basketViewModel = BasketViewModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_basket, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BasketAdapter.MyViewHolder, position: Int) {
        val meal: Meal = basketList[position]

        holder.bindView(meal)

        holder.minusBT.setOnClickListener {
            Toast.makeText(context, "click -", Toast.LENGTH_SHORT).show()
        }

        holder.deleteBT.setOnClickListener {
            Toast.makeText(context, "Delete!", Toast.LENGTH_SHORT).show()
        }

        holder.plusClick()

    }

    override fun getItemCount(): Int = basketList.size

    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {

        val plusBT = v.findViewById<ImageView>(R.id.plusBT)!!
        val minusBT = v.findViewById<ImageView>(R.id.minusBT)!!
        val deleteBT = v.findViewById<ImageView>(R.id.deleteBT)!!

        fun bindView(meal: Meal){
            val price = "%.2f".format(meal.price)

            v.nameMealTV.text = meal.name
            v.amountTV.text = meal.amount.toString()
            v.priceTV.text = price
        }

        fun plusClick(){


            plusBT.setOnClickListener {
                Toast.makeText(context, "click +", Toast.LENGTH_SHORT).show()
            }
        }
    }
}