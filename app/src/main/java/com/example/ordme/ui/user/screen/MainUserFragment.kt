package com.example.ordme.ui.user.screen

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.google.firebase.auth.FirebaseAuth

class MainUserFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_main_user

    private val fbAuth = FirebaseAuth.getInstance()

    override fun subscribeUi() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.logout_user_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logoutUserAction -> {
                fbAuth.signOut()
                findNavController().navigate(R.id.action_mainUserFragment_to_loginUserFragment)
            }
        }
        return false
    }

    override fun unsubscribeUi() {

    }
}