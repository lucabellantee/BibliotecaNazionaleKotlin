package com.example.biblioteca_nazionale.fragments

import NotificationAdapter
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentNotificationsBinding
import com.example.biblioteca_nazionale.utils.NotificationReceiver
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private var _binding: FragmentNotificationsBinding? = null
    private val model: FirebaseViewModel = FirebaseViewModel()
    private val binding get() = _binding!!

    companion object {
        var flag = true
    }

    private val notificationPermission = Manifest.permission.ACCESS_NOTIFICATION_POLICY
    private val notificationPermissionRequestCode = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkSelfPermissionCompat(notificationPermission) == PackageManager.PERMISSION_GRANTED) {
            // Permesso già concesso
            showSnackbar(getString(R.string.notification_permission_granted))
        } else {
            // Richiedi il permesso
            requestNotificationPermission()
        }

        if (flag) {
            flag = false
            model.getAllDate().thenAccept { expirationBook ->
                for (book in expirationBook) {
                    val context: Context = requireContext()
                    val intent = Intent(context, NotificationReceiver::class.java)
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

        val notificationIds = allEntries.keys.filter { it.startsWith("title_") }
            .map { it.removePrefix("title_").toInt() }.toSet()

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

    private fun requestNotificationPermission() {
        if (shouldShowRequestPermissionRationaleCompat(notificationPermission)) {
            // Fornisci una spiegazione aggiuntiva all'utente se il permesso non è stato concesso in precedenza
            showPermissionRationaleSnackbar()
        } else {
            // Richiedi il permesso direttamente
            requestPermissions(arrayOf(notificationPermission), notificationPermissionRequestCode)
        }
    }

    private fun showPermissionRationaleSnackbar() {
        Snackbar.make(binding.root, R.string.notification_access_required, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.ok_notification) {
                requestPermissions(
                    arrayOf(notificationPermission),
                    notificationPermissionRequestCode
                )
            }.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == notificationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permesso concessso dall'utente
                showSnackbar(getString(R.string.notification_permission_granted))
            } else {
                // Permesso negato dall'utente
                showSnackbar(getString(R.string.notification_permission_denied))
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun checkSelfPermissionCompat(permission: String): Int {
        return ContextCompat.checkSelfPermission(requireContext(), permission)
    }

    private fun shouldShowRequestPermissionRationaleCompat(permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)
    }
}
