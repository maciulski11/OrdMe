package com.example.ordme.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.data.model.Message
import com.example.ordme.services.FirebaseRepository
import kotlinx.android.synthetic.main.item_message.view.*

class MessageAdapter(var messageList: ArrayList<Message>, private val v: View) :
    RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = messageList[position]

        holder.chooseMessage.setOnClickListener {
            holder.iconImageView.setImageResource(R.drawable.ic_baseline_fastfood_24)
            FirebaseRepository().updateItemMessage(message.uid ?: "")

            val bundle = Bundle()
            bundle.putString(
                "uidMessage",
                message.uid
            )

            v.findNavController().navigate(R.id.action_messageFragment_to_messageReadFragment, bundle)
        }

        holder.bindView(message)
    }

    override fun getItemCount(): Int = messageList.size

    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {

        val chooseMessage = v.findViewById<ConstraintLayout>(R.id.chooseMessage)!!
        val iconImageView: ImageView = itemView.findViewById(R.id.icon)


        fun bindView(m: Message) {
            v.title.text = m.title
            v.info.text = m.info

            if (m.read == true) {
                iconImageView.setImageResource(R.drawable.ic_baseline_fastfood_24)
            } else {
                iconImageView.setImageResource(R.drawable.ic_baseline_fastfood_black)
            }

        }
    }
}