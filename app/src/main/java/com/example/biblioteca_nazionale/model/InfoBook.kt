package com.example.biblioteca_nazionale.model

import com.google.gson.annotations.SerializedName

data class InfoBook(
    @SerializedName("title")val title: String,
    @SerializedName("authors")val authors: List<String>,
    @SerializedName("description")val description: String,
    @SerializedName("publisher")val publisher: String,
    @SerializedName("publishedDate") val publishedDate: String
)
