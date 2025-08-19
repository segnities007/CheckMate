package com.segnities007.local.converter

import androidx.room.TypeConverter
import com.segnities007.model.DayOfWeek

class WeeklyTemplateConverters {
    @TypeConverter
    fun fromDayOfWeekSetToString(days: Set<DayOfWeek>): String = days.joinToString(",") { it.name }

    @TypeConverter
    fun toDayOfWeekSet(data: String): Set<DayOfWeek> =
        if (data.isEmpty()) {
            emptySet()
        } else {
            data.split(',').map { dayString -> DayOfWeek.valueOf(dayString) }.toSet()
        }

    @TypeConverter
    fun fromIntList(list: List<Int>): String = list.joinToString(",")

    @TypeConverter
    fun toIntList(data: String): List<Int> = if (data.isEmpty()) emptyList() else data.split(",").map { it.toInt() }
}
