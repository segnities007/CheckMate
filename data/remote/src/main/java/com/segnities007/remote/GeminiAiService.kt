package com.segnities007.remote

import android.util.Log
import com.segnities007.model.calendar.CalendarEvent
import com.segnities007.model.item.Item
import com.segnities007.remote.model.Content
import com.segnities007.remote.model.GeminiRequest
import com.segnities007.remote.model.GeminiResponse
import com.segnities007.remote.model.GenerationConfig
import com.segnities007.remote.model.Part
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


private const val modelName = "gemini-2.5-flash-lite"
private const val baseUrl = "https://generativelanguage.googleapis.com/v1beta"

private const val PROMPT_HEADER = "あなたは持ち物管理のエキスパートです。以下のカレンダーイベントに最適な持ち物を推奨してください。"
private val PROMPT_RULES = """
【指示】
1. イベントの内容・場所・時間帯を考慮して最適なアイテムを3〜5個選ぶ
2. 出力は選んだアイテムIDのみ（カンマ区切り, 例: 1,5,12）
3. 説明や理由は書かない（IDのみ）
4. 存在しないIDは含めない
        """.trimIndent()

class GeminiAiService(private val apiKey: String) {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                },
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
            connectTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
            socketTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
        }
    }

    suspend fun recommendItemsForEvent(event: CalendarEvent, availableItems: List<Item>): List<Int> =
        if (apiKey.isBlank()) emptyList() else withContext(Dispatchers.IO) {
            runCatching {
                val prompt = buildPrompt(event, availableItems)
                val request = GeminiRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(temperature = 0.3, maxOutputTokens = 256),
                )
                val response = httpClient.post("$baseUrl/models/$modelName:generateContent?key=$apiKey") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
                val geminiResponse = response.body<GeminiResponse>()
                if (geminiResponse.error != null) {
                    Log.e("GeminiAiService", "Gemini API error: ${geminiResponse.error}")
                    emptyList()
                } else {
                    val text = geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text.orEmpty()
                    parseAiResponse(text, availableItems)
                }
            }.getOrElse { e ->
                Log.e("GeminiAiService", "Error calling Gemini API", e)
                emptyList()
            }
        }

    private fun buildPrompt(
        event: CalendarEvent,
        availableItems: List<Item>,
    ): String {
        return buildString {
            appendLine(PROMPT_HEADER)
            appendLine()
            appendLine("【イベント情報】")
            appendLine("タイトル: ${event.title}")
            appendLine("説明: ${event.description ?: "なし"}")
            appendLine("場所: ${event.location ?: "なし"}")
            appendLine("開始時刻: ${event.startDateTime}")
            appendLine("終了時刻: ${event.endDateTime}")
            appendLine("カテゴリ: ${event.categories.joinToString(", ").ifEmpty { "なし" }}")
            appendLine()
            appendLine("【利用可能なアイテム一覧】")
            availableItems.forEach { item ->
                appendLine("ID=${item.id}, 名=${item.name}, カテゴリ=${item.category}")
            }
            appendLine()
            appendLine(PROMPT_RULES)
            append("回答:") // 末尾に余計な改行を増やさない
        }
    }

    private fun parseAiResponse(response: String, availableItems: List<Item>): List<Int> = try {
        val allowed = availableItems.map { it.id }.toSet()
        response.trim()
            .split(',')
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in allowed }
            .distinct()
    } catch (e: Exception) {
        Log.e("GeminiAiService", "Error parsing AI response: $response", e)
        emptyList()
    }
}
