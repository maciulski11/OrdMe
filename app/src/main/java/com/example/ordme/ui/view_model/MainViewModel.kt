package com.example.ordme.ui.view_model

import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ordme.R
import com.example.ordme.data.model.Basket
import com.example.ordme.data.model.Meal
import com.example.ordme.data.model.Restaurant
import com.example.ordme.data.model.User
import com.example.ordme.ui.repository.FirebaseRepository

class MainViewModel: ViewModel() {

    private val repository = FirebaseRepository()

    var meal: MutableLiveData<Meal?> = MutableLiveData()
    var restaurantList = MutableLiveData<ArrayList<Restaurant>>()
    var mealsList = MutableLiveData<ArrayList<Meal>>()
    var basketList = MutableLiveData<ArrayList<Basket>>()


    fun createNewUser(user: User){
        repository.createNewUser(user)
    }

    fun fetchRestaurantsList() {
        repository.fetchRestaurantsList {
            restaurantList.postValue(it)
        }
    }
    fun fetchRestaurantMeals(uid: String){
        repository.fetchRestaurantMeals(uid){
            mealsList.postValue(it)
        }
    }

        fun fetchBasketList() {
        repository.fetchBasketListForCurrentUser {
            basketList.postValue(it)


        }
    }

    fun fetchRestaurant(uid: String, v: View){
        repository.fetchRestaurant(uid){

            val nameRestaurant = v.findViewById<TextView>(R.id.textView7)
            nameRestaurant.text = it!!.nameRestaurant
        }
    }
}