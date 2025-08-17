package com.segnities007.repository

import com.segnities007.local.dao.WeeklyTemplateDao
import com.segnities007.local.entity.toDomain
import com.segnities007.local.entity.toEntity
import com.segnities007.model.WeeklyTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeeklyTemplateRepositoryImpl(
    private val dao: WeeklyTemplateDao
) : WeeklyTemplateRepository {

    override fun getTemplatesForDay(day: String): Flow<List<WeeklyTemplate>> =
        dao.getTemplatesForDay(day).map { list -> list.map { it.toDomain() } }

    override suspend fun getAllTemplates(): List<WeeklyTemplate> {
        return dao.getAllTemplates().map { it.toDomain() }
    }

    override suspend fun insertTemplate(template: WeeklyTemplate) {
        dao.insert(template.toEntity())
    }

    override suspend fun updateTemplate(template: WeeklyTemplate) {
        dao.update(template.toEntity())
    }

    override suspend fun deleteTemplate(template: WeeklyTemplate) {
        dao.delete(template.toEntity())
    }
}
