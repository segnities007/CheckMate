package com.segnities007.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.segnities007.local.dao.ItemCheckStateDao
import com.segnities007.local.dao.ItemDao
import com.segnities007.local.dao.WeeklyTemplateDao
import com.segnities007.local.entity.ItemCheckStateEntity
import com.segnities007.local.entity.ItemEntity
import com.segnities007.local.entity.WeeklyTemplateEntity

@Database(entities = [ItemEntity::class, WeeklyTemplateEntity::class, ItemCheckStateEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    abstract fun weeklyTemplateDao(): WeeklyTemplateDao

    abstract fun itemCheckStateDao(): ItemCheckStateDao
}
