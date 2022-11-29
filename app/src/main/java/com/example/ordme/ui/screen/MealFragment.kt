package com.example.ordme.ui.screen

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.adapter.MealAdapter
import com.example.ordme.data.model.Addition
import com.example.ordme.data.model.Basket
import com.example.ordme.data.model.Meal
import com.example.ordme.ui.repository.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_meal.*
import java.util.*

class MealViewModel(var meall: Meal? = null): ViewModel() {

    private val repository = FirebaseRepository()
    var meal: MutableLiveData<Meal?> = MutableLiveData()

    private val additions: ArrayList<Addition>
        get() = meal.value?.additions ?: arrayListOf()

    fun fetchMeal(restaurantId: String, mealId: String) {
        repository.fetchMeal(restaurantId, mealId){
            meal.postValue(it)
        }
    }

    fun incrementAddition(uidAdd: String) {
        val index = additions.indexOfFirst { it.nameAddition == uidAdd }

        if (index != -1) {
            additions[index].amount = additions[index].amount?.inc()

            val price =
                (additions[index].amount?.inc()!! - 1) * (additions[index].priceAddition!!.toDouble())

            Log.d("ADDITION", "$price, ${additions[index].amount}, ${additions[index].priceAddition}")

            val x = additions.sumOf { add-> add.amount!! * add.priceAddition!! }

            Log.d("ADDITION SUM", "${meal.value?.additions}")

        }
    }

    fun decrementAddition(uidAdd: String) {
        val index = additions.indexOfFirst { it.nameAddition == uidAdd }

        if (index != -1) {
            additions[index].amount = additions[index].amount?.dec()
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

    val xxx: Double
        get() =
            additions.sumOf { add-> add.amount!! * add.priceAddition!! }

    val totalPrice: String
        get() {
            val price = meal.value?.price ?: 0.0
            val amount = meal.value?.amount?.toDouble() ?: 0.0
            return "%.2f".format((price + xxx) * amount)
        }
    }

class MealFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_meal

    private lateinit var adapter: MealAdapter
    private lateinit var additionsList: ArrayList<Addition>
    private var mealViewModel = MealViewModel()

    var basket: Basket? = null

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {
        //TODO: Jak przesłać obiekt Meal
        val meal = requireArguments().getParcelable<Meal>("meal")

        additionRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        additionRecyclerView.setHasFixedSize(true)

        additionsList = arrayListOf()

        FirebaseRepository().fetchAdditions(meal?.uidRestaurant!!, meal.uidMeal!!){ it ->
            mealViewModel.meall = it ?: Meal()

            mealViewModel.meall?.additions?.let {
                adapter = MealAdapter({ uid ->
                    mealViewModel.incrementAddition(uid)
                    mealViewModel.meal.observe(this){
                        view?.let {
                            val priceTV = it.findViewById<TextView>(R.id.addMealToBasketBT)
                            priceTV.text = mealViewModel.totalPrice
                        }
                    }
                },{ uid ->
                    mealViewModel.decrementAddition(uid)
                    mealViewModel.meal.observe(this){
                        view?.let {
                            val priceTV = it.findViewById<TextView>(R.id.addMealToBasketBT)
                            priceTV.text = mealViewModel.totalPrice
                        }
                    }
                },it)
                additionRecyclerView.adapter = adapter
            }
        }

        mealViewModel.meal.observe(this) {
            view?.let {
                val nameMeal = it.findViewById<TextView>(R.id.nameMealTV)
                val priceTV = it.findViewById<TextView>(R.id.addMealToBasketBT)
                val amountTV = it.findViewById<TextView>(R.id.amountTV)

                nameMeal.text = meal.name ?: "-"
                amountTV.text = (mealViewModel.meal.value?.amount ?: 0).toString()
                priceTV.text = mealViewModel.totalPrice
            }
        }

        mealViewModel.fetchMeal(meal.uidRestaurant, meal.uidMeal)

        FirebaseRepository().fetchBasket(meal.uidRestaurant) {
            basket = it ?: Basket(meal.uidRestaurant)

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
                mealViewModel.meal.value?.uid = UUID.randomUUID().toString()

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
            bundle.putDouble(
                "price",
                meal.price!!.toDouble()
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