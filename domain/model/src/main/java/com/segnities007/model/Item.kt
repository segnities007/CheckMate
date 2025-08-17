package com.segnities007.model

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Item @OptIn(ExperimentalTime::class) constructor(
    val id: Int = 0,
    val name: String = "",
    val description: String? = null,
    val category: ItemCategory = ItemCategory.OTHER_SUPPLIES,
    val imagePath: String? = null,
    val createdAt: Instant = Clock.System.now(),
)
