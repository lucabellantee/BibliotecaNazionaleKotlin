package com.example.biblioteca_nazionale.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biblioteca_nazionale.firebase.FirebaseDB
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


    fun saveNewUser(uid: String, email: String){
        firebase.saveNewUser(Users(uid,email))
    }
}