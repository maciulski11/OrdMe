package com.example.ordme.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.adapter.CheckoutAdapter
import com.example.ordme.data.model.Basket
import com.example.ordme.data.model.Meal
import com.example.ordme.data.model.Order
import com.example.ordme.services.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.fragment_checkout.returnBT
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class CheckoutViewModel: ViewModel(){

    var basket: MutableLiveData<Basket?> = MutableLiveData(null)
    var order: MutableLiveData<Order?> = MutableLiveData(null)
    var meal: MutableLiveData<Meal?> = MutableLiveData()

    val meals: ArrayList<Meal>
        get() = basket.value?.meals ?: arrayListOf()

    fun fetchCheckout(uidRestaurant: String) {
        FirebaseRepository().fetchBasket(uidRestaurant) {
            basket.value = it ?: Basket(uidRestaurant)
        }
    }

    fun submitOrder(order: Order, uidRestaurant: String, uid: String){
        FirebaseRepository().submitOrder(order, uidRestaurant, uid)
    }

    val orderValue: Double
        get() =
            meals.sumOf { meal -> (meal.price ?: 0.0) * (meal.amount?.toDouble() ?: 0.0) }

    val delivery: Double
    get() = 2.99
    
    val paymentService: Double
    get() = orderValue * 0.1

    val discount: Double
    get() = -20.00

    val totalPrice: Double
    get() = orderValue + delivery + paymentService + discount

}

class CheckoutFragment: BaseFragment() {
    override val layout: Int  = R.layout.fragment_checkout

    private lateinit var adapter: CheckoutAdapter
    private val checkoutViewModel = CheckoutViewModel()
    private val mainViewModel: MainViewModel by activityViewModels()

    @SuppressLint("SimpleDateFormat")
    override fun subscribeUi() {

        val basket = requireArguments().getParcelable<Basket>("basket")

        recyclerViewOrders.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewOrders.setHasFixedSize(true)

        checkoutViewModel.basket.observe(this) {
            updatePrices()

            adapter = CheckoutAdapter(basketList = checkoutViewModel.meals)
            recyclerViewOrders.adapter = adapter
        }

        checkoutViewModel.fetchCheckout(basket?.uid.toString())

        returnToBasket()
        returnToRestaurant()

        addressBT.setOnClickListener {
            findNavController().navigate(R.id.action_checkoutFragment_to_locationFragment)
        }

        submitOrderBT.setOnClickListener {
                val uid = UUID.randomUUID().toString()
                val time = DateTimeFormatter
                    .ofPattern("dd.MM.yyyy HH:mm:ss")
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now())

                Order(
                    uid,
                    basket?.meals,
                    "",
                    checkoutViewModel.totalPrice,
                    "",
                    time,
                    false
                ).let { order ->
                    checkoutViewModel.submitOrder(order, basket?.uid.toString(), uid)
                }

                findNavController().navigate(R.id.action_checkoutFragment_to_finalFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updatePrices() {

        orderValueTV.text = "%.2f".format(checkoutViewModel.orderValue)
        deliveryTV.text = "%.2f".format(checkoutViewModel.delivery)
        serviceTV.text = "%.2f".format(checkoutViewModel.paymentService)
        discountTV.text = "%.2f".format(checkoutViewModel.discount)
        totalTV.text = "%.2f".format(checkoutViewModel.totalPrice)
    }

    private fun returnToRestaurant() {
        val basket = requireArguments().getParcelable<Basket>("basket")

        //if you click, return all the data in that meal back as it was
        returnToRestaurantBT.setOnClickListener {

            val bundle = Bundle()
            bundle.putString(
                "uidRestaurant",
                basket?.uid
            )
            mainViewModel.fetchRestaurantMeals(basket?.uid!!)

            findNavController().navigate(R.id.action_checkoutFragment_to_restaurantFragment, bundle)
        }
    }

    private fun returnToBasket() {
        val basket = requireArguments().getParcelable<Basket>("basket")

        returnBT.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(
                "uidRestaurant",
                basket?.uid
            )
            checkoutViewModel.fetchCheckout(basket?.uid!!)

            findNavController().navigate(R.id.action_checkoutFragment_to_basketFragment, bundle)
        }
    }

    override fun unsubscribeUi() {

    }
}