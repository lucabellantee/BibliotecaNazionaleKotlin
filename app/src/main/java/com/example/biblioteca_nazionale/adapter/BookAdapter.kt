import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.model.MiniBook

class BookAdapter(private val books: List<MiniBook>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        println(books)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position] as? MiniBook
        println(books)
        if (book != null) {
            holder.bind(book)
        } else {
            Log.d("NIENTE", "NIENTE")
            // Gestisci il caso in cui l'elemento nella posizione specificata non sia un oggetto di tipo MiniBook
        }
    }

    override fun getItemCount(): Int {
        println(books)
        return books.size
    }

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.bookTitleItem)
        private val imageImageView: ImageView = itemView.findViewById(R.id.bookImageItem)
        private val dateTextView: TextView = itemView.findViewById(R.id.bookDate)

        fun bind(book: MiniBook) {
            titleTextView.text = book.isbn
            dateTextView.text = book.date
            Glide.with(itemView)
                .load(book.image)
                .apply(RequestOptions().centerCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageImageView)
        }
    }
}
