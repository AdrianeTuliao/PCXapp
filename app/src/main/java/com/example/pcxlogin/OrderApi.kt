package com.example.pcxlogin

import retrofit2.Call
import retrofit2.http.GET

interface OrderApi {
    @GET("fetch_orders_users.php")
    fun getOrders(): Call<List<Order>>
}

data class Order(
    val order_status: String,
    val items: String,
    val payment_status: String,
    val total: String,
    val image: String
)
