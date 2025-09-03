package com.segnities007.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
)

@Serializable
data class Content(
    val parts: List<Part>,
)

@Serializable
data class Part(
    val text: String,
)

@Serializable
data class GenerationConfig(
    val temperature: Double = 0.7,
    val topK: Int = 40,
    val topP: Double = 0.95,
    val maxOutputTokens: Int = 1024,
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val error: GeminiError? = null,
)

@Serializable
data class Candidate(
    val content: Content,
)

@Serializable
data class GeminiError(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null,
)