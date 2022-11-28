package com.example.ordme.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.persistableBundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.base.BaseDialogFragment
import com.example.ordme.dialogs.DeleteDialogFragment
import com.example.ordme.ui.data.Addition
import com.example.ordme.ui.data.Meal
import com.example.ordme.ui.repository.FirebaseRepository
import com.example.ordme.ui.screen.BasketViewModel
import com.example.ordme.ui.screen.MealViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_delete.view.*
import kotlinx.android.synthetic.main.item_basket.view.*
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.joinAll
import retrofit2.http.DELETE
import kotlin.math.absoluteValue

class BasketAdapter(
    val context: Context,
    val onInc: (String) -> Unit,
    val onDec: (String) -> Unit,
    val onDelete: (String) -> Unit,
    val activity: Activity,
    var basketList: ArrayList<Meal> = arrayListOf()
) :
    RecyclerView.Adapter<BasketAdapter.MyViewHolder>() {

    private var meals: ArrayList<Meal> = ArrayList()
    var meal: MutableLiveData<Meal?> = MutableLiveData()
    var addition: MutableLiveData<Addition?> = MutableLiveData()

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
        private val additionsTV = v.findViewById<TextView>(R.id.additionsTV)

        fun bindView(meal: Meal) {
            val price = "%.2f".format(meal.price)
            val x = addition.value?.nameAddition

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
                DeleteDialogFragment(action =  {
                    onDelete(meal.uid ?: "")
                }).show(activity)
            }
        }
    }
}