package com.segnities007.usecase.template

import com.segnities007.repository.WeeklyTemplateRepository

/**
 * 全テンプレートを削除するUse Case
 */
class ClearAllTemplatesUseCase(
    private val weeklyTemplateRepository: WeeklyTemplateRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            weeklyTemplateRepository.clearAllTemplates()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
