package com.example.biblioteca_nazionale.model

data class BookFirebase(
    var commenti: HashMap<String, HashMap<String, HashMap<String, String>>>,
    var starRanking: HashMap<String,String>
) {
    override fun toString(): String = "COMMENTI:  " + commenti.toString()  +  "RANKING LIBRO:   " + starRanking.toString()
}
