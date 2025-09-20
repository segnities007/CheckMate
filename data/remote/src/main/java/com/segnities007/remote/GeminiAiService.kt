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
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private const val modelName = "gemini-2.5-flash-lite"
private const val baseUrl = "https://generativelanguage.googleapis.com/v1beta"

private const val PROMPT_VERSION = "pv2" // 変更時にキャッシュ無効化用
private const val PROMPT_HEADER = "あなたは持ち物管理アシスタント。与えられたイベントごとに最適な持ち物ID(既存IDのみ)を3〜5個返す。"
private val PROMPT_RULES =
    """
出力条件:
1.JSONのみ/説明禁止/Markdown禁止
2.不明なら items は 空配列
3.存在しないIDを含めない
4.各イベント独立
JSON形式: {"results":[{"index":0,"items":[1,2,3]}]}
    """.trimIndent()

class GeminiAiService(
    private val apiKey: String,
) {
    // JSON デコード用（不要キー無視）
    private val batchJson = Json { ignoreUnknownKeys = true }

    private val httpClient =
        HttpClient(CIO) {
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

    suspend fun recommendItemsForEvent(
        event: CalendarEvent,
        availableItems: List<Item>,
    ): List<Int> =
        if (apiKey.isBlank()) {
            emptyList()
        } else {
            withContext(Dispatchers.IO) {
                var attempt = 0
                val maxAttempts = 3
                while (attempt < maxAttempts) {
                    attempt++
                    val result =
                        runCatching {
                            val prompt = buildPrompt(event, availableItems)
                            val request =
                                GeminiRequest(
                                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                                    generationConfig = GenerationConfig(temperature = 0.0, maxOutputTokens = 256),
                                )
                            val response =
                                httpClient.post("$baseUrl/models/$modelName:generateContent?key=$apiKey") {
                                    contentType(ContentType.Application.Json)
                                    setBody(request)
                                }
                            val geminiResponse = response.body<GeminiResponse>()
                            if (geminiResponse.error != null) {
                                val code = geminiResponse.error?.code
                                val status = geminiResponse.error?.status
                                val message = geminiResponse.error?.message
                                Log.e("GeminiAiService", "Gemini API error (attempt $attempt): $code $status $message")
                                if (code == 503 || status == "UNAVAILABLE") {
                                    // transient -> retry
                                    null
                                } else {
                                    // non-retryable
                                    return@withContext emptyList<Int>()
                                }
                            } else {
                                val text =
                                    geminiResponse.candidates
                                        ?.firstOrNull()
                                        ?.content
                                        ?.parts
                                        ?.firstOrNull()
                                        ?.text
                                        .orEmpty()
                                return@withContext parseAiResponse(text, availableItems)
                            }
                        }.getOrElse { e ->
                            Log.e("GeminiAiService", "Error calling Gemini API (attempt $attempt)", e)
                            null
                        }
                    if (result != null) return@withContext result
                    if (attempt < maxAttempts) {
                        delay(300L * attempt) // simple backoff
                    }
                }
                emptyList()
            }
        }

    /**
     * バッチ推奨: 複数イベントを一度に問い合わせ、 index ベースで結果を返す。
     * 返却 Map の key は event.id。
     */
    suspend fun recommendItemsForEvents(
        events: List<CalendarEvent>,
        availableItems: List<Item>,
        chunkSize: Int = 25,
    ): Map<String, List<Int>> =
        if (apiKey.isBlank() || events.isEmpty()) {
            emptyMap()
        } else {
            withContext(Dispatchers.IO) {
                val start = System.currentTimeMillis()
                val allMetrics = mutableListOf<BatchMetrics>()
                val combined = mutableMapOf<String, List<Int>>()
                val chunks = if (events.size <= chunkSize) listOf(events) else events.chunked(chunkSize)
                for ((ci, chunk) in chunks.withIndex()) {
                    val metrics = BatchMetrics(chunkCount = chunks.size, chunkIndex = ci, events = chunk.size)
                    val map = performSingleBatchWithRetry(chunk, availableItems, metrics)
                    combined += map
                    allMetrics += metrics
                }
                val elapsed = System.currentTimeMillis() - start
                logAggregated(allMetrics, elapsed, events.size)
                combined
            }
        }

    private suspend fun performSingleBatchWithRetry(
        events: List<CalendarEvent>,
        items: List<Item>,
        metrics: BatchMetrics,
    ): Map<String, List<Int>> {
        var attempt = 0
        val maxAttempts = 3
        var remaining = events
        val result = mutableMapOf<String, List<Int>>()
        while (attempt < maxAttempts && remaining.isNotEmpty()) {
            attempt++
            metrics.attempts = attempt
            val prompt = buildBatchPrompt(remaining, items)
            val request =
                GeminiRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(temperature = 0.0, maxOutputTokens = 384),
                )
            val response =
                runCatching {
                    httpClient.post("$baseUrl/models/$modelName:generateContent?key=$apiKey") {
                        contentType(ContentType.Application.Json)
                        setBody(request)
                    }
                }.getOrElse { e ->
                    Log.e("GeminiAiService", "Batch request failure (attempt=$attempt)", e)
                    if (attempt == maxAttempts) return result
                    delay(200L * attempt)
                    continue
                }
            val geminiResponse = response.body<GeminiResponse>()
            if (geminiResponse.error != null) {
                val code = geminiResponse.error?.code
                val status = geminiResponse.error?.status
                Log.e("GeminiAiService", "Gemini API error (batch attempt=$attempt): ${geminiResponse.error}")
                if (code == 503 || status == "UNAVAILABLE") {
                    delay(300L * attempt)
                    continue
                } else {
                    return result
                }
            }
            val text =
                geminiResponse.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text
                    .orEmpty()
            val parsed = parseBatchResponse(text, remaining, items)
            metrics.salvageUsed = metrics.salvageUsed || parsed.salvageUsed
            metrics.rawChars += text.length
            metrics.truncatedDetected = metrics.truncatedDetected || parsed.truncated
            // add parsed
            result += parsed.results
            // determine missing
            val missingIds = remaining.filterIndexed { idx, _ -> (idx !in parsed.matchedOriginalIndices) }
            remaining = missingIds
            if (remaining.isNotEmpty() && attempt < maxAttempts) {
                delay(150L * attempt)
            }
        }
        return result
    }

    private fun buildPrompt(
        event: CalendarEvent,
        availableItems: List<Item>,
    ): String =
        buildString {
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

    private fun parseAiResponse(
        response: String,
        availableItems: List<Item>,
    ): List<Int> =
        try {
            val allowed = availableItems.map { it.id }.toSet()
            response
                .trim()
                .split(',')
                .mapNotNull { it.trim().toIntOrNull() }
                .filter { it in allowed }
                .distinct()
        } catch (e: Exception) {
            Log.e("GeminiAiService", "Error parsing AI response: $response", e)
            emptyList()
        }

    private fun buildBatchPrompt(
        events: List<CalendarEvent>,
        availableItems: List<Item>,
    ): String =
        buildString {
            appendLine("version:$PROMPT_VERSION")
            appendLine(PROMPT_HEADER)
            appendLine(PROMPT_RULES)
            appendLine("EVENTS:")
            events.forEachIndexed { idx, e ->
                val durMin =
                    kotlin
                        .runCatching {
                            (
                                (e.endDateTime.hour * 60 + e.endDateTime.minute) -
                                    (e.startDateTime.hour * 60 + e.startDateTime.minute)
                            ).coerceAtLeast(0)
                        }.getOrDefault(0)
                val desc = (e.description ?: "").replace("\n", " ").take(120)
                append("[$idx]|t=")
                append(e.title.take(60))
                append("|wd=")
                append(
                    e.startDateTime.dayOfWeek.name
                        .take(3),
                )
                append("|start=")
                append(String.format("%02d:%02d", e.startDateTime.hour, e.startDateTime.minute))
                append("|dur=")
                append(durMin)
                if (desc.isNotBlank()) {
                    append("|notes=")
                    append(desc)
                }
                appendLine()
            }
            appendLine("ITEMS:")
            availableItems.take(50).forEach { item ->
                append(item.id)
                append(':')
                append(item.name.replace('\n', ' ').take(40))
                append('(')
                append(item.category)
                append(')')
                appendLine()
            }
            append("ANSWER JSON ONLY:")
        }

    private fun parseBatchResponse(
        response: String,
        events: List<CalendarEvent>,
        availableItems: List<Item>,
    ): ParsedBatchResult {
        val allowed = availableItems.map { it.id }.toSet()
        if (response.isBlank()) return ParsedBatchResult(emptyMap(), emptySet(), false, false)
        val cleaned =
            response
                .trim()
                .removePrefix("```json")
                .removePrefix("```JSON")
                .removePrefix("````")
                .removeSuffix("```")
                .trim()
        // strict parse
        try {
            val parsed = batchJson.decodeFromString<BatchRecommendResponse>(cleaned)
            val result = mutableMapOf<String, List<Int>>()
            val matchedIdx = mutableSetOf<Int>()
            parsed.results.forEach { entry ->
                if (entry.index in events.indices) {
                    val ids = entry.items.filter { it in allowed }.distinct()
                    result[events[entry.index].id] = ids
                    matchedIdx += entry.index
                }
            }
            return ParsedBatchResult(result, matchedIdx, false, false)
        } catch (_: Exception) {
            // salvage attempt
        }
        val balanced = balancedSubstring(cleaned)
        val truncated = balanced.length < cleaned.length
        val pattern = Regex("\\{\\s*\"index\"\\s*:\\s*(\\d+)\\s*,\\s*\"items\"\\s*:\\s*\\[([^\\]]*)\\]\\s*}")
        val matches = pattern.findAll(balanced)
        val result = mutableMapOf<String, List<Int>>()
        val matchedIdx = mutableSetOf<Int>()
        matches.forEach { m ->
            val idx = m.groupValues.getOrNull(1)?.toIntOrNull() ?: return@forEach
            if (idx !in events.indices) return@forEach
            val itemsPart = m.groupValues.getOrNull(2).orEmpty()
            val ids =
                if (itemsPart.isBlank()) {
                    emptyList()
                } else {
                    itemsPart
                        .split(',')
                        .mapNotNull { it.trim().toIntOrNull() }
                        .filter { it in allowed }
                        .distinct()
                }
            result[events[idx].id] = ids
            matchedIdx += idx
        }
        return ParsedBatchResult(result, matchedIdx, true, truncated)
    }

    private fun balancedSubstring(src: String): String {
        var brace = 0
        var bracket = 0
        var lastOk = -1
        src.forEachIndexed { i, c ->
            when (c) {
                '{' -> brace++
                '}' -> brace--
                '[' -> bracket++
                ']' -> bracket--
            }
            if (brace < 0 || bracket < 0) return@forEachIndexed
            if (brace == 0 && bracket == 0) lastOk = i
        }
        return if (lastOk >= 0) src.substring(0, lastOk + 1) else src
    }

    private fun logAggregated(
        metrics: List<BatchMetrics>,
        elapsedMs: Long,
        totalEvents: Int,
    ) {
        val salvage = metrics.count { it.salvageUsed }
        val truncated = metrics.count { it.truncatedDetected }
        val attempts = metrics.maxOfOrNull { it.attempts } ?: 0
        val rawChars = metrics.sumOf { it.rawChars }
        val json =
            "{" +
                "\"phase\":\"ai_batch\"," +
                "\"totalEvents\":$totalEvents," +
                "\"chunks\":${metrics.size}," +
                "\"elapsedMs\":$elapsedMs," +
                "\"salvageChunks\":$salvage," +
                "\"truncatedChunks\":$truncated," +
                "\"maxAttempts\":$attempts," +
                "\"rawChars\":$rawChars," +
                "\"version\":\"$PROMPT_VERSION\"" +
                "}"
        Log.i("GeminiAiService", json)
    }

    private data class ParsedBatchResult(
        val results: Map<String, List<Int>>,
        val matchedOriginalIndices: Set<Int>,
        val salvageUsed: Boolean,
        val truncated: Boolean,
    )

    private data class BatchMetrics(
        val chunkCount: Int,
        val chunkIndex: Int,
        val events: Int,
        var attempts: Int = 0,
        var salvageUsed: Boolean = false,
        var truncatedDetected: Boolean = false,
        var rawChars: Int = 0,
    )

    // ==== JSON デシリアライズ用モデル ====
    @Serializable
    private data class BatchRecommendResponse(
        @SerialName("results") val results: List<BatchResultEntry> = emptyList(),
    )

    @Serializable
    private data class BatchResultEntry(
        @SerialName("index") val index: Int,
        @SerialName("items") val items: List<Int> = emptyList(),
    )
}
