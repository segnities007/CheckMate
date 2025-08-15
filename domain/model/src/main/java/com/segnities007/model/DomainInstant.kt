package com.segnities007.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@JvmInline
value class DomainInstant(val epochMillis: Long) {
    @OptIn(ExperimentalTime::class)
    fun toInstant(): Instant = Instant.fromEpochMilliseconds(epochMillis)
    companion object {
        @OptIn(ExperimentalTime::class)
        fun fromInstant(instant: Instant) = DomainInstant(instant.toEpochMilliseconds())
    }
}

