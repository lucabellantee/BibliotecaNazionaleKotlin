package com.example.biblioteca_nazionale.viewmodel

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.biblioteca_nazionale.firebase.FirebaseDB
import com.example.biblioteca_nazionale.model.BookFirebase
import com.example.biblioteca_nazionale.model.UserSettings
import com.example.biblioteca_nazionale.model.Users
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

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

    fun getEmailLoggedUser(): String = firebase.getCurrentEmail()

    fun getUidLoggedUser(): String = firebase.getCurrentUid()

    fun getCurrentUser(uid: String): CompletableFuture<Users> {
        val futureResult = CompletableFuture<Users>()
        // TODO METTERE: firebase.getCurrentUid()
        this.getUserInfo("provaUser").observeForever { documentSnapshot ->
            val data = documentSnapshot
           // Log.d("/IMPORTANTE", data.toString())
            val impostazioniData = data?.get("userSettings") as? HashMap<String, Any>
            //Log.d("IMPOSTAZIONI: ", impostazioniData.toString())
            val libriPrenotatiData = impostazioniData?.get("libriPrenotati") as? HashMap<String, ArrayList<String>>
           // Log.d("LIBRI PRENOTATI",libriPrenotatiData.toString())
            val commentiData = impostazioniData?.get("commenti") as? HashMap<String, HashMap<String, String>>
           // Log.d("COMMENTI: ",commentiData.toString())
            val uid = data?.get("uid") as? String
            //Log.d("UID: ", uid.toString())
            val email = data?.get("email") as? String
           // Log.d("EMAIL: ", email.toString())

            if (impostazioniData != null && libriPrenotatiData != null && commentiData != null && uid != null && email != null) {
                val users = Users(uid, email, UserSettings(libriPrenotatiData, commentiData))
                futureResult.complete(users)
            } else {
                // Gestisci il caso in cui i dati siano nulli o mancanti
                futureResult.completeExceptionally(Exception("Dati mancanti o nulli"))
            }
        }

        return futureResult
    }


    fun getAllUser(): CompletableFuture<ArrayList<Users>>{
        val futureResult = CompletableFuture<ArrayList<Users>>()
        val allUser = ArrayList<Users>()

        this.getAllDocument().observeForever {
            allDocument ->
            Log.d("ALL DOCUMENT SIZE:",allDocument.size.toString())
            for(document in allDocument){
                var impostazioniData = document?.get("userSettings") as? HashMap<String, Any>
                var libriPrenotatiData = impostazioniData?.get("libriPrenotati") as? HashMap<String, ArrayList<String>>
                var commentiData = impostazioniData?.get("commenti") as? HashMap<String, HashMap<String, String>>
                var uid = document?.get("uid") as? String
                var email = document?.get("email") as? String

                var userSettings = UserSettings(libriPrenotatiData,commentiData)
                var tmpUser = Users(uid.toString(), email.toString(), userSettings)
               // Log.d("SINGOLO USER: ", tmpUser.toString())
                allUser.add(tmpUser)
            }
        }
        //Log.d("SIZE allUser ", allUser.size.toString())
        futureResult.complete(allUser)
        return futureResult
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
        //val uid = "provaUser" // TODO METTERE: firebase.getCurrentUid()
        val uid = firebase.getCurrentUid()
        val currentUser = this.getCurrentUser(uid)
        currentUser.thenAccept { user ->
            user.userSettings?.addNewBook(idLibro, isbn,placeBooked,image)
            firebase.updateBookPrenoted(user)
        }.exceptionally { throwable ->
            // Gestione di eventuali errori nel recupero dell'utente
            Log.e("/FirebaseViewModel", "Errore nel recupero dell'utente: ${throwable.message}")
            null
        }

    }


    fun removeBookBooked(idLibro: String){

        val uid = "provaUser" // TODO METTERE: firebase.getCurrentUid()
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


    fun addNewCommentUserSide(commentDate: String, comment: String){
        val currentUser = this.getCurrentUser("provaUser")  // TODO METTERE: firebase.getCurrentUid()
        currentUser.thenAccept { user ->
            user.userSettings?.addNewComment(commentDate,comment)
            firebase.addCommentUserSide(user)
        }.exceptionally { throwable ->
            // Gestione di eventuali errori nel recupero dell'utente
            Log.e("/FirebaseViewModel", "Errore nel recupero dell'utente: ${throwable.message}")
            null
        }

    }

    fun removeCommentUserSide(idComment: String, currentUser: Users){
        val currentUser = this.getCurrentUser("provaUser")  // TODO METTERE: firebase.getCurrentUid()
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

