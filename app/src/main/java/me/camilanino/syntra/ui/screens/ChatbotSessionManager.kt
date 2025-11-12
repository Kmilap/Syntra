package me.camilanino.syntra.ui.screens

/* ============================================================
 * CHATBOT SESSION MANAGER — Manejo del historial de conversación
 * ============================================================ */

object ChatbotSessionManager {


    private val conversations = mutableMapOf<String, MutableList<Pair<String, String>>>()

    // Añadir un nuevo mensaje al historial
    fun addMessage(role: String, sender: String, message: String) {
        val conversation = conversations.getOrPut(role) { mutableListOf() }
        conversation.add(sender to message)


        if (conversation.size > 10) {
            conversation.removeAt(0)
        }
    }

    // Obtener el historial de conversación
    fun getConversationHistory(role: String): List<Pair<String, String>> {
        return conversations[role]?.toList() ?: emptyList()
    }

    // Limpiar el historial
    fun clearHistory(role: String) {
        conversations.remove(role)
    }
}