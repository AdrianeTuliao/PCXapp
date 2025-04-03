package com.example.pcxlogin

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CartClient {
    private const val BASE_URL = "http://192.168.250.183/PCXadmin/"

    val instance: CartApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(CartApi::class.java)
    }
}
