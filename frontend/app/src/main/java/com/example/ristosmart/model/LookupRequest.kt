package com.example.ristosmart.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
// The body sent to the API

@Serializable
data class LookupRequest(
    val upc: String
)


// The data structure returned by the API

@Serializable
data class LookupResponse(
    val code: String,
    val total: Int,
    val offset: Int,
    val items: List<ProductItem>
)

@Serializable
data class ProductItem(
    val ean: String,
    val title: String,
    val description: String,
    val brand: String,    val model: String,
    val color: String,
    val size: String,
    val dimension: String,
    val weight: String,
    val category: String,
    @SerialName("lowest_recorded_price")
    val lowest_recorded_price: Double,
    @SerialName("highest_recorded_price")
    val highestRecordedPrice: Double,
    val images: List<String>,
    val offers: List<Offer>,
    val asin: String? = null,
    val elid: String? = null
)

@Serializable
data class Offer(
    val merchant: String,
    val domain: String,
    val title: String,
    val currency: String,
    @SerialName("list_price")
    val listPrice: String, // Kept as String because it's empty "" in some JSON entries
    val price: Double,
    val shipping: String,
    val condition: String,
    val availability: String,
    val link: String,
    @SerialName("updated_t")
    val updatedTimestamp: Long
)