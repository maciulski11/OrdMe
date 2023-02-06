package com.example.ordme.ui.view_model

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ordme.R
import com.example.ordme.data.model.Basket
import com.example.ordme.data.model.Meal
import com.example.ordme.data.model.Restaurant
import com.example.ordme.data.model.User
import com.example.ordme.ui.repository.FirebaseRepository

class MainViewModel: ViewModel() {

    private val repository = FirebaseRepository()
    val user1 = repository.getUserData()


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

    fun fetchRestaurant(uid: String, v: View, context: Context){
        repository.fetchRestaurant(uid){

            val nameRestaurant = v.findViewById<TextView>(R.id.textView7)
            val icon = v.findViewById<ImageView>(R.id.icon)
            val image = v.findViewById<ImageView>(R.id.imageView)

            nameRestaurant.text = it!!.nameRestaurant

            Glide.with(context)
                .load(it.icon)
                .override(180,180)
                .circleCrop()
                .into(icon)

            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels

            Glide.with(context)
                .load(it.image_photo)
                .apply(RequestOptions().override(screenWidth, 550))
                .into(image)
        }
    }

//    fun uploadUserPhoto(bytes: ByteArray) {
//        FirebaseRepository().uploadUserPhoto(bytes)
//    }
}