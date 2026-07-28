package com.example.data.model

data class PricingPackage(
    val id: String,
    val title: String,
    val subtitle: String,
    val priceRange: String,
    val guestCapacity: String,
    val inclusions: List<String>,
    val isPopular: Boolean = false,
    val badgeText: String = ""
)

data class GalleryItem(
    val id: String,
    val title: String,
    val category: String, // Hall, Stage, Dining Area, Parking, Decoration
    val drawableResId: Int,
    val description: String
)

data class Facility(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String
)
