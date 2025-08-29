package com.segnities007.model.item

import kotlinx.serialization.Serializable

@Serializable
data class BarcodeInfo(
    val barcode: String,
    val format: String,
    val rawValue: String,
    val displayValue: String,
    val timestamp: Long = System.currentTimeMillis(),
)

@Serializable
data class ProductInfo(
    val barcode: String,
    val name: String,
    val description: String,
    val category: ItemCategory,
    val manufacturer: String? = null,
    val imageUrl: String? = null,
    val isbn: String? = null,
    val publisher: String? = null,
    val author: String? = null,
)
