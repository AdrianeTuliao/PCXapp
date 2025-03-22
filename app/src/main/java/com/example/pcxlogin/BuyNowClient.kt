package com.example.pcxlogin

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BuyNowClient {
    private const val BASE_URL = "http://192.168.18.127/PCXadmin/"

    val instance: BuyNowApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(BuyNowApi::class.java)
    }
}
