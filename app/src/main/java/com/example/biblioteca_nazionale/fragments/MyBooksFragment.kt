package com.example.biblioteca_nazionale.fragments

import BookAdapter
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentMyBooksBinding
import com.example.biblioteca_nazionale.model.MiniBook
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth

class MyBooksFragment : Fragment(R.layout.fragment_my_books) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentMyBooksBinding
    private lateinit var adapter: BookAdapter
    private lateinit var firebaseViewModel: FirebaseViewModel
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyBooksBinding.bind(view)
        firebaseViewModel = ViewModelProvider(requireActivity()).get(FirebaseViewModel::class.java)
        val appo: ArrayList<MiniBook> = ArrayList()

        adapter = BookAdapter(appo)
        adapter.setOnBookClickListener(object : BookAdapter.OnBookClickListener {
            override fun onBookClick(position: Int) {
                val libro = appo[position]
                val action = MyBooksFragmentDirections.actionMyBooksFragmentToDeleteBookingFragment2(libro)
                findNavController().navigate(action)
            }
        })

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMyBooks.layoutManager = layoutManager
        binding.recyclerViewMyBooks.adapter = adapter

        firebaseViewModel.getAllUser().observe(viewLifecycleOwner) { usersList ->
            appo.clear()
            for (user in usersList) {
                if (user.UID == firebaseAuth.currentUser?.uid) {
                    val userSettings = user.userSettings
                    userSettings?.libriPrenotati?.let { libri ->
                        appo.addAll(libri)
                    }
                    break
                }
            }
            adapter.notifyDataSetChanged()
        }
    }
}
