package com.example.biblioteca_nazionale.fragments

import BookAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.model.MiniBook
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth

class MyBooksFragment : Fragment(R.layout.fragment_my_books) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookAdapter
    private val fbViewModel: FirebaseViewModel = FirebaseViewModel()
    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val firebaseViewModel: FirebaseViewModel = ViewModelProvider(requireActivity()).get(FirebaseViewModel::class.java)
        val currentUser = firebaseViewModel.getCurrentUser(firebaseViewModel.firebase.getCurrentUid().toString()).get()
        val libriPrenotati: ArrayList<MiniBook>? = currentUser.userSettings?.libriPrenotati
        var appo:ArrayList<MiniBook> = ArrayList()
        /*val libriPrenotati: ArrayList<MiniBook>? =
            firebaseViewModel.getCurrentUser(firebaseViewModel.getUidLoggedUser()).get().userSettings?.libriPrenotati*/

        fbViewModel.getAllUser().observe(viewLifecycleOwner) { usersList ->
            println(usersList)
            for (user in usersList) {
                if(user.UID == firebaseAuth.currentUser!!.uid) {
                    val userSettings = user.userSettings
                    if (userSettings != null) {
                        val libri = userSettings.libriPrenotati
                        if (libri != null) {
                            for (libro in libri) {
                                Log.d("LIBRO34", libro.bookPlace+" "+libro.isbn+" "+libro.image+" "+libro.date)
                                appo.add(libro)
                            }
                        }
                    }
                    break
                }
            }
            adapter.notifyDataSetChanged()
        }

        val view = inflater.inflate(R.layout.fragment_my_books, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMyBooks)
        adapter = BookAdapter(appo)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return view
    }
}
