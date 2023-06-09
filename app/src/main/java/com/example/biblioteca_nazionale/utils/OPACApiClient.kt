package com.example.biblioteca_nazionale.utils

import com.example.biblioteca_nazionale.interface_.OPACApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL_OPAC: String = "http://opac.sbn.it/opacmobilegw/"

class OPACApiClient {
    private val apiService: OPACApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_OPAC)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(OPACApiService::class.java)
    }

    fun getApiService(): OPACApiService {
        return apiService
    }
}