package com.hastashilpa.model

data class Blueprint(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val dimensions: String = "",
    val materials: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val imageUrl: String = "",
    val pdfUrl: String = ""
)
