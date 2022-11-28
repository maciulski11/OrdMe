package com.example.ordme.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.ui.data.Addition
import kotlinx.android.synthetic.main.item_addition.view.*

class MealAdapter(val onAddAddition: (String) -> Unit, val onAddedAddition: (String) -> Unit, var additionsList: ArrayList<Addition>): RecyclerView.Adapter<MealAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_addition, parent, false)

            return MyViewHolder(itemView)
        }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val addition = additionsList[position]

        holder.bindView(addition)
    }

    override fun getItemCount(): Int = additionsList.size

    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {

        fun bindView(addition: Addition){
            val price = "%.2f".format(addition.priceAddition)
            val buttonAdd = v.findViewById<Button>(R.id.addAdditionBT)
            val buttonAdded = v.findViewById<Button>(R.id.addedAdditionBT)


            v.nameAdditionCheckBox.text = addition.nameAddition
            v.priceAdditionTV.text = price
            buttonAdd.setOnClickListener {
                onAddAddition(addition.nameAddition ?: "")

                buttonAdd.visibility = View.INVISIBLE
                buttonAdded.visibility = View.VISIBLE
            }

            buttonAdded.setOnClickListener {
                onAddedAddition(addition.nameAddition ?: "")

                buttonAdd.visibility = View.VISIBLE
                buttonAdded.visibility = View.INVISIBLE
            }

         }
    }
}