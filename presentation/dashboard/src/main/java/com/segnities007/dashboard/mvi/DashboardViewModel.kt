package com.segnities007.dashboard.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.repository.ItemCheckStateRepository
import com.segnities007.repository.ItemRepository
import com.segnities007.repository.WeeklyTemplateRepository
import com.segnities007.ui.mvi.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class DashboardViewModel(
    private val itemRepository: ItemRepository,
    private val weeklyTemplateRepository: WeeklyTemplateRepository,
    private val itemCheckStateRepository: ItemCheckStateRepository,
    private val reducer: DashboardReducer = DashboardReducer(),
) : BaseViewModel<DashboardIntent, DashboardState, DashboardEffect>(DashboardState()) {
    init {
        viewModelScope.launch {
            handleIntent(DashboardIntent.LoadDashboardData)
        }
    }

    override suspend fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadDashboardData -> loadDashboardData()
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun loadDashboardData() {
        setState { state -> reducer.reduce(state, DashboardIntent.LoadDashboardData) }
        try {
            val allItems = itemRepository.getAllItems()
            val itemCount = allItems.size
            val templateCount = weeklyTemplateRepository.getAllTemplates().size
            val uncheckedItems = itemRepository.getUncheckedItemsForToday()
            val today =
                Clock.System
                    .now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
            val templatesForToday = weeklyTemplateRepository.getTemplatesForDay(today.dayOfWeek.name)
            val itemIdsForToday = templatesForToday.flatMap { it.itemIds }.distinct()
            val scheduledItemCountToday = itemIdsForToday.size
            val checkStates = itemCheckStateRepository.getCheckStatesForItems(itemIdsForToday)
            val checkedItemCountToday =
                checkStates.count { state ->
                    state.history.any { it.date == today && it.isChecked }
                }
            val completionRateToday =
                if (scheduledItemCountToday > 0) {
                    ((checkedItemCountToday.toDouble() / scheduledItemCountToday.toDouble()) * 100.0).toInt()
                } else {
                    0
                }

            // Historical completion stats across all recorded histories
            val allItemIds = allItems.map { it.id }
            val allCheckStates = itemCheckStateRepository.getCheckStatesForItems(allItemIds)
            val totalRecordsCount = allCheckStates.sumOf { it.history.size }
            val totalCheckedRecordsCount = allCheckStates.sumOf { state -> state.history.count { record -> record.isChecked } }
            val historicalCompletionRate =
                if (totalRecordsCount > 0) {
                    ((totalCheckedRecordsCount.toDouble() / totalRecordsCount.toDouble()) * 100.0).toInt()
                } else {
                    0
                }

            // Unchecked items for tomorrow
            val tomorrow = today.plus(1, DateTimeUnit.DAY)
            val templatesForTomorrow = weeklyTemplateRepository.getTemplatesForDay(tomorrow.dayOfWeek.name)
            val itemIdsForTomorrow = templatesForTomorrow.flatMap { it.itemIds }.distinct()
            val itemsForTomorrow = allItems.filter { itemIdsForTomorrow.contains(it.id) }
            val checkStatesForTomorrow = itemCheckStateRepository.getCheckStatesForItems(itemIdsForTomorrow)
            val checkStateMapByItemId = checkStatesForTomorrow.associateBy { it.itemId }
            val uncheckedItemsTomorrow =
                itemsForTomorrow.filter { item ->
                    val state = checkStateMapByItemId[item.id]
                    val record = state?.history?.find { it.date == tomorrow }
                    // Missing record means unchecked; existing false also unchecked
                    record?.isChecked != true
                }

            setState {
                it.copy(
                    isLoading = false,
                    itemCount = itemCount,
                    templateCount = templateCount,
                    uncheckedItemsToday = uncheckedItems,
                    scheduledItemCountToday = scheduledItemCountToday,
                    checkedItemCountToday = checkedItemCountToday,
                    completionRateToday = completionRateToday,
                    totalRecordsCount = totalRecordsCount,
                    totalCheckedRecordsCount = totalCheckedRecordsCount,
                    historicalCompletionRate = historicalCompletionRate,
                    uncheckedItemsTomorrow = uncheckedItemsTomorrow,
                )
            }
        } catch (e: Exception) {
            val errorMessage = e.localizedMessage ?: "不明なエラーが発生しました"
            setState { it.copy(isLoading = false, error = errorMessage) }
//            sendEffect { DashboardEffect.ShowError("データの読み込みに失敗しました: $errorMessage") }
        }
    }
}
