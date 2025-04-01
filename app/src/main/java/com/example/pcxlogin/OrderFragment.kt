package com.example.pcxlogin

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
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

    // Declare variables
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

        // Initialize views
        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerViewMemory)
        tabLayout = view.findViewById(R.id.orderStatusTabLayout)

        ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        orderAdapter = OrderAdapter(filteredList) { order ->
            showCancelConfirmation(order)
        }
        ordersRecyclerView.adapter = orderAdapter

        api = OrderClient.instance

        setupTabs()
        fetchOrders()

        return view
    }

    // Helper function to set up tabs
    private fun setupTabs() {
        val tabs =
            listOf("Pending Orders", "Preparing", "Ready for Pick Up", "Completed", "Cancelled")

        tabs.forEach { status ->
            tabLayout.addTab(tabLayout.newTab().setText(status))
        }

        // Set default tab
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

    // Fetch orders from the API
    private fun fetchOrders() {
        val api = OrderClient.instance

        api.getOrders().enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    val orders = response.body()
                    Log.d("API_RESPONSE", "Orders: $orders")

                    if (orders != null && orders.isNotEmpty()) {
                        orderList.clear()
                        orderList.addAll(orders)
                        filterOrdersByStatus("Pending Orders")
                    } else {
                        Log.d("API_RESPONSE", "No orders returned from the server.")
                    }
                } else {
                    Log.e("API_ERROR", "Response Code: ${response.code()}")
                    Log.e("API_ERROR", "Error Body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Log.e("API_ERROR", "Request failed: ${t.localizedMessage}")
                t.printStackTrace()
                Toast.makeText(requireContext(), "Error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })

    }

    // Filter orders based on the selected tab
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

    // Show a confirmation dialog before canceling an order
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

    // Cancel an order
    private fun cancelOrder(order: Order) {
        api.cancelOrder(order.id, order.quantity, order.items)
            .enqueue(object : Callback<ApiRes> {
                override fun onResponse(call: Call<ApiRes>, response: Response<ApiRes>) {

                    Log.d("CANCEL_ORDER", "Response Code: ${response.code()}")
                    Log.d("CANCEL_ORDER", "Response Body: ${response.body()}")
                    Log.d("CANCEL_ORDER", "Error Body: ${response.errorBody()?.string()}")

                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Order cancelled successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        fetchOrders()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to cancel order",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                // Handle failure
                override fun onFailure(call: Call<ApiRes>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(requireContext(), "Error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }

            })
    }
}

