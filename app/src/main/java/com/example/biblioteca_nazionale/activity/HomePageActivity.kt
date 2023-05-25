package com.example.biblioteca_nazionale.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookListAdapter
import com.example.biblioteca_nazionale.databinding.HomePageBinding
import com.example.biblioteca_nazionale.fragments.BookListFragment
import com.example.biblioteca_nazionale.viewmodel.BooksViewModel
import com.google.firebase.auth.FirebaseAuth

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

        firebaseAuth = FirebaseAuth.getInstance()


        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setOnItemSelectedListener {item ->
            when(item.itemId){
                R.id.profileIcon -> {
                    findNavController(R.id.fragmentContainer).navigate(R.id.action_bookListFragment_to_profileFragment2)
                    true
                }
                else -> false
            }
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        val bookListFrag = BookListFragment()

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, bookListFrag) //Quì serve la recyclerView
        fragmentTransaction.commit()
    }

}