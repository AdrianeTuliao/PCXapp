package com.example.pcxlogin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment() {

    private lateinit var cartAdapter: CartAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var selectAllCheckbox: CheckBox
    private lateinit var totalPriceTextView: TextView
    private lateinit var checkoutButton: Button

    private var cartItems: MutableList<CartItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerCart)
        selectAllCheckbox = view.findViewById(R.id.checkboxSelectAll)
        totalPriceTextView = view.findViewById(R.id.txtTotalPrice)
        checkoutButton = view.findViewById(R.id.btnCheckout)

        setupRecyclerView()

        selectAllCheckbox.setOnCheckedChangeListener { _, isChecked ->
            cartAdapter.selectAllItems(isChecked)
            updateTotalPrice()
        }

        checkoutButton.setOnClickListener {
            val selectedItems = cartAdapter.getSelectedItems()
            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(), "No items selected for checkout", Toast.LENGTH_SHORT).show()
            } else {
                proceedToCheckout(selectedItems)
            }
        }

        loadCartItems()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            context = requireContext(),
            cartItems = cartItems,
            onQuantityChange = { updatedItem ->
                // ✅ Use cartApi instance
                CartClient.instance.updateCartQuantity(
                    userId = 1,
                    productId = updatedItem.productId,
                    quantity = updatedItem.quantity
                ).enqueue(object : Callback<ApiRes1> {
                    override fun onResponse(call: Call<ApiRes1>, response: Response<ApiRes1>) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Quantity updated", Toast.LENGTH_SHORT).show()
                            updateTotalPrice()
                        }
                    }

                    override fun onFailure(call: Call<ApiRes1>, t: Throwable) {
                        Toast.makeText(requireContext(), "Failed to update quantity", Toast.LENGTH_SHORT).show()
                    }
                })
            },
            onItemSelectionChange = {
                updateTotalPrice()
                syncSelectAllCheckbox()
            },
            cartApi = CartClient.instance  // ✅ Pass cartApi instance here
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = cartAdapter
    }


    private fun loadCartItems() {
        val userId = 1

        CartClient.instance.fetchCartItems(userId).enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.isSuccessful) {
                    Log.d("CartFragment", "API Response: ${response.body()}") // Debug the response
                    response.body()?.let {
                        cartItems.clear()
                        cartItems.addAll(it.cart_items)  // Ensure items are added
                        cartAdapter.notifyDataSetChanged() // Refresh RecyclerView
                    }
                } else {
                    Log.e("CartFragment", "API Error: ${response.errorBody()?.string()}")
                }
            }



            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error loading cart: ${t.message}", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }

        })
    }


    private fun updateTotalPrice() {
        val selectedItems = cartAdapter.getSelectedItems()
        val totalPrice = selectedItems.sumOf { it.productPrice * it.quantity }

        totalPriceTextView.text = "₱${String.format("%,.2f", totalPrice)}"
    }

    private fun syncSelectAllCheckbox() {
        if (cartItems.isNullOrEmpty()) return // ✅ Prevent null or empty list crash

        val allSelected = cartItems.all { it.isSelected }

        if (selectAllCheckbox.isChecked != allSelected) {
            selectAllCheckbox.setOnCheckedChangeListener(null)
            selectAllCheckbox.isChecked = allSelected
            selectAllCheckbox.setOnCheckedChangeListener { _, isChecked ->
                cartAdapter.selectAllItems(isChecked)
                updateTotalPrice()
            }
        }
    }



    private fun proceedToCheckout(selectedItems: List<CartItem>) {
        // TODO: Replace with your own logic, navigate to checkout or confirm purchase
        Toast.makeText(requireContext(), "Proceeding to checkout with ${selectedItems.size} items", Toast.LENGTH_SHORT).show()

        // Example: Loop through and process the selected items
        selectedItems.forEach {
            // You can pass these items to your checkout screen or API
            println("Item: ${it.productName}, Quantity: ${it.quantity}")
        }
    }
}
