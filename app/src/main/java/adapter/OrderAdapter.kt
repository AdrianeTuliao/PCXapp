package com.example.pcxlogin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pcxlogin.databinding.ItemOrdersBinding

class OrderAdapter(private val orderList: List<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: ItemOrdersBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val Order = orderList[position]
        holder.binding.orderStatus.text = Order.order_status
        holder.binding.productName.text = Order.items
        val totalPrice = Order.total.toDoubleOrNull() ?: 0.0
        holder.binding.productPrice.text = formatPrice(totalPrice)
        holder.binding.paymentStatus.text = Order.payment_status

        val baseUrl = "http://192.168.18.127/PCXadmin/"
        Glide.with(holder.itemView.context)
            .load(baseUrl + Order.image)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.binding.productImage)
    }

    private fun formatPrice(price: Double): String {
        return "₱%,.2f".format(price)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}

