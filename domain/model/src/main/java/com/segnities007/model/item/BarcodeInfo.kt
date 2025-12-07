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


