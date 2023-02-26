package com.example.ordme.ui.view_model

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ordme.R
import com.example.ordme.data.model.*
import com.example.ordme.services.FirebaseRepository

class MainViewModel: ViewModel() {

    private val repository = FirebaseRepository()

    var meal: MutableLiveData<Meal?> = MutableLiveData()
    var restaurantList = MutableLiveData<ArrayList<Restaurant>>()
    var messageList = MutableLiveData<ArrayList<Message>>()
    var mealsList = MutableLiveData<ArrayList<Meal>>()
    var basketList = MutableLiveData<ArrayList<Basket>>()

    fun updateUserData(success: () -> Unit) {
        repository.updateUserData {
            success()
        }
    }

    fun createNewUser(user: User) {
        repository.createNewUser(user)
    }

    fun fetchRestaurantsList() {
        repository.fetchRestaurantsList {
            restaurantList.postValue(it)
        }
    }

    fun fetchMessage() {
        repository.fetchMessage {
            messageList.postValue(it)
        }
    }

    fun fetchReadMessage(uid: String, v: View) {
        repository.fetchReadMessage(uid) {

            val title = v.findViewById<TextView>(R.id.titleMessage)
            val message = v.findViewById<TextView>(R.id.message)

            title.text = it!!.title
            message.text = it.message
        }
    }

    fun fetchRestaurantMeals(uid: String){
        repository.fetchRestaurantMeals(uid){
            mealsList.postValue(it)
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

    fun fetchBasketList() {
        repository.fetchBasketListForCurrentUser {
            basketList.postValue(it)
        }
    }

    fun mapLocation(context: Context, childFragmentManager: FragmentManager, layoutId: Int){
        repository.mapLocation(context, childFragmentManager, layoutId)
    }
}