package me.camilanino.syntra.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/* ==============================================================================
 * CHATBOT BRAIN
 * Gestiona la lógica local, pasos guiados y decide cuándo usar la IA
 * ============================================================================== */

object ChatbotBrain {

    // ===  1. MENSAJE DE BIENVENIDA ===
    fun getWelcomeMessage(role: String): ChatMessage {
        return if (role == "usuario") {
            ChatMessage(
                text = "👋 ¡Hola! Soy SyntraBot. Puedo ayudarte a **reportar fallas**, **consultar el mapa**, **ver tu historial** o dejar un **feedback**.",
                isUser = false
            )
        } else {
            ChatMessage(
                text = "👮 ¡Hola, agente! Soy SyntraBot. Puedo asistirte en **revisar reportes**, **actualizar estados** o consultar **estadísticas**.",
                isUser = false
            )
        }
    }

    // === PROCESADOR PRINCIPAL DEL MENSAJE ===
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    suspend fun processMessage(
        userText: String,
        role: String,
        apiKey: String
    ): ChatMessage = withContext(Dispatchers.IO) {

        val lower = userText.lowercase().trim()


        ChatbotSessionManager.addMessage(role, "user", lower)


        when {
            // === 2.1 Respuestas locales inmediatas ===
            listOf(
                "hola", "buenas", "buenos dias", "buenas tardes", "buenas noches",
                "hey", "holi", "holaaa", "que tal", "saludo", "saludos",
                "hi", "hello", "alo", "qué más", "q mas", "buen dia"
            ).any { it in lower } -> {
                return@withContext ChatMessage("¡Hola! ¿Cómo puedo ayudarte hoy? 😊", false)
            }

            listOf(
                "gracias", "muchas gracias", "mil gracias", "ok", "listo", "perfecto",
                "entendido", "dale", "vale", "genial", "super", "bien", "de acuerdo"
            ).any { it in lower } -> {
                return@withContext ChatMessage("¡Con gusto! Si necesitas más ayuda, solo escríbeme.", false)
            }

            // === 2.2 Módulo de reportes ===

            listOf(

                "hacer un reporte", "crear reporte", "crear un reporte", "reportar",
                "nueva falla", "reporte nuevo", "quiero hacer un reporte", "necesito reportar",
                "como hago un reporte", "cómo hago un reporte", "como reportar", "cómo reportar",
                "reportar semaforo", "reportar semáforo", "crear incidencia", "agregar reporte",
                "reportar una falla", "levantar reporte", "registrar reporte",
                "cargar un reporte", "abrir reporte", "diligenciar reporte",
                "denunciar semaforo", "denunciar semáforo", "iniciar reporte",
                "generar reporte", "hacer reporte de semaforo", "hacer reporte de semáforo",
                "nuevo reporte de falla", "quiero reportar una falla", "necesito crear un reporte",
                "donde reporto", "dónde reporto"
            ).any { it in lower } -> {
                return@withContext ChatMessage(
                    text = """
            📋 **Paso a paso para crear un reporte en Syntra:**
            
            1️⃣ Pulsa **"Crear reporte"** para abrir la pantalla de reportes.  
            2️⃣ Toca la barra de **ubicación** y selecciona el punto exacto en el mapa.  
            3️⃣ Elige el **estado** (Operativo / Inspección / Falla crítica).  
            4️⃣ Describe la falla y, si puedes, **adjunta una foto**.  
            5️⃣ Toca **"Reportar"** para enviarlo.
            
            Si luego quieres consultar tus reportes pasados, abre el **Historial**.
        """.trimIndent(),
                    isUser = false,
                    buttons = listOf(
                        ChatButton("📍 Reportar falla", "report_screen/$role?fromMenu=false&fromMap=false&fromChatbot=true"),
                        ChatButton("🕓 Ver historial", "history_screen/$role?fromMenu=false&fromMap=false&fromChatbot=true")



                    )
                )
            }


            // === 2.3 Módulo de historial===
            listOf(

                "ver mis reportes", "mis reportes", "historial", "ver historial", "consultar reportes",
                "ver antiguos reportes", "revisar mis reportes", "mis incidencias", "revisar historial",
                "historial de reportes", "mis registros", "ver mis incidencias", "consultar historial",
                "abrir historial", "listar mis reportes", "lista de reportes",
                "donde veo mis reportes", "donde ver mis reportes", "dónde veo mis reportes", "dónde ver mis reportes",
                "ver reportes anteriores", "ver reportes pasados", "ver lo que reporte", "lo que reporté",
                "mis casos", "reportes que hice", "consultar mis casos", "reporte realizado",
                "historial personal", "historial propio", "abrir mis reportes", "ver mis tickets"
            ).any { it in lower } -> {
                return@withContext ChatMessage(
                    text = "Aquí puedes consultar todos tus reportes registrados. 🕓",
                    isUser = false,
                    buttons = listOf(
                        ChatButton("🕓 Abrir historial", "history_screen/$role?fromMenu=false&fromMap=false&fromChatbot=true")

                    )
                )
            }





            // === 2.5 Módulo de mapa ===
            listOf(

                "mapa", "ver mapa", "abrir mapa", "ver ubicacion", "ver ubicación",
                "mostrar mapa", "ubicar", "ver punto", "ver marcadores", "ver reportes en mapa",
                "mapa de reportes", "mapa interactivo", "abrir el mapa", "abrir el mapa de reportes",
                "donde esta", "dónde está", "ver en el mapa", "ver ubicaciones", "mostrar ubicaciones",
                "mapita", "map", "map view", "map screen", "localizar", "localizacion", "localización",
                "ver coordenadas", "coordenadas", "ver sitio", "abrir geografia", "abrir geografía"
            ).any { it in lower } -> {
                return@withContext ChatMessage(
                    text = "Abre el mapa interactivo para ver reportes por ubicación. 🌍",
                    isUser = false,
                    buttons = listOf(
                        ChatButton("🗺️ Abrir mapa", "mapa_screen/$role?fromMenu=false&fromChatbot=true")
                    )
                )
            }
            /// === 2.6 Módulo de feedback===
            listOf(

                "feedback", "dejar feedback", "sugerencia", "sugerencias", "comentario", "comentarios",
                "quiero opinar", "opinion", "opinión", "dar feedback", "dejar comentario",
                "dejar sugerencia", "dar sugerencia", "enviar feedback", "enviar comentario",
                "escribir feedback", "escribir comentario", "retroalimentacion", "retroalimentación",
                "caja de sugerencias", "buzon", "buzón", "feedback usuario", "opiniones",
                "quiero sugerir", "recomendacion", "recomendación", "sugerir", "queja", "reclamo"
            ).any { it in lower } -> {
                if (role == "usuario") {
                    return@withContext ChatMessage(
                        text = "Puedes dejar un comentario o sugerencia en la sección de feedback. 💬",
                        isUser = false,
                        buttons = listOf(
                            ChatButton(
                                label = "📝 Abrir feedback",
                                destination = "feedback_screen/usuario?fromMenu=false&fromChatbot=true"
                            )
                        )
                    )
                } else {
                    return@withContext ChatMessage(
                        text = "El módulo de feedback está reservado para los ciudadanos. 🚫",
                        isUser = false
                    )
                }
            }

            // === 2.7 Módulo de estadísticas  ===
            listOf(
                // 30+ variantes
                "estadisticas", "estadísticas", "ver estadísticas", "ver estadisticas",
                "datos", "resumen", "panel", "panel de datos", "metricas", "métricas",
                "analitica", "analítica", "graficos", "gráficos", "kpis", "indicadores",
                "reportes agregados", "estadistica general", "estadística general",
                "informe", "informe actual", "datos actualizados", "consolidado",
                "tablero", "dashboard", "panel estadistico", "panel estadístico",
                "ver cifras", "ver datos", "resumen de reportes"
            ).any { it in lower } -> {
                if (role == "usuario") {
                    return@withContext ChatMessage(
                        text = "Las estadísticas están disponibles solo para el personal de tránsito. 🚫",
                        isUser = false
                    )
                } else {
                    return@withContext ChatMessage(
                        text = "Accede al panel de estadísticas para ver los datos más recientes. 📊",
                        isUser = false,
                        buttons = listOf(
                            ChatButton(
                                label = "📈 Ver estadísticas",
                                destination = "estadisticas_screen/agente?fromChatbot=true"

                            )
                        )
                    )
                }
            }
            // === 2.8. Perfil ===
            listOf(
                "perfil", "mi cuenta", "cerrar sesión", "datos personales",
                "datos de usuarios", "Datos de usuario", "configuración", "Configuracion",
                "mi perfil", "editar perfil", "cambiar mi nombre"
            ).any { it in lower } -> {
                return@withContext ChatMessage(
                    text = "👤 Desde tu perfil puedes revisar tus datos o cerrar sesión.",
                    isUser = false,
                    buttons = listOf(
                        ChatButton(
                            label = "👤 Ir al perfil",
                            destination = if (role == "usuario")
                                "profile_user?fromMenu=false&fromChatbot=true"
                            else
                                "profile_transito?fromMenu=false&fromChatbot=true"

                        )
                    )
                )
            }

            // === 2.9 Aprendizaje guiado===
            listOf("primera vez", "cómo usar", "ayuda", "tutorial", "no sé usar").any { it in lower } -> {
                return@withContext ChatMessage(
                    text = """
            💡 Bienvenido a Syntra.
            Te explico rápidamente cómo moverte en la app:
            - Usa **Reportar falla** para informar un semáforo dañado.
            - **Mapa** te muestra los puntos activos.
            - En **Historial** puedes ver tus reportes previos.
            - Y en **Feedback** puedes dejar tus sugerencias.

            ¿Por cuál te gustaría empezar?
        """.trimIndent(),
                    isUser = false,
                    buttons = listOf(
                        ChatButton(
                            label = "📍 Crear reporte",
                            destination = "report_screen/$role?fromMenu=true&fromChatbot=true"
                        ),
                        ChatButton(
                            label = "🗺️ Ver mapa",
                            destination = "mapa_screen/$role?fromMenu=true&fromChatbot=true"
                        ),
                        ChatButton(
                            label = "🕓 Ver historial",
                            destination = "history_screen/$role?fromMenu=true&fromChatbot=true"
                        )

                    )
                )
            }
        }

        // === 2.10. Si no hay coincidencia local → IA ===

        return@withContext try {
            val previousContext = ChatbotSessionManager.getConversationHistory(role)
            val enhancedPrompt = buildString {
                appendLine("Contexto de la app: Syntra es un proyecto universitario para reportes de semáforos.")
                appendLine("Pantallas clave: Reportes, Historial, Mapa, Perfil, Feedback (solo usuario), Estadísticas (solo tránsito).")
                appendLine("Reglas: Usuario y Agente pueden crear/ver reportes. Usuario NO edita/borra/actualiza estado; Tránsito sí.")
                appendLine("Guía de estilo: respuestas cortas, claras, accionables y en español neutro.")
                appendLine()
                if (previousContext.isNotEmpty()) {
                    appendLine("Historial reciente:")
                    previousContext.takeLast(10).forEach { appendLine("- $it") }
                    appendLine()
                }
                appendLine("Usuario dice: $userText")
                appendLine("Responde como SyntraBot.")
            }

            val aiResponse = getSyntraAIResponse(apiKey, enhancedPrompt, role)
            ChatbotSessionManager.addMessage(role, "assistant", aiResponse)
            ChatMessage(aiResponse, false)
        } catch (e: Exception) {
            e.printStackTrace()
            ChatMessage(
                text = "No pude conectar con el servidor de IA 🤖. Intentémoslo de nuevo más tarde.",
                isUser = false
            )
        }
    }
}