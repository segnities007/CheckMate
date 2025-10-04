package com.segnities007.usecase.template

import com.segnities007.model.WeeklyTemplate
import com.segnities007.repository.WeeklyTemplateRepository

/**
 * テンプレートを更新するUse Case
 */
class UpdateTemplateUseCase(
    private val weeklyTemplateRepository: WeeklyTemplateRepository
) {
    suspend operator fun invoke(template: WeeklyTemplate): Result<Unit> {
        return try {
            if (template.title.isBlank()) {
                return Result.failure(IllegalArgumentException("テンプレート名は必須です"))
            }
            weeklyTemplateRepository.updateTemplate(template)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
