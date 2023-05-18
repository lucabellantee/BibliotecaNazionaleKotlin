package com.example.biblioteca_nazionale.model

import java.util.Date

data class BookResponse(
    var autorePrincipale: String,
    var citazioni: List<>,
    var codiceIdentificativo: String,
    var livello: String,
    var localizzazioni: List<>,
    var luogoNormalizzato: List<>,
    var nomi: List<>,
    var note: List<>,
    var numeri: List<>,
    var progressivoId: Int,
    var pubblicazione: String,
    var tipo: String,
    var titolo: String
)
