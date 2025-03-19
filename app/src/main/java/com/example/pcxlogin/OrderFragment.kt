package com.example.pcxlogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderFragment : Fragment() {

    private lateinit var ordersRecyclerView: RecyclerView
    private val orderList = mutableListOf<Order>()
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order, container, false)

        // Initialize RecyclerView
        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerViewMemory)
        ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        orderAdapter = OrderAdapter(orderList)
        ordersRecyclerView.adapter = orderAdapter

        fetchOrders()

        return view
    }

    private fun fetchOrders() {
        val api = OrderClient.instance

        api.getOrders().enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        orderList.clear()
                        orderList.addAll(it)
                        orderAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}
