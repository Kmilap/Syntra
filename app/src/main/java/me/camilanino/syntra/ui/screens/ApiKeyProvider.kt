package me.camilanino.syntra.ui.screens

import android.content.Context
import java.util.Properties

object ApiKeyProvider {
    fun getOpenAIKey(context: Context): String? {
        return try {
            val properties = Properties()
            val inputStream = context.assets.open("local.properties")
            properties.load(inputStream)
            properties.getProperty("OPENAI_API_KEY")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}