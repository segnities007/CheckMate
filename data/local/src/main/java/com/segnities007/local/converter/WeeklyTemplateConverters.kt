package com.segnities007.local.converter

import androidx.room.TypeConverter
import com.segnities007.model.DayOfWeek

class WeeklyTemplateConverters {
    @TypeConverter
    fun fromMap(map: Map<Int, Boolean>): String = map.entries.joinToString(",") { "${it.key}:${it.value}" }

    @TypeConverter
    fun toMap(value: String): Map<Int, Boolean> =
        if (value.isEmpty()) {
            emptyMap()
        } else {
            value.split(",").associate {
                val (key, v) = it.split(":")
                key.toInt() to v.toBoolean()
            }
        }

    @TypeConverter
    fun fromDayOfWeekSetToString(days: Set<DayOfWeek>): String = days.joinToString(",") { it.name }

    @TypeConverter
    fun toDayOfWeekSet(data: String): Set<DayOfWeek> =
        if (data.isEmpty()) {
            emptySet()
        } else {
            data.split(',').map { dayString -> DayOfWeek.valueOf(dayString) }.toSet()
        }
}
