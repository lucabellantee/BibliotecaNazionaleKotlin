package com.example.biblioteca_nazionale.model

data class UserSettings(
    val libriPrenotati: HashMap<String, String>?,
    val commenti: HashMap<String, HashMap<String, String>>?
) {
    override fun toString(): String = "Libri prenotati: " + libriPrenotati.toString() + " " + "Commenti: " + commenti.toString()
}
