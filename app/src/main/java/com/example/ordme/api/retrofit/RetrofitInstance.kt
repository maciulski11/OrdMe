package com.example.ordme.api.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    //lazy -> jest to samo co init
    val api: MealApi by lazy {
        Retrofit.Builder()
            //bierzemy podstawowy url: https://www.themealdb.com/api/json/v1/1/random.php
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            //nastepnie konwertujemy json na kotlin
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MealApi::class.java)//dajemy interface jako parametr
    }
}