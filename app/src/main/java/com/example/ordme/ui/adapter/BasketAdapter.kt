package com.example.ordme.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.persistableBundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.ui.data.Meal
import com.example.ordme.ui.repository.FirebaseRepository
import com.example.ordme.ui.screen.BasketViewModel
import com.example.ordme.ui.screen.MealViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_basket.view.*
import retrofit2.http.DELETE
import kotlin.math.absoluteValue

class BasketAdapter(val context: Context, val onInc: (String) -> Unit, val onDec: (String) -> Unit, val onDelete: (String) -> Unit, var basketList: ArrayList<Meal> = arrayListOf()) :
    RecyclerView.Adapter<BasketAdapter.MyViewHolder>() {

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

        val plusBT = v.findViewById<ImageView>(R.id.plusBT)!!
        val minusBT = v.findViewById<ImageView>(R.id.minusBT)!!
        val deleteBT = v.findViewById<ImageView>(R.id.deleteBT)!!

        fun bindView(meal: Meal) {
            val price = "%.2f".format(meal.price)

            v.nameMealTV.text = meal.name
            v.amountTV.text = meal.amount.toString()
            v.priceTV.text = price

            plusBT.setOnClickListener {
                onInc(meal.uid ?: "")
            }

            minusBT.setOnClickListener {
                onDec(meal.uid ?: "")
            }

            deleteBT.setOnClickListener {
                onDelete(meal.uid ?: "")
            }

        }
    }
}