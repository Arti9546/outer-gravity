package com.hastashilpa.model

data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val artisanId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
