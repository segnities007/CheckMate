package com.segnities007.repository

import android.content.Context
import java.io.File
import java.io.IOException

class ImageRepositoryImpl(
    private val context: Context,
) : ImageRepository {
    /**
     * アプリ内ストレージに画像を保存。
     * @param imagePath 保存元のファイルパス
     * @param fileName 保存先のファイル名
     * @return 保存後のパス
     */
    override suspend fun saveImage(
        imagePath: String,
        fileName: String,
    ): String {
        val srcFile = File(imagePath)
        if (!srcFile.exists()) throw IOException("Source file does not exist")

        val destFile = File(context.filesDir, fileName)
        srcFile.copyTo(destFile, overwrite = true)
        return destFile.absolutePath
    }

    /**
     * 保存済み画像を削除
     */
    override suspend fun deleteImage(path: String) {
        val file = File(path)
        if (file.exists()) file.delete()
    }
}
