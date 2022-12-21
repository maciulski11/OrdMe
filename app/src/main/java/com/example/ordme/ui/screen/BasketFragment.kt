package com.example.ordme.ui.screen


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.adapter.BasketAdapter
import com.example.ordme.data.model.Basket
import com.example.ordme.data.model.Meal
import com.example.ordme.data.model.Order
import com.example.ordme.ui.repository.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_basket.*

class BasketViewModel() : ViewModel() {

    var basket: MutableLiveData<Basket?> = MutableLiveData(null)
    var meal: MutableLiveData<Meal?> = MutableLiveData()

    val meals: ArrayList<Meal>
        get() = basket.value?.meals ?: arrayListOf()

    val totalPrice: Double
        get() =
            meals.sumOf { meal ->
                ((meal.price ?: 0.0) + (meal.additions?.filter { (it.amount ?: 0) > 0 }!!
                    .sumOf { it.priceAddition ?: 0.0 })) *
                        (meal.amount?.toDouble() ?: 0.0)
            }

    fun fetchBasket(uidRestaurant: String) {
        FirebaseRepository().fetchBasket(uidRestaurant) {
            basket.value = it ?: Basket(uidRestaurant)
        }
    }

    fun incrementAmount(mealId: String) {
        val index = meals.indexOfFirst { it.uid == mealId }

        if (index != -1) {
            meals[index].amount = meals[index].amount?.inc()

            FirebaseRepository().update(basket.value!!)
        }
    }

    fun decrementAmount(mealId: String) {
        val index = meals.indexOfFirst { it.uid == mealId }

        if (index != -1) {
            if ((meals[index].amount ?: 0) > 1) {
                meals[index].amount = meals[index].amount?.dec()

                FirebaseRepository().update(basket.value!!)
            }
        }
    }

    fun deleteMeal(mealId: String) {
        val index = meals.indexOfFirst { it.uid == mealId }

        meals.removeAt(index)

        FirebaseRepository().update(basket.value!!)
    }
}

class BasketFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_basket

    private lateinit var adapter: BasketAdapter

    //wywolanie viewmodel we calej aktywnosci, poniewaz ten fragment nie jest wywolywany po kolei
    private val mainViewModel: MainViewModel by activityViewModels()
    private var basketViewModel = BasketViewModel()

    override fun subscribeUi() {

        val uidRestaurant = requireArguments().getString("uidRestaurant").toString()

        recyclerViewBasket.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewBasket.setHasFixedSize(true)

        basketViewModel.basket.observe(this) {
            updateAdapter()
            updateTotalPrice()
        }

        basketViewModel.fetchBasket(uidRestaurant = uidRestaurant)

        checkoutBT.setOnClickListener {

            if (basketViewModel.meals.isEmpty()) {
                Toast.makeText(context, "Your basket is empty.", Toast.LENGTH_SHORT).show()
            } else {

                val map = hashMapOf(
                    "totalPrice" to basketViewModel.totalPrice
                )
                FirebaseRepository().addTotalPrice(
                    basketViewModel.basket.value?.uid.toString(),
                    map
                )

                val bundle = Bundle()
                bundle.putParcelable(
                    "basket",
                    Basket(
                        basketViewModel.basket.value?.uid,
                        basketViewModel.meals,
                        basketViewModel.basket.value?.information,
                        basketViewModel.basket.value?.totalPrice
                    )
                )
                findNavController().navigate(R.id.action_basketFragment_to_checkoutFragment, bundle)
            }
        }

        exitBT.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(
                "uidRestaurant",
                basketViewModel.basket.value?.uid!!
            )

            mainViewModel.fetchRestaurantMeals(basketViewModel.basket.value?.uid!!)

            findNavController().navigate(
                R.id.action_basketFragment_to_restaurantFragment,
                bundle
            )
        }
    }

    private fun updateAdapter() {
        adapter = BasketAdapter(requireContext(), {
            basketViewModel.incrementAmount(it)
            adapter.update(basketViewModel.meals)
            updateTotalPrice()
        }, {
            basketViewModel.decrementAmount(it)
            adapter.update(basketViewModel.meals)
            updateTotalPrice()
        }, {
            basketViewModel.deleteMeal(it)
            adapter.update(basketViewModel.meals)
            updateTotalPrice()
        },
            requireActivity(),
            basketList = basketViewModel.meals
        )

        recyclerViewBasket.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalPrice() {
        totalPrice.text = "%.2f".format(basketViewModel.totalPrice)
    }

    override fun unsubscribeUi() {
        basketViewModel.basket.removeObservers(this)
    }
}