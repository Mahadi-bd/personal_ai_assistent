package com.example.data.ai

import android.content.Context
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Gemini model ids. Google retires models from time to time:
 * if a model stops working, change the id here (see https://ai.google.dev/gemini-api/docs/models).
 * In thinking mode the service tries THINKING first and automatically falls back to FAST.
 */
object GeminiModels {
    const val FAST = "gemini-3.6-flash"
    const val THINKING = "gemini-3.8-flash"
}

private data class AiResult(val text: String?, val error: String?)

class GeminiAiService(private val context: Context) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    private val certSha1: String by lazy { AppSignature.sha1Hex(context) }

    fun streamAiResponse(
        prompt: String,
        isThinkingMode: Boolean,
        personaRole: String,
        voiceTone: String,
        targetLanguage: String
    ): Flow<String> = flow {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()

        val systemInstruction = "You are the assistant inside the LIFE EASY app, a personal productivity helper. " +
                "Role: $personaRole. Tone: $voiceTone. Reply language: $targetLanguage " +
                "(if the user writes in another language, reply in the user's language). " +
                "Be helpful, concise and accurate. If you are not sure about something, say so. " +
                if (isThinkingMode) "Think carefully and give a well-structured, step-by-step answer." else ""

        val result = if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            AiResult(null, "AI is not configured yet. The developer needs to add a Gemini API key (GEMINI_API_KEY) and rebuild the app.")
        } else {
            val models = if (isThinkingMode) listOf(GeminiModels.THINKING, GeminiModels.FAST) else listOf(GeminiModels.FAST)
            var last = AiResult(null, "Unknown error")
            for (model in models) {
                last = callGemini(apiKey, model, systemInstruction, prompt, thinking = isThinkingMode && model == GeminiModels.THINKING)
                if (last.text != null) break
            }
            last
        }

        val output = result.text ?: ("⚠ " + (result.error ?: "Could not get a response."))
        val words = output.split(" ")
        val buffer = StringBuilder()
        for (word in words) {
            buffer.append(word).append(" ")
            emit(buffer.toString())
            delay(20)
        }
    }.flowOn(Dispatchers.IO)

    private fun callGemini(
        apiKey: String,
        model: String,
        systemInstruction: String,
        prompt: String,
        thinking: Boolean
    ): AiResult {
        return try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent"

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                }))
                put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemInstruction))))
                if (thinking) {
                    put("generationConfig", JSONObject().put("thinkingConfig", JSONObject().put("thinkingLevel", "high")))
                }
            }

            val builder = Request.Builder()
                .url(url)
                .header("x-goog-api-key", apiKey)
                .header("X-Android-Package", context.packageName)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            if (certSha1.isNotEmpty()) builder.header("X-Android-Cert", certSha1)

            httpClient.newCall(builder.build()).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val msg = when (response.code) {
                        400, 403 -> "The AI key was rejected (check the API key and its restrictions)."
                        404 -> "AI model \"$model\" is not available."
                        429 -> "Too many requests. Please wait a moment and try again."
                        else -> "AI service error (${response.code})."
                    }
                    return AiResult(null, msg)
                }
                val candidates = JSONObject(body).optJSONArray("candidates")
                val parts = candidates?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")
                val sb = StringBuilder()
                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        if (!part.optBoolean("thought", false)) sb.append(part.optString("text"))
                    }
                }
                val text = sb.toString().trim()
                if (text.isEmpty()) AiResult(null, "The AI returned an empty answer. Please rephrase your question.")
                else AiResult(text, null)
            }
        } catch (_: java.io.IOException) {
            AiResult(null, "No internet connection. Please check your network and try again.")
        } catch (_: Exception) {
            AiResult(null, "Something went wrong while contacting the AI.")
        }
    }
}
