package com.example.biblioteca_nazionale.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.biblioteca_nazionale.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import java.util.concurrent.CompletableFuture


class FirebaseDB {


    companion object {
        val firebaseAuth = FirebaseAuth.getInstance()
        val db = com.google.firebase.ktx.Firebase.firestore

        // Prendo il riferimento allo user corrente -> codice UID
        val user = firebaseAuth.currentUser
    }

    fun getFirebaseAuthIstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    var userInfoLiveData: MutableLiveData<DocumentSnapshot> = MutableLiveData()
    fun getAllUserInfoFromUid(uid: String): MutableLiveData<DocumentSnapshot> {

        val docRef = db.collection("utenti").document(uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userInfoLiveData.value = document


                } else {
                     Log.d("/FirebaseDB", "Documento vuoto")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("/FirebaseDB", "${exception.message}")
            }

        return userInfoLiveData
    }


    var allUserInfoLiveData: MutableLiveData<ArrayList<DocumentSnapshot>> = MutableLiveData()

    fun getAllUserFromDB(): MutableLiveData<ArrayList<DocumentSnapshot>> {
        val allDoc = db.collection("utenti")

        allDoc.get()
            .addOnSuccessListener { allDocument ->
                val documentList = ArrayList<DocumentSnapshot>()
                for (document in allDocument) {
                    documentList.add(document)
                }
                allUserInfoLiveData.value = documentList
            }
            .addOnFailureListener {
                Log.e("/FirebaseDB", it.toString())
            }

        return allUserInfoLiveData
    }


    fun saveNewUser(newUser: Users) {

        db.collection("utenti").document(newUser.UID)
            .set(newUser)
            .addOnSuccessListener {
                Log.d("/HomePageActivity", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { Log.d("/HomePageActivity", "Error writing document") }

    }

    fun getCurrentEmail(): String? = firebaseAuth.currentUser?.email

    fun getCurrentUid(): String? = firebaseAuth.currentUser?.uid


    fun updateBookPrenoted(newUser: Users): CompletableFuture<Void> {
        val uid = newUser.UID
        val futureResult = CompletableFuture<Void>()

        if (uid != null) {
            db.collection("utenti")
                .document(uid)
                .set(newUser, SetOptions.merge())
                .addOnSuccessListener {
                    futureResult.complete(null)
                }
                .addOnFailureListener { e ->
                    futureResult.completeExceptionally(e)
                }
        } else {
            futureResult.completeExceptionally(IllegalArgumentException("UID utente nullo."))
        }

        return futureResult
    }


    fun deleteBookPrenoted(newUser: Users){
        db.collection("utenti").document(newUser.UID).delete()
        db.collection("utenti").document(newUser.UID).set(newUser)
    }

    fun addCommentUserSide(newUser: Users) {
        db.collection("utenti").document(newUser.UID).delete()
        db.collection("utenti").document(newUser.UID).set(newUser)
    }


    fun removeCommentUserSide(user: Users): CompletableFuture<Void> {
        val futureResult = CompletableFuture<Void>()

        val documentRef = db.collection("utenti").document(user.UID)
        documentRef.set(user, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("/FirebaseViewModel", "Documento utente aggiornato correttamente.")
                futureResult.complete(null)
            }
            .addOnFailureListener { e ->
                Log.e("/FirebaseViewModel", "Errore nell'aggiornamento del documento utente: ${e.message}")
                futureResult.completeExceptionally(e)
            }

        return futureResult
    }

    var bookInfoLiveData: MutableLiveData<DocumentSnapshot> = MutableLiveData()
    fun getAllBookInfoFromId(idLibro: String): MutableLiveData<DocumentSnapshot> {

        val docRef = db.collection("libri").document("ID_LIBRO")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    bookInfoLiveData.value = document

                } else {
                    Log.d("/FirebaseDB", "Documento vuoto")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("/FirebaseDB", "Errore lettura dati !!!")
            }
        return bookInfoLiveData
    }
}