package com.example.biblioteca_nazionale.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.databinding.SearchingResultBinding
import com.example.biblioteca_nazionale.model.Book

class BookListAdapter(private val onItemClick: (Book) -> Unit) :
    RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {

    private var bookList: List<Book> = emptyList()

    fun setData(books: List<Book>) {
        bookList = books
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SearchingResultBinding.inflate(inflater, parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        holder.bind(book)
    }

    override fun getItemCount(): Int = bookList.size

    inner class BookViewHolder(private val binding: SearchingResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.book = book
            binding.root.setOnClickListener { onItemClick.invoke(book) }
            binding.executePendingBindings()
        }
    }
}
