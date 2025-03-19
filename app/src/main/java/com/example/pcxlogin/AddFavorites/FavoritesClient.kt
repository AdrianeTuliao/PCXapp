package com.example.pcxlogin.AddFavorites

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FavoritesClient {
    private const val BASE_URL = "http://192.168.18.127/PCXadmin/"

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val favoritesApi: FavoritesApi by lazy {
        retrofit.create(FavoritesApi::class.java)
    }
}