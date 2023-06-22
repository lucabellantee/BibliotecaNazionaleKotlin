package com.example.biblioteca_nazionale.fragments

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.NotificationCompat
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    private val CHANNEL_ID = "my_channel_id"
    val notificationId = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Nervo", "Nervo")

        /*binding.button234.setOnClickListener {
            // Creare un oggetto NotificationCompat.Builder per generare la notifica
            val textTitle: String = "Titolo"
            val textContent: String = "Contenuto"

            val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_welcome)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            Log.d("SettingsFragment", "primo")
            // Ottenere un riferimento al NotificationManager
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            Log.d("SettingsFragment", "Secondo")
            // Generare e visualizzare la notifica utilizzando il NotificationManager
            notificationManager.notify(notificationId, builder.build())
            Log.d("SettingsFragment", "Terzo")
        }*/
    }
}