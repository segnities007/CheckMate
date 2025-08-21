package com.segnities007.repository.model

import com.segnities007.model.item.ItemCheckRecord
import com.segnities007.repository.LocalDateSerializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ExportItemCheckRecord(
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val isChecked: Boolean = false
)

fun ItemCheckRecord.toExport(): ExportItemCheckRecord = ExportItemCheckRecord(
    date = date,
    isChecked = isChecked
)

fun ExportItemCheckRecord.toDomain(): ItemCheckRecord = ItemCheckRecord(
    date = date,
    isChecked = isChecked
)