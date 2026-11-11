package com.example.kulinernav.data

/** Model data satu tempat kuliner. */
data class Place(
    val id: Int,
    val name: String,
    val category: String,
    val location: String,
    val rating: Double,
    val description: String
)
