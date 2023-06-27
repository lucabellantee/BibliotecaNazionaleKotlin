package com.example.biblioteca_nazionale.fragments

import BookAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookListAdapter
import com.example.biblioteca_nazionale.databinding.FragmentMyBooksBinding
import com.example.biblioteca_nazionale.model.MiniBook
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth

class MyBooksFragment : Fragment(R.layout.fragment_my_books) {

    private lateinit var recyclerView: RecyclerView
    private  lateinit var binding: FragmentMyBooksBinding
    private val fbViewModel: FirebaseViewModel = FirebaseViewModel()
    val firebaseAuth = FirebaseAuth.getInstance()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyBooksBinding.bind(view)
        println("ciaoooo")
        val firebaseViewModel: FirebaseViewModel = ViewModelProvider(requireActivity()).get(FirebaseViewModel::class.java)
        //val currentUser = firebaseViewModel.getCurrentUser(firebaseViewModel.firebase.getCurrentUid().toString()).get()
        var appo:ArrayList<MiniBook> = ArrayList()

        fbViewModel.getAllUser().observe(viewLifecycleOwner) { usersList ->
            println(usersList)
            for (user in usersList) {
                if(user.UID == firebaseAuth.currentUser!!.uid) {
                    val userSettings = user.userSettings
                    if (userSettings != null) {
                        val libri = userSettings.libriPrenotati
                        if (libri != null) {
                            for (libro in libri) {
                                appo.add(libro)
                            }
                        }
                    }

                 //   println(appo)
                    break
                }
            }
            val adapter = BookAdapter(appo)
            val layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewMyBooks.layoutManager = layoutManager
            binding.recyclerViewMyBooks.adapter = adapter

            // Imposta l'adapter sulla RecyclerView dopo aver popolato la lista
           // recyclerView.adapter = adapter
        }


/*
        recyclerView = view.findViewById(R.id.recyclerViewMyBooks)
        adapter = BookAdapter(appo)

        //recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
*/

        //println(appo)

        //val layoutManager = LinearLayoutManager(requireContext())
        //binding.recyclerViewMyBooks.layoutManager = layoutManager

       /* adapter.setOnBookClickListener(object : BookAdapter.OnBookClickListener{
            override fun onBookClick(position: Int) {
                val libro  = appo.get(position)

                val action = MyBooksFragmentDirections.actionMyBooksFragmentToDeleteBookingFragment2(libro)
                findNavController().navigate(action)
            }
        })
        recyclerView.adapter = adapter

    }
}