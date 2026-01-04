package com.example.ristosmart.repository

import com.example.ristosmart.model.InventoryItem
import com.example.ristosmart.model.InventoryResponse
import com.example.ristosmart.model.InventoryUpdateRequest
import com.example.ristosmart.network.RetrofitClient

class InventoryRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getInventory(): Result<InventoryResponse> {
        val token = TokenRepository.getAccessToken() ?: return Result.failure(Exception("No access token found"))

        return try {
            val response = apiService.getInventory("Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API call failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun putInventoryItem(productId: String, quantity: Int): Result<InventoryResponse> {
        val token = TokenRepository.getAccessToken() ?: return Result.failure(Exception("No access token found"))
        val requestBody = InventoryUpdateRequest(amount = quantity, operation="add")

        return try {
            val response = apiService.putInventoryItem("Bearer $token", productId, requestBody)
            if (response.isSuccessful) {
                return getInventory()
            } else {
                Result.failure(Exception("API call failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteInventoryItem(productId: String, quantity: Int): Result<InventoryResponse> {
        val token = TokenRepository.getAccessToken() ?: return Result.failure(Exception("No access token found"))
        val requestBody = InventoryUpdateRequest(amount = quantity, operation = "remove")

        return try {
            val response = apiService.putInventoryItem("Bearer $token", productId, requestBody)
            if (response.isSuccessful) {
                return getInventory()
            } else {
                Result.failure(Exception("API call failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun putInventoryItemByBarcode(barcode: String, quantity: Int): Result<InventoryResponse> {
        val token = TokenRepository.getAccessToken() ?: return Result.failure(Exception("No access token found"))
        val updateRequestBody = InventoryUpdateRequest(amount = quantity, operation = "add")

        return try {
            val searchResponse = apiService.getInventoryByEan("Bearer $token", barcode)

            if (searchResponse.isSuccessful) {
                val searchBody = searchResponse.body()
                val product = searchBody?.data?.firstOrNull()
                val productId = product?.id

                if (productId != null) {
                    val updateResponse = apiService.putInventoryItem("Bearer $token", productId, updateRequestBody)

                    if (updateResponse.isSuccessful) {
                        return getInventory()
                    } else {
                        Result.failure(Exception("Update failed with code: ${updateResponse.code()}"))
                    }
                } else {
                    Result.failure(Exception("Product not found for barcode: $barcode"))
                }
            } else {
                Result.failure(Exception("Barcode lookup failed with code: ${searchResponse.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
