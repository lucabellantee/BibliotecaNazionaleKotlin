import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.model.BooksResponse
import com.example.biblioteca_nazionale.model.RequestBookName
import com.example.biblioteca_nazionale.model.RequestCode
import com.example.biblioteca_nazionale.model.RequestCodeLocation
import com.google.android.gms.fido.fido2.api.common.ResidentKeyRequirement.UnsupportedResidentKeyRequirementException
import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class RequestViewModel : ViewModel() {

    private val libraries: MutableLiveData<List<RequestCodeLocation>> = MutableLiveData()
    private lateinit var book: Book

    fun getLibraries(): MutableLiveData<List<RequestCodeLocation>> {
        return libraries
    }

    fun fetchDataBook(book: Book) {
        this.book = book
        val bookName = this.book.info?.title?.replace(" ", "+") ?: ""
        val url = URL("http://opac.sbn.it/opacmobilegw/search.json?any=$bookName")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                if (connection.responseCode == 200) {
                    val inputStream = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                    val request = Gson().fromJson(inputStreamReader, RequestBookName::class.java)
                    inputStream.close()

                    fetchDataCode(request)

                } else {
                    // Gestisci la risposta non riuscita (es. responseCode diverso da 200)
                }
                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                println(e)
                // Gestisci l'eccezione
            }
        }
    }

    /*var i = 0
                while (i < request.briefRecords.size) {
                    val record = request.briefRecords[i]
                    val formattedRecordTitolo = record.titolo.replace("\\s".toRegex(), "")
                    val formattedBookTitolo = book.info.title?.replace("\\s".toRegex(), "") ?: ""

                    val bool = formattedRecordTitolo.contains(formattedBookTitolo, ignoreCase = true)
                    if (bool) {
                        bookCode = record.codiceIdentificativo.replace("\\", "")
                        url = URL("http://opac.sbn.it/opacmobilegw/full.json?bid=$bookCode")
                        break
                    }
                    i++
                }*/

    private fun fetchDataCode(request: RequestBookName) {
        val localizzazioniList = mutableListOf<RequestCodeLocation>()
        var completedRequests = 0
        val totalRequests = request.briefRecords.size

        for (briefRecord in request.briefRecords) {
            val bookCode: String = briefRecord.codiceIdentificativo.replace("\\", "")
            val url = URL("http://opac.sbn.it/opacmobilegw/full.json?bid=$bookCode")

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val connection = url.openConnection() as HttpURLConnection
                    if (connection.responseCode == 200) {
                        val inputStream = connection.inputStream
                        val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                        val requestCode = Gson().fromJson(inputStreamReader, RequestCode::class.java)
                        inputStream.close()

                        localizzazioniList.addAll(requestCode.localizzazioni)
                    } else {
                        // Gestisci la risposta non riuscita (es. responseCode diverso da 200)
                    }
                    connection.disconnect()
                } catch (e: Exception) {
                    e.printStackTrace()
                    println(e)
                    // Gestisci l'eccezione
                } finally {
                    completedRequests++
                    if (completedRequests == totalRequests) {
                        // Aggiorna libraries nel ViewModel dopo il completamento di tutte le richieste
                        withContext(Dispatchers.Main) {
                            libraries.value = localizzazioniList
                        }
                    }
                }
            }
        }
    }
}


