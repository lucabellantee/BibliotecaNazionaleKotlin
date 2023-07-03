package com.example.biblioteca_nazionale.interface_

import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.model.BooksResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int
    ): BooksResponse

    @GET("volumes/{id}")
    suspend fun getBookById(@Path("id") bookId: String): Book
}
