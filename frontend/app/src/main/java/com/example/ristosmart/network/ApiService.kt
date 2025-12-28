package com.example.ristosmart.network

import com.example.ristosmart.model.LoginRequest
import com.example.ristosmart.model.LoginResponse
import com.example.ristosmart.model.MenuResponse
import com.example.ristosmart.model.OrderRequest
import com.example.ristosmart.model.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/menu/")
    suspend fun getMenu(): Response<MenuResponse>

    @POST("api/orders/")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: OrderRequest
    ): Response<OrderResponse>
}
