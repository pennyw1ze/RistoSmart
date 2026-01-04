package com.example.ristosmart.repository

import com.example.ristosmart.model.InventoryEmptyResponse
import com.example.ristosmart.model.InventoryItem
import com.example.ristosmart.model.InventoryProductRequest
import com.example.ristosmart.model.InventoryResponse
import com.example.ristosmart.model.InventoryUpdateRequest
import com.example.ristosmart.model.LookupRequest
import com.example.ristosmart.network.RetrofitClient
import com.example.ristosmart.network.UpcTrialApi

class InventoryRepository {
    private val apiService = RetrofitClient.apiService
    private val upcTrialApi = RetrofitClient.upcTrialApi


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

    suspend fun postInventory(
        name: String,
        ean: String,
        price: Double,
        quantity: Int,
        category: String? = null,
        description: String? = null,
        imageUrl: String? = null,
    ): Result<InventoryEmptyResponse> {
        val token = TokenRepository.getAccessToken()
            ?: return Result.failure(Exception("No access token found"))

        val request = InventoryProductRequest(
            name = name,
            ean = ean,
            price = price,
            quantity = quantity,
            category = category,
            description = description,
            imageUrl = imageUrl
        )

        return try {
            val response = apiService.postProductToInventory("Bearer $token", request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Failed to post inventory: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun fetchNewItem(barcode: String, quantity: Int): Result<InventoryResponse> {
        val token = TokenRepository.getAccessToken() ?: return Result.failure(Exception("No access token found"))
        val requestBody = LookupRequest(upc = barcode)

        return try {

            val response = upcTrialApi.lookupTrialBarcode(requestBody)

            if (response.isSuccessful) {
                val lookupBody = response.body()    //lookupresponse
                println("LOOKUP BODY: $lookupBody")
                if (lookupBody != null) {

                    val productItem = lookupBody.items.first()


                    val currentTimestamp = java.time.Instant.now().toString()

                    // Try to get price from offers, fallback to recorded price, default to 0.0
                    val derivedPrice = productItem.offers.firstOrNull { it.price > 0 }?.price
                        ?: productItem.lowest_recorded_price
                        ?: 0.0

                    val newItem = InventoryItem(
                        id = "FETCHED-ITEM-\${productItem.ean}\"dajeromadaje-fetchedfromapi$currentTimestamp",
                        name = productItem.title,
                        ean = productItem.ean,
                        quantity = quantity,
                        imageUrl = productItem.images.firstOrNull() ?: "",
                        description = "description",
                        createdAt = currentTimestamp,
                        updatedAt = currentTimestamp,
                        price = derivedPrice,
                        category = productItem.category
                    )

                    println(newItem)

                    val saveResult = postInventory(
                        name = newItem.name,
                        ean = newItem.ean,
                        price = newItem.price,
                        quantity = quantity,
                        category = newItem.category,
                        description = newItem.description,
                        imageUrl = newItem.imageUrl
                    )

                    if (saveResult.isSuccess) {
                        println("PRODUCT ADDED TO THE DB SUCCESSFULLY! :)")

                        // 6. Return the formatted response to update the UI
                        val inventoryResponse = InventoryResponse(
                            data = listOf(newItem),
                            success = true,
                            count = 1
                        )
                        Result.success(inventoryResponse)
                    } else {
                        // If saving failed, we return that failure
                        Result.failure(saveResult.exceptionOrNull() ?: Exception("Failed to save new item to DB"))
                    }
                } else {
                    Result.failure(Exception("Lookup response body is null"))
                }
            } else {
                Result.failure(Exception("Lookup failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
