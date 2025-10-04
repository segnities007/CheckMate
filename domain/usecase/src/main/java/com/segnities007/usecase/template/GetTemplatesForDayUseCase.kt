package com.segnities007.usecase.template

import com.segnities007.model.WeeklyTemplate
import com.segnities007.repository.WeeklyTemplateRepository

/**
 * 曜日別にテンプレートを取得するUse Case
 */
class GetTemplatesForDayUseCase(
    private val weeklyTemplateRepository: WeeklyTemplateRepository
) {
    suspend operator fun invoke(dayOfWeek: String): Result<List<WeeklyTemplate>> {
        return try {
            val templates = weeklyTemplateRepository.getTemplatesForDay(dayOfWeek)
            Result.success(templates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
