package com.example.biblioteca_nazionale.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.NotificationAdapter
import com.example.biblioteca_nazionale.databinding.FragmentNotificationsBinding
import com.example.biblioteca_nazionale.utils.NotificationReceiver
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private var _binding: FragmentNotificationsBinding? = null
    private val model: FirebaseViewModel = FirebaseViewModel()
    private val binding get() = _binding!!

    companion object {
        var flag = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (flag) {
            flag = false
            model.getAllDate().thenAccept { expirationBook ->
                for (book in expirationBook) {
                    val context: Context = requireContext()
                    val intent = Intent(context, NotificationReceiver::class.java)
                    val uid = model.firebase.getCurrentUid()
                    intent.putExtra("uid", uid)
                    intent.putExtra("title", "Book return")
                    intent.putExtra(
                        "text",
                        "Your book ${book.isbn} taken from the library ${book.bookPlace} will expire ${book.date}"
                    )
                    context.sendBroadcast(intent)
                }
            }
        }

        val sharedPreferences =
            requireContext().getSharedPreferences("notification_data", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        val notificationIds = allEntries.keys
            .filter { it.startsWith("title_") }
            .map { it.removePrefix("title_").toInt() }
            .toSet()

        val uid = model.firebase.getCurrentUid()
        val filteredNotificationIds = notificationIds.filter { notificationId ->
            sharedPreferences.getString("uid_$notificationId", "") == uid
        }

        val notificationList = mutableListOf<Triple<Int, String, String>>()
        if (notificationIds != null) {
            for (notificationId in filteredNotificationIds) {
                val title = sharedPreferences.getString("title_$notificationId", "")
                val text = sharedPreferences.getString("text_$notificationId", "")
                val notificationInfo = Triple(notificationId, title, text)
                notificationList.add(notificationInfo as Triple<Int, String, String>)
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
