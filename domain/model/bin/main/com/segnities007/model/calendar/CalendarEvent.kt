package com.segnities007.model.calendar


import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable

data class CalendarEvent(
    val id: String,
    val title: String,
    val description: String? = null,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val location: String? = null,
    val recurrenceRule: RecurrenceRule? = null,
    val categories: List<String> = emptyList(),
)

@Serializable

data class RecurrenceRule(
    val frequency: Frequency,
    val interval: Int = 1,
    val until: LocalDateTime? = null,
    val count: Int? = null,
    val byDay: List<DayOfWeek> = emptyList(),
)

@Serializable

enum class Frequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
}

@Serializable

enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY,
}

@Serializable

data class EventGroup(
    val dayOfWeek: DayOfWeek,
    val timeSlot: TimeSlot,
    val events: List<CalendarEvent>,
)

@Serializable

enum class TimeSlot {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT,
}
