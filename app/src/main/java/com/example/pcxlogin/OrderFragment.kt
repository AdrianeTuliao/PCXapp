package com.example.pcxlogin

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderFragment : Fragment() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var api: OrderApi
    private val orderList = mutableListOf<Order>()
    private val filteredList = mutableListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order, container, false)

        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerViewMemory)
        tabLayout = view.findViewById(R.id.orderStatusTabLayout)

        ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Pass the cancel callback lambda when initializing the adapter
        orderAdapter = OrderAdapter(filteredList) { order ->
            showCancelConfirmation(order)
        }
        ordersRecyclerView.adapter = orderAdapter

        api = OrderClient.instance

        setupTabs()
        fetchOrders()

        return view
    }

    private fun setupTabs() {
        val tabs =
            listOf("Pending Orders", "Preparing", "Ready for Pick Up", "Completed", "Cancelled")

        tabs.forEach { status ->
            tabLayout.addTab(tabLayout.newTab().setText(status))
        }

        // Listener for tab selection
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val selectedStatus = tabs[it.position]
                    filterOrdersByStatus(selectedStatus)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun fetchOrders() {
        val api = OrderClient.instance

        api.getOrders().enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        orderList.clear()
                        orderList.addAll(it)
                        filterOrdersByStatus("Pending Orders") // Default tab
                    }
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun filterOrdersByStatus(status: String) {
        filteredList.clear()

        val apiStatus = when (status) {
            "Pending Orders" -> "Pending Orders"
            "Preparing" -> "Preparing Stage"
            "Ready for Pick Up" -> "Ready for Pick Up"
            "Completed" -> "Completed"
            "Cancelled" -> "Cancelled"
            else -> ""
        }

        val filtered = orderList.filter { it.order_status == apiStatus }
        filteredList.addAll(filtered)
        orderAdapter.notifyDataSetChanged()
    }

    private fun showCancelConfirmation(order: Order) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Order?")
            .setMessage("Are you sure you want to cancel this order?")
            .setPositiveButton("Yes") { _, _ ->
                cancelOrder(order)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelOrder(order: Order) {
        api.cancelOrder(order.id, order.quantity, order.items)
            .enqueue(object : Callback<ApiRes> {
                override fun onResponse(call: Call<ApiRes>, response: Response<ApiRes>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Order cancelled successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        fetchOrders() // Refresh orders after cancel
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to cancel order",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiRes>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(requireContext(), "Error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }

            })
    }
}

