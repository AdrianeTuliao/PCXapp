package com.example.pcxlogin

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DialogBuyNow(
    context: Context,
    private val productId: Int,
    private val productName: String,
    private val productPrice: String,
    private val productImageUrl: String,
    private val stock: Int,
    private val customerName: String,
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
            showConfirmationDialog()
        }

        btnClose?.setOnClickListener {
            dismiss()
        }

        updateButtonsState(btnDecrease)
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(context)
            .setTitle("Confirm Order")
            .setMessage("Are you sure you want to place this order for $quantity item(s) totaling ₱${String.format("%,.2f", basePrice * quantity)}?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                placeOrder()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun placeOrder() {
        val api = BuyNowClient.instance

        val call = api.createOrder(
            customerName = customerName,
            items = productName,
            total = basePrice * quantity,
            quantity = quantity,
            productId = productId,
            imageUrl = productImageUrl
        )

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.success) {
                    Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                    onBuyNowClicked(quantity, basePrice * quantity)
                    dismiss()
                } else {
                    Toast.makeText(context, apiResponse?.message ?: "Failed to place order.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
