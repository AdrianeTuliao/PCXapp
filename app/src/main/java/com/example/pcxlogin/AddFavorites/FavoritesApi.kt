package com.example.pcxlogin.AddFavorites

import com.example.pcxlogin.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FavoritesApi {
    @POST("add_favorite.php")
    fun addFavorite(@Body request: AddFavoriteRequest): Call<ApiResponse>
}

data class AddFavoriteRequest(
    val user_id: Int,
    val product_id: Int,
    val product_name: String,
    val imageUrl: String,
    val price: Double
)
data class FavoriteItemLocal(
    val name: String,
    val price: Double,
    val imageUrl: String
)



