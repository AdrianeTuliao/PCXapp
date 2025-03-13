package com.example.pcxlogin

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog

class DialogBuyNow(
    context: Context,
    private val productName: String,
    private val productPrice: String,
    private val productImageUrl: String,
    private val stock: Int,   // Pass stock as Int
    private val onBuyNowClicked: (quantity: Int, totalPrice: Double) -> Unit
) : BottomSheetDialog(context) {

    private var quantity = 1
    private var basePrice = productPrice.replace("₱", "").replace(",", "").toDouble()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_button)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        val txtProductName = findViewById<TextView>(R.id.productName)
        val txtProductPrice = findViewById<TextView>(R.id.productPrice)
        val imgProduct = findViewById<ImageView>(R.id.productImage)
        val txtStock = findViewById<TextView>(R.id.productStock)
        val txtQuantity = findViewById<TextView>(R.id.Quantity)
        val btnIncrease = findViewById<Button>(R.id.btnPlus)
        val btnDecrease = findViewById<Button>(R.id.btnMinus)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmPurchase)
        val btnClose = findViewById<Button>(R.id.btnCancel)

        txtProductName?.text = productName
        txtProductPrice?.text = formatPrice(basePrice)
        txtQuantity?.text = quantity.toString()
        txtStock?.text = "Stock: $stock"

        Glide.with(context)
            .load(productImageUrl)
            .centerCrop()
            .into(imgProduct!!)

        btnIncrease?.setOnClickListener {
            if (quantity < stock) {
                quantity++
                updatePrice(txtProductPrice!!)
                txtQuantity?.text = quantity.toString()
                animateQuantityChange(txtQuantity!!)
                updateButtonsState(btnDecrease)
            }
        }

        btnDecrease?.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updatePrice(txtProductPrice!!)
                txtQuantity?.text = quantity.toString()
                animateQuantityChange(txtQuantity!!)
                updateButtonsState(btnDecrease)
            }
        }

        btnConfirm?.setOnClickListener {
            onBuyNowClicked(quantity, basePrice * quantity)
            dismiss()
        }

        btnClose?.setOnClickListener {
            dismiss()
        }

        updateButtonsState(btnDecrease)
    }

    private fun updatePrice(txtProductPrice: TextView) {
        val totalPrice = basePrice * quantity
        txtProductPrice.text = formatPrice(totalPrice)
    }

    private fun formatPrice(price: Double): String {
        return "₱%,.2f".format(price)
    }

    private fun updateButtonsState(btnDecrease: Button?) {
        btnDecrease?.isEnabled = quantity > 1
    }

    private fun animateQuantityChange(view: TextView) {
        view.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(100)
            .withEndAction {
                view.animate().scaleX(1f).scaleY(1f).duration = 100
            }
    }
}
