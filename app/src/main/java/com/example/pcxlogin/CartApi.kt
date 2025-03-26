package com.example.pcxlogin

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CartApi {
    @FormUrlEncoded
    @POST("add_to_cart.php")
    fun addToCart(
        @Field("user_id") userId: Int,
        @Field("product_id") productId: Int,
        @Field("product_name") productName: String,
        @Field("product_price") productPrice: Double,
        @Field("product_image_url") productImageUrl: String,
        @Field("quantity") quantity: Int,
        @Field("product_stock") productStock: Int
    ): Call<ApiRes1>

    @FormUrlEncoded
    @POST("fetch_cart.php")
    fun fetchCartItems(
        @Field("user_id") userId: Int
    ): Call<CartResponse>

    @FormUrlEncoded
    @POST("remove_from_cart.php")
    fun removeFromCart(
        @Field("user_id") userId: Int,
        @Field("product_id") productId: Int
    ): Call<ApiRes1>

    @FormUrlEncoded
    @POST("update_cart.php")
    fun updateCartQuantity(
        @Field("user_id") userId: Int,
        @Field("product_id") productId: Int,
        @Field("quantity") quantity: Int
    ): Call<ApiRes1>
}

data class CartItem(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_price") val productPrice: Double,
    @SerializedName("product_image_url") val productImageUrl: String,
    @SerializedName("quantity") var quantity: Int,
    @SerializedName("product_stock") val productStock: Int,
    var isSelected: Boolean = false
)

data class ApiRes1(
    val success: Boolean,
    val message: String
)

data class CartResponse(
    val success: Boolean,
    val cart_items: List<CartItem>
)



