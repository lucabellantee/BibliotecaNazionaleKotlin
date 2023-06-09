package com.example.biblioteca_nazionale.activity

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookListAdapter
import com.example.biblioteca_nazionale.databinding.HomePageBinding
import com.example.biblioteca_nazionale.firebase.FirebaseDB
import com.example.biblioteca_nazionale.fragments.BookListFragment
import com.example.biblioteca_nazionale.fragments.MyBooksFragment
import com.example.biblioteca_nazionale.fragments.NotificationsFragment
import com.example.biblioteca_nazionale.fragments.ProfileFragment
import com.example.biblioteca_nazionale.fragments.SettingsFragment
import com.example.biblioteca_nazionale.viewmodel.BooksViewModel
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomePageActivity : AppCompatActivity() {

    lateinit var binding: HomePageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    var model:BooksViewModel = BooksViewModel()
    lateinit var adapter:BookListAdapter
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        binding = HomePageBinding.inflate(layoutInflater)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setupWithNavController(navController)


// INIZIO PROVA CHIAMATE DB FIREBASE CON PATTTERN MVVVM


        val firebaseViewModel: FirebaseViewModel by viewModels()
        // Create the observer which updates the UI.
        val userInfoObserver = Observer<DocumentSnapshot> { currentUserInfo ->
            // Update the UI, in this case, a TextView.
            //Log.d("/HomePageActivity", currentUserInfo.data.toString())

            //firebaseViewModel.getCurrentUser("provaUser").toString()
           // Log.d("/HomePageActivity",firebaseViewModel.getCurrentUser("provaUser").toString())
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        // model.currentName.observe(this, nameObserver)
        firebaseViewModel.getUserInfo("1cK02hokWHS1Ivnr1iKr34JKe4q1").observe(this,userInfoObserver)

// FINE PROVA CHIAMATE DB FIREBASE CON PATTTERN MVVVM


    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

}