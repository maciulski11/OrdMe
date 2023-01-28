package com.example.ordme.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ordme.R
import com.example.ordme.api.pojo.Meal
import com.example.ordme.api.pojo.MealList
import com.example.ordme.api.retrofit.RetrofitInstance
import com.example.ordme.base.BaseFragment
import com.example.ordme.data.model.Restaurant
import com.example.ordme.databinding.FragmentChooseRestaurantBinding
import com.example.ordme.ui.adapter.ChooseRestaurantAdapter
import com.example.ordme.ui.view_model.MainViewModel
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_choose_restaurant.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ChooseRestaurantFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_choose_restaurant

    private lateinit var binding: FragmentChooseRestaurantBinding

    private var restaurantsList = ArrayList<Restaurant>()
    private lateinit var adapter: ChooseRestaurantAdapter

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {
        recyclerViewChooseRestaurant.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewChooseRestaurant.setHasFixedSize(true)

        //inicjujemy nasza liste:
        restaurantsList = arrayListOf()

        adapter = ChooseRestaurantAdapter(requireContext(), restaurantsList, requireView())
        recyclerViewChooseRestaurant.adapter = adapter

        //API: uzywamy instancji modernizacji zeby wywolac losowy posilek
        RetrofitInstance.api.getRandomMeal()
            .enqueue(object : Callback<MealList> { //uzywamy wywolania zwrotnego z callback
                override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                    if (response.body() != null) { //spr. czy callback nie jest nullem

                        //dajemy meals i index 0 poniewaz bierzemy tylko 1 posilek
                        val randomMeal: Meal = response.body()!!.meals[0]
                        Glide.with(this@ChooseRestaurantFragment)
                            .load(randomMeal.strMealThumb)
                            .into(binding.randomMeal)

                        Glide.with(this@ChooseRestaurantFragment)
                            .load(randomMeal.strMealThumb)
                            .into(binding.rawndomMeal)


                        Log.d("TEST", "meal id ${randomMeal.idMeal} name ${randomMeal.strMeal}")
                    } else {
                        return
                    }
                }

                override fun onFailure(call: Call<MealList>, t: Throwable) {
                    Log.d("ChooseRestaurantFragment", t.message.toString())
                }

            })


        viewModel.restaurantList.observe(this) {
            adapter.restaurantList = it
            adapter.notifyDataSetChanged()
        }

        viewModel.fetchRestaurantsList()
        viewModel.fetchBasketList()

        searchRestaurant.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString(
//                "uidRestaurant",
//
//            )
            findNavController().navigate(R.id.action_chooseRestaurantFragment_to_searchViewFragment2)
        }

    }

    override fun unsubscribeUi() {
        viewModel.restaurantList.removeObservers(this)
    }
}