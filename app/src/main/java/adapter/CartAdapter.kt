package com.example.pcxlogin

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<CartItem>,
    private val onQuantityChange: (CartItem) -> Unit,
    private val onItemSelectionChange: (CartItem) -> Unit,
    private val cartApi: CartApi
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkboxItem: CheckBox = itemView.findViewById(R.id.checkboxItem)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productStock: TextView = itemView.findViewById(R.id.productStock)
        val btnIncrease: Button = itemView.findViewById(R.id.btnPlus)
        val btnDecrease: Button = itemView.findViewById(R.id.btnMinus)
        val quantityText: TextView = itemView.findViewById(R.id.Quantity)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        // Set product details
        holder.productName.text = cartItem.productName
        holder.productPrice.text = "₱${String.format("%,.2f", cartItem.productPrice)}"
        holder.quantityText.text = cartItem.quantity.toString()
        holder.productStock.text = "Stocks: ${cartItem.productStock}"

        // Load image using Glide
        Glide.with(holder.productImage.context)
            .load(cartItem.productImageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.productImage)

        // Handle quantity increase
        holder.btnIncrease.setOnClickListener {
            if (cartItem.quantity < cartItem.productStock) {
                cartItem.quantity++
                holder.quantityText.text = cartItem.quantity.toString()
                onQuantityChange(cartItem)
            }
        }

        // Handle quantity decrease
        holder.btnDecrease.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                holder.quantityText.text = cartItem.quantity.toString()
                onQuantityChange(cartItem)
            }
        }

        holder.btnRemove.setOnClickListener {
            if (position >= 0 && position < cartItems.size) {
                removeFromCart(cartItem, position)
            } else {
                Toast.makeText(context, "Invalid item", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle item selection checkbox
        holder.checkboxItem.setOnCheckedChangeListener(null)
        holder.checkboxItem.isChecked = cartItem.isSelected

        holder.checkboxItem.setOnCheckedChangeListener { _, isChecked ->
            cartItem.isSelected = isChecked
            onItemSelectionChange(cartItem)
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    fun updateCartItems(newItems: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newItems)
        notifyDataSetChanged()
    }

    fun selectAllItems(select: Boolean) {
        cartItems.forEach { it.isSelected = select }
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<CartItem> {
        return cartItems.filter { it.isSelected }
    }

    private fun removeFromCart(item: CartItem, position: Int) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0) // Default to 0 if not found

        if (userId == 0) {
            Log.e("CartAdapter", "Error: userId is 0! Make sure it's retrieved properly.")
            Toast.makeText(context, "User ID is missing!", Toast.LENGTH_SHORT).show()
            return
        } else {
            Log.d("CartAdapter", "Retrieved User ID: $userId")
        }

        Log.d("CartAdapter", "Removing item - User ID: $userId, Product ID: ${item.productId}")

        val call = cartApi.removeFromCart(userId, item.productId)
        call.enqueue(object : Callback<ApiRes1> {
            override fun onResponse(call: Call<ApiRes1>, response: Response<ApiRes1>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        cartItems.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to remove item: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
                        Log.e("CartAdapter", "API Response Error: ${apiResponse?.message}")
                    }
                } else {
                    Toast.makeText(context, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("CartAdapter", "Server Error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ApiRes1>, t: Throwable) {
                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("CartAdapter", "Network Error: ${t.message}")
            }
        })
    }
}
