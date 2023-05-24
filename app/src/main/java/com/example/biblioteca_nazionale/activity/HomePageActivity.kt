package com.example.biblioteca_nazionale.activity

import android.os.Bundle
import android.view.View
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.HomePageBinding
import com.example.biblioteca_nazionale.fragments.BookListFragment
class HomePageActivity : AppCompatActivity(){

    lateinit var binding: HomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomePageBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)


        val fragmentManager: FragmentManager = supportFragmentManager
        val bookListFrag = BookListFragment()

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, bookListFrag)
        fragmentTransaction.commit()



    }


    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
    }
}


