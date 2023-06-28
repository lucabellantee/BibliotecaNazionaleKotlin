package com.example.biblioteca_nazionale.fragments

import NotificationAdapter
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
import com.example.biblioteca_nazionale.utils.NotificationReceiver

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

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

        // Recupera le informazioni delle notifiche dalle SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("notification_data", Context.MODE_PRIVATE)
        println(sharedPreferences)
        // Recupera tutte le chiavi e i valori presenti nelle SharedPreferences
        val allEntries = sharedPreferences.all

        // Stampa le chiavi e i valori delle SharedPreferences
        for ((key, value) in allEntries) {
            println(key +" "+value)
        }
        //val notificationIds = sharedPreferences.getStringSet("notification_ids", emptySet())
        val notificationIds = allEntries.keys.filter { it.startsWith("title_") }.map { it.removePrefix("title_").toInt() }.toSet()
        println(notificationIds)

        // Recupera le informazioni delle notifiche corrispondenti agli id
        val notificationList = mutableListOf<Pair<String, String>>()
        if (notificationIds != null) {
            println(notificationIds)
            for (notificationId in notificationIds) {
                println(notificationId)
                val title = sharedPreferences.getString("title_$notificationId", "")
                val text = sharedPreferences.getString("text_$notificationId", "")
                println(title+" "+text)
                val notificationInfo = Pair(title, text)
                println(notificationInfo)
                notificationList.add(notificationInfo as Pair<String, String>)
                println(notificationList)
            }
        }

        // Imposta l'adapter per la RecyclerView
        println(notificationList)
        val adapter = NotificationAdapter(requireContext(), notificationList)
        binding.recyclerViewNotifications.adapter = adapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
