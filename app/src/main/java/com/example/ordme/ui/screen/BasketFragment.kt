package com.example.ordme.ui.screen

import androidx.fragment.app.FragmentResultListener
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_basket.*

class BasketFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_basket

    override fun subscribeUi() {

        //it transferred data from FragmentMeal to FragmentBasket and this lambda loaded these data without used navigate
        parentFragmentManager.setFragmentResultListener("dataRestaurantAndMeal", this,
            FragmentResultListener { _, result ->

                val data = result.getString("uidRestaurant")
                textView9.text = data.toString()
            })

    }

    override fun unsubscribeUi() {

    }
}