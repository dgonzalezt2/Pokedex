package com.example.pokedex.ui.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

fun showCartNotification(context: Context, title: String, message: String, iconRes: Int, notificationId: Int = 1) {
    val channelId = "cart_channel"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Carrito",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(iconRes)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    val hasPermission = if (Build.VERSION.SDK_INT >= 33) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
    if (hasPermission) {
        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Puedes mostrar un mensaje al usuario si lo deseas
        }
    } else {
        // Puedes mostrar un mensaje al usuario indicando que no tiene permiso para notificaciones
    }
}
