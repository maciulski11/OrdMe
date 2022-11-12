package com.example.ordme.ui.screen


import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.adapter.BasketAdapter
import com.example.ordme.ui.data.Basket
import com.example.ordme.ui.data.Meal
import com.example.ordme.ui.repository.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_basket.*

class BasketViewModel(var basket: Basket? = null) : ViewModel() {

    val meals: MutableLiveData<ArrayList<Basket>> = MutableLiveData(arrayListOf())
    var meal: MutableLiveData<Meal?> = MutableLiveData()

}

class BasketFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_basket

    private val fbAuth = FirebaseAuth.getInstance()
    private lateinit var adapter: BasketAdapter
    private lateinit var basketList: ArrayList<Basket>


    //wywolanie viewmodel we calej aktywnosci, poniewaz ten fragment nie jest wywolywany po kolei
    val mainViewModel: MainViewModel by activityViewModels()
    private var basketViewModel = BasketViewModel()
    private var restaurantViewModel = RestaurantViewModel()

    private val viewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {

        val uidRestaurant = requireArguments().getString("uidRestaurant").toString()

        recyclerViewBasket.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewBasket.setHasFixedSize(true)

        basketList = arrayListOf()


        FirebaseRepository().fetchBasket(uidRestaurant) { it ->
            basketViewModel.basket = it ?: Basket(restaurantViewModel.restaurantId)

            basketViewModel.basket?.meals?.let {
                adapter = BasketAdapter(it, requireContext())
                recyclerViewBasket.adapter = adapter

            }
        }

        checkoutBT.setOnClickListener {
            findNavController().navigate(R.id.action_basketFragment_to_checkoutFragment)
        }

        exitBT.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(
                "uidRestaurant",
                basketViewModel.basket?.uid!!
            )

            mainViewModel.fetchRestaurantMeals(basketViewModel.basket?.uid!!)

            findNavController().navigate(R.id.action_basketFragment_to_restaurantFragment, bundle)
        }
    }

    override fun unsubscribeUi() {
        basketViewModel.meals.removeObservers(this)

    }
}