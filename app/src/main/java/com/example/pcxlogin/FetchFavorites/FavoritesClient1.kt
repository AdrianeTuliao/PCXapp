package com.example.pcxlogin.FetchFavorites

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FavoritesClient1 {
    private const val BASE_URL = "http://192.168.250.183/PCXadmin/"

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val fetchFavApi: FetchFavApi by lazy {
        retrofit.create(FetchFavApi::class.java)
    }
}