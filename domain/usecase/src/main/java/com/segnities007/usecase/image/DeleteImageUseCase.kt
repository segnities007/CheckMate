package com.segnities007.usecase.image

import com.segnities007.repository.ImageRepository

/**
 * 画像を削除するUse Case
 */
class DeleteImageUseCase(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(path: String): Result<Unit> {
        return try {
            imageRepository.deleteImage(path)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
