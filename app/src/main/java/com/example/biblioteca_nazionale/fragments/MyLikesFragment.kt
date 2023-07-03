package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookListAdapter
import com.example.biblioteca_nazionale.adapter.LikesAdapter
import com.example.biblioteca_nazionale.databinding.FragmentMyLikesBinding
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.viewmodel.BooksViewModel
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel

class MyLikesFragment : Fragment(R.layout.fragment_my_likes) {

    private lateinit var binding: FragmentMyLikesBinding

    private val fbViewModel: FirebaseViewModel = FirebaseViewModel()

    private val model: BooksViewModel = BooksViewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMyLikesBinding.bind(view)

        binding.layoutPrincipale.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        binding.toolbar.setNavigationOnClickListener {
            val action = MyLikesFragmentDirections.actionMyLikesFragmentToBookListFragment()
            findNavController().navigate(action)
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMyLikes.layoutManager = layoutManager

        var libriList = ArrayList<Book>()

        fbViewModel.getUserMiPiace().thenAccept { likes ->
            for (like in likes.distinct()) {
                model.searchBooksById(like.bookId).observe(viewLifecycleOwner) { libro ->
                    libriList.add(libro)
                    println(libriList)

                    val adapter = LikesAdapter(libriList)
                    adapter.setOnBookClickListener(object : LikesAdapter.OnBookClickListener {
                        override fun onBookClick(position: Int) {
                            val libro = libriList[position]
                            if (libro != null) {
                                val action =
                                    MyLikesFragmentDirections.actionMyLikesFragmentToBookInfoFragment(
                                        libro
                                    )
                                findNavController().navigate(action)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Book not found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
                    binding.recyclerViewMyLikes.adapter = adapter
                }
            }
            binding.progressBar.visibility = View.GONE
            if(likes.isEmpty()){
                binding.layoutPrincipale.visibility = View.VISIBLE
                binding.recyclerViewMyLikes.visibility = View.GONE
            }
            else{
                binding.layoutPrincipale.visibility = View.GONE
                binding.recyclerViewMyLikes.visibility = View.VISIBLE
            }
        }
    }

}