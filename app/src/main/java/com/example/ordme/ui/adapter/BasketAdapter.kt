package com.example.ordme.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.ui.data.Meal
import com.example.ordme.ui.screen.BasketViewModel
import com.example.ordme.ui.screen.MealViewModel
import kotlinx.android.synthetic.main.item_basket.view.*
import kotlin.math.absoluteValue

class BasketAdapter(var basketList: ArrayList<Meal>, val context: Context): RecyclerView.Adapter<BasketAdapter.MyViewHolder>() {

    private var meals: ArrayList<Meal> = ArrayList()


    @SuppressLint("NotifyDataSetChanged")
    fun update(meals: ArrayList<Meal>){
        this.meals = ArrayList(meals.reversed())

        notifyDataSetChanged()
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun update(selected: Meal? = null) {
//        this.selected = selected
//
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_basket, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BasketAdapter.MyViewHolder, position: Int) {
        val meal: Meal = basketList[position]

        holder.bindView(meal)

//        holder.minusBT.setOnClickListener {
//            Toast.makeText(context, "click -", Toast.LENGTH_SHORT).show()
//            holder.minusClick(meal)
//        }

//        holder.deleteBT.setOnClickListener {
//            Toast.makeText(context, "$meal", Toast.LENGTH_SHORT).show()
//        }

        holder.plusClick(meal)

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

        fun minusClick(meal: Meal){
            if(meal.amount ?: 0 > 1) {
                val newValue = meal.amount?.dec()

                v.amountTV.text = newValue.toString()
            }
        }

        fun plusClick(meal: Meal){


            plusBT.setOnClickListener {
                Toast.makeText(context, "click +", Toast.LENGTH_SHORT).show()

            }
        }
    }
}