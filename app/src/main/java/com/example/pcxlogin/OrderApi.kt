package com.example.pcxlogin

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OrderApi {

    @GET("fetch_orders_users.php")
    fun getOrders(): Call<List<Order>>

    @POST("cancel_order.php")
    @FormUrlEncoded
    fun cancelOrder(
        @Field("id_order") orderId: Int,
        @Field("quantity") quantity: Int,
        @Field("items") items: String
    ): Call<ApiRes>
}

data class ApiRes(
    val status: String,
    val message: String
)

data class Order(
    val id: Int,
    val items: String,
    val quantity: Int,
    val total: String,
    val payment_status: String,
    val order_status: String,
    val imageUrl: String,
)



