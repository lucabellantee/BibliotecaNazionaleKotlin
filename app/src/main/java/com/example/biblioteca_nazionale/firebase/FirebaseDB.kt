package com.example.biblioteca_nazionale.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.biblioteca_nazionale.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


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
            .addOnSuccessListener { /*Log.d("/HomePageActivity", "DocumentSnapshot successfully written!")*/ }
            .addOnFailureListener { /*Log.d("/HomePageActivity", "Error writing document")*/ }
    }

/*    lateinit var userDocuments: MutableLiveData<List<DocumentSnapshot>>
    suspend fun getAllUserInfo(): List<DocumentSnapshot> = suspendCoroutine { continuation -> userDocuments = MutableLiveData()
        FirebaseFirestore.getInstance().collection("utenti")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val myListOfDocuments: List<DocumentSnapshot> = task.result?.documents ?: emptyList()
                    continuation.resume(myListOfDocuments)
                    // userDocuments = MutableLiveData()
                    userDocuments.postValue(myListOfDocuments)
                } else {
                    val exception = task.exception
                    continuation.resumeWithException(exception ?: Exception("Failed to retrieve user info"))
                }
            }
    }*/


     var userInfoLiveData: MutableLiveData<DocumentSnapshot> =  MutableLiveData()
   fun getAllUserInfoFromUid(uid: String): MutableLiveData<DocumentSnapshot> {

       val docRef = db.collection("utenti").document(uid)
       docRef.get()
           .addOnSuccessListener { document ->
               if (document != null) {
                   Log.d("/FirebaseDB", "DocumentSnapshot data: ${document.data}")
                   userInfoLiveData.value = document

               } else {
                  Log.d("/FirebaseDB", "Documento vuoto")
               }
           }
           .addOnFailureListener { exception ->
            Log.d("/FirebaseDB", "Errore lettura dati !!!")
           }
        return userInfoLiveData
   }



    fun saveNewUser(newUser: Users){

        db.collection("utenti").document(newUser.UID)
            .set(newUser)
            .addOnSuccessListener { Log.d("/HomePageActivity", "DocumentSnapshot successfully written!") }
            .addOnFailureListener {Log.d("/HomePageActivity", "Error writing document") }

    }


}