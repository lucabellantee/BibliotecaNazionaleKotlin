package com.example.biblioteca_nazionale.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biblioteca_nazionale.firebase.FirebaseDB
import com.google.firebase.firestore.DocumentSnapshot

class FirebaseViewModel: ViewModel() {

    val firebase = FirebaseDB()
/*
    fun getUserInfo(): MutableLiveData<List<DocumentSnapshot>> {
        return firebase.getAllUserInfo()
    } */


    fun getUserInfo(uid: String): MutableLiveData<DocumentSnapshot>{
        firebase.getAllUserInfoFromUid(uid)
        return firebase.userInfoLiveData
    }
}