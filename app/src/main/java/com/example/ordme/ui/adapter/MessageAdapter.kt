package com.example.ordme.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.data.model.Message
import kotlinx.android.synthetic.main.item_message.view.*

class MessageAdapter(var messageList: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = messageList[position]

        holder.bindView(message)
    }

    override fun getItemCount(): Int = messageList.size

    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v){

        val chooseMessage = v.findViewById<ConstraintLayout>(R.id.chooseMessage)!!

        fun bindView(m: Message) {
            v.title.text = m.title
            v.info.text = m.info
        }
    }
}