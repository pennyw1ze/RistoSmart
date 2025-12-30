package com.example.ristosmart.repository

import com.example.ristosmart.model.InventoryResponse
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

    // --- ADD THIS FUNCTION ---
//    suspend fun getProductByBarcode(barcode: String): Result<InventoryResponse> {
//        val token = TokenRepository.getAccessToken() ?: return Result.failure(Exception("No access token found"))
//
//        return try {
//            // Assuming your API has an endpoint like /inventory?barcode=...
//            // OR /inventory/{barcode}
//            // You will need to add this method to your Retrofit ApiService interface first.
//            val response = apiService.searchInventoryByBarcode("Bearer $token", barcode)
//
//            if (response.isSuccessful) {
//                val body = response.body()
//                if (body != null) {
//                    Result.success(body)
//                } else {
//                    Result.failure(Exception("Product not found"))
//                }
//            } else {
//                Result.failure(Exception("API error: ${response.code()}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

}
