package com.example.pcxlogin.FetchFavorites

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pcxlogin.FavoritesAdapter
import com.example.pcxlogin.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.pcxlogin.AddFavorites.FavoriteItemLocal

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var favoritesAdapter: FavoritesAdapter
    private val favoriteItems = mutableListOf<FavoriteItemResponse>()

    companion object {
        val favoriteItem = mutableListOf<FavoriteItemLocal>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewFavorites)

        favoritesAdapter = FavoritesAdapter(favoriteItems) { selectedItem ->
            navigateToProduct(selectedItem) // Navigate to the product when clicked
        }

        recyclerView.adapter = favoritesAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        Log.d("FavoritesDebug", "Fragment View Created. Fetching favorites...")
        fetchFavoritesFromApi()
    }

    private fun fetchFavoritesFromApi() {
        val fetchFavApi = FavoritesClient1.fetchFavApi
        val userId = 1  // You may want to dynamically get this ID

        Log.d("FavoritesDebug", "Fetching favorites for user ID: $userId")

        val call1 = fetchFavApi.getFavorites(userId)

        call1.enqueue(object : Callback<List<FavoriteItemResponse>> {
            override fun onResponse(
                call: Call<List<FavoriteItemResponse>>,
                response: Response<List<FavoriteItemResponse>>
            ) {
                if (response.isSuccessful) {
                    favoriteItems.clear()

                    val responseBody = response.body()
                    Log.d("FavoritesDebug", "Full API Response: $responseBody")

                    responseBody?.let {
                        for (item in it) {
                            Log.d("FavoritesDebug", "Item: ${item.productName}, Image URL: ${item.imageUrl}")
                        }
                        favoriteItems.addAll(it)
                    }

                    favoritesAdapter.notifyDataSetChanged()
                } else {
                    Log.e("FavoritesDebug", "Unsuccessful response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<FavoriteItemResponse>>, t: Throwable) {
                Log.e("FavoritesDebug", "Failed to fetch favorites: ${t.message}")
            }
        })
    }

    private fun navigateToProduct(selectedItem: FavoriteItemResponse) {
        val bundle = Bundle().apply {
            putInt("product_id", selectedItem.productId)
            putString("product_name", selectedItem.productName)
            putString("image_url", selectedItem.imageUrl)
            putString("price", selectedItem.price.toString())
        }

        findNavController().navigate(R.id.productDetailsFragment, bundle)

    }


}