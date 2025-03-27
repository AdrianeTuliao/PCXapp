package com.example.pcxlogin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pcxlogin.FetchFavorites.FavoritesFragment
import com.example.pcxlogin.databinding.HomePageBinding

class HomePage : AppCompatActivity() {
    private lateinit var binding: HomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())

        // Bottom Nav handling
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.shop -> replaceFragment(ShopFragment())
                R.id.favorites -> replaceFragment(FavoritesFragment())
                R.id.home -> replaceFragment(HomeFragment())
                R.id.orders -> replaceFragment(OrderFragment())
                R.id.account -> replaceFragment(AccountFragment())
            }
            true
        }

        binding.sCart.setOnClickListener {
            replaceFragment(CartFragment())
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}