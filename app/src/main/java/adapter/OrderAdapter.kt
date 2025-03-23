package com.example.pcxlogin

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pcxlogin.databinding.ItemOrdersBinding

class OrderAdapter(
    private val orderList: List<Order>,
    private val onCancelClicked: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: ItemOrdersBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        val binding = holder.binding

        binding.orderStatus.text = order.order_status
        binding.productName.text = order.items

        val totalPrice = order.total.toDoubleOrNull() ?: 0.0
        binding.productPrice.text = formatPrice(totalPrice)

        binding.productQuantity.text = "Quantity: ${order.quantity}"

        binding.paymentStatus.text = order.payment_status

        Log.d("CancelOrder", "id=${order.id}, qty=${order.quantity}, productId=${order.items}")

        val baseUrl = "http://192.168.18.127/PCXadmin/"
        Glide.with(holder.itemView.context)
            .load(baseUrl + order.image)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(binding.productImage)

        // Handle Cancel button visibility based on order status
        val status = order.order_status.trim()

        if (status == "Pending Orders" || status == "Preparing Stage") {
            binding.cancelButton.visibility = View.VISIBLE
        } else {
            binding.cancelButton.visibility = View.GONE

        }

        // Handle cancel click
        binding.cancelButton.setOnClickListener {
            onCancelClicked(order)
        }
    }

    private fun formatPrice(price: Double): String {
        return "₱%,.2f".format(price)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}
