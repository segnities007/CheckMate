package com.segnities007.usecase.ics

import com.segnities007.model.WeeklyTemplate
import com.segnities007.repository.IcsTemplateRepository

/**
 * 生成されたテンプレートを保存するUse Case
 */
class SaveGeneratedTemplatesUseCase(
    private val icsTemplateRepository: IcsTemplateRepository
) {
    suspend operator fun invoke(templates: List<WeeklyTemplate>): Result<Unit> {
        return try {
            icsTemplateRepository.saveGeneratedTemplates(templates)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
