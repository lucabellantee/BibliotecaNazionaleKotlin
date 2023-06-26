package com.example.biblioteca_nazionale.model

import java.time.LocalDate

data class UserSettings(
    //var libriPrenotati: HashMap<String, ArrayList<String>>?,
    var libriPrenotati: ArrayList<MiniBook>?,
    var commenti: HashMap<String, HashMap<String, String>>?
) {
    companion object{
        var idComment: String = "0"
    }
    override fun toString(): String = "Libri prenotati: " + libriPrenotati.toString() + " " + "Commenti: " + commenti.toString()

    fun addNewBook(bookName: String, isbn: String, bookedPlace: String, image: String) {
        val newElement = MiniBook(isbn, bookedPlace, image, LocalDate.now().plusDays(14).toString())

        if (libriPrenotati == null) {
            libriPrenotati = ArrayList()
        }

        libriPrenotati?.add(newElement)


        println(newElement)

    }


    fun removeBook(bookName: String) {
        val iterator = libriPrenotati?.iterator()

        if (iterator != null) {
            while (iterator.hasNext()) {
                val book = iterator.next()
                if (book.isbn == bookName) {
                    iterator.remove()
                    break
                }
            }
        }
    }

    fun addNewComment(commentDate: String, comment: String){
        val addNewComment = HashMap<String,String>()

        if (commenti == null) {
            commenti = HashMap()
        }

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

data class MiniBook (
    var isbn: String,
    var bookPlace: String,
    var image: String,
    var date: String
)
