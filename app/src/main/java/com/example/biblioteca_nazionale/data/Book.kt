package com.example.biblioteca_nazionale.data

import java.util.Date

data class Book(
    var id: String? = null,
    var title: String? = null,
    var author: String? = null,
    var publisher: String? = null,
    var description: String? = null,
    var coverImageUrl: String? = null,
    var createdDate: Date? = null,
    var updatedDate: Date? = null
)

