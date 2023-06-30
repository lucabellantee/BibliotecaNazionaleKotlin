package com.example.biblioteca_nazionale.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.LoginBinding
import com.example.biblioteca_nazionale.firebase.FirebaseDB
import com.example.biblioteca_nazionale.model.Users
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import java.net.SocketTimeoutException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebase: FirebaseDB = FirebaseDB()
    private val firebaseViewModel: FirebaseViewModel by viewModels()

    // login google
    private lateinit var googleSignInClient: GoogleSignInClient
    private var isLoggingIn = false
    // login google

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // INIZIO LOGIN GOOGLE

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleReg.setOnClickListener {
            signInGoogle()
        }

        // FINE LOGIN GOOGLE

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textViewRegFromLog.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            if (!isLoggingIn) {
                isLoggingIn = true
                val email = binding.EditTextSearch.text.toString()
                val pass = binding.password.text.toString()

                if (email.isNotEmpty() && pass.isNotEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            isLoggingIn = false
                            if (task.isSuccessful) {
                                firebaseViewModel.getAllUser().observe(this) { arrayOfUser ->
                                    var userIsPresent = false
                                    Log.d("arrayOfUser", arrayOfUser.size.toString())
                                    for (utente in arrayOfUser) {
                                        if (utente.UID == firebaseAuth.currentUser?.uid.toString() && utente.email.equals(
                                                firebaseAuth.currentUser?.email.toString()
                                            )
                                        ) {
                                            userIsPresent = true
                                        }
                                    }
                                    if (!userIsPresent) {
                                        Log.d("/LoginActivity", "SALVO IL NUOVO UTENTE !!!!")
                                        val newUser =
                                            Users(firebaseAuth.uid.toString(), email, null)
                                        firebase.saveNewUser(newUser)
                                    }
                                }
                                val intent = Intent(this, HomePageActivity::class.java)
                                startActivity(intent)
                            } else {
                                loginErrorHandling(task)
                            }
                        }
                } else {
                    Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()

        //firebaseAuth.signOut()

        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }
    }


    // INIZIO FUNZIONI LOGIN GOOGLE

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                // Salvo i miei dati su FireBase nella collection "Utenti"

                firebaseViewModel.getAllUser().observe(this) { arrayOfUser ->
                    var userIsPresent = false
                    // val app = ArrayList<Boolean>()
                    //Log.d("arrayOfUser", arrayOfUser.size.toString())
                    for (utente in arrayOfUser) {
                        if ((utente.UID.equals(account.idToken.toString())) && (utente.email.equals(
                                account.email
                            ))
                        ) userIsPresent =
                            true
                        println(userIsPresent)
                    }
                    if (userIsPresent == false) {
                        println(account.id)
                        println(account.email.toString())
                        //Log.d("/LoginActivity", "SALVO IL NUOVO UTENTE !!!!")
                        val newUser =
                            Users(firebaseAuth.uid.toString(), account.email.toString(), null)
                        firebase.saveNewUser(newUser)

                    }

                    Toast.makeText(this, "Login successfully", Toast.LENGTH_LONG).show()
                    // Se corretto entro nella HomePageActivity, attivandola con questo comadno
                    startActivity(Intent(this , HomePageActivity::class.java))
                }

            }
        }
    }

    private fun loginErrorHandling(it: Task<AuthResult>){
        when (it.exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                alertDialog("Credential error","The credentials you entered are incorrect")
            }
            is FirebaseAuthEmailException -> {
                alertDialog("Registration error","Email not valid, try again!")
            }
            is FirebaseAuthInvalidUserException -> {
                alertDialog("User error","The supplied user does not exist or has been disabled")
            }
            is FirebaseAuthUserCollisionException -> {
                alertDialog("Email collision","The email address provided is already associated with another account")
            }
            is FirebaseAuthException ->{
                alertDialog("Authentication error","Generic authentication error")
            }
            is SocketTimeoutException -> {
                alertDialog("Network error","Check your internet connection")
            }
            else -> {
                alertDialog("General error","Pay attention")
            }
        }
    }


    private fun alertDialog(title: String , description: String){
        var alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(description)
        alertDialogBuilder.setPositiveButton("I understand") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

}