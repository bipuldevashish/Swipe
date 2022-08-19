package com.bipuldevashish.swipe.repository

import com.bipuldevashish.swipe.api.ApiService
import com.bipuldevashish.swipe.models.AddProductResponse
import com.bipuldevashish.swipe.models.ProductListResponse
import okhttp3.MultipartBody

class RepositoryImpl(private val api: ApiService) : Repository {

    override suspend fun getProductList(): ProductListResponse {
        return api.getProductList()
    }

    override suspend fun addProduct(
        requestBody: MultipartBody
    ): AddProductResponse {
        return api.addProduct(
            requestBody
        )
    }


}