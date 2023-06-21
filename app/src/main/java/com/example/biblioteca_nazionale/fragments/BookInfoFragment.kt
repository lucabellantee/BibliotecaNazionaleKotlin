package com.example.biblioteca_nazionale.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentBookInfoBinding
import com.example.biblioteca_nazionale.model.Book
import android.location.Geocoder
import android.util.Log
import com.example.biblioteca_nazionale.model.RequestBookName
import com.example.biblioteca_nazionale.model.RequestCode
import com.example.biblioteca_nazionale.viewmodel.OPACViewModel
import java.util.Locale
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class BookInfoFragment : Fragment(R.layout.fragment_book_info) {

    lateinit var binding: FragmentBookInfoBinding
    private lateinit var toolbar: MaterialToolbar

    private val opacModel: OPACViewModel = OPACViewModel()


    private var isExpanded = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookInfoBinding.bind(view)

        fetchDataBook().start()




        opacModel.searchIdentificativoLibro("Animal Farm")

        Log.d("yolo", opacModel.getOpacLiveData().value?.briefRecords?.get(0).toString())


        val mapView: MapView = binding.mapView
        mapView.onCreate(savedInstanceState)

        val cityName = "Teramo"

        // Ottieni le coordinate geografiche corrispondenti al nome della città utilizzando il servizio di geocoding
        val geocoder = context?.let { Geocoder(it, Locale.getDefault()) }
        val addressList = geocoder?.getFromLocationName(cityName, 1)

        if (addressList != null) {
            if (addressList.isNotEmpty()) {
                mapView.getMapAsync { googleMap ->
                    // Personalizzazione e visualizzazione della mappa
                    googleMap.uiSettings.isZoomControlsEnabled = true // Abilita i controlli di zoom
                    googleMap.uiSettings.isMyLocationButtonEnabled =
                        true // Abilita il pulsante "La mia posizione"
                    googleMap.uiSettings.isScrollGesturesEnabled =
                        true // Abilita il gesto di scorrimento sulla mappa
                    googleMap.uiSettings.isRotateGesturesEnabled = true
                    googleMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true

                    googleMap.setMapStyle(context?.let {
                        MapStyleOptions.loadRawResourceStyle(
                            it,
                            R.raw.map_style
                        )
                    }) // Carica lo stile personalizzato della mappa

                    val address = addressList[0]
                    val initialLatLng = LatLng(address.latitude, address.longitude)
                    // Imposta la posizione iniziale della mappa

                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(initialLatLng, 12f)
                    googleMap.moveCamera(cameraUpdate)

                    // Aggiungi il marker e le altre personalizzazioni come desiderato
                    val markerOptions = MarkerOptions()
                        .position(initialLatLng)
                        .title(cityName)
                        .snippet("La città che non dorme mai")
                    googleMap.addMarker(markerOptions)
                    googleMap.moveCamera(cameraUpdate)

                    // Aggiungi altre personalizzazioni e funzionalità alla mappa secondo le tue esigenze

                    // Esempio: Aggiungi un'interazione al clic su un marker
                    googleMap.setOnMarkerClickListener { marker ->
                        // Gestisci l'evento del clic sul marker
                        // ...
                        // Restituisci true per indicare che l'evento è stato gestito
                        true
                    }

                    // Esempio: Aggiungi un'interazione al clic sulla mappa
                    googleMap.setOnMapClickListener { latLng ->
                        // Gestisci l'evento del clic sulla mappa
                        // ...
                    }
                }
            }

            toolbar = binding.toolbar

            toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
            toolbar.setNavigationOnClickListener {
                val action = BookInfoFragmentDirections.actionBookInfoFragmentToBookListFragment()
                findNavController().navigate(action)
            }

            binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    val action =
                        BookInfoFragmentDirections.actionBookInfoFragmentToBookListFragment(
                            focusSearchView = true
                        )
                    findNavController().navigate(action)
                }
            }


            val book = arguments?.getParcelable<Book>("book")

            book?.let {
                binding.textViewBookName.text = it.info?.title ?: ""
                binding.textViewAutore.text = it.info?.authors?.toString() ?: ""

                val description = it.info?.description
                if (description.isNullOrEmpty()) {
                    binding.textViewDescription.text = "Descrizione non disponibile"
                    binding.textMoreDescription.visibility = View.GONE
                } else
                    binding.textViewDescription.text = description

                Glide.with(requireContext())
                    .load(book.info.imageLinks?.thumbnail.toString())
                    .apply(RequestOptions().placeholder(R.drawable.baseline_book_24)) // Immagine di fallback
                    .into(binding.imageViewBook)
            }

            val spannableString = SpannableString("Leggi di più")
            spannableString.setSpan(UnderlineSpan(), 0, "Leggi di più".length, 0)
            binding.textMoreDescription.text = spannableString

            binding.textViewDescription.post {
                if (binding.textViewDescription.lineCount < 5) {
                    binding.textMoreDescription.visibility = View.GONE
                } else {
                    binding.textMoreDescription.visibility = View.VISIBLE
                    binding.textMoreDescription.setOnClickListener {
                        isExpanded = !isExpanded
                        updateDescriptionText()
                    }
                    binding.textViewDescription.maxLines = 5
                    binding.textViewDescription.ellipsize = TextUtils.TruncateAt.END
                }
            }
        }
    }
    private fun fetchDataBook(): Thread {
        return Thread {
            try {
                val url = URL("http://opac.sbn.it/opacmobilegw/search.json?any=harry+potter")
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {
                    val inputStream = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                    val request = Gson().fromJson(inputStreamReader, RequestBookName::class.java)
                    Log.d("cacca", request.toString())
                    inputStream.close()

                    fetchDataCode(request).start()
                } else {
                    // Gestisci la risposta non riuscita (es. responseCode diverso da 200)
                }
                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                // Gestisci l'eccezione
            }
        }
    }

    private fun fetchDataCode(request: RequestBookName): Thread {
        val code = request.briefRecords[0].codiceIdentificativo.replace("\\", "")
        Log.d("codice", code)
        return Thread {
            try {
                val url = URL("http://opac.sbn.it/opacmobilegw/full.json?bid=ITICCURL10093707")
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {
                    val inputStream = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                    val requestCode = Gson().fromJson(inputStreamReader, RequestCode::class.java)
                    println(requestCode.localizzazioni[0].shelfmarks[0].shelfmark)
                    Log.d("hhhhhhhhhhhhhh", requestCode.localizzazioni[0].shelfmarks[0].shelfmark)

                    binding.descrizioneText.text = requestCode.localizzazioni[0].shelfmarks[0].shelfmark

                    inputStream.close()
                } else {
                    Log.d("else", "Error: " + connection.responseMessage)
                }
                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                // Gestisci l'eccezione
            }
        }
    }



    override fun onResume() {
        super.onResume()
        val mapView: MapView = binding.mapView

        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        val mapView: MapView = binding.mapView
        mapView.onPause()
    }

    private fun updateDescriptionText() {
        val maxLines = if (isExpanded) Integer.MAX_VALUE else 5
        binding.textViewDescription.maxLines = maxLines

        var buttonText=""
        if (isExpanded){
            buttonText = "Leggi meno"
            binding.textViewDescription.ellipsize = null
        }
        else{
            buttonText = "Leggi di più"
            binding.textViewDescription.ellipsize = TextUtils.TruncateAt.END
        }
        val spannableString = SpannableString(buttonText)
        spannableString.setSpan(UnderlineSpan(), 0, buttonText.length, 0)
        binding.textMoreDescription.text = spannableString
    }
}
