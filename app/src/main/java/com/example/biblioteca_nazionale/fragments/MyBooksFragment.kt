import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookAdapter
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.model.ImageLinks
import com.example.biblioteca_nazionale.model.InfoBook
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.example.biblioteca_nazionale.model.MiniBook

class MyBooksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val firebaseViewModel: FirebaseViewModel = ViewModelProvider(requireActivity()).get(FirebaseViewModel::class.java)
        val currentUser = firebaseViewModel.getCurrentUser(firebaseViewModel.firebase.getCurrentUid().toString()).get()
        //val libriPrenotati: HashMap<String, ArrayList<String>>? = currentUser.userSettings?.libriPrenotati
        val libriPrenotati: ArrayList<MiniBook>? = currentUser.userSettings?.libriPrenotati


        val bookList: List<Book> = libriPrenotati?.map { bookData ->
            val isbn = bookData.isbn
            val titolo = bookData.bookPlace
            val linkImmagine = bookData.image
            val dataScadenza = bookData.date

            val infoBook = InfoBook(titolo, null, null, null, null, ImageLinks(null.toString(), linkImmagine))

            Book(isbn, infoBook)
        } ?: listOf()


        val view = inflater.inflate(R.layout.fragment_my_books, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMyBooks)
        adapter = BookAdapter(bookList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return view
    }
}
