package com.example.pcxlogin.FetchFavorites

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FetchFavApi {
    @GET("fetch_favorites.php")
    fun getFavorites(@Query("user_id") userId: Int): Call<List<FavoriteItemResponse>>
}

data class FavoriteItemLocal(
    val name: String,
    val price: Double,
    val imageUrl: String,
    val stocks: Int
)

data class FavoriteItemResponse(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_image") val imageUrl: String,
    @SerializedName("stocks") val stocks: Int,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") val quantity: Int
)

