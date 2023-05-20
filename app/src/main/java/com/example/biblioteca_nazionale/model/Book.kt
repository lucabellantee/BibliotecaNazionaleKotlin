package com.example.biblioteca_nazionale.model

import java.io.Serializable


class Book(
    val progressivoId: Int,
    val codiceIdentificativo: String,
    val isbn: String,
    val autorePrincipale: String,
    val copertina: String,
    val titolo: String,
    val pubblicazione: String,
    val livello: String,
    val tipo: String
) : Serializable
