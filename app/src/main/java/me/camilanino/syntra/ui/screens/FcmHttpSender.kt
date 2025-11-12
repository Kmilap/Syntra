package me.camilanino.syntra.ui.screens

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object FcmHttpSender {

    // URL oficial para enviar notificaciones FCM v1
    private const val FCM_URL = "https://fcm.googleapis.com/v1/projects/loginproyecto-69207/messages:send"

    // --- ENVÍO DE NOTIFICACIÓN REAL ---
    fun sendNotification(accessToken: String, deviceToken: String) {
        try {
            Log.i("FCM_HTTP", "📤 Iniciando envío de notificación FCM...")

            val client = OkHttpClient()

            // Estructura del cuerpo JSON (mensaje que llega al dispositivo)
            val jsonMessage = JSONObject().apply {
                put("message", JSONObject().apply {
                    put("token", deviceToken)
                    put("notification", JSONObject().apply {
                        put("title", "Syntra Notificación")
                        put("body", "Prueba exitosa desde tu app 🚦")
                    })
                    put("data", JSONObject().apply {
                        put("extraInfo", "Mensaje interno de prueba")
                    })
                })
            }

            val requestBody = jsonMessage.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

            // Petición HTTP hacia Firebase Cloud Messaging
            val request = Request.Builder()
                .url(FCM_URL)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                Log.i("FCM_HTTP", "✅ Notificación enviada correctamente: ${response.body?.string()}")
            } else {
                Log.e("FCM_HTTP", "❌ Error al enviar notificación: ${response.code} - ${response.body?.string()}")
            }

        } catch (e: Exception) {
            Log.e("FCM_HTTP", "🚨 Excepción al enviar la notificación: ", e)
        }
    }
}