package com.example.ordme.ui.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ordme.ui.data.Basket
import com.example.ordme.ui.data.Restaurant
import com.example.ordme.ui.data.User
import com.example.ordme.ui.repository.FirebaseRepository
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel: ViewModel() {

    private val repository = FirebaseRepository()

    private lateinit var db: FirebaseFirestore

    var restaurantList = MutableLiveData<ArrayList<Restaurant>>()
    var basketList = MutableLiveData<ArrayList<Basket>>()


    fun createNewUser(user: User){
        repository.createNewUser(user)
    }

    fun fetchRestaurants() {
        repository.fetchRestaurantList {
            restaurantList.postValue(it)
        }
    }

    fun fetchBasketList() {
        repository.fetchBasketListForCurrentUser {
            basketList.postValue(it)
        }
    }

    fun getBasketWith(uid: String): Basket? =
        basketList.value?.firstOrNull { it.uid == uid }

}