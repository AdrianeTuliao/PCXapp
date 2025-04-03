package com.example.pcxlogin

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ChangePasswordClient {
    private const val BASE_URL = "http://192.168.250.183/PCXadmin/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val changePasswordApi: ChangePasswordApi by lazy {
        retrofit.create(ChangePasswordApi::class.java)
    }
}
