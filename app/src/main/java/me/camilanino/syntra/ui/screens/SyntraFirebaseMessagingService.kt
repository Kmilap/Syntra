package me.camilanino.syntra.ui.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import me.camilanino.syntra.R

class SyntraFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: "Nuevo reporte"
        val body = message.notification?.body ?: "Tu reporte se ha registrado exitosamente."

        // --- Guardar la notificación en el historial interno ---
        NotificationStorage.addNotification(title, body)

        // --- Configurar sonido
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // --- Crear canal de notificaciones (Android 8 o superior) ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "report_channel"
            val channelName = "Reportes Syntra"
            val channelDescription = "Notificaciones de reportes enviados"

            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDescription
                enableLights(true)
                enableVibration(true)
                setSound(
                    soundUri,
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // --- Construir la notificación del sistema  ---
        val notification = NotificationCompat.Builder(this, "report_channel")
            .setSmallIcon(R.drawable.campanita) // ícono visible en barra del sistema
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .build()

        // --- Mostrar la notificación ---
        val notificationManager = NotificationManagerCompat.from(this)
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(
                (System.currentTimeMillis() % 10000).toInt(),
                notification
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("Nuevo token FCM: $token")
    }
}