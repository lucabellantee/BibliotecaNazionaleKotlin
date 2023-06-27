package com.example.biblioteca_nazionale.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biblioteca_nazionale.firebase.FirebaseDB
import com.example.biblioteca_nazionale.model.BookFirebase
import com.example.biblioteca_nazionale.model.UserSettings
import com.example.biblioteca_nazionale.model.Users
import com.google.firebase.firestore.DocumentSnapshot
import java.util.concurrent.CompletableFuture
import com.example.biblioteca_nazionale.model.MiniBook
import com.example.biblioteca_nazionale.model.Review
import java.text.SimpleDateFormat
import java.util.Calendar


class FirebaseViewModel: ViewModel() {

    val firebase = FirebaseDB()
    /*
    fun getUserInfo(): MutableLiveData<List<DocumentSnapshot>> {
        return firebase.getAllUserInfo()
    } */

    private fun convertHashMapToMiniBook(hashMap: HashMap<*, *>): MiniBook {
        val isbn = hashMap["isbn"] as? String ?: ""
        val bookPlace = hashMap["bookPlace"] as? String ?: ""
        val image = hashMap["image"] as? String ?: ""
        val date = hashMap["date"] as? String ?: ""

        return MiniBook(isbn, bookPlace, image, date)
    }

    private fun convertHashMapToReview(hashMap: HashMap<*, *>): Review {
        val idComment = hashMap["idComment"] as? String ?: ""
        val reviewText = hashMap["reviewText"] as? String ?: ""
        val reviewTitle = hashMap["reviewTitle"] as? String ?: ""
        val isbn = hashMap["isbn"] as? String ?: ""

        val vote = hashMap["vote"].toString().toFloat()
        val date = hashMap["date"] as? String ?: ""

        return Review(idComment, reviewText, reviewTitle, isbn, vote, date)
    }


    fun getUserInfo(uid: String): MutableLiveData<DocumentSnapshot>{
      return firebase.getAllUserInfoFromUid(uid)
    }

    fun getAllDocument(): MutableLiveData<ArrayList<DocumentSnapshot>>{
        return firebase.getAllUserFromDB()
    }


 /*   fun saveNewUser(uid: String, email: String){
        firebase.saveNewUser(Users(uid,email))
    }*/

    fun getEmailLoggedUser(): String = firebase.getCurrentEmail().toString()

    fun getUidLoggedUser(): String = firebase.getCurrentUid().toString()

    fun getCurrentUser(uid: String): CompletableFuture<Users> {
        val futureResult = CompletableFuture<Users>()

        this.getUserInfo(getUidLoggedUser()).observeForever { documentSnapshot ->
            val data = documentSnapshot
            val impostazioniData = data?.get("userSettings") as? HashMap<*, *>
            val libriPrenotatiData = impostazioniData?.get("libriPrenotati") as? ArrayList<HashMap<*, *>>
            val commentiData = impostazioniData?.get("commenti") as? ArrayList<HashMap<*, *>>
            val uid = data?.get("uid") as? String
            val email = data?.get("email") as? String

            if (uid != null && email != null) {
                val libriPrenotati = libriPrenotatiData?.map { convertHashMapToMiniBook(it) } as ArrayList<MiniBook>?
                val commenti = commentiData?.map { convertHashMapToReview(it) } as ArrayList<Review>?

                val userSettings = commenti?.let { UserSettings(libriPrenotati, it) }
                val users = Users(uid, email, userSettings)
                futureResult.complete(users)
            } else {
                futureResult.completeExceptionally(Exception("Dati mancanti o nulli"))
            }
        }
        return futureResult
    }



    fun getAllUser(): LiveData<ArrayList<Users>> {
        val allUserLiveData = MutableLiveData<ArrayList<Users>>()

        this.getAllDocument().observeForever { allDocument ->
            val allUser = ArrayList<Users>()
            for (document in allDocument) {
                val impostazioniData = document?.get("userSettings") as? HashMap<*, *>
                val libriPrenotatiData = impostazioniData?.get("libriPrenotati") as? ArrayList<HashMap<*, *>>
                val commentiData = impostazioniData?.get("commenti") as? ArrayList<HashMap<*, *>>
                val uid = document?.get("uid") as? String
                val email = document?.get("email") as? String

                val libriPrenotati = libriPrenotatiData?.map { convertHashMapToMiniBook(it) } as ArrayList<MiniBook>?
                val commenti = commentiData?.map { convertHashMapToReview(it) } as ArrayList<Review>?

                val userSettings = commenti?.let { UserSettings(libriPrenotati, it) }
                val tmpUser = Users(uid.toString(), email.toString(), userSettings)
                allUser.add(tmpUser)
            }
            allUserLiveData.value = allUser
        }
        return allUserLiveData
    }

