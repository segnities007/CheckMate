package com.segnities007.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.segnities007.local.converter.DomainInstantConverter
import com.segnities007.local.converter.ItemCategoryConverter
import com.segnities007.local.dao.ItemDao
import com.segnities007.local.entity.ItemEntity

@Database(entities = [ItemEntity::class], version = 1)
@TypeConverters(
    ItemCategoryConverter::class,
    DomainInstantConverter::class,
    ItemCategoryConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}