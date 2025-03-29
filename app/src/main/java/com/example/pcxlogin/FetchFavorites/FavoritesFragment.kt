package com.example.pcxlogin.FetchFavorites

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pcxlogin.FavoritesAdapter
import com.example.pcxlogin.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.bumptech.glide.Glide
import android.widget.Button
import android.widget.ImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var tvNoFavorites: TextView
    private val favoriteItems = mutableListOf<FavoriteItemResponse>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewFavorites)
        tvNoFavorites = view.findViewById(R.id.tvNoFavorites)

        favoritesAdapter = FavoritesAdapter(favoriteItems) { showProductBottomSheet(it) }
        recyclerView.adapter = favoritesAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchFavoritesFromApi()
    }

    private fun fetchFavoritesFromApi() {
        val fetchFavApi = FavoritesClient1.fetchFavApi
        val userId = 1 // TODO: Replace with dynamic user ID

        fetchFavApi.getFavorites(userId).enqueue(object : Callback<List<FavoriteItemResponse>> {
            override fun onResponse(
                call: Call<List<FavoriteItemResponse>>,
                response: Response<List<FavoriteItemResponse>>
            ) {
                if (response.isSuccessful) {
                    favoriteItems.clear()
                    response.body()?.let { favoriteItems.addAll(it) }
                    favoritesAdapter.notifyDataSetChanged()
                    updateNoFavoritesMessage()
                }
            }

            override fun onFailure(call: Call<List<FavoriteItemResponse>>, t: Throwable) {
                // Handle failure (optional: show an error message)
            }
        })
    }

    private fun showProductBottomSheet(item: FavoriteItemResponse) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_button, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogView)

        val productImage = dialogView.findViewById<ImageView>(R.id.productImage)
        val productName = dialogView.findViewById<TextView>(R.id.productName)
        val productPrice = dialogView.findViewById<TextView>(R.id.productPrice)
        val productStock = dialogView.findViewById<TextView>(R.id.productStock)
        val quantityText = dialogView.findViewById<TextView>(R.id.Quantity)
        val btnMinus = dialogView.findViewById<Button>(R.id.btnMinus)
        val btnPlus = dialogView.findViewById<Button>(R.id.btnPlus)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        Glide.with(requireContext()).load(item.imageUrl).into(productImage)
        productName.text = item.productName
        productPrice.text = "₱${item.price}"
        productStock.text = "Stocks: ${item.stock}"
        quantityText.text = "1"

        btnMinus.setOnClickListener {
            val currentQuantity = quantityText.text.toString().toInt()
            if (currentQuantity > 1) {
                quantityText.text = (currentQuantity - 1).toString()
            }
        }

        btnPlus.setOnClickListener {
            val currentQuantity = quantityText.text.toString().toInt()
            quantityText.text = (currentQuantity + 1).toString()
        }

        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun updateNoFavoritesMessage() {
        tvNoFavorites.visibility = if (favoriteItems.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        fetchFavoritesFromApi()
    }
}
