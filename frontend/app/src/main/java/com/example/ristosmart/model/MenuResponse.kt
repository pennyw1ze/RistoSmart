package com.example.ristosmart.model

import kotlinx.serialization.Serializable

@Serializable
data class MenuResponse(
    val count: Int,
    val data: List<MenuItem>,
    val success: Boolean
)
