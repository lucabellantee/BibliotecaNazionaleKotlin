package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookAdapter
import com.example.biblioteca_nazionale.databinding.FragmentMyBooksBinding
import com.example.biblioteca_nazionale.model.MiniBook
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar

class MyBooksFragment : Fragment(R.layout.fragment_my_books) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentMyBooksBinding
    private lateinit var adapter: BookAdapter
    private lateinit var firebaseViewModel: FirebaseViewModel
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var isDataLoaded = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMyBooksBinding.bind(view)

        binding.progressBar.visibility = View.VISIBLE
        binding.layoutTotal.visibility = View.GONE

        firebaseViewModel = ViewModelProvider(requireActivity()).get(FirebaseViewModel::class.java)
        val appo: ArrayList<MiniBook> = ArrayList()

        firebaseViewModel.getAllUser().observe(viewLifecycleOwner) { usersList ->
            appo.clear()
            for (user in usersList) {
                if (user.UID == firebaseAuth.currentUser!!.uid) {
                    val userSettings = user.userSettings
                    if (userSettings != null) {
                        val libri = userSettings.libriPrenotati
                        if (libri != null) {
                            val inputFormat = "dd/MM/yyyy"
                            val outputFormat = "dd/MM/yyyy"

                            val inputFormatter = SimpleDateFormat(inputFormat)
                            val outputFormatter = SimpleDateFormat(outputFormat)

                            for (libro in libri) {
                                val date = inputFormatter.parse(libro.date)
                                val calendar = Calendar.getInstance()
                                calendar.time = date
                                calendar.add(Calendar.DAY_OF_MONTH, 14)
                                val datePlus14Days = calendar.time
                                val datePlus14DaysString = outputFormatter.format(datePlus14Days)
                                libro.date = datePlus14DaysString
                                appo.add(libro)
                            }
                        }
                    }
                    break
                }
                //adapter.notifyDataSetChanged()
            }
            println(appo)
            adapter = BookAdapter(appo)
            if (appo.isNotEmpty()) {

                binding.progressBar.visibility = View.GONE
                binding.layoutPrincipale.visibility = View.GONE
                binding.layoutTotal.visibility=View.VISIBLE

                adapter.setOnBookClickListener(object : BookAdapter.OnBookClickListener {
                    override fun onBookClick(position: Int) {
                        val libro = appo[position]
                        val action =
                            MyBooksFragmentDirections.actionMyBooksFragmentToDeleteBookingFragment2(
                                libro
                            )
                        findNavController().navigate(action)
                    }
                })

                val layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerViewMyBooks.layoutManager = layoutManager
                binding.recyclerViewMyBooks.adapter = adapter
            } else {
                binding.progressBar.visibility = View.GONE
                binding.layoutPrincipale.visibility = View.VISIBLE
                binding.layoutTotal.visibility=View.VISIBLE
            }
        }
    }
}
