package com.example.ordme.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.dialogs.DeleteDialogFragment
import com.example.ordme.data.model.Meal
import kotlinx.android.synthetic.main.item_basket.view.*

class BasketAdapter(
    val context: Context,
    val onInc: (String) -> Unit,
    val onDec: (String) -> Unit,
    val onDelete: (String) -> Unit,
    val activity: Activity,
    var basketList: ArrayList<Meal> = arrayListOf()
) : RecyclerView.Adapter<BasketAdapter.MyViewHolder>() {

    private var meals: ArrayList<Meal> = ArrayList()
    var meal: MutableLiveData<Meal?> = MutableLiveData()

    @SuppressLint("NotifyDataSetChanged")
    fun update(meals: ArrayList<Meal>) {

        this.meals.addAll(meals)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_basket, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemId(i: Int): Long = i.toLong()

    override fun onBindViewHolder(holder: BasketAdapter.MyViewHolder, position: Int) {
        val meal: Meal = basketList[position]

        holder.bindView(meal)

    }

    override fun getItemCount(): Int = basketList.size

    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {

        private val plusBT = v.findViewById<ImageView>(R.id.plusBT)!!
        private val minusBT = v.findViewById<ImageView>(R.id.minusBT)!!
        private val deleteBT = v.findViewById<ImageView>(R.id.deleteBT)!!
        private val additionsCount = v.findViewById<TextView>(R.id.additionsCount)

        fun bindView(meal: Meal) {
            val mealPrice = (meal.price ?: 0.0) * (meal.amount ?: 0).toDouble()
            val additionsPrice = meal.additions?.filter { it.amount ?: 0 > 0 }?.sumOf { it.priceAddition ?: 0.0 * (it.amount?.toDouble() ?: 0.0) } ?: 0.0
            val totalPrice = mealPrice + (additionsPrice * (meal.amount ?: 0).toDouble())

            v.nameMealTV.text = meal.name
            v.amountTV.text = meal.amount.toString()
            val priceTV = "%.2f".format(totalPrice)
            v.priceTV.text = priceTV

            plusBT.setOnClickListener {
                onInc(meal.uid ?: "")
            }

            minusBT.setOnClickListener {
                onDec(meal.uid ?: "")
            }

            deleteBT.setOnClickListener {
                DeleteDialogFragment(action =  {
                    onDelete(meal.uid ?: "")
                }).show(activity)
            }

            additionsCount.text = "${meal.additions?.sumOf { it.amount ?: 0}}"
        }
    }
}