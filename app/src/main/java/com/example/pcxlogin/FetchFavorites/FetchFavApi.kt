package com.example.pcxlogin.FetchFavorites

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FetchFavApi {
    @GET("fetch_favorites.php")
    fun getFavorites(@Query("user_id") userId: Int): Call<List<FavoriteItemResponse>>
}

data class FavoriteItemResponse(
    val product_id: Int,
    val product_name: String,
    val imageUrl: String,
    val price: Double
)
