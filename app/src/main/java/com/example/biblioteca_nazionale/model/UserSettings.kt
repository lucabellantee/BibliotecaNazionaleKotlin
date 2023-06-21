package com.example.biblioteca_nazionale.model

data class UserSettings(
    val libriPrenotati: HashMap<String, ArrayList<String>>?,
    val commenti: HashMap<String, HashMap<String, String>>?
) {
    companion object{
        var idComment: String = "0"
    }
    override fun toString(): String = "Libri prenotati: " + libriPrenotati.toString() + " " + "Commenti: " + commenti.toString()

    fun addNewBook(bookName: String, isbn: String, bookedPlace: String, image: String){
        val newElement = ArrayList<String>()
        newElement.add(isbn)
        newElement.add(bookedPlace)
        newElement.add(image)
        libriPrenotati?.set(bookName,newElement)
    }

    fun removeBook(bookName: String){
        if(libriPrenotati?.containsKey(bookName) == true) {
            libriPrenotati.remove(bookName)
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
            commenti.remove(idComment)
        }
    }
}
