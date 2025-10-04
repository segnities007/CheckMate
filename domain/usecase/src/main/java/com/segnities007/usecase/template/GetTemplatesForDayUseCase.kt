package com.segnities007.usecase.template

import com.segnities007.model.WeeklyTemplate
import com.segnities007.repository.WeeklyTemplateRepository

/**
 * 曜日別にテンプレートを取得するUse Case
 */
class GetTemplatesForDayUseCase(
    private val weeklyTemplateRepository: WeeklyTemplateRepository
) {
    suspend operator fun invoke(dayOfWeek: String): List<WeeklyTemplate> {
        return weeklyTemplateRepository.getTemplatesForDay(dayOfWeek)
    }
}
