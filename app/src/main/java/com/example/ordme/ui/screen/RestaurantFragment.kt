package com.example.ordme.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.adapter.RestaurantAdapter
import com.example.ordme.data.model.Basket
import com.example.ordme.data.model.Meal
import com.example.ordme.ui.repository.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_restaurant.*
import kotlinx.android.synthetic.main.fragment_restaurant.returnBT

class RestaurantViewModel(var restaurantId: String = "", var basket: Basket? = null) : ViewModel() {

}

class RestaurantFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_restaurant

    private lateinit var mealsList: ArrayList<Meal>
    private lateinit var adapter: RestaurantAdapter
    private var restaurantViewModel = RestaurantViewModel()

    private val viewModel: MainViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {

        restaurantViewModel.restaurantId = requireArguments().getString("uidRestaurant").toString()

        recyclerViewChooseDish.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewChooseDish.setHasFixedSize(true)

        mealsList = arrayListOf()

        adapter = RestaurantAdapter(mealsList, requireView())
        recyclerViewChooseDish.adapter = adapter

        viewModel.mealsList.observe(this) {
            adapter.mealsList = it
            adapter.notifyDataSetChanged()
        }

        viewModel.fetchRestaurant(restaurantViewModel.restaurantId, requireView(), requireContext())
        viewModel.fetchRestaurantMeals(restaurantViewModel.restaurantId)

        //if you click, return all the data in that meal back as it was
        returnBT.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(
                "uidRestaurant",
                restaurantViewModel.restaurantId
            )

            viewModel.fetchRestaurantMeals(restaurantViewModel.restaurantId)

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

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.basket -> {
                val bundle = Bundle()
                bundle.putString(
                    "uidRestaurant",
                    restaurantViewModel.restaurantId
                )
                findNavController().navigate(
                    R.id.action_restaurantFragment_to_basketFragment,
                    bundle
                )
            }
        }
        return false
    }

    override fun unsubscribeUi() {
        viewModel.mealsList.removeObservers(this)
    }
}