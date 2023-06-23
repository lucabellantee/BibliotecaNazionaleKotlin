package com.example.biblioteca_nazionale.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.LoginBinding
import com.example.biblioteca_nazionale.firebase.FirebaseDB
import com.example.biblioteca_nazionale.model.UserSettings
import com.example.biblioteca_nazionale.model.Users
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebase: FirebaseDB = FirebaseDB()
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    // login google
    private lateinit var googleSignInClient : GoogleSignInClient
    // login google

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.login)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // INIZIO LOGIN GOOGLE

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this , gso)

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
            val email = binding.EditTextSearch.text.toString()
            val pass = binding.password.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {

                       // val allUser = firebaseViewModel.getAllUser()
                        firebaseViewModel.getAllUser().observe(this, { arrayOfUser ->
                            var userIsPresent = false
                            Log.d("arrayOfUser", arrayOfUser.size.toString())
                            for(utente in arrayOfUser){
                                //Log.d("ECCOMI","SONO QUI")
                                // Log.d("/LoginActivity",utente.toString())
                                Log.d("userIsPresent",userIsPresent.toString())
                                if((utente.UID.equals(firebaseAuth.currentUser?.uid.toString()))) userIsPresent = true
                            }
                            if(userIsPresent == false) {
                                Log.d("/LoginActivity", "SALVO IL NUOVO UTENTE !!!!")
                                val newUser = Users(firebaseAuth.uid.toString(),email,null)
                                firebase.saveNewUser(newUser)
                            }
                        })
                       /* firebaseViewModel.getAllUser().thenAccept { arrayOfUser ->
                           var userIsPresent = false
                            Log.d("arrayOfUser", arrayOfUser.size.toString())
                           for(utente in arrayOfUser){
                               //Log.d("ECCOMI","SONO QUI")
                              // Log.d("/LoginActivity",utente.toString())
                               if((utente.email.equals(firebaseAuth.currentUser?.email.toString()))) userIsPresent = true
                           }
                            if(userIsPresent == false) {
                               // Log.d("/LoginActivity", "SALVO IL NUOVO UTENTE !!!!")
                                val newUser = Users(firebaseAuth.uid.toString(),email,null)
                                firebase.saveNewUser(newUser)
                            }
                        }.exceptionally { throwable ->
                            // Gestione di eventuali errori nel recupero dell'utente
                            Log.e("/LoginActivity", "Errore di tutti gli utenti: ${throwable.message}")
                            null
                        }*/

                        val intent = Intent(this, HomePageActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        firebaseAuth.signOut()

     if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }
    }


  /*  override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {

             if (firebaseAuth.currentUser != null) {
                 val intent = Intent(this, HomePageActivity::class.java)
                 startActivity(intent)
             }
        }   */


        // INIZIO FUNZIONI LOGIN GOOGLE

        private fun signInGoogle(){
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

        private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if (result.resultCode == Activity.RESULT_OK){

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

        private fun handleResults(task: Task<GoogleSignInAccount>) {
            if (task.isSuccessful){
                val account : GoogleSignInAccount? = task.result
                if (account != null){
                    updateUI(account)
                }
            }else{
                Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
            }
        }

        private fun updateUI(account: GoogleSignInAccount) {
            val credential = GoogleAuthProvider.getCredential(account.idToken , null)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful){
                    // Salvo i miei dati su FireBase nella collection "Utenti"
                    val firebaseViewModel: FirebaseViewModel by viewModels()

                    firebaseViewModel.getAllUser().observe(this, { arrayOfUser ->
                        var userIsPresent = false
                        val app = ArrayList<Boolean>()
                        Log.d("arrayOfUser", arrayOfUser.size.toString())
                        for(utente in arrayOfUser){
                            if((utente.UID.equals(account.idToken.toString()))) userIsPresent = true
                            app.add(userIsPresent)
                        }
                        if(userIsPresent == false) {
                            Log.d("/LoginActivity", "SALVO IL NUOVO UTENTE !!!!")
                            val newUser = Users(account.idToken.toString(),account.email.toString(),null)
                            firebase.saveNewUser(newUser)
                        }
                    })
                    // Se corretto entro nella HomePageActivity, attivandola con questo comadno
                    startActivity(Intent(this , HomePageActivity::class.java))
                }else{
                    // Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "Error login with Google" , Toast.LENGTH_LONG).show()
                }
            }
        }

}