package com.example.ordme.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.adapter.RestaurantAdapter
import com.example.ordme.ui.data.Basket
import com.example.ordme.ui.data.Meal
import com.example.ordme.ui.data.Restaurant
import com.example.ordme.ui.repository.FirebaseRepository
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_meal.*
import kotlinx.android.synthetic.main.fragment_restaurant.*
import kotlinx.android.synthetic.main.fragment_restaurant.returnBT

class RestaurantViewModel(var restaurantId: String = "", var basket: Basket? = null): ViewModel() {

}


class RestaurantFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_restaurant

    private var db = FirebaseFirestore.getInstance()
    private var restaurant = Restaurant()
    private lateinit var mealsList: ArrayList<Meal>
    private lateinit var adapter: RestaurantAdapter
    private var viewModel = RestaurantViewModel()


    override fun subscribeUi() {

        viewModel.restaurantId = requireArguments().getString("uidRestaurant").toString()

        recyclerViewChooseDish.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewChooseDish.setHasFixedSize(true)

        mealsList = arrayListOf()

        adapter = RestaurantAdapter(mealsList, requireView())
        recyclerViewChooseDish.adapter = adapter


        textView7.text = restaurant.nameRestaurant
        textView8.text = restaurant.uid
        textView7.text = "Empty"


        //if you click, return all the data in that meal back as it was
        returnBT.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(
                "uidRestaurant",
                viewModel.restaurantId
            )

            findNavController().navigate(R.id.action_restaurantFragment_to_mainUserFragment, bundle)
        }
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
                val bundle = Bundle()
                bundle.putString(
                    "uidRestaurant",
                    viewModel.restaurantId
                )
                findNavController().navigate(R.id.action_restaurantFragment_to_basketFragment, bundle)
            }
        }
        return false
    }

    override fun unsubscribeUi() {

    }
}