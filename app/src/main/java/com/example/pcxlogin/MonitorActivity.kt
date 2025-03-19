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

class MonitorActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private var productList = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.shop

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.favorites -> {
                    val intent = Intent(this, FavoritesFragment::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.orders -> {
                    val intent = Intent(this, OrderFragment::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.account -> {
                    val intent = Intent(this, AccountFragment::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }


        recyclerView = findViewById(R.id.recyclerViewMonitor)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(productList)
        recyclerView.adapter = adapter

        fetchProducts()
    }


    private fun fetchProducts() {
        val category = "Monitor"
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
                        Toast.makeText(this@MonitorActivity, "No products found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<Product>>, t: Throwable) {
                    Toast.makeText(this@MonitorActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
