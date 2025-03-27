package com.example.pcxlogin

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class ProductDetailsFragment : Fragment(R.layout.fragment_product_details) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productName = arguments?.getString("product_name") ?: "Unknown"
        val productImage = arguments?.getString("image_url")
        val productPrice = arguments?.getString("price") ?: "0.00"

        val productNameTextView: TextView = view.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = view.findViewById(R.id.productPriceTextView)
        val productImageView: ImageView = view.findViewById(R.id.productImageView)

        productNameTextView.text = productName
        productPriceTextView.text = "₱$productPrice"

        // Load image using Glide
        productImage?.let {
            Glide.with(this)
                .load(it)
                .into(productImageView)
        }
    }
}
