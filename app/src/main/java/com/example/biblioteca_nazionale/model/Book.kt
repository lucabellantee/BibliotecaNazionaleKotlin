package com.example.biblioteca_nazionale.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Book( //Il tutto fatto in base ai dati restitutiti dal json di SBN
    //@SerializedName("parametro") //Così posso rendere uno degli elementi modificabile
    val progressivoId: Int,
    val codiceIdentificativo: String,
    val isbn: String,
    val autorePrincipale: String,
    val copertina: String,
    val titolo: String,
    val pubblicazione: String,
    val livello: String,
    val tipo: String
)

