package com.bipuldevashish.swipe.api

import com.bipuldevashish.swipe.models.AddProductResponse
import com.bipuldevashish.swipe.models.ProductListResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    /**
     * For fetching the list of products
     */
    @GET("api/public/get")
    suspend fun getProductList(): ProductListResponse

    /**
     * For Adding a new product to the Database.
     */


    @POST("api/public/add")
    suspend fun addProduct(
        @Body requestBody: RequestBody
    ) : AddProductResponse
}