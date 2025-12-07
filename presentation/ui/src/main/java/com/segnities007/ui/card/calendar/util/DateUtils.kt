package com.segnities007.ui.card.calendar.util

import com.segnities007.model.WeeklyTemplate
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

fun japaneseWeekday(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
    DayOfWeek.SUNDAY -> "日"
    DayOfWeek.MONDAY -> "月"
    DayOfWeek.TUESDAY -> "火"
    DayOfWeek.WEDNESDAY -> "水"
    DayOfWeek.THURSDAY -> "木"
    DayOfWeek.FRIDAY -> "金"
    DayOfWeek.SATURDAY -> "土"
}

fun hasTemplateFor(date: LocalDate, templates: List<WeeklyTemplate>): Boolean {
    val modelDay = date.dayOfWeek
    return templates.any { modelDay in it.daysOfWeek }
}

