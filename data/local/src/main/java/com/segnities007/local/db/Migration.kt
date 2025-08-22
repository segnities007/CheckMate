package com.segnities007.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // itemsテーブルにbarcodeInfoとproductInfoカラムを追加
        database.execSQL(
            "ALTER TABLE items ADD COLUMN barcodeInfo TEXT"
        )
        database.execSQL(
            "ALTER TABLE items ADD COLUMN productInfo TEXT"
        )
    }
}
