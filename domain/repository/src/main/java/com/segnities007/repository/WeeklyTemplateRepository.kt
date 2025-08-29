package com.segnities007.repository

import com.segnities007.model.WeeklyTemplate
import kotlinx.coroutines.flow.Flow

interface WeeklyTemplateRepository {
    suspend fun getTemplatesForDay(day: String): List<WeeklyTemplate>

    suspend fun getAllTemplates(): List<WeeklyTemplate>

    suspend fun insertTemplate(template: WeeklyTemplate)

    suspend fun updateTemplate(template: WeeklyTemplate)

    suspend fun deleteTemplate(template: WeeklyTemplate)

    suspend fun clearAllTemplates()
}
