package com.example.pcxlogin

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    var stocks: Int,
    val imageUrl: String,
    val favorite: Boolean
)

