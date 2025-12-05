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
import kotlinx.coroutines.flow.first
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

    override suspend fun generateTemplatesFromIcs(uri: String): List<WeeklyTemplate> {
        val icsContent = readIcsFile(Uri.parse(uri))
        val events = parseIcsContent(icsContent)
        return generateTemplatesFromEvents(events)
    }

    override suspend fun generateTemplatesFromEvents(events: List<CalendarEvent>): List<WeeklyTemplate> {
        // 重複イベント除外（同一タイトル + 開始/終了が同じ）
        val dedupedEvents = deduplicateEvents(events)
        val removedCount = events.size - dedupedEvents.size
        if (removedCount > 0) {
            Log.d("IcsTemplate", "Deduplicated $removedCount duplicate ICS events (original=${events.size}, unique=${dedupedEvents.size})")
        }
        // 週パターン圧縮: 同じ曜日+開始時刻+タイトル(正規化) の繰り返しを1件代表にまとめる
        val patternCollapsed = collapseWeeklyPatterns(dedupedEvents)
        if (patternCollapsed.size < dedupedEvents.size) {
            Log.d("IcsTemplate", "Weekly pattern collapse: reduced ${dedupedEvents.size} -> ${patternCollapsed.size}")
        }
        val eventGroups = groupEventsByDayAndTime(patternCollapsed)
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

    // ===== Signature-based cache key (date independent, normalized) =====
    private fun signatureKey(e: CalendarEvent): String =
        buildString {
            append(normalizeTitleForPattern(e.title))
            append('|')
            append(e.startDateTime.dayOfWeek.name)
            append('|')
            append(e.startDateTime.hour)
            append('|')
            val duration =
                ((e.endDateTime.hour * 60 + e.endDateTime.minute) - (e.startDateTime.hour * 60 + e.startDateTime.minute))
                    .coerceAtLeast(
                        0,
                    )
            append(duration / 15 * 15) // bucket 15min
            append('|')
            append("v2") // prompt/version tag sync
        }

    // 学生用途に偏らないため動的にルールを決定 (学術イベント比率で academic / general を切替)
    private fun buildFallbackRules(events: List<CalendarEvent>): List<Pair<Regex, List<String>>> {
        val academicIndicator = Regex("(授業|講義|class|lesson|英語|数学|国語|理科|試験|テスト|exam)")
        val academicCount = events.count { academicIndicator.containsMatchIn(it.title.lowercase()) }
        val ratio = if (events.isEmpty()) 0.0 else academicCount.toDouble() / events.size
        val academic =
            listOf(
                Regex("英語|English", RegexOption.IGNORE_CASE) to listOf("辞書", "ノート", "筆箱"),
                Regex("国語|現代文|古文") to listOf("ノート", "筆箱"),
                Regex("数学|算数|Math", RegexOption.IGNORE_CASE) to listOf("ノート", "筆箱", "電卓"),
                Regex("理科|化学|物理|Science", RegexOption.IGNORE_CASE) to listOf("ノート", "筆箱"),
                Regex("体育|スポーツ|Sports|yoga|ヨガ|run|ラン", RegexOption.IGNORE_CASE) to listOf("ドリンク", "タオル", "ハンカチ"),
                Regex("美術|Art|図工", RegexOption.IGNORE_CASE) to listOf("筆箱", "スケッチブック"),
                Regex("音楽|Music", RegexOption.IGNORE_CASE) to listOf("筆箱", "ノート"),
                Regex("試験|テスト|exam", RegexOption.IGNORE_CASE) to listOf("筆箱", "ノート", "pc"),
            )
        val general =
            listOf(
                Regex("meeting|mtg|面談|面接|打合せ|打ち合わせ", RegexOption.IGNORE_CASE) to listOf("pc", "ノート", "筆箱", "充電器"),
                Regex("出社|出張|office|勤務|work", RegexOption.IGNORE_CASE) to listOf("pc", "財布", "鍵", "充電器"),
                Regex("スポーツ|運動|yoga|ヨガ|run|ラン", RegexOption.IGNORE_CASE) to listOf("ドリンク", "タオル", "ハンカチ"),
                Regex("サロン|美容|美容院|理髪|hair|カット", RegexOption.IGNORE_CASE) to listOf("財布", "目薬", "ハンカチ"),
                Regex("旅行|trip|travel|旅", RegexOption.IGNORE_CASE) to listOf("財布", "充電器", "pc"),
                Regex("面接|interview", RegexOption.IGNORE_CASE) to listOf("pc", "筆箱", "財布"),
                Regex("買い物|shopping", RegexOption.IGNORE_CASE) to listOf("財布", "エコバッグ"),
            )
        // 学術割合が 30% 以上なら academic + general (少し一般も) そうでなければ general + 一部 academic
        return if (ratio >= 0.3) academic + general.take(3) else general + academic.take(3)
    }

    private data class CacheEntry(
        val ids: List<Int>,
        val timestamp: Long,
    )

    private val recommendationCache = LinkedHashMap<String, CacheEntry>(100, 0.75f, true)
    private val cacheTtlMs = 24 * 60 * 60 * 1000L // 24h
    private val maxCacheEntries = 200

    private fun evictIfNeeded() {
        val now = System.currentTimeMillis()
        val iter = recommendationCache.entries.iterator()
        while (iter.hasNext()) {
            val e = iter.next()
            if (now - e.value.timestamp > cacheTtlMs) iter.remove()
        }
        if (recommendationCache.size > maxCacheEntries) {
            // LRU 先頭から削除
            val overflow = recommendationCache.size - maxCacheEntries
            repeat(overflow) {
                val firstKey = recommendationCache.entries.firstOrNull()?.key ?: return
                recommendationCache.remove(firstKey)
            }
        }
    }

    private fun putCache(
        key: String,
        ids: List<Int>,
    ) {
        evictIfNeeded()
        recommendationCache[key] = CacheEntry(ids, System.currentTimeMillis())
    }

    private fun getCache(key: String): List<Int>? {
        val entry = recommendationCache[key] ?: return null
        return if (System.currentTimeMillis() - entry.timestamp <= cacheTtlMs) entry.ids else null
    }

    private fun keywordFilterItems(
        items: List<Item>,
        events: List<CalendarEvent>,
    ): List<Item> {
        if (items.size <= 30) return items
        val joined = events.joinToString(" ") { it.title + " " + (it.description ?: "") }
        val lowered = joined.lowercase()
        val studyKeywords = listOf("class", "授業", "lesson", "講義", "英", "数", "math", "english")
        val sportKeywords = listOf("sport", "体育", "yoga", "ヨガ", "run")
        val artKeywords = listOf("art", "美術", "図工")
        val matchedCats = mutableSetOf<ItemCategory>()
        if (studyKeywords.any { lowered.contains(it) }) matchedCats += ItemCategory.STUDY_SUPPLIES
        if (sportKeywords.any { lowered.contains(it) }) matchedCats += ItemCategory.CLOTHING_SUPPLIES
        if (artKeywords.any { lowered.contains(it) }) matchedCats += ItemCategory.HOBBY_SUPPLIES
        if (matchedCats.isEmpty()) return items.take(40)
        val filtered = items.filter { it.category in matchedCats }
        return if (filtered.isNotEmpty()) filtered.take(40) else items.take(40)
    }

    private fun fallbackItemsFor(
        event: CalendarEvent,
        allItems: List<Item>,
        fallbackRules: List<Pair<Regex, List<String>>>,
    ): List<Int> {
        val rule = fallbackRules.firstOrNull { it.first.containsMatchIn(event.title) } ?: return emptyList()
        val wantedNames = rule.second
        return allItems
            .filter { wantedNames.any { wn -> it.name.contains(wn, ignoreCase = true) } }
            .map { it.id }
            .take(6)
    }

    private suspend fun generateItemIdsForEvents(events: List<CalendarEvent>): List<Int> {
        val allItemsFull = itemRepository.getAllItems().first()
        val dynamicFallbackRules = buildFallbackRules(events)
        val allItems = keywordFilterItems(allItemsFull, events)
        if (events.isEmpty()) return emptyList()
        val resultIds = mutableListOf<Int>()
        // group by signature to reduce queries
        val groups = events.groupBy { signatureKey(it) }
        val signatureRepresentative = groups.mapValues { (_, list) -> list.minByOrNull { it.startDateTime }!! }
        val (cachedSigs, toQuerySigs) = signatureRepresentative.values.partition { getCache(signatureKey(it)) != null }
        cachedSigs.forEach { rep ->
            getCache(signatureKey(rep))?.let { ids ->
                groups[signatureKey(rep)]!!.forEach { _ -> resultIds += ids }
            }
        }
        val toQueryEvents = toQuerySigs
        if (toQueryEvents.isNotEmpty()) {
            val batchMap =
                try {
                    geminiAiService.recommendItemsForEvents(toQueryEvents, allItems)
                } catch (e: Exception) {
                    Log.e("IcsTemplate", "Batch recommend failure", e)
                    emptyMap()
                }
            toQueryEvents.forEach { e ->
                val ids = batchMap[e.id].orEmpty().ifEmpty { fallbackItemsFor(e, allItemsFull, dynamicFallbackRules) }
                putCache(signatureKey(e), ids)
                // expand to all events sharing the signature
                groups[signatureKey(e)]?.forEach { _ -> resultIds += ids }
            }
        }
        return resultIds.distinct()
    }

    private fun deduplicateEvents(events: List<CalendarEvent>): List<CalendarEvent> = events.distinctBy { dedupKey(it) }

    private fun dedupKey(e: CalendarEvent): String =
        buildString {
            append(e.title.trim())
            append('#')
            append(e.startDateTime)
            append('#')
            append(e.endDateTime)
        }

    // ===== Weekly pattern collapse =====
    private fun collapseWeeklyPatterns(events: List<CalendarEvent>): List<CalendarEvent> {
        if (events.isEmpty()) return emptyList()
        // key = dayOfWeek + startHour + normalizedTitle
        val map = LinkedHashMap<String, CalendarEvent>()
        events.forEach { e ->
            val key =
                buildString {
                    append(e.startDateTime.dayOfWeek.name)
                    append('#')
                    append(e.startDateTime.hour)
                    append('#')
                    append(normalizeTitleForPattern(e.title))
                }
            val existing = map[key]
            if (existing == null || e.startDateTime < existing.startDateTime) {
                map[key] = e
            }
        }
        return map.values.toList()
    }

    private fun normalizeTitleForPattern(raw: String): String =
        raw
            .lowercase()
            .replace("（", "(")
            .replace("）", ")")
            .replace(Regex("[\\[\\]]"), "") // remove brackets
            .replace(Regex("[\n\t]"), " ") // newline / tab -> space
            .replace("\u3000", " ") // full-width space
            .replace(Regex("\\s+"), " ") // collapse whitespace
            .trim()

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
