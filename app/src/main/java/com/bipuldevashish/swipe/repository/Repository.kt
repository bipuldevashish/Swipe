package com.bipuldevashish.swipe.repository

import com.bipuldevashish.swipe.models.AddProductResponse
import com.bipuldevashish.swipe.models.ProductListResponse
import okhttp3.MultipartBody


interface Repository {
    suspend fun getProductList(): ProductListResponse

    suspend fun addProduct(
        requestBody: MultipartBody
    ): AddProductResponse
}