package com.segnities007.local.dao

import androidx.room.*
import com.segnities007.local.entity.WeeklyTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyTemplateDao {

    @Query("SELECT * FROM weekly_templates WHERE dayOfWeek = :day")
    fun getTemplatesForDay(day: String): Flow<List<WeeklyTemplateEntity>>

    @Query("SELECT * FROM weekly_templates")
    suspend fun getAllTemplates(): List<WeeklyTemplateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: WeeklyTemplateEntity)

    @Update
    suspend fun update(template: WeeklyTemplateEntity)

    @Delete
    suspend fun delete(template: WeeklyTemplateEntity)
}
