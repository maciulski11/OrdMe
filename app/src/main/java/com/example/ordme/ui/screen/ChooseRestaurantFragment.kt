package com.example.ordme.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ordme.R
import com.example.ordme.api.pojo.Meal
import com.example.ordme.api.pojo.MealList
import com.example.ordme.api.retrofit.RetrofitInstance
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.adapter.ChooseRestaurantAdapter
import com.example.ordme.data.model.Restaurant
import com.example.ordme.databinding.FragmentChooseRestaurantBinding
import com.example.ordme.ui.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_choose_restaurant.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        searchRestaurant.setOnClickListener {
            
        }

        //uzywamy instancji modernizacji zeby wywolac losowy posilek
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

//        val name = viewModel.meal.value?.name
//
//        searchRestaurant.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
//            androidx.appcompat.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//                searchRestaurant.clearFocus()
//                if (name!!.contains(p0.toString())){
//                    adapter.restaurantsList.filter { it.nameRestaurant == p0 }
//                } else {
//
//                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(p0: String?): Boolean {
//                adapter.restaurantsList.filter { it.nameRestaurant == p0 }
//                return false
//            }
//        })

        viewModel.restaurantList.observe(this) {
            adapter.restaurantsList = it
            adapter.notifyDataSetChanged()
        }

        viewModel.fetchRestaurantsList()
        viewModel.fetchBasketList()
    }

    override fun unsubscribeUi() {
        viewModel.restaurantList.removeObservers(this)
    }
}