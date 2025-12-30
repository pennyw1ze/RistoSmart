package com.example.ristosmart.repository

import com.example.ristosmart.model.Order
import com.example.ristosmart.model.OrderItemRequest
import com.example.ristosmart.model.OrderRequest
import com.example.ristosmart.model.OrderResponse
import com.example.ristosmart.model.UpdateOrderStatusRequest
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
                    Result.failure(Exception(orderResponse.message ?: "Unknown error"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Order creation failed: ${response.code()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrders(): Result<List<Order>> {
        val token = TokenRepository.accessToken.value
        if (token.isNullOrBlank()) {
            return Result.failure(Exception("No access token found"))
        }

        return try {
            val authHeader = "Bearer $token"
            val response = apiService.getOrders(authHeader)

            if (response.isSuccessful && response.body() != null) {
                val ordersListResponse = response.body()!!
                if (ordersListResponse.success) {
                    Result.success(ordersListResponse.data)
                } else {
                    Result.failure(Exception("Failed to fetch orders"))
                }
            } else {
                 val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Fetch orders failed: ${response.code()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<OrderResponse> {
        val token = TokenRepository.accessToken.value
        if (token.isNullOrBlank()) {
            return Result.failure(Exception("No access token found"))
        }

        return try {
            val authHeader = "Bearer $token"
            val request = UpdateOrderStatusRequest(status = newStatus)
            val response = apiService.updateOrderStatus(authHeader, orderId, request)

            if (response.isSuccessful && response.body() != null) {
                val orderResponse = response.body()!!
                if (orderResponse.success) {
                    Result.success(orderResponse)
                } else {
                    Result.failure(Exception(orderResponse.message ?: "Failed to update status"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Update status failed: ${response.code()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
