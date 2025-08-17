package com.segnities007.local.dao

import androidx.room.*
import com.segnities007.local.entity.WeeklyTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyTemplateDao {

    @Query("SELECT * FROM weekly_templates WHERE dayOfWeek = :dayOfWeek")
    fun getTemplatesByDay(dayOfWeek: String): Flow<List<WeeklyTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WeeklyTemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: WeeklyTemplateEntity)

    @Query("DELETE FROM weekly_templates WHERE dayOfWeek = :dayOfWeek")
    suspend fun deleteByDay(dayOfWeek: String)
}
