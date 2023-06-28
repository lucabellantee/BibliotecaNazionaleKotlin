package com.example.biblioteca_nazionale.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.biblioteca_nazionale.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val channelId = "my_channel_id"
        val channelName = "My Channel"
        val title = intent?.getStringExtra("title")
        val text = intent?.getStringExtra("text")

        val sharedPreferences = context?.getSharedPreferences("notification_data", Context.MODE_PRIVATE)
        var lastNotificationId = sharedPreferences?.getInt("last_notification_id", 0) ?: 0

        val notificationId = lastNotificationId + 1

        val editor = sharedPreferences?.edit()
        editor?.putInt("last_notification_id", notificationId)
        val notificationIdsSet = sharedPreferences?.getStringSet("notification_ids", emptySet())?.toMutableSet()
        notificationIdsSet?.add(notificationId.toString())
        editor?.putStringSet("notification_ids", notificationIdsSet)
        editor?.apply()

        editor?.putString("title_$notificationId", title)
        editor?.putString("text_$notificationId", text)
        editor?.putInt("logo_$notificationId", R.drawable.ic_launcher_foreground)
        editor?.apply()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED

            val notificationManager = context?.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            //.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logo_welcome))

        val notificationManager = context?.getSystemService(NotificationManager::class.java)
        notificationManager?.notify(notificationId, notificationBuilder.build())
    }
}
