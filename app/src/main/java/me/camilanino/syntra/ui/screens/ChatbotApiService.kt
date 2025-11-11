package me.camilanino.syntra.ui.screens

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.camilanino.syntra.ui.screens.ChatbotSessionManager

/* ============================================================
 * CHATBOT API SERVICE — CONEXIÓN CON OPENAI DESDE SYNTRA
 * Versión híbrida compatible con el SessionManager
 * ============================================================ */

suspend fun getSyntraAIResponse(apiKey: String, userMessage: String, role: String): String {
    return withContext(Dispatchers.IO) {
        try {
            // Crear instancia del servicio desde OpenAIService.kt
            val service = createOpenAIServiceSyntra(apiKey)

            // Normalizar el texto del usuario
            val lower = userMessage.lowercase().trim()

            // Obtener historial de conversación (según el rol)
            val history = ChatbotSessionManager.getConversationHistory(role)

            // Prompt base según el rol
            val systemPrompt = when (role.lowercase()) {
                "usuario" -> "Eres SyntraBot, un asistente amable que ayuda a los ciudadanos a reportar fallas en semáforos, consultar el mapa y revisar sus reportes."
                "agente" -> "Eres SyntraBot, un asistente experto en apoyo al personal de tránsito. Ayudas a revisar reportes, actualizar estados y consultar estadísticas."
                else -> "Eres SyntraBot, un asistente que brinda ayuda general sobre la aplicación Syntra."
            }

            // Incluir historial previo como contexto en el prompt
            val conversationHistory = history.joinToString("\n") { (sender, message) ->
                "${if (sender == "user") "Usuario" else "SyntraBot"}: $message"
            }

            val fullPrompt = """
                $systemPrompt
                Contexto reciente de la conversación:
                $conversationHistory

                Usuario: $userMessage
                SyntraBot:
            """.trimIndent()

            // Construcción del cuerpo del mensaje (reutilizando las clases del OpenAIService)
            val request = ChatRequestAPI(
                model = "gpt-3.5-turbo",
                messages = listOf(
                    ChatMessageAPI("system", fullPrompt),
                    ChatMessageAPI("user", userMessage)
                )
            )

            // Llamada al endpoint de OpenAI
            val response = service.getChatCompletion(request)

            // Procesar respuesta
            val aiResponse = response.choices.firstOrNull()?.message?.content?.trim()
                ?: "No entendí tu mensaje 😅, ¿podrías reformularlo?"

            // Guardar la interacción en el historial
            ChatbotSessionManager.addMessage(role, "user", userMessage)
            ChatbotSessionManager.addMessage(role, "bot", aiResponse)

            // Devolver la respuesta generada
            aiResponse

        } catch (e: Exception) {
            e.printStackTrace()
            "Ocurrió un error al conectar con el servidor de SyntraBot."
        }
    }
}