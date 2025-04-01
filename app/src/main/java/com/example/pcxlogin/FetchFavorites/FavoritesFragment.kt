package com.example.pcxlogin.FetchFavorites

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pcxlogin.FavoritesAdapter
import com.example.pcxlogin.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.bumptech.glide.Glide
import android.widget.Button
import android.widget.ImageView
import com.example.pcxlogin.ApiResponse
import com.example.pcxlogin.BuyNowClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var tvNoFavorites: TextView
    private lateinit var tvMyFav: TextView
    private val favoriteItems = mutableListOf<FavoriteItemResponse>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewFavorites)
        tvNoFavorites = view.findViewById(R.id.tvNoFavorites)
        tvMyFav = view.findViewById(R.id.tvMyFav)

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
                    response.body()?.let {
                        favoriteItems.addAll(it)
                    }
                    favoritesAdapter.notifyDataSetChanged()
                    updateNoFavoritesMessage()
                } else {
                    Log.e("FavoritesFragment", "API error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<FavoriteItemResponse>>, t: Throwable) {
                Log.e("FavoritesFragment", "API call failed: ${t.message}")
            }
        })
    }

    private fun showOutOfStockDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Out of Stock")
            .setMessage("This item is currently out of stock and cannot be ordered.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showStockExceededDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Stock Limit Reached")
            .setMessage("You cannot order more than the available stock.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
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
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmPurchase)

        Glide.with(requireContext()).load(item.imageUrl).into(productImage)
        productName.text = item.productName
        productPrice.text = "₱${item.price}"
        productStock.text = "Stocks: ${item.stocks}"
        quantityText.text = "1"

        btnMinus.setOnClickListener {
            val currentQuantity = quantityText.text.toString().toInt()
            if (currentQuantity > 1) {
                quantityText.text = (currentQuantity - 1).toString()
            }
        }


        btnPlus.setOnClickListener {
            val currentQuantity = quantityText.text.toString().toInt()
            if (currentQuantity < item.stocks) {
                quantityText.text = (currentQuantity + 1).toString()
            } else {
                showStockExceededDialog()
            }
        }

        btnConfirm.setOnClickListener {
            val selectedQuantity = quantityText.text.toString().toInt()
            if (item.stocks == 0) {
                showOutOfStockDialog()
            } else if (selectedQuantity > item.stocks) {
                showStockExceededDialog()
            } else {
                showConfirmationDialog(item, selectedQuantity)
            }
        }

        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showConfirmationDialog(item: FavoriteItemResponse, quantity: Int) {
        val totalPrice = item.price * quantity
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Order")
            .setMessage("Are you sure you want to place this order for $quantity item(s) totaling ₱${String.format("%,.2f", totalPrice)}?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                placeOrder(item, quantity)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun placeOrder(item: FavoriteItemResponse, quantity: Int) {
        val api = BuyNowClient.instance
        val call = api.createOrder(
            customerName = "John Doe",
            items = item.productName,
            total = item.price * quantity,
            quantity = quantity,
            productId = item.productId,
            imageUrl = item.imageUrl
        )

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.success) {
                    Toast.makeText(requireContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show()
                    fetchFavoritesFromApi()
                } else {
                    Toast.makeText(requireContext(), apiResponse?.message ?: "Failed to place order.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateNoFavoritesMessage() {
        if (favoriteItems.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvNoFavorites.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            tvNoFavorites.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        fetchFavoritesFromApi()
    }
}
