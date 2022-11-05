package com.example.ordme.api.retrofit

import com.example.ordme.api.pojo.MealList
import retrofit2.Call
import retrofit2.http.GET

interface MealApi {

    //tworzymy funkcje ktora pobierze nam jeden posilek
    // i nie pobiera zadnych parametrow tylko zwraca call, upewnij sie ze zaimporotowales call ->retrofit
    @GET("random.php") //-> oznacza ze chce pobrac dane api,
                                    // kopiujemy tylko ostanie z linku: https://www.themealdb.com/api/json/v1/1/random.php
    fun getRandomMeal(): Call<MealList> //wywolanie przyjmuje liste posilkow
}