package com.segnities007.repository

import com.segnities007.model.WeeklyTemplate
import kotlinx.coroutines.flow.Flow

interface WeeklyTemplateRepository {
    fun getTemplatesForDay(day: String): Flow<List<WeeklyTemplate>>
    suspend fun getAllTemplates(): List<WeeklyTemplate>
    suspend fun insertTemplate(template: WeeklyTemplate)
    suspend fun updateTemplate(template: WeeklyTemplate)
    suspend fun deleteTemplate(template: WeeklyTemplate)
}
