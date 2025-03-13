package com.example.pcxlogin

data class Product(
    val name: String,
    val description: String,
    val price: Double,
    var stocks: Int,
    val imageUrl: String
)

