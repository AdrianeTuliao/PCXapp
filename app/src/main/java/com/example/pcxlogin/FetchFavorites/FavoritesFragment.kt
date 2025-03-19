package com.example.pcxlogin.FetchFavorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
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

        favoritesAdapter = FavoritesAdapter(favoriteItems)
        recyclerView.adapter = favoritesAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchFavoritesFromApi()
    }

    private fun fetchFavoritesFromApi() {
        val fetchFavApi = FavoritesClient1.fetchFavApi

        val call1 = fetchFavApi.getFavorites(userId = 1)

        call1.enqueue(object : Callback<List<FavoriteItemResponse>> {
            override fun onResponse(
                call: Call<List<FavoriteItemResponse>>,
                response: Response<List<FavoriteItemResponse>>
            ) {
                if (response.isSuccessful) {
                    favoriteItems.clear()
                    response.body()?.let {
                        favoriteItems.addAll(it)
                    }
                    favoritesAdapter.notifyDataSetChanged()
                } else {
                    println("Unsuccessful response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<FavoriteItemResponse>>, t: Throwable) {
                println("Failed to fetch favorites: ${t.message}")
            }
        })
    }
}