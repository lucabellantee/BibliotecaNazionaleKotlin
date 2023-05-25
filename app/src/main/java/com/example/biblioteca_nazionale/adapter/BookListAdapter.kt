package com.example.biblioteca_nazionale.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.model.BooksResponse


class BookListAdapter(var data: LiveData<BooksResponse>) :
    RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {

    private lateinit var mListner: OnBookClickListener


    interface OnBookClickListener {
        fun onBookClick(position: Int)
    }


    fun setOnBookClickListener(listner: OnBookClickListener) {
        mListner = listner
    }

    class BookViewHolder(itemView: View, listner: OnBookClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.book_title)
        val desc: TextView = itemView.findViewById(R.id.book_description)
        val author: TextView = itemView.findViewById(R.id.book_author)
        val cover: ImageView = itemView.findViewById(R.id.imageViewCover)

        init {
            itemView.setOnClickListener {
                listner.onBookClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.searching_result, parent, false)
        return BookViewHolder(itemView, mListner)
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
        return (data.value?.items?.size ?: 0)
        //return data?.items?.size ?: 0
    }
}
