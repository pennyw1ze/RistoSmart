package com.example.ristosmart.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This data class defines a User based on the backend model.
 */
@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
    @SerialName("full_name")
    val fullName: String? = null,
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("last_login")
    val lastLogin: String? = null
)
