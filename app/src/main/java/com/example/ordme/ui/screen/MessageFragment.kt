package com.example.ordme.ui.screen

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.data.model.Message
import com.example.ordme.ui.adapter.MessageAdapter
import com.example.ordme.services.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_message.*

class MessageFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_message

    private lateinit var adapter: MessageAdapter
    private var messageList = ArrayList<Message>()

    private val viewModel: MainViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {

        recyclerViewMessage.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewMessage.setHasFixedSize(true)

        messageList = arrayListOf()

        adapter = MessageAdapter(messageList, requireView())
        recyclerViewMessage.adapter = adapter

        viewModel.messageList.observe(this) {
            adapter.messageList = it
            adapter.notifyDataSetChanged()
        }

        FirebaseRepository().updateMessage {
            viewModel.fetchMessage()
        }

        returnBT.setOnClickListener {

            findNavController().navigate(R.id.action_messageFragment_to_chooseRestaurantFragment)
        }
    }

    override fun unsubscribeUi() {
        viewModel.messageList.removeObservers(this)
    }
}