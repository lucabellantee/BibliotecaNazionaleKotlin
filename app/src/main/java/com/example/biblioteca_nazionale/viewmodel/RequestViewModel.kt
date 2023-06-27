import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblioteca_nazionale.cache.LibrariesCache
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.model.RequestBookName
import com.example.biblioteca_nazionale.model.RequestCode
import com.example.biblioteca_nazionale.model.RequestCodeLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class RequestViewModel : ViewModel() {

    private val libraries: MutableLiveData<List<RequestCodeLocation>?> = MutableLiveData()
    private lateinit var book: Book

    fun getLibraries(): MutableLiveData<List<RequestCodeLocation>?> {
        return libraries
    }

    fun fetchDataBook(book: Book) {

        this.book = book
        val bookName = this.book.info?.title?.replace(" ", "+") ?: ""
        val cacheKey = LibrariesCache.getCacheKey(bookName)
        val cachedResult = LibrariesCache.getResult(cacheKey)

        if (cachedResult != null) {
            libraries.postValue(cachedResult)
        } else {

            val retrofit = Retrofit.Builder()
                .baseUrl("http://opac.sbn.it/opacmobilegw/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val request = apiService.searchBooks(bookName)
                    fetchDataCode(request, cacheKey)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Gestisci l'eccezione
                }
            }
        }
    }

    private suspend fun fetchDataCode(request: RequestBookName, cacheKey: String) {
        val localizzazioniList = mutableListOf<RequestCodeLocation>()
        val formattedBookTitolo = book.info?.title?.replace("\\s".toRegex(), "") ?: ""

        request.briefRecords.forEach { briefRecord ->
            val record = briefRecord
            val formattedRecordTitolo = record.titolo.replace("\\s".toRegex(), "")
            val bool = formattedRecordTitolo.contains(formattedBookTitolo, ignoreCase = true)

            if (bool) {
                val bookCode = record.codiceIdentificativo.replace("\\", "")

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://opac.sbn.it/opacmobilegw/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(ApiService::class.java)

                try {
                    val requestCode = apiService.getBookDetails(bookCode)
                    localizzazioniList.addAll(requestCode.localizzazioni)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Gestisci l'eccezione
                }
            }
        }

        libraries.postValue(localizzazioniList)
        LibrariesCache.putResult(cacheKey, localizzazioniList)
    }

    interface ApiService {
        @GET("search.json")
        suspend fun searchBooks(@Query("any") bookName: String): RequestBookName

        @GET("full.json")
        suspend fun getBookDetails(@Query("bid") bookCode: String): RequestCode
    }
}
