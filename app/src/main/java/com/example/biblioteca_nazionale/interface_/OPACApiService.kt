package com.example.biblioteca_nazionale.interface_

import com.example.biblioteca_nazionale.model.BooksResponse
import com.example.biblioteca_nazionale.model.OPACResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OPACApiService {
    @GET("search.json")
    //fun searchBooks(@Query("q") query: String): Response<BooksResponse>
    //fun searchBooks(@Query("q") query: String): Call<BooksResponse>
    suspend fun searchIdentificativoLibro(@Query("any") query: String): OPACResponse
}