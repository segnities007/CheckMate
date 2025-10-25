package com.segnities007.ui.card.calendar.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

fun weekDaysStartingSunday(centerDate: LocalDate): List<LocalDate> {
    val start = centerDate.minus(when (centerDate.dayOfWeek) {
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
    }, DateTimeUnit.DAY)

    return (0..6).map { start.plus(it, DateTimeUnit.DAY) }
}

