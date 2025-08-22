package com.segnities007.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class OpenLibraryResponse(
    val title: String? = null,
    val authors: List<Author>? = null,
    val publishers: List<Publisher>? = null,
    val number_of_pages: Int? = null,
    val covers: List<Int>? = null,
    val isbn_10: List<String>? = null,
    val isbn_13: List<String>? = null,
    val description: String? = null
)

@Serializable
data class Author(
    val name: String? = null
)

@Serializable
data class Publisher(
    val name: String? = null
)
