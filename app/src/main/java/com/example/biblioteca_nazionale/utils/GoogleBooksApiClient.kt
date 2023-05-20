package com.example.biblioteca_nazionale.utils

import com.example.biblioteca_nazionale.interface_.GoogleBooksApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL: String = "https://www.googleapis.com/books/v1/"

class GoogleBooksApiClient {
    private val apiService: GoogleBooksApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(GoogleBooksApiService::class.java)
    }

    fun getApiService(): GoogleBooksApiService {
        return apiService
    }
}
