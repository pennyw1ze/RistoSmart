package com.example.ristosmart.network

import com.example.ristosmart.model.InventoryResponse
import com.example.ristosmart.model.LoginRequest
import com.example.ristosmart.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/inventory/")
    suspend fun getInventory(@Header("Authorization") token: String): Response<InventoryResponse>
}
