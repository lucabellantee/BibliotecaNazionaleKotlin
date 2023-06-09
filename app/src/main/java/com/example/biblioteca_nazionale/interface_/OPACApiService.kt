package com.example.biblioteca_nazionale.interface_

import com.example.biblioteca_nazionale.model.OPACResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OPACApiService {
    @GET("search.json")
    suspend fun searchIdentificativoLibro(@Query("any") query: String): OPACResponse
}