package com.example.ristosmart.repository

import com.example.ristosmart.model.LoginRequest
import com.example.ristosmart.model.LoginResponse
import com.example.ristosmart.network.RetrofitClient

class AuthRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                if (loginResponse.success) {
                    Result.success(loginResponse)
                } else {
                    Result.failure(Exception(loginResponse.message ?: "Unknown error"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Login failed: ${response.code()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
