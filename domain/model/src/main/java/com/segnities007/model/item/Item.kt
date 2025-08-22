package com.segnities007.model.item

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Item
    @OptIn(ExperimentalTime::class)
    constructor(
        val id: Int = 0,
        val name: String = "",
        val description: String = "",
        val category: ItemCategory = ItemCategory.OTHER_SUPPLIES,
        val imagePath: String = "",
        val barcodeInfo: BarcodeInfo? = null,
        val productInfo: ProductInfo? = null,
        val createdAt: Instant = Clock.System.now(),
    )
