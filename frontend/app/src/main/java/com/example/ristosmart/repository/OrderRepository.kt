package com.example.ristosmart.repository

import com.example.ristosmart.model.OrderItemRequest
import com.example.ristosmart.model.OrderRequest
import com.example.ristosmart.model.OrderResponse
import com.example.ristosmart.network.RetrofitClient

class OrderRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun createOrder(
        items: List<OrderItemRequest>,
        tableNumber: Int,
        specialInstructions: String?
    ): Result<OrderResponse> {
        val token = TokenRepository.accessToken.value
        if (token.isNullOrBlank()) {
            return Result.failure(Exception("No access token found"))
        }

        val request = OrderRequest(
            items = items,
            tableNumber = tableNumber,
            specialInstructions = specialInstructions
        )

        return try {
            // Bearer token
            val authHeader = "Bearer $token"
            val response = apiService.createOrder(authHeader, request)

            if (response.isSuccessful && response.body() != null) {
                val orderResponse = response.body()!!
                if (orderResponse.success) {
                    Result.success(orderResponse)
                } else {
                    Result.failure(Exception(orderResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Order creation failed: ${response.code()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
