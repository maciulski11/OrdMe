package com.example.ordme.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.adapter.ChooseRestaurantAdapter
import com.example.ordme.ui.data.Restaurant
import com.example.ordme.ui.repository.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_choose_restaurant.*

class ChooseRestaurantFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_choose_restaurant

    private val fbAuth = FirebaseAuth.getInstance()
    private var restaurantsList = ArrayList<Restaurant>()
    private lateinit var adapter: ChooseRestaurantAdapter
    private lateinit var db: FirebaseFirestore

    val viewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {
        recyclerViewChooseRestaurant.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewChooseRestaurant.setHasFixedSize(true)

        //inicjujemy nasza liste:
        restaurantsList = arrayListOf()

        adapter = ChooseRestaurantAdapter(requireContext(), restaurantsList, requireView())
        recyclerViewChooseRestaurant.adapter = adapter

        downloadDataFromFirebase()


        viewModel.restaurantList.observe(this) {
            adapter.restaurantsList = it
            adapter.notifyDataSetChanged()
        }

        viewModel.fetchRestaurants()
        viewModel.fetchBasketList()

    }

    private fun downloadDataFromFirebase(){
        db = FirebaseFirestore.getInstance()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.basket_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.basket -> {

                findNavController().navigate(R.id.action_mainUserFragment_to_basketFragment)
            }
        }
        return false
    }

    override fun unsubscribeUi() {
        viewModel.restaurantList.removeObservers(this)
    }
}