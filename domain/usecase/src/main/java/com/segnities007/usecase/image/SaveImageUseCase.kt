package com.segnities007.usecase.image

import com.segnities007.repository.ImageRepository

/**
 * 画像を保存するUse Case
 */
class SaveImageUseCase(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(imagePath: String, fileName: String): Result<String> {
        return try {
            val savedPath = imageRepository.saveImage(imagePath, fileName)
            Result.success(savedPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
