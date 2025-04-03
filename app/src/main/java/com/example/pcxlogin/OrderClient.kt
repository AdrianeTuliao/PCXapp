package com.example.pcxlogin

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OrderClient {

    private const val BASE_URL = "http://192.168.250.183/PCXadmin/"

    val instance: OrderApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(OrderApi::class.java)
    }
}
