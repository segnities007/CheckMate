package com.segnities007.repository

import com.segnities007.local.dao.ItemDao
import com.segnities007.local.dao.ItemCheckStateDao // ItemCheckStateDaoをインポート
import com.segnities007.local.entity.toDomain
import com.segnities007.local.entity.toEntity
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCheckRecord
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.ExperimentalTime

class ItemRepositoryImpl(
    private val itemDao: ItemDao,
    private val itemCheckStateDao: ItemCheckStateDao // ItemCheckStateDaoをコンストラクタに追加
) : ItemRepository {
    override suspend fun getAllItems(): List<Item> = itemDao.getAll().map { it.toDomain() }

    override suspend fun getItemById(id: Int): Item? = itemDao.getById(id)?.toDomain()

    override suspend fun insertItem(item: Item) {
        itemDao.insert(item.toEntity())
    }

    override suspend fun deleteItem(id: Int) {
        itemDao.deleteById(id)
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun getUncheckedItemsForToday(): List<Item> {
        val today = kotlin.time.Clock.System.todayIn(TimeZone.currentSystemDefault())
        val allItems = itemDao.getAll().map { it.toDomain() }
        val uncheckedItems = mutableListOf<Item>()

        for (item in allItems) {
            val checkStateEntity = itemCheckStateDao.getByItemId(item.id) 
            val checkState = checkStateEntity?.toDomain()
            if (checkState != null) {
                val todayRecord = checkState.history.find { it.date == today }
                if (todayRecord == null || !todayRecord.isChecked) {
                    uncheckedItems.add(item)
                }
            } else {
                uncheckedItems.add(item)
            }
        }
        return uncheckedItems
    }
}
