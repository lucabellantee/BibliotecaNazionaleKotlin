package com.example.biblioteca_nazionale.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.biblioteca_nazionale.MainActivity
import com.example.biblioteca_nazionale.databinding.RegisterBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import java.net.SocketTimeoutException

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: RegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val db = FirebaseFirestore.getInstance()

        binding.textViewLoginFromReg.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.arrowIcon.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })

        binding.regButtonLayReg.setOnClickListener {
            val email = binding.emailTextViewInsert.text.toString()
            val pass = binding.passwordTextViewInsert.text.toString()
            val confirmPass = binding.passConfirmTextViewInsert.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            firebaseAuth.signOut()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            registerErrorHandling(it)
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }



    private fun registerErrorHandling(it: Task<AuthResult>){
        when (it.exception) {
            is FirebaseAuthWeakPasswordException -> {
                alertDialog("Registration error","The password must contain 6 characters, of which at least 1 capital letter")
            }
            is FirebaseAuthEmailException -> {
                alertDialog("Registration error","Email not valid, try again!")

            }
            is FirebaseAuthUserCollisionException -> {
                alertDialog("Registration error","The email address provided is already associated with another account")
            }
            is SocketTimeoutException -> {
                alertDialog("Network error","Check your internet connection")
            }
            else -> {
                alertDialog("General error","Pay attention to what you entered")
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