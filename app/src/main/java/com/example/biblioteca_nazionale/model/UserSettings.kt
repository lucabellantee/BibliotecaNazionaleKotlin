package com.example.biblioteca_nazionale.model

import java.time.LocalDate

data class UserSettings(
    var libriPrenotati: HashMap<String, ArrayList<String>>?,
    var commenti: HashMap<String, HashMap<String, String>>?
) {
    companion object{
        var idComment: String = "0"
    }
    override fun toString(): String = "Libri prenotati: " + libriPrenotati.toString() + " " + "Commenti: " + commenti.toString()

    fun addNewBook(bookName: String, isbn: String, bookedPlace: String, image: String){

        if(libriPrenotati == null) libriPrenotati = HashMap<String, ArrayList<String>>()

        val newElement = ArrayList<String>()
        newElement.add(0,isbn)
        newElement.add(1,bookedPlace)
        newElement.add(2,image)
        newElement.add(3,LocalDate.now().plusDays(14).toString()) //dataScadenza 14 giorni dopo
        libriPrenotati?.set(bookName,newElement)
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
