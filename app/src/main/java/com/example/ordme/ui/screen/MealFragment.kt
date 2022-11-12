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
import kotlinx.android.synthetic.main.fragment_meal.*
import kotlinx.android.synthetic.main.fragment_meal.returnBT

class MealViewModel(): ViewModel() {

    private val repository = FirebaseRepository()
    var meal: MutableLiveData<Meal?> = MutableLiveData()
    var basket: MutableLiveData<Basket?> = MutableLiveData()

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

    val priceOfBasket: String
        get() {
            val priceOfBasket: Double = totalPrice.toDouble()
            return "%.2f".format(priceOfBasket)
        }

}

class MealFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_meal

    private var mealViewModel = MealViewModel()

    var basket: Basket? = null

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {
        //TODO: Jak przesłać obiekt Meal
        val meal = requireArguments().getParcelable<Meal>("meal")

        mealViewModel.meal.observe(this) { meal ->
            view?.let {
                val nameMeal = it.findViewById<TextView>(R.id.nameMealTV)
                val priceTV = it.findViewById<TextView>(R.id.addMealToBasketBT)
                val amountTV = it.findViewById<TextView>(R.id.amountTV)

                nameMeal.text = meal?.name ?: "-"
                amountTV.text = (meal?.amount ?: 0).toString()

                priceTV.text = mealViewModel.totalPrice
            }
        }


        mealViewModel.fetchMeal(meal?.uidRestaurant!!, meal.uidMeal!!)

        FirebaseRepository().fetchBasket(meal.uidRestaurant) {
            basket = it ?: Basket(meal.uidRestaurant)

            Log.d("Basket", "${basket?.totalPrice}")

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
                meal.uidMeal
            )
            bundle.putString(
                "uidRestaurant",
                meal.uidRestaurant
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
        val meal = requireArguments().getParcelable<Meal>("meal")

        //if you click, return all the data in that meal back as it was
        returnBT.setOnClickListener {

            val bundle = Bundle()
            bundle.putString(
                "uidRestaurant",
                meal?.uidRestaurant
            )

            mainViewModel.fetchRestaurantMeals(meal?.uidRestaurant!!)

            findNavController().navigate(R.id.action_mealFragment_to_restaurantFragment, bundle)
        }
    }

    override fun unsubscribeUi() {
        mealViewModel.meal.removeObservers(this)
    }
}