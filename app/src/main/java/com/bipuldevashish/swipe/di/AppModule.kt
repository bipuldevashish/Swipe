package com.bipuldevashish.swipe.di

import com.bipuldevashish.swipe.api.ApiService
import com.bipuldevashish.swipe.repository.Repository
import com.bipuldevashish.swipe.repository.RepositoryImpl
import com.bipuldevashish.swipe.ui.MainActivityViewModel
import com.example.assignment.util.Constants.Companion.BASE_URL
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val appModule = module {

    /**
     * Single instance of retrofit
     * retrofit instance injection
     */
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /**
     * Single instance of repository
     * repository injection
     */
    single<Repository> {
        RepositoryImpl(get())
    }

    /**
     * ViewModel injection
     */

    viewModel {
        MainActivityViewModel(get())
    }

}