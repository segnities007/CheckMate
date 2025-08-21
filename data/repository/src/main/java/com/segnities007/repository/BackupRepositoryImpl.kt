package com.segnities007.repository

import com.segnities007.local.dao.ItemCheckStateDao
import com.segnities007.local.dao.ItemDao
import com.segnities007.local.dao.WeeklyTemplateDao
import com.segnities007.local.entity.toDomain
import com.segnities007.local.entity.toEntity
import com.segnities007.model.DayOfWeek
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.model.item.ItemCheckRecord
import com.segnities007.model.item.ItemCheckState
import com.segnities007.repository.model.ExportData
import com.segnities007.repository.model.toDomain
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.datetime.LocalDate
import kotlin.time.Instant
import kotlin.time.ExperimentalTime

class BackupRepositoryImpl(
    private val itemDao: ItemDao,
    private val itemCheckStateDao: ItemCheckStateDao,
    private val weeklyTemplateDao: WeeklyTemplateDao,
    private val json: Json = Json { prettyPrint = true }
) : BackupRepository {

    override suspend fun exportData(): String {
        val items = itemDao.getAll().map { it.toDomain() }
        val states = itemCheckStateDao.getAll().map { it.toDomain() }
        val templates = weeklyTemplateDao.getAll().map { it.toDomain() }

        val exportData = ExportData.fromDomain(items, states, templates)
        return json.encodeToString<ExportData>(exportData)
    }

    override suspend fun importData(jsonString: String) {
        val data = json.decodeFromString<ExportData>(jsonString)


        itemDao.clearAll()
        itemCheckStateDao.clearAll()
        weeklyTemplateDao.clearAll()

        itemDao.insertAll(data.items.map { it.toDomain().toEntity() })
        itemCheckStateDao.insertAll(data.states.map { it.toDomain().toEntity() })
        weeklyTemplateDao.insertAll(data.templates.map { it.toDomain().toEntity() })
    }
}

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString())
}

@OptIn(ExperimentalTime::class)
object InstantSerializer : KSerializer<Instant> {
    override val descriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())
}




