package com.segnities007.usecase.template

import com.segnities007.model.WeeklyTemplate
import com.segnities007.repository.WeeklyTemplateRepository

/**
 * 全てのテンプレートを取得するUse Case
 */
class GetAllTemplatesUseCase(
    private val weeklyTemplateRepository: WeeklyTemplateRepository
) {
    suspend operator fun invoke(): List<WeeklyTemplate> {
        return weeklyTemplateRepository.getAllTemplates()
    }
}
