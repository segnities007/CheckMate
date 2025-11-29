package com.segnities007.ui.card.calendar.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

fun weekDaysStartingOn(centerDate: LocalDate, startDay: DayOfWeek = DayOfWeek.SUNDAY): List<LocalDate> {
    val offset = when (centerDate.dayOfWeek) {
        startDay -> 0
        DayOfWeek.MONDAY -> if (startDay == DayOfWeek.SUNDAY) 1 else ((DayOfWeek.MONDAY.ordinal - startDay.ordinal + 7) % 7)
        DayOfWeek.TUESDAY -> if (startDay == DayOfWeek.SUNDAY) 2 else ((DayOfWeek.TUESDAY.ordinal - startDay.ordinal + 7) % 7)
        DayOfWeek.WEDNESDAY -> if (startDay == DayOfWeek.SUNDAY) 3 else ((DayOfWeek.WEDNESDAY.ordinal - startDay.ordinal + 7) % 7)
        DayOfWeek.THURSDAY -> if (startDay == DayOfWeek.SUNDAY) 4 else ((DayOfWeek.THURSDAY.ordinal - startDay.ordinal + 7) % 7)
        DayOfWeek.FRIDAY -> if (startDay == DayOfWeek.SUNDAY) 5 else ((DayOfWeek.FRIDAY.ordinal - startDay.ordinal + 7) % 7)
        DayOfWeek.SATURDAY -> if (startDay == DayOfWeek.SUNDAY) 6 else ((DayOfWeek.SATURDAY.ordinal - startDay.ordinal + 7) % 7)
        else -> 0
    }

    val start = centerDate.minus(offset, DateTimeUnit.DAY)
    return (0..6).map { start.plus(it, DateTimeUnit.DAY) }
}