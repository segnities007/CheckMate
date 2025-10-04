package com.segnities007.usecase.ics

import android.net.Uri
import com.segnities007.model.WeeklyTemplate
import com.segnities007.repository.IcsTemplateRepository

/**
 * ICSファイルからテンプレートを生成するUse Case
 */
class GenerateTemplatesFromIcsUseCase(
    private val icsTemplateRepository: IcsTemplateRepository
) {
    suspend operator fun invoke(uri: Uri): Result<List<WeeklyTemplate>> {
        return try {
            val templates = icsTemplateRepository.generateTemplatesFromIcs(uri)
            Result.success(templates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
