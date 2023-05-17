package com.example.biblioteca_nazionale.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class LoginLogoutViewModel : ViewModel() {


    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    val utenteCorrente: MutableLiveData<FirebaseUser?> = MutableLiveData()


    // Login asincrono con email e password
    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        withContext(Dispatchers.IO){
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        utenteCorrente.value = firebaseAuth.currentUser
                    } else {
                        utenteCorrente.value = null
                    }
                }
        }
    }





    fun initGoogleSignInClient(googleSignInOptions: GoogleSignInOptions, googleSignInClient: GoogleSignInClient) {
        this.googleSignInClient = googleSignInClient
    }


    // Login asincorno con account Google
    suspend fun signInWithGoogle(account: GoogleSignInAccount) {

        withContext(Dispatchers.IO){
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        utenteCorrente.value = firebaseAuth.currentUser
                    } else {
                        utenteCorrente.value = null
                    }
                }
        }
    }

/*
    suspend fun signOut() {
        withContext(Dispatchers.IO){
            firebaseAuth.signOut()
            googleSignInClient.signOut()
        }
    }
    */
    suspend fun signOut() {
        withContext(Dispatchers.IO){
            firebaseAuth.signOut()
            utenteCorrente.value = null
        }
    }
}
