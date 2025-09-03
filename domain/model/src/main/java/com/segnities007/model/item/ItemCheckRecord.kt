package com.segnities007.model.item

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
@Immutable
data class ItemCheckRecord
    @OptIn(ExperimentalTime::class)
    constructor(
        val date: LocalDate =
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date,
        val isChecked: Boolean = false,
    )
