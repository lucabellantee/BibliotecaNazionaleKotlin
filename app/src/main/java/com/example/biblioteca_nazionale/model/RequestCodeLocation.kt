package com.example.biblioteca_nazionale.model

data class RequestCodeLocation(
    var isil: String,
    var shelfmarks: List<RequestCodeLibrary>
)
