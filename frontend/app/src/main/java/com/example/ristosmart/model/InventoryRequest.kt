package com.example.ristosmart.model

import kotlinx.serialization.Serializable

@Serializable
data class InventoryRequest(
    val token: String,
    val username: String,
    val password: String
)
