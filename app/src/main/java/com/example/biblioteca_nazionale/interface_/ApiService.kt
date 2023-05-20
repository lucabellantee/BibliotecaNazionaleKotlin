package com.example.biblioteca_nazionale.interface_

import com.example.biblioteca_nazionale.model.Book
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("search.json") //ricerca per isbn
    fun getBookISBN(): Call<List<Book>>

    @GET("full.json") //ricerca per BID full.json
    fun getBookBID(): Call<List<Book>>
}