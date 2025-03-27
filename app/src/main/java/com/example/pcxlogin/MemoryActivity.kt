package com.example.pcxlogin

import adapter.ProductAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pcxlogin.FetchFavorites.FavoritesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MemoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private var productList = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {

            finish()
        }

        // Setup Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerViewMemory)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(productList)
        recyclerView.adapter = adapter

        fetchProducts()
    }


    private fun fetchProducts() {
        val category = "Memory"
        RetrofitClient.instance.getProductsByCategory(category)
            .enqueue(object : retrofit2.Callback<List<Product>> {
                override fun onResponse(
                    call: retrofit2.Call<List<Product>>,
                    response: retrofit2.Response<List<Product>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val products = response.body()!!
                        productList.clear()
                        productList.addAll(products)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@MemoryActivity, "No products found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<Product>>, t: Throwable) {
                    Toast.makeText(this@MemoryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
