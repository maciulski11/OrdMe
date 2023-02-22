package com.example.ordme.ui.screen

import androidx.fragment.app.activityViewModels
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.view_model.MainViewModel

class MessageReadFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_message_read

    private val viewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {

        val message = requireArguments().getString("uidMessage").toString()

        viewModel.fetchReadMessage(message, requireView())
    }

    override fun unsubscribeUi() {

    }
}