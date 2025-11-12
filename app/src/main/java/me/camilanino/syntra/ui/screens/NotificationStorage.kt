package me.camilanino.syntra.ui.screens

import androidx.compose.runtime.mutableStateListOf

// Modelo de datos
data class NotificationData(
    val title: String,
    val message: String,
    val timestamp: String
)

object NotificationStorage {

    private val notifications = mutableStateListOf<NotificationData>()

    // Agregar una nueva notificación
    fun addNotification(title: String, message: String) {
        val timestamp = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(java.util.Date())
        notifications.add(
            NotificationData(
                title = title,
                message = message,
                timestamp = timestamp
            )
        )
    }

    // Obtener todas las notificaciones almacenadas
    fun getAllNotifications(): List<NotificationData> {
        return notifications
    }

    // Borrar todas las notificaciones almacenadas
    fun clearAll() {
        notifications.clear()
    }
}