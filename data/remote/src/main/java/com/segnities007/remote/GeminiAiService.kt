package com.segnities007.remote

import android.util.Log
import com.segnities007.model.calendar.CalendarEvent
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
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

class GeminiAiService(
    private val apiKey: String,
) {
    private val httpClient =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                    },
                )
            }
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.INFO
            }
            install(HttpTimeout) {
                requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
                connectTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
                socketTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
            }
        }

    suspend fun recommendItemsForEvent(
        event: CalendarEvent,
        availableItems: List<Item>,
    ): List<Int> =
        withContext(Dispatchers.IO) {
            try {
                if (apiKey.isBlank()) {
                    Log.w("GeminiAiService", "API key is blank")
                    return@withContext emptyList()
                }

                val prompt = buildPrompt(event, availableItems)
                Log.d("GeminiAiService", "Sending prompt to Gemini: $prompt")

                val request =
                    GeminiRequest(
                        contents =
                            listOf(
                                Content(
                                    parts = listOf(Part(text = prompt)),
                                ),
                            ),
                        generationConfig =
                            GenerationConfig(
                                temperature = 0.3, // より一貫性のある回答のため
                                maxOutputTokens = 256, // 短い回答のため
                            ),
                    )

                val response =
                    httpClient.post(
                        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=$apiKey",
                    ) {
                        contentType(ContentType.Application.Json)
                        setBody(request)
                    }

                val geminiResponse = response.body<GeminiResponse>()

                // エラーチェック
                if (geminiResponse.error != null) {
                    Log.e("GeminiAiService", "Gemini API error: ${geminiResponse.error}")
                    return@withContext emptyList<Int>()
                }

                val responseText =
                    geminiResponse.candidates
                        ?.firstOrNull()
                        ?.content
                        ?.parts
                        ?.firstOrNull()
                        ?.text ?: ""
                Log.d("GeminiAiService", "Gemini response: $responseText")

                return@withContext parseAiResponse(responseText, availableItems)
            } catch (e: Exception) {
                Log.e("GeminiAiService", "Error calling Gemini API", e)
                return@withContext emptyList<Int>()
            }
        }

    private fun buildPrompt(
        event: CalendarEvent,
        availableItems: List<Item>,
    ): String {
        val itemsInfo =
            availableItems.joinToString("\n") { item ->
                "ID: ${item.id}, 名前: ${item.name}, カテゴリ: ${item.category}"
            }

        return """
あなたは持ち物管理のエキスパートです。以下のカレンダーイベントに最適な持ち物を推奨してください。

【イベント情報】
タイトル: ${event.title}
説明: ${event.description ?: "なし"}
場所: ${event.location ?: "なし"}
開始時刻: ${event.startDateTime}
終了時刻: ${event.endDateTime}
カテゴリ: ${event.categories.joinToString(", ").ifEmpty { "なし" }}

【利用可能なアイテム一覧】
$itemsInfo

【指示】
1. イベントの内容、場所、時間帯を考慮して、最適なアイテムを3-5個選んでください
2. 回答は選択したアイテムのIDのみを、カンマ区切りで返してください（例: 1,5,12）
3. 説明や理由は不要です、IDのみを返してください
4. 存在しないIDは返さないでください

回答:
            """.trimIndent()
    }

    private fun parseAiResponse(
        response: String,
        availableItems: List<Item>,
    ): List<Int> =
        try {
            val availableItemIds = availableItems.map { it.id }.toSet()

            response
                .trim()
                .split(",")
                .mapNotNull { idStr ->
                    idStr.trim().toIntOrNull()
                }.filter { id -> availableItemIds.contains(id) }
                .distinct()
                .also { selectedIds ->
                    Log.d("GeminiAiService", "Parsed item IDs: $selectedIds")
                }
        } catch (e: Exception) {
            Log.e("GeminiAiService", "Error parsing AI response: $response", e)
            emptyList()
        }

    fun close() {
        httpClient.close()
    }
}
