package com.example.biblioteca_nazionale.interface_

import com.example.biblioteca_nazionale.model.BooksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("volumes")
    //fun searchBooks(@Query("q") query: String): Response<BooksResponse>
    //fun searchBooks(@Query("q") query: String): Call<BooksResponse>
    suspend fun searchBooks(@Query("q") query: String): BooksResponse
}