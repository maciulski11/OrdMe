package com.example.ordme.ui.screen

import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_meal.*

class MessageFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_message

    override fun subscribeUi() {


        returnBT.setOnClickListener {

            findNavController().navigate(R.id.action_messageFragment_to_chooseRestaurantFragment)
        }
    }

    override fun unsubscribeUi() {

    }
}