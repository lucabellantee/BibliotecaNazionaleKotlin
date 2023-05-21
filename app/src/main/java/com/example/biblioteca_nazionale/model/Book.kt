package com.example.biblioteca_nazionale.model

import com.google.gson.annotations.SerializedName

data class Book(
    @SerializedName("id") val id: String,
    @SerializedName("volumeInfo") val info: InfoBook
)


