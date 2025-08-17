package com.segnities007.repository

import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.WeekDay
import kotlinx.coroutines.flow.Flow

interface WeeklyTemplateRepository {
    fun getTemplatesByDay(dayOfWeek: WeekDay): Flow<List<WeeklyTemplate>>
    suspend fun saveTemplate(template: WeeklyTemplate)
    suspend fun deleteTemplate(template: WeeklyTemplate)
    suspend fun deleteByDay(dayOfWeek: WeekDay)
}
