package com.example.ristosmart.network

import com.example.ristosmart.model.InventoryPutResponse
import com.example.ristosmart.model.InventoryResponse
import com.example.ristosmart.model.InventoryUpdateRequest
import com.example.ristosmart.model.LoginRequest
import com.example.ristosmart.model.LoginResponse
import com.example.ristosmart.model.MenuResponse
import com.example.ristosmart.model.OrderRequest
import com.example.ristosmart.model.OrderResponse
import com.example.ristosmart.model.OrdersListResponse
import com.example.ristosmart.model.UpdateOrderStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/inventory/")
    suspend fun getInventory(@Header("Authorization") token: String): Response<InventoryResponse>

    @GET("api/menu/")
    suspend fun getMenu(): Response<MenuResponse>

    @POST("api/orders/")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: OrderRequest
    ): Response<OrderResponse>

    @GET("api/orders/")
    suspend fun getOrders(
        @Header("Authorization") token: String
    ): Response<OrdersListResponse>

    @PUT("api/orders/{order_id}/status")
    suspend fun updateOrderStatus(
        @Header("Authorization") token: String,
        @Path("order_id") orderId: String,
        @Body request: UpdateOrderStatusRequest
    ): Response<OrderResponse>

    @PATCH("api/inventory/{product_id}/quantity")
    suspend fun putInventoryItem(
        @Header("Authorization") token: String,
        @Path("product_id") productId: String,
        @Body request: InventoryUpdateRequest
    ): Response<InventoryPutResponse>

    @GET("api/inventory/ean/{ean}")
    suspend fun getInventoryByEan(
        @Header("Authorization") token: String,
        @Path("ean") ean: String
    ): Response<InventoryResponse>


}
