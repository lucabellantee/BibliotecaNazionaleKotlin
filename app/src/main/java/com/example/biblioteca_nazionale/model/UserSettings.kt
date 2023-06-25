package com.example.biblioteca_nazionale.model

import android.util.Log
import java.time.LocalDate

data class UserSettings(
    //var libriPrenotati: HashMap<String, ArrayList<String>>?,
    var libriPrenotati: HashMap<String, ArrayList<miniBook>>?,
    var commenti: HashMap<String, HashMap<String, String>>?
) {
    companion object{
        var idComment: String = "0"
    }
    override fun toString(): String = "Libri prenotati: " + libriPrenotati.toString() + " " + "Commenti: " + commenti.toString()

    fun addNewBook(bookName: String, isbn: String, bookedPlace: String, image: String) {
        val newElement = miniBook(isbn, bookedPlace, image, LocalDate.now().plusDays(14).toString())

        if (libriPrenotati == null) {
            libriPrenotati = HashMap()
        }

        /*if (!libriPrenotati!!.containsKey(isbn)) {
            val bookElements = ArrayList<miniBook>()
            bookElements.add(newElement)
            libriPrenotati?.set(bookName, bookElements)
        } else {
            val bookElements = libriPrenotati!![isbn]
            bookElements?.add(newElement)
        }*/
        val newElement2 = ArrayList<miniBook>()
        newElement2.add(newElement)
        libriPrenotati!!.set(bookName, newElement2)

        Log.d("LIBRIII", libriPrenotati.toString())
    }


    fun removeBook(bookName: String){
        if(libriPrenotati?.containsKey(bookName) == true) {
            libriPrenotati!!.remove(bookName)
        }
    }

    fun addNewComment(commentDate: String, comment: String){
        val addNewComment = HashMap<String,String>()
        idComment.toInt().plus(1).toString()
        addNewComment.set(commentDate,comment)
        commenti?.set(idComment,addNewComment)
    }

    fun removeComment(idComment: String){
        if(commenti?.containsKey(idComment) == true){
            commenti!!.remove(idComment)
        }
    }
}

data class miniBook (
    var isbn: String,
    var bookPlace: String,
    var image: String,
    var date: String
)
