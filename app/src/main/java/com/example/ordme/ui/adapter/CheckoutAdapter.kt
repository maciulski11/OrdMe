package com.example.ordme.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.data.model.Meal
import kotlinx.android.synthetic.main.item_checkout.view.*

class CheckoutAdapter(var basketList: ArrayList<Meal> = arrayListOf()): RecyclerView.Adapter<CheckoutAdapter.MyViewHolder>() {

    private var meals: ArrayList<Meal> = ArrayList()
    var meal: MutableLiveData<Meal?> = MutableLiveData()

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
            val mealPrice = (meal.price ?: 0.0) * (meal.amount ?: 0).toDouble()
            val additionsPrice = meal.additions?.filter { (it.amount ?: 0) > 0 }?.sumOf {
                it.priceAddition ?: (0.0 * (it.amount?.toDouble() ?: 0.0))
            } ?: 0.0
            val totalPrice = mealPrice + (additionsPrice * (meal.amount ?: 0).toDouble())
            val price = "%.2f".format(totalPrice)

            v.nameMealTV.text = meal.name
            v.amountTV.text = meal.amount.toString()
            v.priceTV.text = price

        }
    }
}