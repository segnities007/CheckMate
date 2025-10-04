package com.segnities007.usecase.template

import com.segnities007.model.WeeklyTemplate
import com.segnities007.repository.WeeklyTemplateRepository

/**
 * テンプレートを削除するUse Case
 */
class DeleteTemplateUseCase(
    private val weeklyTemplateRepository: WeeklyTemplateRepository
) {
    suspend operator fun invoke(template: WeeklyTemplate): Result<Unit> {
        return try {
            weeklyTemplateRepository.deleteTemplate(template)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
