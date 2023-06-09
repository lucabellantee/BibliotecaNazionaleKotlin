package com.example.biblioteca_nazionale.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biblioteca_nazionale.firebase.FirebaseDB
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
        var allData = this.getUserInfo(uid)
        val data = allData.value?.data

        val impostazioniData = data?.get("Impostazioni") as? HashMap<String, Any>
        //Log.d("/FirebaseViewModel",impostazioniData.toString())
        val libriPrenotatiData = impostazioniData?.get("libriPrenotati") as? HashMap<String, ArrayList<String>>
        val commentiData = impostazioniData?.get("commenti") as? HashMap<String, HashMap<String, String>>
        Log.d("/FirebaseViewModel",commentiData.toString())
        val uid = data?.get("uid") as? String
        val email = data?.get("email") as? String

        return Users(uid.toString(),email.toString(), UserSettings(libriPrenotatiData , commentiData ))
    }

}