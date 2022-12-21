package com.example.ordme.ui.screen

import androidx.navigation.fragment.findNavController
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_meal.*

class ProfileFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_profile

    override fun subscribeUi() {

        returnBT.setOnClickListener {

            findNavController().navigate(R.id.action_profileFragment2_to_chooseRestaurantFragment)
        }

    }


    override fun unsubscribeUi() {

    }
}