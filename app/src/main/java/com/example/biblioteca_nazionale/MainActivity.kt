package com.example.biblioteca_nazionale

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.Button
import com.example.biblioteca_nazionale.activity.HomePageActivity
import com.example.biblioteca_nazionale.activity.LoginActivity
import com.example.biblioteca_nazionale.activity.RegistrationActivity
import com.example.biblioteca_nazionale.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        setContentView(R.layout.activity_main)

        val loginButt : Button = findViewById<Button>(R.id.loginButtonWelcPage)
        val regButt : Button = findViewById<Button>(R.id.loginButtonWelcPage2)

        loginButt.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        })

        regButt.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        })
    }

    /*private lateinit var binding: ActivityMainBinding
    lateinit var username : EditText
    lateinit var password: EditText
    lateinit var loginButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loginButton.setOnClickListener(View.OnClickListener {
            if (binding.username.text.toString() == "user" && binding.password.text.toString() == "1234"){
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
            }
        })
    }*/
}