package com.example.biblioteca_nazionale.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.HomePageBinding
import com.example.biblioteca_nazionale.fragments.BlankFragment
import com.example.biblioteca_nazionale.fragments.BookListFragment

class HomePageActivity : AppCompatActivity() {

    lateinit var binding: HomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)
        binding = HomePageBinding.inflate(layoutInflater)

        val fragmentManager: FragmentManager = supportFragmentManager
        val bookListFragment = BookListFragment()
        val blankFrag = BlankFragment()

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, blankFrag) //Quì serve la recyclerView
        fragmentTransaction.commit()
    }
}
