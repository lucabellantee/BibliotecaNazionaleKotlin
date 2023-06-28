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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentNotificationsBinding
import com.example.biblioteca_nazionale.utils.NotificationReceiver
import com.google.firebase.auth.FirebaseAuth

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
        //intent.putExtra("uid", firebaseAuth.currentUser?.uid)

        /*binding.button234.setOnClickListener {
            context.sendBroadcast(intent)
        }*/

        val sharedPreferences = requireContext().getSharedPreferences("notification_data", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        /*val currentUserUid = firebaseAuth.currentUser?.uid
        val notificationIds = allEntries.keys.filter {
            it.startsWith("title_") && sharedPreferences.getString("uid_$it", "") == currentUserUid
        }.map { it.removePrefix("title_").toInt() }.toSet()*/

        val notificationIds = allEntries.keys.filter { it.startsWith("title_") }.map { it.removePrefix("title_").toInt() }.toSet()

        val notificationList = mutableListOf<Pair<String, String>>()
        if (notificationIds != null) {
            for (notificationId in notificationIds) {
                val title = sharedPreferences.getString("title_$notificationId", "")
                val text = sharedPreferences.getString("text_$notificationId", "")
                val notificationInfo = Pair(title, text)
                notificationList.add(notificationInfo as Pair<String, String>)
            }
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewNotifications.layoutManager = layoutManager

        val adapter = NotificationAdapter(notificationList)
        binding.recyclerViewNotifications.adapter = adapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
