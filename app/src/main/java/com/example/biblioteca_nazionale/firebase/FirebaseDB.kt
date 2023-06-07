package com.example.biblioteca_nazionale.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.biblioteca_nazionale.model.UserSettings
import com.example.biblioteca_nazionale.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore


class FirebaseDB {


    companion object{
        val firebaseAuth = FirebaseAuth.getInstance()
        val db = com.google.firebase.ktx.Firebase.firestore
        // Prendo il riferimento allo user corrente -> codice UID
        val user = firebaseAuth.currentUser
    }

    fun writeUidAndEmail(){

        val newUser = Users(user?.uid.toString()  , user?.email.toString(), UserSettings(null,null))
        db.collection("utenti").document(user?.uid.toString())
            .set(newUser)
            .addOnSuccessListener { /*Log.d("/HomePageActivity", "DocumentSnapshot successfully written!")*/ }
            .addOnFailureListener { /*Log.d("/HomePageActivity", "Error writing document")*/ }
    }

    var userInfoLiveData: MutableLiveData<DocumentSnapshot> =  MutableLiveData()
   fun getAllUserInfoFromUid(uid: String): MutableLiveData<DocumentSnapshot> {

       val docRef = db.collection("utenti").document("provaUser")
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

    fun getCurrentEmail(): String = user?.email.toString()

    fun getCurrentUid(): String = user?.uid.toString()

    /*
    fun updateSettings(currentUser: Users){

        currentUser.userSettings = UserSettings(libriPrenotati , recensioni)
        val campi = hashMapOf(
            currentUser.email to "email",
            currentUser.UID to "uid" ,
            currentUser.userSettings.libriPrenotati.toString() to "libri prenotati",
            currentUser.userSettings.recensioni.toString() to "recensioni"
        )
        db.collection("utenti").document(currentUser.UID).set(campi)
            .addOnSuccessListener {
                // Operazione completata con successo
            }
            .addOnFailureListener { e ->
                // Gestione dell'errore
            }

    } */
/*
    var userAllLiveData: MutableLiveData<DocumentSnapshot> =  MutableLiveData()
    fun readUserFromDb(uid: String): MutableLiveData<DocumentSnapshot> {

        val docRef = db.collection("utenti").document(uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("/FirebaseDB", "DocumentSnapshot data: ${document.data}")
                    userAllLiveData.value = document

                } else {
                    Log.d("/FirebaseDB", "Documento vuoto")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("/FirebaseDB", "Errore lettura dati !!!")
            }
        return userAllLiveData
    } */
}