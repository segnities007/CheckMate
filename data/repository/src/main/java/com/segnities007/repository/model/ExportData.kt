package com.segnities007.repository.model

import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCheckState
import kotlinx.serialization.Serializable

@Serializable
data class ExportData(
    val items: List<ExportItem> = emptyList(),
    val states: List<ExportItemCheckState> = emptyList(),
    val templates: List<ExportWeeklyTemplate> = emptyList()
) {
    companion object {
        fun fromDomain(
            items: List<Item>,
            states: List<ItemCheckState>,
            templates: List<WeeklyTemplate>
        ): ExportData = ExportData(
            items = items.map { it.toExport() },
            states = states.map { it.toExport() },
            templates = templates.map { it.toExport() }
        )
    }
}

