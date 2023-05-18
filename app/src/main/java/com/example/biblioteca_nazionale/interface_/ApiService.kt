package com.example.biblioteca_nazionale.interface_

import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.model.BookResponse
import retrofit2.http.GET

interface ApiService {
    @GET("search.json") //ricerca per isbn
    fun getBookISBN(): List<BookResponse>

    @GET("full.json") //ricerca per BID full.json
    fun getBookBID(): List<BookResponse>
}