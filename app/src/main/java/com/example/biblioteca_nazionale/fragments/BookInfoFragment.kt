import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentBookInfoBinding
import com.example.biblioteca_nazionale.model.Book
import java.io.Serializable

class BookInfoFragment: Fragment(R.layout.fragment_book_info) {

    lateinit var binding: FragmentBookInfoBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookInfoBinding.bind(view)

        val selectedBook = arguments?.getSerializable("selectedBook")
        // Utilizza il libro selezionato come desideri
        selectedBook?.let {
        }
    }
}
