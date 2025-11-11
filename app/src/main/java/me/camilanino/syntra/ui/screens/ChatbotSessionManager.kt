package me.camilanino.syntra.ui.screens

/* ============================================================
 * CHATBOT SESSION MANAGER — Manejo del historial de conversación
 * ============================================================ */

object ChatbotSessionManager {

    // Mapa que guarda el historial de conversación por rol
    private val conversations = mutableMapOf<String, MutableList<Pair<String, String>>>()

    // Añadir un nuevo mensaje al historial
    fun addMessage(role: String, sender: String, message: String) {
        val conversation = conversations.getOrPut(role) { mutableListOf() }
        conversation.add(sender to message)

        // 🔹 Limita el historial a los últimos 10 mensajes
        if (conversation.size > 10) {
            conversation.removeAt(0)
        }
    }

    // Obtener el historial de conversación del rol actual
    fun getConversationHistory(role: String): List<Pair<String, String>> {
        return conversations[role]?.toList() ?: emptyList()
    }

    // Limpiar el historial (por ejemplo, al cerrar sesión o reiniciar chat)
    fun clearHistory(role: String) {
        conversations.remove(role)
    }
}