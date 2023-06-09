package com.example.biblioteca_nazionale.model

import com.google.gson.annotations.SerializedName

data class OPACBook(
    @SerializedName("codiceIdentificativo") val codiceIdentificativo: String,
)
