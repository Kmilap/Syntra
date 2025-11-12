package me.camilanino.syntra.ui.screens

import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import java.io.InputStream

object GenerateAccessToken {

    fun getAccessToken(context: Context): String? {
        return try {
            Log.i("TOKEN", "🔑 Iniciando proceso de autenticación...")

            // Intentar abrir el archivo JSON desde assets
            val inputStream: InputStream = context.assets.open("service-account.json")
            Log.i("TOKEN", "📁 Archivo service-account.json encontrado correctamente.")

            // Cargar credenciales desde el archivo JSON
            val credentials = GoogleCredentials
                .fromStream(inputStream)
                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))

            Log.i("TOKEN", "⚙️ Credenciales cargadas, refrescando token...")
            credentials.refreshIfExpired()

            val token = credentials.accessToken.tokenValue
            Log.i("TOKEN", "✅ TOKEN TEMPORAL GENERADO CON ÉXITO → $token")

            token
        } catch (e: Exception) {
            Log.e("TOKEN", "❌ Error detallado: ${e.message}", e)
            null
        }
    }
}