package com.example.biblioteca_nazionale.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        binding = HomePageBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_back -> {
                    firebaseAuth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                handleSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                handleSearch(newText)
                return true
            }
        })

        val fragmentManager: FragmentManager = supportFragmentManager
        val bookListFrag = BookListFragment()

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, bookListFrag) //Quì serve la recyclerView
        fragmentTransaction.commit()
    }

    fun handleSearch(query: String) {
        model.searchBooks(query)
        adapter = BookListAdapter(model.getLibriLiveData())
    }

}
