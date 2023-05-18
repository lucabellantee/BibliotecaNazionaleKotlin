package com.example.biblioteca_nazionale.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.HomePageBinding
import com.example.biblioteca_nazionale.fragments.BookListFragment
import com.example.biblioteca_nazionale.interface_.ApiService
import com.example.biblioteca_nazionale.model.BookResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder

const val BASE_URL = "http://opac.sbn.it/opacmobilegw/"

class HomePageActivity : AppCompatActivity() {

    lateinit var binding: HomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        binding = HomePageBinding.inflate(layoutInflater)

        val fragmentManager: FragmentManager = supportFragmentManager
        val bookListFrag = BookListFragment()

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, bookListFrag) //Quì serve la recyclerView
        fragmentTransaction.commit()
    }

    private fun getData(){
        val retrofitBld = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiService::class.java)

        val retrofitDati = retrofitBld.getBookISBN()

        retrofitDati.enqueue(object: Callback<List<BookResponse>?>) {
            fun onResponse(call: Call<List<BookResponse>>, response: Response<List<BookResponse>?>) {
                val responseBody = response.body()!!

                val stringBuilder = StringBuilder()
                for (data in responseBody) {
                    stringBuilder.append(data.codiceIdentificativo) //li seleziono io, quindi posso prendere solo quelli che mi interessano
                    stringBuilder.append("\n")          //tra quelli di BookResponse

                    testo = findViewById<TextView>(R.id.qualcosa)
                    testo.text = stringBuilder
                }
            }

            fun onFailure(call: Call<List<BookResponse>?>, t: Throwable) {
                Log.d("HomePageActivity", "onFailure: "+t.message)
            }
        }
    }

}
