package com.example.biblioteca_nazionale.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentNotificationsBinding
import com.example.biblioteca_nazionale.databinding.FragmentSettingsBinding
import com.example.biblioteca_nazionale.utils.NotificationReceiver

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context: Context = requireContext()
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("title", "Titolo della notifica")
        intent.putExtra("text", "Testo della notifica")

        binding.button234.setOnClickListener {
            context.sendBroadcast(intent)
        }

        Log.d("Nervo", "Nervo")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
