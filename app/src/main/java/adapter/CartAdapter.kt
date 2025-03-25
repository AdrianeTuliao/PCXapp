package com.example.pcxlogin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onQuantityChange: (CartItem) -> Unit,
    private val onItemSelectionChange: (CartItem) -> Unit // Add this if you want to handle selections in fragment
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkboxItem: CheckBox = itemView.findViewById(R.id.checkboxItem)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productStock: TextView = itemView.findViewById(R.id.productStock)
        val btnIncrease: Button = itemView.findViewById(R.id.btnPlus)
        val btnDecrease: Button = itemView.findViewById(R.id.btnMinus)
        val quantityText: TextView = itemView.findViewById(R.id.Quantity) // Displays current quantity
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
        holder.productStock.text = "Stocks: ${cartItem.productStock}" // Optional: show stock info

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

        // Handle item selection checkbox
        holder.checkboxItem.setOnCheckedChangeListener(null) // Prevent recursion
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
}
