package com.example.biblioteca_nazionale.fragments


import RequestViewModel
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.location.Address
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
import androidx.lifecycle.lifecycleScope
import com.example.biblioteca_nazionale.model.RequestCodeLocation
import com.google.android.gms.location.LocationServices
import java.util.Locale
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyStore.TrustedCertificateEntry


class BookInfoFragment : Fragment(R.layout.fragment_book_info) {

    lateinit var binding: FragmentBookInfoBinding
    private lateinit var toolbar: MaterialToolbar

    private val modelRequest: RequestViewModel = RequestViewModel()
    val db = Firebase.firestore


    private var isExpanded = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookInfoBinding.bind(view)

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

            modelRequest.fetchDataBook(it)

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

            val mapView: MapView = binding.mapView
            mapView.onCreate(savedInstanceState)


            val cityName = "Teramo"

            val geocoder = context?.let { Geocoder(it, Locale.getDefault()) }

            val addressList = geocoder?.getFromLocationName(cityName, 1)

            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireContext())

            if (addressList != null) {
                if (addressList.isNotEmpty()) {
                    mapView.getMapAsync { googleMap ->
                        // Personalizzazione e visualizzazione della mappa
                        googleMap.uiSettings.isZoomControlsEnabled =
                            true // Abilita i controlli di zoom
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
                        val markerOptions = MarkerOptions()
                            .position(initialLatLng)
                            .title("teramo")
                            .snippet("Seleziona questa biblioteca") // Descrizione opzionale
                        googleMap.addMarker(markerOptions)

                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(initialLatLng, 12f)
                        googleMap.moveCamera(cameraUpdate)

                        val geoApiContext = GeoApiContext.Builder()
                            .apiKey("AIzaSyCtTj2ohggFHtNX2asYNXL1kj31pO8wO_Y") // Replace with your actual API key
                            .build()

                        modelRequest.getLibraries().observe(viewLifecycleOwner) { libraries ->
                            libraries?.let { libraryList ->
                                lifecycleScope.launch(Dispatchers.Main) {

                                    val librariesNames = libraryList.flatMap { library ->
                                        library.shelfmarks.mapNotNull { it.shelfmark }
                                    }

                                    withContext(Dispatchers.IO) {
                                        for (libraryName in librariesNames) {
                                            val geocodingResult =
                                                GeocodingApi.geocode(geoApiContext, libraryName)
                                                    .await()

                                            if (geocodingResult.isNotEmpty()) {
                                                val location = geocodingResult[0].geometry.location
                                                val libraryLatLng =
                                                    LatLng(location.lat, location.lng)

                                                println(libraryLatLng.latitude)

                                                // Aggiungi un marker per la biblioteca sulla mappa
                                                withContext(Dispatchers.Main) {
                                                    val markerOptions = MarkerOptions()
                                                        .position(libraryLatLng)
                                                        .title(libraryName)
                                                        .snippet("Seleziona questa biblioteca") // Descrizione opzionale
                                                    googleMap.addMarker(markerOptions)

                                                    googleMap.setOnMarkerClickListener { marker ->
                                                        binding.textViewNomeBiblioteca.text =
                                                            marker.title
                                                        true
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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

        var buttonText = ""
        if (isExpanded) {
            buttonText = "Leggi meno"
            binding.textViewDescription.ellipsize = null
        } else {
            buttonText = "Leggi di più"
            binding.textViewDescription.ellipsize = TextUtils.TruncateAt.END
        }
        val spannableString = SpannableString(buttonText)
        spannableString.setSpan(UnderlineSpan(), 0, buttonText.length, 0)
        binding.textMoreDescription.text = spannableString
    }
}

