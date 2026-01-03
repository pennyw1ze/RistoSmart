package com.example.ristosmart.network

import com.example.ristosmart.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // region Authentication
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<LogoutResponse>

    @POST("api/auth/register")
    suspend fun registerUser(
        @Header("Authorization") token: String,
        @Body request: RegisterRequest
    ): Response<UserResponse>

    @GET("api/auth/roles")
    suspend fun getRoles(@Header("Authorization") token: String): Response<RolesResponse>
    // endregion

    // region Menu
    @GET("api/menu/")
    suspend fun getMenu(
        @Query("category") category: String? = null,
        @Query("available") available: Boolean? = null
    ): Response<MenuResponse>

    @GET("api/menu/available")
    suspend fun getAvailableMenu(): Response<MenuResponse>

    @GET("api/menu/{menu_id}")
    suspend fun getMenuItemById(@Path("menu_id") menuId: String): Response<MenuItemResponse>

    @POST("api/menu/")
    suspend fun createMenuItem(
        @Header("Authorization") token: String,
        @Body request: CreateMenuItemRequest
    ): Response<MenuItemResponse>

    @PUT("api/menu/{menu_id}")
    suspend fun updateMenuItem(
        @Header("Authorization") token: String,
        @Path("menu_id") menuId: String,
        @Body request: UpdateMenuItemRequest
    ): Response<MenuItemResponse>

     @PUT("api/menu/{menu_id}")
    suspend fun updateMenuAvailability(
        @Header("Authorization") token: String,
        @Path("menu_id") menuId: String,
        @Body request: UpdateAvailabilityRequest
    ): Response<MenuItemResponse>

    @DELETE("api/menu/{menu_id}")
    suspend fun deleteMenuItem(
        @Header("Authorization") token: String,
        @Path("menu_id") menuId: String
    ): Response<GenericSuccessResponse>
    // endregion

    // region Users
    @GET("api/users/")
    suspend fun getUsers(@Header("Authorization") token: String): Response<UsersResponse>

    @GET("api/users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UserResponse>

    @GET("api/users/{user_id}")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): Response<UserResponse>

    @PUT("api/users/{user_id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String,
        @Body user: User
    ): Response<UserResponse>

    @DELETE("api/users/{user_id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): Response<GenericSuccessResponse>
    // endregion

    // region Orders
    @GET("api/orders/")
    suspend fun getOrders(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null,
        @Query("table_number") tableNumber: Int? = null,
        @Query("order_type") orderType: String? = null
    ): Response<OrdersListResponse>

    @POST("api/orders/")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: OrderRequest
    ): Response<OrderResponse>

    @GET("api/orders/{order_id}")
    suspend fun getOrderById(
        @Header("Authorization") token: String,
        @Path("order_id") orderId: String
    ): Response<OrderResponse>

    @DELETE("api/orders/{order_id}")
    suspend fun deleteOrder(
        @Header("Authorization") token: String,
        @Path("order_id") orderId: String
    ): Response<GenericSuccessResponse>

    @PUT("api/orders/{order_id}/status")
    suspend fun updateOrderStatus(
        @Header("Authorization") token: String,
        @Path("order_id") orderId: String,
        @Body request: UpdateOrderStatusRequest
    ): Response<OrderResponse>

     @PUT("api/orders/{order_id}/items/{item_id}/status")
    suspend fun updateOrderItemStatus(
        @Header("Authorization") token: String,
        @Path("order_id") orderId: String,
        @Path("item_id") itemId: String,
        @Body request: UpdateOrderStatusRequest
    ): Response<OrderResponse>
    // endregion

    // region Inventory
    @GET("api/inventory/")
    suspend fun getInventory(@Header("Authorization") token: String): Response<InventoryResponse>
    // endregion
}
