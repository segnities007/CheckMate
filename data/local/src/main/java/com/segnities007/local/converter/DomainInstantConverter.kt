package com.segnities007.local.converter

import androidx.room.TypeConverter
import com.segnities007.model.DomainInstant


class DomainInstantConverter {
    @TypeConverter
    fun fromDomainInstant(value: DomainInstant): Long = value.epochMillis

    @TypeConverter
    fun toDomainInstant(value: Long): DomainInstant = DomainInstant(value)
}