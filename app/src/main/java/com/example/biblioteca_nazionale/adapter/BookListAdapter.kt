package com.example.biblioteca_nazionale.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.model.BooksResponse

class BookListAdapter(var data: LiveData<BooksResponse>): RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {

    class BookViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val title = row.findViewById<TextView>(R.id.book_title)
        val desc = row.findViewById<TextView>(R.id.book_description)
        val author = row.findViewById<TextView>(R.id.book_author)
        //val cover = row.findViewById<ImageView>(R.id.imageViewCover)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListAdapter.BookViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.searching_result, parent, false)

        return BookViewHolder(layout)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        //val currentBook = data[position]
        //val currentBook = data.value?.get(position)
        Log.d("BookListAdapter", "Valore: ${data.value?.items?.get(0)}")

        //val currentBook = data.value?.items?.get(position)
        val currentBook = data.value?.items?.get(position)
        holder.title.text = currentBook?.info?.title ?: ""
        holder.desc.text = currentBook?.info?.description ?: ""
        holder.author.text = currentBook?.info?.authors.toString()

    }


    override fun getItemCount(): Int {
        return data.value?.items?.size ?: 0
        //return data?.items?.size ?: 0
    }
}