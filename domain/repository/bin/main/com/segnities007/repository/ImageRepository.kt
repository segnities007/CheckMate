package com.segnities007.repository

interface ImageRepository {
    suspend fun saveImage(
        imagePath: String,
        fileName: String,
    ): String

    suspend fun deleteImage(path: String)
}
