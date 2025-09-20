package com.segnities007.templates.mvi

import com.segnities007.model.WeeklyTemplate
import org.junit.Assert.assertEquals
import org.junit.Test

class TemplatesReducerTest {
    @Test
    fun setWeeklyTemplates_updatesList() {
        val initial = TemplatesUiState(weeklyTemplates = emptyList())
        val templates = listOf(WeeklyTemplate(id = 1, title = "T1", daysOfWeek = emptySet(), itemIds = emptyList()))
        val updated = TemplatesReducer.reduce(initial, TemplatesIntent.SetWeeklyTemplates(templates))
        assertEquals(1, updated.weeklyTemplates.size)
        assertEquals("T1", updated.weeklyTemplates.first().title)
    }

    @Test
    fun updateSearchQuery_changesQuery() {
        val initial = TemplatesUiState(searchQuery = "")
        val updated = TemplatesReducer.reduce(initial, TemplatesIntent.UpdateSearchQuery("foo"))
        assertEquals("foo", updated.searchQuery)
    }
}
