package com.segnities007.model

data class Item(
    val id: String,
    val name: String,
    val description: String?,
    val category: ItemCategory,
    val imagePath: String?,
    val createdAt: DomainInstant,
)