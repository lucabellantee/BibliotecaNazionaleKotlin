package com.example.biblioteca_nazionale.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biblioteca_nazionale.firebase.FirebaseDB
import com.example.biblioteca_nazionale.model.BookFirebase
import com.example.biblioteca_nazionale.model.UserSettings
import com.example.biblioteca_nazionale.model.Users
import com.google.firebase.firestore.DocumentSnapshot
import java.util.concurrent.CompletableFuture
import com.example.biblioteca_nazionale.model.MiniBook
import com.example.biblioteca_nazionale.model.Review


class FirebaseViewModel: ViewModel() {

    val firebase = FirebaseDB()
/*
    fun getUserInfo(): MutableLiveData<List<DocumentSnapshot>> {
        return firebase.getAllUserInfo()
    } */


    fun getUserInfo(uid: String): MutableLiveData<DocumentSnapshot>{
      return firebase.getAllUserInfoFromUid(uid)
    }

    fun getAllDocument(): MutableLiveData<ArrayList<DocumentSnapshot>>{
        return firebase.getAllUserFromDB()
    }


 /*   fun saveNewUser(uid: String, email: String){
        firebase.saveNewUser(Users(uid,email))
    }*/

    fun getEmailLoggedUser(): String = firebase.getCurrentEmail().toString()

    fun getUidLoggedUser(): String = firebase.getCurrentUid().toString()

    fun getCurrentUser(uid: String): CompletableFuture<Users> {
        val futureResult = CompletableFuture<Users>()
        // TODO METTERE: firebase.getCurrentUid()
        this.getUserInfo(getUidLoggedUser()).observeForever { documentSnapshot ->
            val data = documentSnapshot
           // Log.d("/IMPORTANTE", data.toString())
            val impostazioniData = data?.get("userSettings") as? HashMap<String, Any>
            //Log.d("IMPOSTAZIONI: ", impostazioniData.toString())
            val libriPrenotatiData = impostazioniData?.get("libriPrenotati") as? ArrayList<MiniBook>
            // Log.d("LIBRI PRENOTATI",libriPrenotatiData.toString())
            val commentiData = impostazioniData?.get("commenti") as? ArrayList<Review>
           // Log.d("COMMENTI: ",commentiData.toString())
            val uid = data?.get("uid") as? String
            //Log.d("UID: ", uid.toString())
            val email = data?.get("email") as? String
           // Log.d("EMAIL: ", email.toString())

//          impostazioniData != null && libriPrenotatiData != null && commentiData != null && uid != null && email != null
            if (uid != null && email != null) {
                val users = Users(uid, email, UserSettings(libriPrenotatiData, commentiData))
                futureResult.complete(users)
                Log.d("Future Result: ", futureResult.isDone.toString())
            } else {
                // Gestisci il caso in cui i dati siano nulli o mancanti
                futureResult.completeExceptionally(Exception("Dati mancanti o nulli"))
            }
        }

        return futureResult
    }



    fun getAllUser(): LiveData<ArrayList<Users>> {
        val allUserLiveData = MutableLiveData<ArrayList<Users>>()

        this.getAllDocument().observeForever { allDocument ->
            val allUser = ArrayList<Users>()
            for (document in allDocument) {
                val impostazioniData = document?.get("userSettings") as? HashMap<String, Any>
                //val libriPrenotatiData = impostazioniData?.get("libriPrenotati") as? HashMap<String, ArrayList<String>>
                val libriPrenotatiData = impostazioniData?.get("libriPrenotati") as? ArrayList<MiniBook>
                val commentiData = impostazioniData?.get("commenti") as? ArrayList<Review>
                val uid = document?.get("uid") as? String
                val email = document?.get("email") as? String

                val userSettings = UserSettings(libriPrenotatiData, commentiData)
                val tmpUser = Users(uid.toString(), email.toString(), userSettings)
                allUser.add(tmpUser)
            }

            allUserLiveData.value = allUser
        }

        return allUserLiveData
    }


    fun getBookInfoResponseFromDB(idLibro: String): MutableLiveData<DocumentSnapshot>{
        return firebase.getAllBookInfoFromId(idLibro)
    }

