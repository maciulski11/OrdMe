package com.example.ordme.ui.screen

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.data.Basket
import com.example.ordme.ui.data.Meal
import com.example.ordme.ui.repository.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_meal.*
import kotlinx.android.synthetic.main.fragment_meal.returnBT

class MealViewModel(var basket: Basket? = null): ViewModel() {

    private val repository = FirebaseRepository()
    var meal: MutableLiveData<Meal?> = MutableLiveData()

    val mealId: String?
        get() = meal.value?.uidMeal

    fun fetchMeal(restaurantId: String, mealId: String) {
        repository.fetchMeal(restaurantId, mealId){
            meal.postValue(it)
        }
    }

    fun incrementAmount() {
        val newValue = meal.value
        newValue?.amount = meal.value?.amount?.inc()
        meal.value = newValue
    }

    fun decrementAmount() {
        if(meal.value?.amount ?: 0 > 1) {
            val newValue = meal.value
            newValue?.amount = meal.value?.amount?.dec()
            meal.value = newValue

        }
    }

    val totalPrice: String
        get() {
            val price = meal.value?.price ?: 0.0
            val amount = meal.value?.amount?.toDouble() ?: 0.0
            return "%.2f".format(price * amount)
        }

}

class MealFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_meal

    private var mealViewModel = MealViewModel()
    private var restaurantViewModel = RestaurantViewModel()

    var basket: Basket? = null

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {
        //get data from RestaurantAdapter
        //TODO: Jak przesłać obiekt Meal
//        requireArguments().getParcelable("meal", Meal::class.java)
//        val restaurantId = requireArguments().getString("uidRestaurant") ?: ""
        val mealId = requireArguments().getString("uidMeal") ?: ""
        restaurantViewModel.restaurantId = requireArguments().getString("uidRestaurant") ?: ""
//        mealViewModel.mealId = requireArguments().getString("uidMeal")
//        mealViewModel.price = requireArguments().getDouble("price")
//        mealViewModel.priceStart = requireArguments().getDouble("priceStart")
//        mealViewModel.amount = requireArguments().getInt("amount")

        mealViewModel.meal.observe(this) { meal ->
            view?.let {
                val nameMeal = it.findViewById<TextView>(R.id.nameMealTV)
                val priceTV = it.findViewById<TextView>(R.id.addMealToBasketBT)
                val amountTV = it.findViewById<TextView>(R.id.amountTV)

                nameMeal.text = meal?.name ?: "-"
                amountTV.text = (meal?.amount ?: 0).toString()

                //TODO: Change to button
                priceTV.text = mealViewModel.totalPrice
            }
        }


        mealViewModel.fetchMeal(restaurantViewModel.restaurantId, mealId)

        FirebaseRepository().fetchBasket(restaurantViewModel.restaurantId) {
            basket = it ?: Basket(restaurantViewModel.restaurantId)
        }

        plusBT.setOnClickListener {
            mealViewModel.incrementAmount()

        }

        minusBT.setOnClickListener {
            mealViewModel.decrementAmount()

        }

        addMealToBasketBT.setOnClickListener {

            Toast.makeText(requireContext(), "Added", Toast.LENGTH_SHORT).show()

            mealViewModel.meal.value?.let {
                mealViewModel.meal.value?.price = mealViewModel.totalPrice.toDouble()
                basket?.meals?.add(it)
                Log.d("Basket", "$it")
                basket?.let { basket ->
                    FirebaseRepository().update(basket)

                    Log.d("Basket", "$basket")

                }
            }

            val bundle = Bundle()
            bundle.putString(
                "uidMeal",
                mealViewModel.mealId
            )
            bundle.putString(
                "uidRestaurant",
                restaurantViewModel.restaurantId
            )

            //it transferred data to FragmentBasket without used navigate
            parentFragmentManager.setFragmentResult("dataRestaurantAndMeal", bundle)

            findNavController().navigate(
                R.id.action_mealFragment_to_restaurantFragment,
                bundle
            )
        }

        returnToRestaurant()
    }

    private fun returnToRestaurant(){
        //if you click, return all the data in that meal back as it was
        returnBT.setOnClickListener {

            val bundle = Bundle()
            bundle.putString(
                "uidRestaurant",
                restaurantViewModel.restaurantId
            )

            mainViewModel.fetchRestaurantMeals(restaurantViewModel.restaurantId)

            findNavController().navigate(R.id.action_mealFragment_to_restaurantFragment, bundle)
        }
    }

    override fun unsubscribeUi() {
        mealViewModel.meal.removeObservers(this)
    }
}