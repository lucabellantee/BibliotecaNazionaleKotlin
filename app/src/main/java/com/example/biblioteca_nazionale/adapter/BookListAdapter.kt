package com.example.biblioteca_nazionale.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.model.Book

class BookListAdapter(var data: List<Book>): RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {

    class BookViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val title = row.findViewById<TextView>(R.id.book_title)
        val desc = row.findViewById<TextView>(R.id.book_description)
        val author = row.findViewById<TextView>(R.id.book_author)
        val cover = row.findViewById<ImageView>(R.id.imageViewCover)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListAdapter.BookViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.searching_result, parent, false)

        return BookViewHolder(layout)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentBook = data[position]
        holder.title.text = currentBook.title
        holder.desc.text = currentBook.description
        holder.author.text = currentBook.author

        val resourceId = holder.itemView.context.resources.getIdentifier(
            currentBook.coverImageUrl, "drawable", holder.itemView.context.packageName
        )
        Glide.with(holder.cover)
            .load(resourceId)
            .into(holder.cover)
    }


    override fun getItemCount(): Int {
        return data.size
    }
}