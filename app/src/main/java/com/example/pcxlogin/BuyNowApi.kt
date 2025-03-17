package com.example.pcxlogin

import retrofit2.http.Field
import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BuyNowApi {
    @FormUrlEncoded
    @POST("create_order.php")
    fun createOrder(
        @Field("customer_name") customerName: String,
        @Field("items") items: String,
        @Field("total") total: Double,
        @Field("quantity") quantity: Int,
        @Field("product_id") productId: Int
    ): Call<ApiResponse>
}

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val stock: Int
)
