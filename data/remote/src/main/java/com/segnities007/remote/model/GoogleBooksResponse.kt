package com.segnities007.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class GoogleBooksResponse(
    val items: List<GoogleBookItem>? = null
)

@Serializable
data class GoogleBookItem(
    val volumeInfo: VolumeInfo? = null
)

@Serializable
data class VolumeInfo(
    val title: String? = null,
    val authors: List<String>? = null,
    val publisher: String? = null,
    val description: String? = null,
    val imageLinks: ImageLinks? = null,
    val industryIdentifiers: List<IndustryIdentifier>? = null
)

@Serializable
data class ImageLinks(
    val thumbnail: String? = null
)

@Serializable
data class IndustryIdentifier(
    val type: String? = null,
    val identifier: String? = null
)
