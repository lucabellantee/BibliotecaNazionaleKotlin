package com.example.biblioteca_nazionale.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.model.Book

class BookListAdapter(var data: List<Book>): RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {

    class BookViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val title = row.findViewById<TextView>(R.id.book_title)
        val desc = row.findViewById<TextView>(R.id.book_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListAdapter.BookViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.searching_result, parent, false)

        return BookViewHolder(layout)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.title.text = data.get(position).toString()
    }

    override fun getItemCount(): Int {
        return data.size
    }
}