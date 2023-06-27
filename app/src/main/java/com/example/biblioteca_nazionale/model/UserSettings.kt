package com.example.biblioteca_nazionale.model

import java.time.LocalDate
import java.util.UUID

data class UserSettings(
    //var libriPrenotati: HashMap<String, ArrayList<String>>?,
    var libriPrenotati: ArrayList<MiniBook>?,
    var commenti: ArrayList<Review>?
) {

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

    fun addNewComment(reviewText: String, reviewTitle: String, isbn: String, vote: Float) {
        val newComment = Review("C${UUID.randomUUID().toString()}", reviewText, reviewTitle, isbn, vote, LocalDate.now().toString())

        println(newComment)

        if (commenti == null) {
            commenti = ArrayList()
        }

        commenti?.add(newComment)
    }

    fun removeComment(idComment: String){
        val iterator = commenti?.iterator()

        if (iterator != null) {
            while (iterator.hasNext()) {
                val comment = iterator.next()
                if (comment.idComment == idComment) {
                    iterator.remove()
                    break
                }
            }
        }
    }
}

data class Review (
    var idComment: String,
    var reviewText: String,
    var reviewTitle:String,
    var isbn: String,
    var vote:Float,
    var date: String
)
