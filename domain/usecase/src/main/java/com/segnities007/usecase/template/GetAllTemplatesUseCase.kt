package com.segnities007.usecase.template

import com.segnities007.model.WeeklyTemplate
import com.segnities007.repository.WeeklyTemplateRepository

/**
 * 全てのテンプレートを取得するUse Case
 */
class GetAllTemplatesUseCase(
    private val weeklyTemplateRepository: WeeklyTemplateRepository
) {
    suspend operator fun invoke(): Result<List<WeeklyTemplate>> {
        return try {
            val templates = weeklyTemplateRepository.getAllTemplates()
            Result.success(templates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
