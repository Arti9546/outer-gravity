package com.hastashilpa.service

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService {
    // Note: Replace with your actual Gemini API Key from Google AI Studio
    private val apiKey = "**********************************"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
        requestOptions = RequestOptions(apiVersion = "v1beta")
    )

    suspend fun getTrendingDesignSuggestions(): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text("Suggest 3 trending handicraft design ideas for artisans using sustainable materials like bamboo or cane. Provide only the names and a very brief description for each.")
                    }
                )
                response.text ?: "No suggestions available at the moment."
            } catch (e: Exception) {
                "Unable to fetch AI suggestions: ${e.localizedMessage}"
            }
        }
    }
}
