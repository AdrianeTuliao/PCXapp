package com.example.pcxlogin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.productName)
        val descriptionText: TextView = itemView.findViewById(R.id.productDescription)
        val priceText: TextView = itemView.findViewById(R.id.productPrice)
        val imageView: ImageView = itemView.findViewById(R.id.productImage)
        val stockText: TextView = itemView.findViewById(R.id.productStock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        val priceValue = product.price.toDouble() ?: 0.0
        val formattedPrice = String.format("%,.2f", priceValue)

        holder.nameText.text = product.name
        holder.descriptionText.text = product.description
        holder.priceText.text = "₱$formattedPrice"
        holder.stockText.text = "Stocks: ${product.stocks}"

        Glide.with(holder.imageView.context)
            .load(product.imageUrl)
            .centerCrop()
            .into(holder.imageView)


    }

    override fun getItemCount(): Int = products.size
}