    fun getUserByCommentsOfBooks(isbn: String): LiveData<ArrayList<Users>> {
        val allUserLiveData = MutableLiveData<ArrayList<Users>>()

        this.getAllDocument().observeForever { allDocument ->
            val allUser = ArrayList<Users>()

            for (document in allDocument) {
                val uid = document?.get("uid") as? String
                val email = document?.get("email") as? String
                val impostazioniData = document?.get("userSettings") as? HashMap<*, *>
                val commentiData = impostazioniData?.get("commenti") as? ArrayList<HashMap<*, *>>

                val commenti = commentiData?.mapNotNull { convertHashMapToReview(it) } as ArrayList<Review>?

                commenti?.let {
                    val filteredComments = it.filter { comment -> comment.isbn == isbn }

                    if (filteredComments.isNotEmpty()) {
                        val libriPrenotatiData = impostazioniData?.get("libriPrenotati") as? ArrayList<HashMap<*, *>>
                        val libriPrenotati = libriPrenotatiData?.map { convertHashMapToMiniBook(it) } as ArrayList<MiniBook>?

                        val userSettings = UserSettings(libriPrenotati,
                            filteredComments as ArrayList<Review>
                        )
                        val tmpUser = Users(uid.toString(), email.toString(), userSettings)
                        allUser.add(tmpUser)
                    }
                }
            }
            allUserLiveData.value = allUser
        }

        return allUserLiveData
    }


    fun getAllCommentsByIsbn(isbn: String): LiveData<ArrayList<Review>> {
        val allCommentsLiveData = MutableLiveData<ArrayList<Review>>()

        this.getAllDocument().observeForever { allDocument ->
            val allComments = ArrayList<Review>()
            for (document in allDocument) {
                val impostazioniData = document?.get("userSettings") as? HashMap<*, *>
                val commentiData = impostazioniData?.get("commenti") as? ArrayList<HashMap<*, *>>

                val commenti = commentiData?.map { convertHashMapToReview(it) } as ArrayList<Review>?

                // Aggiungi i commenti che hanno il valore di ISBN desiderato
                commenti?.let {
                    val filteredComments = it.filter { comment -> comment.isbn == isbn }
                    allComments.addAll(filteredComments)
                }
            }
            allCommentsLiveData.value = allComments
        }
        return allCommentsLiveData
    }


    fun getBookInfoResponseFromDB(idLibro: String): MutableLiveData<DocumentSnapshot>{
        return firebase.getAllBookInfoFromId(idLibro)
    }

    fun getBookInfo(idLibro: String): BookFirebase{
        var allData = this.getBookInfoResponseFromDB("ID_LIBRO")
        val data = allData.value?.data
        val allComment = data?.get("Commenti") as? HashMap<String, HashMap<String,HashMap<String,String>>>
       // Log.d("/FirebaseViewModel", allComment.toString())
       // val libriPrenotatiData = impostazioniData?.get("libriPrenotati") as? HashMap<String, ArrayList<String>>
        //val comment = allComment?.get("uid utente") as? HashMap<Any,Any>
        //Log.d("/FirebaseViewModel", comment.toString())
        val allRankingStar = data?.get("Stelle recensioni") as? HashMap<String,String>
       // Log.d("/FirebaseViewModel", allRankingStar.toString())

        return BookFirebase(allComment!!, allRankingStar!!)
    }



    fun addNewBookBooked(idLibro: String, isbn: String, placeBooked: String, image: String){
        val uid = firebase.getCurrentUid()
        Log.d("UID: ", firebase.getCurrentUid().toString())
        Log.d("STRINGAA3", idLibro+""+isbn)
        val currentUser = this.getCurrentUser(uid.toString())
        currentUser.thenAccept { utente ->
            Log.d("ISBN:  ", isbn )
            Log.d("IdLibro:  " , idLibro)
            utente.userSettings?.addNewBook(idLibro, isbn, placeBooked, image)
            //Log.d("DOPO" , idLibro + " " + isbn + " " + placeBooked + " " + image)
            //Log.d("USER", utente.toString())
           // Log.d("USERRR", user.email)
            //Log.d("UIDDD", user.UID)
            Log.d("USER", utente.toString())
            firebase.updateBookPrenoted(utente)
            println(utente.userSettings?.libriPrenotati?.get(utente.userSettings?.libriPrenotati!!.size-1))
        }.exceptionally { throwable ->
            // Gestione di eventuali errori nel recupero dell'utente
            Log.e("/FirebaseViewModel", "Errore nel recupero dell'utente: ${throwable.message}")
            null
        }
    }

