package com.example.biblioteca_nazionale.fragments

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.NotificationCompat
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.activity.HomePageActivity
import com.example.biblioteca_nazionale.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    //val notify: NotificationReceiver = NotificationReceiver()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val context: Context = requireContext()
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("title", "Titolo della notifica")
        intent.putExtra("text", "Testo della notifica")

        binding.button234.setOnClickListener {
            context.sendBroadcast(intent)
        }

        Log.d("Nervo", "Nervo")*/

    }
}