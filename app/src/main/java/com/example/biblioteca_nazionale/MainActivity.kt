package com.example.biblioteca_nazionale

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.activity.HomePageActivity
import com.example.biblioteca_nazionale.activity.LoginActivity
import com.example.biblioteca_nazionale.activity.RegistrationActivity
import com.example.biblioteca_nazionale.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val notificationPermissionRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton: Button = findViewById(R.id.loginButtonWelcPage)
        val regButton: Button = findViewById(R.id.loginButtonWelcPage2)

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        regButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            val intent = Intent(
                "android.settings.APPLICATION_DETAILS_SETTINGS",
                android.net.Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, notificationPermissionRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == notificationPermissionRequestCode) {
            if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications are not enabled. Enable notifications to receive alerts from the app.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
