package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inlineData") val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    @Json(name = "mimeType") val mimeType: String,
    @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "topP") val topP: Float? = null,
    @Json(name = "maxOutputTokens") val maxOutputTokens: Int? = null,
    @Json(name = "responseMimeType") val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

class GeminiRepository {
    private val apiService = RetrofitClient.service
    private val apiKey = BuildConfig.GEMINI_API_KEY

    suspend fun generateLegalFeedback(prompt: String, systemPrompt: String? = null): String {
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Note: Gemini API Key is missing. (Configure it in AI Studio Secrets panel).\n\n" +
                    "[Simulated Counsel]: Based on standard legal principles: Make sure you assert your constitutional rights (like the 4th and 5th Amendments). Keep detailed records of dates, witness names, and enforcer names. Search for local statutes related to your eviction or citation."
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = systemPrompt?.let { Content(parts = listOf(Part(text = it))) } ?: Content(
                parts = listOf(Part(text = "You are a highly professional US AI Lawyer. You give insightful legal feedback, analyze statutes, outline constitutional rights, pinpoint potential police/enforcer errors, and highlight legal loopholes. Keep your legal summaries structured, clear, and easy to scan, highlighting federal and state references. State that this is legal information, not binding legal representation."))
            )
        )

        return try {
            val response = apiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "No response received. Please try again."
        } catch (e: Exception) {
            "Failed to reach AI legal advisor: ${e.localizedMessage}. Please check your connection and secrets."
        }
    }

    suspend fun analyzeDocumentLegality(base64Image: String, mimeType: String, description: String): String {
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Note: Gemini API Key is missing.\n\n" +
                    "[Simulated Analysis]: The uploaded item appears to be legal documentation or a scene photo. To fully evaluate legality, ensure the image is high resolution and clearly displays timestamps, signatures, or relevant enforcement conduct. Check if there is missing consent, improper warrant execution, or failure to state probable cause."
        }

        val systemPrompt = "You are an expert criminal and civil defense AI lawyer. Analyze the uploaded image or document for legality, procedural errors, constitutional violations, or loopholes. Give a comprehensive breakdown."
        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = "Analyze this image with description: $description. Look for legal errors, contract loopholes, rights violations, or evidence details."),
                        Part(inlineData = InlineData(mimeType = mimeType, data = base64Image))
                    )
                )
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        return try {
            val response = apiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No analysis received. Please try again."
        } catch (e: Exception) {
            "Failed to analyze image: ${e.localizedMessage}"
        }
    }
}
