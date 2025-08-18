package com.segnities007.local.converter

import androidx.room.TypeConverter

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
    fun fromList(value: List<Int>): String = value.joinToString(",")

    @TypeConverter
    fun toList(value: String): List<Int> = if (value.isEmpty()) emptyList() else value.split(",").map { it.toInt() }
}
