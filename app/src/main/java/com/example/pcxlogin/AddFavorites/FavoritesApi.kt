package com.example.pcxlogin.AddFavorites

import com.example.pcxlogin.ApiResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FavoritesApi {
    @POST("add_favorite.php")
    fun addFavorite(@Body request: AddFavoriteRequest): Call<ApiResponse>

    @GET("fetch_favorites.php")
    fun fetchFavorites(@Query("user_id") userId: Int): Call<List<FavoriteItemResponse>>
}

data class AddFavoriteRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("price") val price: Double,
    @SerializedName("stocks") val stocks: Int
)


data class FavoriteItemLocal(
    val name: String,
    val price: Double,
    val imageUrl: String
)

data class FavoriteItemResponse(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_image") val imageUrl: String,
    @SerializedName("price") val price: Double,
    @SerializedName("stocks") val stocks: Int
)