    fun getBookInfo(idLibro: String): BookFirebase{
        var allData = this.getBookInfoResponseFromDB("ID_LIBRO")
        val data = allData.value?.data
        val allComment = data?.get("Commenti") as? HashMap<String, HashMap<String,HashMap<String,String>>>
       // Log.d("/FirebaseViewModel", allComment.toString())
       // val libriPrenotatiData = impostazioniData?.get("libriPrenotati") as? HashMap<String, ArrayList<String>>
        //val comment = allComment?.get("uid utente") as? HashMap<Any,Any>
        //Log.d("/FirebaseViewModel", comment.toString())
        val allRankingStar = data?.get("Stelle recensioni") as? HashMap<String,String>
       // Log.d("/FirebaseViewModel", allRankingStar.toString())

        return BookFirebase(allComment!!, allRankingStar!!)
    }



    fun addNewBookBooked(idLibro: String, isbn: String, placeBooked: String, image: String){
        val uid = firebase.getCurrentUid()
        Log.d("UID: ", firebase.getCurrentUid().toString())

        val currentUser = this.getCurrentUser(uid.toString())
        currentUser.thenAccept { user ->
            Log.d("PRIMA" ,  idLibro + " " + isbn + " " + placeBooked + " " + image)
            user.userSettings?.addNewBook(idLibro, isbn, placeBooked, image)
            Log.d("DOPO" , idLibro + " " + isbn + " " + placeBooked + " " + image)
            Log.d("USER", user.toString())
           // Log.d("USERRR", user.email)
            //Log.d("UIDDD", user.UID)
            Log.d("USER", user.toString())
            firebase.updateBookPrenoted(user)
            println(user.userSettings?.libriPrenotati?.get(user.userSettings?.libriPrenotati!!.size-1))
        }.exceptionally { throwable ->
            // Gestione di eventuali errori nel recupero dell'utente
            Log.e("/FirebaseViewModel", "Errore nel recupero dell'utente: ${throwable.message}")
            null
        }
    }

    fun newExpirationDate(isbn: String) {
        var expirationDate: String? = null

        firebase.getExpirationDate(isbn) { dataScadenza ->
            if (dataScadenza != null) {
                Log.d("DATAAA", dataScadenza)
                Log.d("DATAAA1", expirationDate.toString())
                expirationDate = dataScadenza
            } else {
                println("Libro non trovato o errore durante il recupero della data di scadenza.")
            }
        }
        if (expirationDate != null) {
            Log.d("DATAAA12", expirationDate.toString())
            println("Data di scadenza: $expirationDate")
        }
    }




    fun removeBookBooked(idLibro: String){

        val uid = getUidLoggedUser() // TODO METTERE: firebase.getCurrentUid()
        val currentUser = this.getCurrentUser(uid)
        currentUser.thenAccept { user ->
            user.userSettings?.removeBook(idLibro)
            firebase.updateBookPrenoted(user)
        }.exceptionally { throwable ->
            // Gestione di eventuali errori nel recupero dell'utente
           Log.e("/FirebaseViewModel", "Errore nel recupero dell'utente: ${throwable.message}")
            null
        }

    }


    fun addNewCommentUserSide(reviewText: String,reviewTitle: String,isbn: String,vote:Float){
        val currentUser = this.getCurrentUser(getUidLoggedUser())  // TODO METTERE: firebase.getCurrentUid()
        currentUser.thenAccept { user ->
            user.userSettings?.addNewComment(reviewText,reviewTitle,isbn,vote)
            firebase.addCommentUserSide(user)
        }.exceptionally { throwable ->
            // Gestione di eventuali errori nel recupero dell'utente
            Log.e("/FirebaseViewModel", "Errore nel recupero dell'utente: ${throwable.message}")
            null
        }

    }

    fun removeCommentUserSide(idComment: String, currentUser: Users){
        val currentUser = this.getCurrentUser(getUidLoggedUser())  // TODO METTERE: firebase.getCurrentUid()
        currentUser.thenAccept { user ->
            user.userSettings?.removeComment(idComment)
            firebase.removeCommentUserSide(user)
        }.exceptionally { throwable ->
            // Gestione di eventuali errori nel recupero dell'utente
            Log.e("/FirebaseViewModel", "Errore nel recupero dell'utente: ${throwable.message}")
            null
        }

    }

}

