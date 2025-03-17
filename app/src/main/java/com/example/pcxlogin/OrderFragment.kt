package com.example.pcxlogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pcxlogin.databinding.FragmentOrderBinding
import retrofit2.Call

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderAdapter: OrderAdapter
    private val orderList = mutableListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)

        binding.ordersRecyclerViewMemory.layoutManager = LinearLayoutManager(requireContext())
        orderAdapter = OrderAdapter(orderList)
        binding.ordersRecyclerViewMemory.adapter = orderAdapter

        fetchOrders()

        return binding.root
    }

    private fun fetchOrders() {
        val api = OrderClient.instance

        api.getOrders().enqueue(object : retrofit2.Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: retrofit2.Response<List<Order>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        orderList.clear()
                        orderList.addAll(it)
                        orderAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                // Handle error
                t.printStackTrace()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

