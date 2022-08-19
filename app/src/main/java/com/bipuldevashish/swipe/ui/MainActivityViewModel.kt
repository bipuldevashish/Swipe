package com.bipuldevashish.swipe.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bipuldevashish.swipe.models.AddProductResponse
import com.bipuldevashish.swipe.models.ProductListResponse
import com.bipuldevashish.swipe.repository.Repository
import com.bipuldevashish.swipe.util.Resource
import kotlinx.coroutines.launch
import okhttp3.MultipartBody


class MainActivityViewModel(private val repository: Repository) : ViewModel() {

    val productList: MutableLiveData<Resource<ProductListResponse>> = MutableLiveData()
    val addProductResponse: MutableLiveData<Resource<AddProductResponse>> = MutableLiveData()

    fun getProductList() = viewModelScope.launch {
        productList.postValue(Resource.Loading())
        val response = repository.getProductList()
        productList.postValue(handleProductListResp(response))
    }

    private fun handleProductListResp(response: ProductListResponse): Resource<ProductListResponse>? {
        if (response.size != 0) {
            return Resource.Success(response)
        }
        return Resource.Error("Something went Wrong...")
    }


    fun addProduct(
         requestBody: MultipartBody
    ) = viewModelScope.launch {
        addProductResponse.postValue(Resource.Loading())
        val response = repository.addProduct(
            requestBody
        )
        Log.d("TAG", "addProduct: $response")
        addProductResponse.postValue(handleAddResponse(response))
    }

    private fun handleAddResponse(response: AddProductResponse): Resource<AddProductResponse> {
        if (response.success){
            return Resource.Success(response)
        }
        return Resource.Error("Something went Wrong...")
    }


}