package com.example.biblioteca_nazionale.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.model.Book

class BookListAdapter(private val data: List<Book>) :
    RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {

    private lateinit var mListner: OnBookClickListener

    interface OnBookClickListener {
        fun onBookClick(position: Int)
    }

    fun setOnBookClickListener(listner:OnBookClickListener){
        mListner = listner
    }

    class BookViewHolder(itemView: View,listner : OnBookClickListener) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.book_title)
        val desc: TextView = itemView.findViewById(R.id.book_description)
        val author: TextView = itemView.findViewById(R.id.book_author)
        val cover: ImageView = itemView.findViewById(R.id.imageViewCover)

        init{
            itemView.setOnClickListener {
                listner.onBookClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.searching_result, parent, false)
        return BookViewHolder(itemView,mListner)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentBook = data[position]
        holder.title.text = currentBook.codiceIdentificativo
        holder.desc.text = currentBook.isbn
        holder.author.text = currentBook.copertina

        // Carica l'immagine della copertina del libro qui, se necessario
        // Glide, Picasso o altre librerie di caricamento delle immagini possono essere utilizzate
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
