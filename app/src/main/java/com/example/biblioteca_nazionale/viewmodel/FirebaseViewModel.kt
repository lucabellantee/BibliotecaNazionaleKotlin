package com.example.biblioteca_nazionale.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biblioteca_nazionale.firebase.FirebaseDB
import com.example.biblioteca_nazionale.model.BookFirebase
import com.example.biblioteca_nazionale.model.UserSettings
import com.example.biblioteca_nazionale.model.Users
import com.google.firebase.firestore.DocumentSnapshot

class FirebaseViewModel: ViewModel() {

    val firebase = FirebaseDB()
/*
    fun getUserInfo(): MutableLiveData<List<DocumentSnapshot>> {
        return firebase.getAllUserInfo()
    } */


    fun getUserInfo(uid: String): MutableLiveData<DocumentSnapshot>{
      return  firebase.getAllUserInfoFromUid(uid)
    }


 /*   fun saveNewUser(uid: String, email: String){
        firebase.saveNewUser(Users(uid,email))
    }*/

    fun getEmailLoggedUser(): String = firebase.getCurrentEmail()

    fun getUidLoggedUser(): String = firebase.getCurrentUid()

    fun getCurrentUser(uid: String): Users {
        var allData = this.getUserInfo("provaUser")
        val data = allData.value?.data
        Log.d("/IMPORTANTE",data.toString())
        val impostazioniData = data?.get("Impostazioni") as? HashMap<String, Any>
        //Log.d("/FirebaseViewModel",impostazioniData.toString())
        val libriPrenotatiData = impostazioniData?.get("libriPrenotati") as? HashMap<String, ArrayList<String>>
        val commentiData = impostazioniData?.get("commenti") as? HashMap<String, HashMap<String, String>>
       // Log.d("/FirebaseViewModel",commentiData.toString())
        val uid = data?.get("uid") as? String
        val email = data?.get("email") as? String

        Log.d("/FirebaseViewModel",uid + " " + email + " " + libriPrenotatiData.toString() + " " + commentiData.toString())

        return Users(uid.toString(),email.toString(), UserSettings(libriPrenotatiData , commentiData ))
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
//                                       firebase.getCurrentUid()
        var newUser = this.getCurrentUser("provaUser")

       // Log.d("/FirebaseViewModel",newUser.toString())
        newUser.userSettings.addNewBook(idLibro,isbn,placeBooked,image)

        //Log.d("/FirebaseViewModel",newUser.toString())

        //firebase.updateBookPrenoted(newUser)
    }

}