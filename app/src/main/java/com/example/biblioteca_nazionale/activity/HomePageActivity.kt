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


        /*
        firebaseAuth = FirebaseAuth.getInstance()
        val db = Firebase.firestore
        // Prendo il riferimento allo user corrente -> codice UID
        val user = FirebaseAuth.getInstance().currentUser
*/
        /*val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController*/

        /*binding.bottomNavigation.setOnItemSelectedListener {item ->
            when(item.itemId){
                R.id.profileIcon -> {
                    findNavController(R.id.fragmentContainer).navigate(R.id.action_bookListFragment_to_profileFragment2)
                    true
                }
                else -> false
            }
        }*/

    //    val firebase = FirebaseDB()
      //  firebase.writeUidAndEmail()


    /*    val firebaseViewModel: FirebaseViewModel by viewModels()
        val firebaseDB = FirebaseDB()
        Log.d("/HomePageActivity",firebaseDB.getAllUserInfo().isInitialized.toString()) //    PROVA DATABASE FIREBASE
            firebaseDB.getAllUserInfo().observe(this, { result ->
                println(result) // Stampa il valore del LiveData
            })*/


        // firebaseDB.getAllUserInfo()

        //
        /*
        val user1 = hashMapOf(
            user?.email.toString() to "email",
            user?.uid to "uid",
        )

        db.collection("utenti").document("datiUtenti")
            .set(user1)
            .addOnSuccessListener { Log.d("/HomePageActivity", "DocumentSnapshot successfully written!") }
            .addOnFailureListener {Log.d("/HomePageActivity", "Error writing document") }



        // Leggo il documento
        val docRef = db.collection("utenti").document("datiUtenti")
        docRef.get()

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "ECCO I DATI: ${document.data}")
                } else {
                    Log.d(TAG, "Nessun dato")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "ERROREEEEEEEE ", exception)
            }

//      FINE PROVA DATABASE FIREBASE */

        /*override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }*/


        val firebaseViewModel: FirebaseViewModel by viewModels()
        // Create the observer which updates the UI.
        val userInfoObserver = Observer<DocumentSnapshot> { newName ->
            // Update the UI, in this case, a TextView.
            Log.d("/HomePageActivity", newName.data.toString())
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
       // model.currentName.observe(this, nameObserver)
        firebaseViewModel.getUserInfo("1cK02hokWHS1Ivnr1iKr34JKe4q1").observe(this,userInfoObserver)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeIcon -> {
                    replaceFragment(BookListFragment())
                    true
                }
                R.id.bookIcon -> {
                    replaceFragment(MyBooksFragment())
                    true
                }
                R.id.notificationIcon -> {
                    replaceFragment(NotificationsFragment())
                    true
                }
                R.id.settingsIcon -> {
                    replaceFragment(SettingsFragment())
                    true
                }
                R.id.profileIcon -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

}