package com.segnities007.repository

import com.segnities007.local.dao.WeeklyTemplateDao
import com.segnities007.local.entity.toDomain
import com.segnities007.local.entity.toEntity
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.WeekDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeeklyTemplateRepositoryImpl(
    private val dao: WeeklyTemplateDao
) : WeeklyTemplateRepository {

    override fun getTemplatesByDay(dayOfWeek: WeekDay): Flow<List<WeeklyTemplate>> {
        return dao.getTemplatesByDay(dayOfWeek.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveTemplate(template: WeeklyTemplate) {
        dao.insertTemplate(template.toEntity())
    }

    override suspend fun deleteTemplate(template: WeeklyTemplate) {
        dao.deleteTemplate(template.toEntity())
    }

    override suspend fun deleteByDay(dayOfWeek: WeekDay) {
        dao.deleteByDay(dayOfWeek.name)
    }
}
