package com.example.ristosmart.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// Generic Success Response for simple messages
@Serializable
data class GenericSuccessResponse(
    val success: Boolean,
    val message: String
)

// region Auth
@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerialName("full_name")
    val fullName: String,
    val role: String
)

@Serializable
data class RolesResponse(
    val success: Boolean,
    val roles: JsonObject // Keeping it as JsonObject for flexibility
)

@Serializable
data class LogoutResponse(
    val success: Boolean,
    val message: String
)
// endregion

// region Menu
@Serializable
data class CreateMenuItemRequest(
    val name: String,
    val price: Double,
    val category: String,
    @SerialName("preparation_time")
    val preparationTime: Int,
    val description: String? = null,
    val allergens: List<String>? = null,
    @SerialName("is_available")
    val isAvailable: Boolean = true,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("nutritional_info")
    val nutritionalInfo: JsonObject? = null,
    @SerialName("tax_amount")
    val taxAmount: Double? = null
)

@Serializable
data class UpdateMenuItemRequest(
    val name: String? = null,
    val price: Double? = null,
    val category: String? = null,
    @SerialName("preparation_time")
    val preparationTime: Int? = null,
    val description: String? = null,
    val allergens: List<String>? = null,
    @SerialName("is_available")
    val isAvailable: Boolean? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("nutritional_info")
    val nutritionalInfo: JsonObject? = null,
    @SerialName("tax_amount")
    val taxAmount: Double? = null
)

@Serializable
data class UpdateAvailabilityRequest(
    @SerialName("is_available")
    val isAvailable: Boolean
)

@Serializable
data class MenuItemResponse(
    val success: Boolean,
    val data: MenuItem,
    val message: String? = null
)
// endregion


// region Users
@Serializable
data class UsersResponse(
    val success: Boolean,
    val count: Int? = null,
    val data: List<User>
)

@Serializable
data class UserResponse(
    val success: Boolean,
    val data: User? = null,
    val count: Int? = null
)
// endregion
