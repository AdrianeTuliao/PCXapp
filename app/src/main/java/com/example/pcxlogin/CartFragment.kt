package com.example.pcxlogin

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
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

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerCart)
        selectAllCheckbox = view.findViewById(R.id.checkboxSelectAll)
        totalPriceTextView = view.findViewById(R.id.txtTotalPrice)
        checkoutButton = view.findViewById(R.id.btnCheckout)

        // Set up RecyclerView
        setupRecyclerView()

        // Handle select-all checkbox
        selectAllCheckbox.setOnCheckedChangeListener { _, isChecked ->
            cartAdapter.selectAllItems(isChecked)
            updateTotalPrice()
        }

        // Handle checkout button click
        checkoutButton.setOnClickListener {
            val selectedItems = cartAdapter.getSelectedItems()
            if (selectedItems.isEmpty()) {
                showToast("No items selected for checkout")
            } else {
                proceedToCheckout(selectedItems)
            }
        }

        // Load cart items from backend
        loadCartItems()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            context = requireContext(),
            cartItems = cartItems,
            onQuantityChange = { updatedItem ->
                updateCartQuantity(updatedItem)
            },
            onItemSelectionChange = {
                updateTotalPrice()
                syncSelectAllCheckbox()
            },
            cartApi = CartClient.instance
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = cartAdapter
    }

    private fun loadCartItems() {
        val userId = 1 // Replace with dynamic user ID

        CartClient.instance.fetchCartItems(userId).enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        cartItems.clear()
                        cartItems.addAll(it.cart_items)
                        cartAdapter.notifyDataSetChanged()
                    }
                } else {
                    logError("Error loading cart", response)
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                logError("Error loading cart", t)
            }
        })
    }

    private fun updateCartQuantity(updatedItem: CartItem) {
        CartClient.instance.updateCartQuantity(
            userId = 1,
            productId = updatedItem.productId,
            quantity = updatedItem.quantity
        ).enqueue(object : Callback<ApiRes1> {
            override fun onResponse(call: Call<ApiRes1>, response: Response<ApiRes1>) {
                if (response.isSuccessful) {
                    showToast("Quantity updated")
                    updateTotalPrice()
                } else {
                    showToast("Failed to update quantity")
                }
            }

            override fun onFailure(call: Call<ApiRes1>, t: Throwable) {
                showToast("Failed to update quantity")
            }
        })
    }

    private fun updateTotalPrice() {
        val selectedItems = cartAdapter.getSelectedItems()
        val totalPrice = selectedItems.sumOf { it.productPrice * it.quantity }
        totalPriceTextView.text = "₱${String.format("%,.2f", totalPrice)}"
    }

    private fun syncSelectAllCheckbox() {
        if (cartItems.isNotEmpty()) {
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
    }

    private fun proceedToCheckout(selectedItems: List<CartItem>) {
        val totalAmount = selectedItems.sumOf { it.productPrice * it.quantity }
        showCheckoutDialog(selectedItems, totalAmount)
    }

    private fun showCheckoutDialog(selectedItems: List<CartItem>, totalAmount: Double) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_order, null)

        val confirmProductImage: ImageView = dialogView.findViewById(R.id.confirmProductImage)
        val confirmProductName: TextView = dialogView.findViewById(R.id.confirmProductName)
        val confirmQuantity: TextView = dialogView.findViewById(R.id.confirmQuantity)
        val confirmTotalPrice: TextView = dialogView.findViewById(R.id.confirmTotalPrice)
        val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)
        val btnConfirm: Button = dialogView.findViewById(R.id.btnConfirm)

        // Set product info in dialog
        val product = selectedItems[0]
        Glide.with(requireContext()).load(product.productImageUrl).into(confirmProductImage)
        confirmProductName.text = product.productName
        confirmQuantity.text = "Quantity: ${selectedItems.sumOf { it.quantity }}"
        confirmTotalPrice.text = "Total: ₱${String.format("%,.2f", totalAmount)}"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnConfirm.setOnClickListener {
            dialog.dismiss()
            placeOrderInBackend(selectedItems)
        }

        dialog.show()
    }

    private fun placeOrderInBackend(selectedItems: List<CartItem>) {
        val userName = "John Doe"  // Replace with dynamic data if needed
        val totalAmount = selectedItems.sumOf { it.productPrice * it.quantity }
        val totalQuantity = selectedItems.sumOf { it.quantity }
        val productId = selectedItems[0].productId // Handle multiple product IDs
        val imageUrl = selectedItems[0].productImageUrl

        val itemsJson = Gson().toJson(selectedItems)

        CartClient.instance.placeOrder(
            customerName = userName,
            items = itemsJson,
            total = totalAmount,
            quantity = totalQuantity,
            productId = productId,
            imageUrl = imageUrl
        ).enqueue(object : Callback<ApiRes1> {
            override fun onResponse(call: Call<ApiRes1>, response: Response<ApiRes1>) {
                if (response.isSuccessful) {
                    handleOrderResponse(response.body(), selectedItems)
                } else {
                    showToast("Failed to place order. Please try again.")
                    logError("Failed API call", response)
                }
            }

            override fun onFailure(call: Call<ApiRes1>, t: Throwable) {
                showToast("Error placing order: ${t.message}")
                logError("Error placing order", t)
            }
        })
    }

    private fun handleOrderResponse(responseBody: ApiRes1?, selectedItems: List<CartItem>) {
        if (responseBody?.success == true) {
            showToast("Order placed successfully!")
            cartItems.removeAll(selectedItems)
            cartAdapter.notifyDataSetChanged()
        } else {
            showToast("Failed to place order: ${responseBody?.message ?: "Unknown error"}")
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun logError(message: String, error: Any) {
        Log.e("CartFragment", "$message: $error")
    }
}
