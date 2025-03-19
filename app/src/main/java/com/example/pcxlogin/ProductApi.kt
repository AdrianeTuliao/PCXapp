package com.example.pcxlogin

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApi {
    @GET("get_products.php")
    fun getProductsByCategory(@Query("category") category: String): Call<List<Product>>
}