    /*
    fun newExpirationDate(id: String) {
        var expirationDate: String? = null

        firebase.getExpirationDate(id) { dataScadenza ->
            if (dataScadenza != null) {
                Log.d("DATAAA", dataScadenza)
                Log.d("DATAAA1", expirationDate.toString())
                expirationDate = dataScadenza
            } else {
                println("Libro non trovato o errore durante il recupero della data di scadenza.")
            }
        }
        if (expirationDate != null) {
            Log.d("DATAAA12", expirationDate.toString())
            println("Data di scadenza: $expirationDate")
        }
    } */


    fun getExpirationDate(idBook: String, place: String): CompletableFuture<String> {
        val futureExpiringDate = CompletableFuture<String>()
        val firebase = FirebaseDB()
        this.getCurrentUser(firebase.getFirebaseAuthIstance().uid.toString()).thenAccept { user ->
            var foundMatchingBook = false
            Log.d("idBook ", idBook)
            Log.d("place ", place)
            for (libroPrenotato in user.userSettings?.libriPrenotati!!) {
                Log.d("idBook-user " , libroPrenotato.isbn)
                Log.d("place-user ", libroPrenotato.bookPlace)
                // todo LUCA: Vedere perchè non entra nel if
                if (libroPrenotato.isbn.equals(idBook) && libroPrenotato.bookPlace.equals(place)) {
                    val inputFormat = SimpleDateFormat("yyyy/MM/dd")
                    val outputFormat = SimpleDateFormat("dd/MM/yyyy")

                    val calendar = Calendar.getInstance()
                    val date = inputFormat.parse(libroPrenotato.date)
                    calendar.time = date

                    calendar.add(Calendar.DAY_OF_MONTH, 14)
                    val expiringDate = outputFormat.format(calendar.time)

                    Log.d("GetExpirationDate", expiringDate)
                    futureExpiringDate.complete(expiringDate)

                    foundMatchingBook = true
                    break
                }
            }

            if (!foundMatchingBook) {
                futureExpiringDate.complete("ERROR")
            }
        }
        return futureExpiringDate
    }
// TRUE -> Libro gia presente e prenotato   ||||||    FALSE -> Libro non presente e non prenotato

    fun bookIsBooked(id: String, place: String): CompletableFuture<Boolean> {
        val uid = getUidLoggedUser()
        val currentUser = this.getCurrentUser(uid)

        val futureIsBooked = CompletableFuture<Boolean>()

        currentUser.thenAccept { user ->
            var isBooked = false
            for (libro in user.userSettings?.libriPrenotati!!) {
                if (libro.isbn == id && libro.bookPlace == place) {
                    isBooked = true
                    break
                }
            }
            futureIsBooked.complete(isBooked)
        }.exceptionally { throwable ->
            // Gestione di eventuali errori nel recupero dell'utente
            Log.e("/FirebaseViewModel", "Errore nel recupero dell'utente: ${throwable.message}")
            futureIsBooked.complete(false)
            null
        }

        return futureIsBooked
    }




    fun removeBookBooked(idLibro: String){

        val uid = getUidLoggedUser() // TODO METTERE: firebase.getCurrentUid()
        val currentUser = this.getCurrentUser(uid)
        currentUser.thenAccept { user ->
            user.userSettings?.removeBook(idLibro)
            firebase.updateBookPrenoted(user)
        }.exceptionally { throwable ->
            // Gestione di eventuali errori nel recupero dell'utente
           Log.e("/FirebaseViewModel", "Errore nel recupero dell'utente: ${throwable.message}")
            null
        }

    }


    fun addNewCommentUserSide(reviewText: String,reviewTitle: String,isbn: String,vote:Float){
        val currentUser = this.getCurrentUser(getUidLoggedUser())  // TODO METTERE: firebase.getCurrentUid()
        currentUser.thenAccept { user ->
            user.userSettings?.addNewComment(reviewText,reviewTitle,isbn,vote)
            firebase.addCommentUserSide(user)
        }.exceptionally { throwable ->
            // Gestione di eventuali errori nel recupero dell'utente
            Log.e("/FirebaseViewModel", "Errore nel recupero dell'utente: ${throwable.message}")
            null
        }

    }

    fun removeCommentUserSide(idComment: String, currentUser: Users){
        val currentUser = this.getCurrentUser(getUidLoggedUser())  // TODO METTERE: firebase.getCurrentUid()
        currentUser.thenAccept { user ->
            user.userSettings?.removeComment(idComment)
            firebase.removeCommentUserSide(user)
        }.exceptionally { throwable ->
            // Gestione di eventuali errori nel recupero dell'utente
            Log.e("/FirebaseViewModel", "Errore nel recupero dell'utente: ${throwable.message}")
            null
        }

    }

}

