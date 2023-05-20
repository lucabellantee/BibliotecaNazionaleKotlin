package com.example.biblioteca_nazionale.model

import com.google.gson.annotations.SerializedName

data class BooksResponse(
    @SerializedName("items") val items: List<Book>
)

