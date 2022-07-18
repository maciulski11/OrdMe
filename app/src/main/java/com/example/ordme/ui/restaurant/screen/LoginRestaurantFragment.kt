package com.example.ordme.ui.restaurant.screen

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login_restaurant.*

class LoginRestaurantFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_login_restaurant

    override fun subscribeUi() {



        joinButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginRestaurantFragment_to_registerRestaurantFragment)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.login_user_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.loginUserAction -> {
                findNavController().navigate(R.id.action_loginRestaurantFragment_to_loginUserFragment)
            }
        }
        return false
    }

    override fun unsubscribeUi() {

    }
}