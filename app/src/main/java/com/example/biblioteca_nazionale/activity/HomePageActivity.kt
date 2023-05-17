package com.example.biblioteca_nazionale.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.fragments.BookListFragment

class HomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        val fragmentManager: FragmentManager = supportFragmentManager
        val bookListFragment = BookListFragment()

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, bookListFragment)
        fragmentTransaction.commit()
    }
}
