package com.segnities007.local.dao

import androidx.room.*
import com.segnities007.local.entity.WeeklyTemplateEntity

@Dao
interface WeeklyTemplateDao {
    @Query("SELECT * FROM weekly_templates WHERE daysOfWeek LIKE '%' || :dayName || '%'")
    suspend fun getTemplatesForDay(dayName: String): List<WeeklyTemplateEntity>

    @Query("SELECT * FROM weekly_templates")
    suspend fun getAll(): List<WeeklyTemplateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: WeeklyTemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<WeeklyTemplateEntity>)

    @Update
    suspend fun update(template: WeeklyTemplateEntity)

    @Delete
    suspend fun delete(template: WeeklyTemplateEntity)

    @Query("DELETE FROM weekly_templates")
    suspend fun clearAll()
}
