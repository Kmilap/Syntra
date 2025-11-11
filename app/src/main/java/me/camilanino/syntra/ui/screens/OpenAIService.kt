package me.camilanino.syntra.ui.screens

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.squareup.moshi.Json

// ============================================================
// 🔹 ESTRUCTURAS DE DATOS PARA LA API DE OPENAI
// ============================================================

data class ChatRequestAPI(
    @Json(name = "model") val model: String = "gpt-3.5-turbo",
    @Json(name = "messages") val messages: List<ChatMessageAPI>
)

data class ChatMessageAPI(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

data class ChatResponseAPI(
    @Json(name = "choices") val choices: List<ChatChoiceAPI>
)

data class ChatChoiceAPI(
    @Json(name = "message") val message: ChatMessageAPI
)

// ============================================================
// 🔹 INTERFAZ RETROFIT: ENVÍA MENSAJE A OPENAI
// ============================================================

interface OpenAIServiceSyntra {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(@Body request: ChatRequestAPI): ChatResponseAPI
}

// ============================================================
// 🔹 FUNCIÓN PARA CREAR LA INSTANCIA DEL SERVICIO
// ============================================================

fun createOpenAIServiceSyntra(apiKey: String): OpenAIServiceSyntra {
    val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
            chain.proceed(newRequest)
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    return retrofit.create(OpenAIServiceSyntra::class.java)
}