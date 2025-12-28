package com.example.ristosmart.repository

import com.example.ristosmart.model.MenuItem
import com.example.ristosmart.network.RetrofitClient

class MenuRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getMenu(): Result<List<MenuItem>> {
        return try {
            val response = apiService.getMenu()
            if (response.isSuccessful && response.body() != null) {
                val menuResponse = response.body()!!
                if (menuResponse.success) {
                    Result.success(menuResponse.data)
                } else {
                    Result.failure(Exception("Failed to fetch menu"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
