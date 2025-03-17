package com.example.pcxlogin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.productName)
        val descriptionText: TextView = itemView.findViewById(R.id.productDescription)
        val priceText: TextView = itemView.findViewById(R.id.productPrice)
        val imageView: ImageView = itemView.findViewById(R.id.productImage)
        val buyNowButton: Button = itemView.findViewById(R.id.btnBuyNow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.buyNowButton.isEnabled = product.stocks > 0
        holder.buyNowButton.text = if (product.stocks > 0) "Buy Now" else "Out of Stock"


        // Ensure product.price is safely converted
        val priceValue = product.price.toDouble() ?: 0.0
        val formattedPrice = String.format("%,.2f", priceValue)

        holder.nameText.text = product.name
        holder.descriptionText.text = product.description
        holder.priceText.text = "₱$formattedPrice"

        Glide.with(holder.imageView.context)
            .load(product.imageUrl)
            .centerCrop()
            .into(holder.imageView)

        holder.buyNowButton.setOnClickListener {
            val context = holder.itemView.context

            val dialog = DialogBuyNow(
                context = context,
                productName = product.name,
                productPrice = "₱${String.format("%,.2f", product.price)}",
                productImageUrl = product.imageUrl,
                stock = product.stocks,
                productId = product.id,
                customerName = "John Doe"
            ) { quantity, totalPrice ->
                Toast.makeText(context, "Bought $quantity for ₱$totalPrice", Toast.LENGTH_SHORT).show()

                // Reduce stocks and update item
                product.stocks -= quantity
                notifyItemChanged(holder.adapterPosition)
            }

            dialog.show()
        }

    }

    override fun getItemCount(): Int = products.size
}
