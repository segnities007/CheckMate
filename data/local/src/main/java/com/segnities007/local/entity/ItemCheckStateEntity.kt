package com.segnities007.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.segnities007.model.item.ItemCheckRecord
import com.segnities007.model.item.ItemCheckState
import kotlinx.datetime.LocalDate

@Entity(tableName = "item_check_states")
data class ItemCheckStateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemId: Int,
    val history: String,
)

fun ItemCheckStateEntity.toDomain(): ItemCheckState {
    val records: List<ItemCheckRecord> =
        if (history.isEmpty()) {
            emptyList()
        } else {
            history.split(",").mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    val date = LocalDate.parse(parts[0])
                    val isChecked = parts[1].toBoolean()
                    ItemCheckRecord(date = date, isChecked = isChecked)
                } else {
                    null
                }
            }
        }
    return ItemCheckState(id = id, itemId = itemId, history = records)
}

fun ItemCheckState.toEntity(): ItemCheckStateEntity {
    val historyString = history.joinToString(",") { "${it.date}:${it.isChecked}" }
    return ItemCheckStateEntity(id = id, itemId = itemId, history = historyString)
}
