package com.segnities007.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.segnities007.local.dao.WeeklyTemplateDao
import com.segnities007.local.entity.toEntity
import com.segnities007.model.DayOfWeek
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.calendar.CalendarEvent
import com.segnities007.model.calendar.EventGroup
import com.segnities007.model.calendar.TimeSlot
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.remote.GeminiAiService
import com.segnities007.repository.BuildConfig
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import com.segnities007.model.calendar.DayOfWeek as CalendarDayOfWeek

@OptIn(ExperimentalTime::class)
class IcsTemplateRepositoryImpl(
    private val context: Context,
    private val weeklyTemplateDao: WeeklyTemplateDao,
    private val itemRepository: ItemRepository,
) : IcsTemplateRepository {
    private val geminiAiService: GeminiAiService by lazy {
        GeminiAiService(BuildConfig.GEMINI_API_KEY)
    }

    override suspend fun generateTemplatesFromIcs(uri: Uri): List<WeeklyTemplate> {
        val icsContent = readIcsFile(uri)
        val events = parseIcsContent(icsContent)
        return generateTemplatesFromEvents(events)
    }

    override suspend fun generateTemplatesFromEvents(events: List<CalendarEvent>): List<WeeklyTemplate> {
        val eventGroups = groupEventsByDayAndTime(events)
        return eventGroups.map { group ->
            WeeklyTemplate(
                title = generateTemplateTitle(group),
                daysOfWeek = setOf(convertToDomainDayOfWeek(group.dayOfWeek)),
                itemIds = generateItemIdsForEvents(group.events),
            )
        }
    }

    override suspend fun saveGeneratedTemplates(templates: List<WeeklyTemplate>) {
        templates.forEach { template ->
            weeklyTemplateDao.insert(template.toEntity())
        }
    }

    private fun readIcsFile(uri: Uri): String =
        context.contentResolver
            .openInputStream(uri)
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: throw IllegalStateException("ICSファイルが読み込めません")

    private fun parseIcsContent(icsContent: String): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()
        val lines = icsContent.lines()
        var currentEvent: MutableMap<String, String>? = null

        for (line in lines) {
            when {
                line.startsWith("BEGIN:VEVENT") -> {
                    currentEvent = mutableMapOf()
                }
                line.startsWith("END:VEVENT") -> {
                    currentEvent?.let { eventData ->
                        events.add(parseEventFromData(eventData))
                    }
                    currentEvent = null
                }
                currentEvent != null -> {
                    parseIcsLine(line, currentEvent)
                }
            }
        }

        return events
    }

    private fun parseIcsLine(
        line: String,
        eventData: MutableMap<String, String>,
    ) {
        val colonIndex = line.indexOf(':')
        if (colonIndex > 0) {
            val key = line.substring(0, colonIndex)
            val value = line.substring(colonIndex + 1)
            eventData[key] = value
        }
    }

    private fun parseEventFromData(eventData: Map<String, String>): CalendarEvent =
        CalendarEvent(
            id = eventData["UID"] ?: generateId(),
            title = eventData["SUMMARY"] ?: "無題のイベント",
            description = eventData["DESCRIPTION"],
            startDateTime = parseDateTime(eventData["DTSTART"]),
            endDateTime = parseDateTime(eventData["DTEND"]),
            location = eventData["LOCATION"],
            recurrenceRule = eventData["RRULE"]?.let { parseRecurrenceRule(it) },
            categories = eventData["CATEGORIES"]?.split(",")?.map { it.trim() } ?: emptyList(),
        )

    private fun parseDateTime(dateTimeString: String?): LocalDateTime {
        if (dateTimeString == null) {
            return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }

        // 基本的な日時パース（実際の実装ではより堅牢にする必要があります）
        return try {
            // 例: 20231201T090000Z または 20231201T090000
            val cleanString = dateTimeString.replace("Z", "").replace("T", "")
            val year = cleanString.substring(0, 4).toInt()
            val month = cleanString.substring(4, 6).toInt()
            val day = cleanString.substring(6, 8).toInt()
            val hour = cleanString.substring(8, 10).toInt()
            val minute = cleanString.substring(10, 12).toInt()

            LocalDateTime(year, month, day, hour, minute, 0, 0)
        } catch (e: Exception) {
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    private fun parseRecurrenceRule(rrule: String): com.segnities007.model.calendar.RecurrenceRule? {
        // 基本的なRRULEパース（実際の実装ではより堅牢にする必要があります）
        return try {
            val parts = rrule.split(";")
            val frequency =
                when {
                    parts.any { it.startsWith("FREQ=") } -> {
                        val freq = parts.find { it.startsWith("FREQ=") }?.substring(5)
                        when (freq) {
                            "DAILY" -> com.segnities007.model.calendar.Frequency.DAILY
                            "WEEKLY" -> com.segnities007.model.calendar.Frequency.WEEKLY
                            "MONTHLY" -> com.segnities007.model.calendar.Frequency.MONTHLY
                            "YEARLY" -> com.segnities007.model.calendar.Frequency.YEARLY
                            else -> com.segnities007.model.calendar.Frequency.WEEKLY
                        }
                    }
                    else -> com.segnities007.model.calendar.Frequency.WEEKLY
                }

            com.segnities007.model.calendar
                .RecurrenceRule(frequency = frequency)
        } catch (e: Exception) {
            null
        }
    }

    private fun groupEventsByDayAndTime(events: List<CalendarEvent>): List<EventGroup> =
        events
            .groupBy { event ->
                val dayOfWeek =
                    when (event.startDateTime.dayOfWeek) {
                        kotlinx.datetime.DayOfWeek.MONDAY -> CalendarDayOfWeek.MONDAY
                        kotlinx.datetime.DayOfWeek.TUESDAY -> CalendarDayOfWeek.TUESDAY
                        kotlinx.datetime.DayOfWeek.WEDNESDAY -> CalendarDayOfWeek.WEDNESDAY
                        kotlinx.datetime.DayOfWeek.THURSDAY -> CalendarDayOfWeek.THURSDAY
                        kotlinx.datetime.DayOfWeek.FRIDAY -> CalendarDayOfWeek.FRIDAY
                        kotlinx.datetime.DayOfWeek.SATURDAY -> CalendarDayOfWeek.SATURDAY
                        kotlinx.datetime.DayOfWeek.SUNDAY -> CalendarDayOfWeek.SUNDAY
                    }
                val timeSlot = getTimeSlot(event.startDateTime)
                Pair(dayOfWeek, timeSlot)
            }.map { (key, groupEvents) ->
                EventGroup(
                    dayOfWeek = key.first,
                    timeSlot = key.second,
                    events = groupEvents,
                )
            }

    private fun getTimeSlot(dateTime: LocalDateTime): TimeSlot =
        when (dateTime.hour) {
            in 5..11 -> TimeSlot.MORNING
            in 12..17 -> TimeSlot.AFTERNOON
            in 18..21 -> TimeSlot.EVENING
            else -> TimeSlot.NIGHT
        }

    private fun generateTemplateTitle(group: EventGroup): String {
        val dayName =
            when (group.dayOfWeek) {
                CalendarDayOfWeek.MONDAY -> "月曜"
                CalendarDayOfWeek.TUESDAY -> "火曜"
                CalendarDayOfWeek.WEDNESDAY -> "水曜"
                CalendarDayOfWeek.THURSDAY -> "木曜"
                CalendarDayOfWeek.FRIDAY -> "金曜"
                CalendarDayOfWeek.SATURDAY -> "土曜"
                CalendarDayOfWeek.SUNDAY -> "日曜"
            }

        val timeName =
            when (group.timeSlot) {
                TimeSlot.MORNING -> "朝"
                TimeSlot.AFTERNOON -> "昼"
                TimeSlot.EVENING -> "夕方"
                TimeSlot.NIGHT -> "夜"
            }

        val eventNames = group.events.map { it.title }.take(2)
        val eventSuffix = if (eventNames.isNotEmpty()) " (${eventNames.joinToString(", ")})" else ""

        return "$dayName$timeName$eventSuffix"
    }

    private suspend fun generateItemIdsForEvents(events: List<CalendarEvent>): List<Int> {
        val allItems = itemRepository.getAllItems()

        return events
            .flatMap { event ->
                try {
                    // Gemini AIによる推奨
                    val aiRecommendedIds = geminiAiService.recommendItemsForEvent(event, allItems)

                    Log.d(
                        "IcsTemplate",
                        "Gemini AI推奨: Event=${event.title}, Items=${aiRecommendedIds.map { id ->
                            allItems.find { it.id == id }?.name ?: "Unknown"
                        }}",
                    )

                    aiRecommendedIds
                } catch (e: Exception) {
                    Log.e("IcsTemplate", "Gemini AI推奨でエラー: Event=${event.title}", e)
                    // エラー時は空のリストを返す（ルールベース推奨は使用しない）
                    emptyList<Int>()
                }
            }.distinct()
    }

    private fun convertToDomainDayOfWeek(calendarDayOfWeek: CalendarDayOfWeek): DayOfWeek =
        when (calendarDayOfWeek) {
            CalendarDayOfWeek.MONDAY -> DayOfWeek.MONDAY
            CalendarDayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
            CalendarDayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
            CalendarDayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
            CalendarDayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
            CalendarDayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
            CalendarDayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
        }

    private fun generateId(): String = "event_${System.currentTimeMillis()}_${(0..999).random()}"
}
