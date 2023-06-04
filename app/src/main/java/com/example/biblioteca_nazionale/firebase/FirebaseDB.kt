package com.example.biblioteca_nazionale.firebase

import android.util.Log
import com.example.biblioteca_nazionale.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore

class FirebaseDB {

    companion object{
        val firebaseAuth = FirebaseAuth.getInstance()
        val db = com.google.firebase.ktx.Firebase.firestore
        // Prendo il riferimento allo user corrente -> codice UID
        val user = firebaseAuth.currentUser
    }

    fun writeUidAndEmail(){


        val newUser = Users(user?.uid.toString()  , user?.email.toString())
        db.collection("utenti").document(user?.uid.toString())
            .set(newUser)
            .addOnSuccessListener { Log.d("/HomePageActivity", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { Log.d("/HomePageActivity", "Error writing document") }
    }
}