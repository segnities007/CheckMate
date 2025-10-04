package com.segnities007.dashboard.mvi

import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCheckState
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.checkstate.GetCheckStatesForItemsUseCase
import com.segnities007.usecase.item.GetAllItemsUseCase
import com.segnities007.usecase.template.GetAllTemplatesUseCase
import com.segnities007.usecase.template.GetTemplatesForDayUseCase
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class DashboardViewModel(
    private val getAllItemsUseCase: GetAllItemsUseCase,
    private val getAllTemplatesUseCase: GetAllTemplatesUseCase,
    private val getTemplatesForDayUseCase: GetTemplatesForDayUseCase,
    private val getCheckStatesForItemsUseCase: GetCheckStatesForItemsUseCase,
) : BaseViewModel<DashboardIntent, DashboardState, DashboardEffect>(DashboardState()) {

    companion object {
        /**
         * パーセンテージ変換用の定数（0.0～1.0 → 0～100）
         */
        private const val PERCENTAGE_MULTIPLIER = 100.0
    }

    init {
        sendIntent(DashboardIntent.LoadDashboardData)
    }

    override suspend fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadDashboardData -> loadDashboardData()
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun loadDashboardData() {
        setState { copy(isLoading = true, error = null) }

        val allItems = getAllItemsUseCase().getOrElse { e ->
            handleLoadError(e)
            return
        }

        val allTemplates = getAllTemplatesUseCase().getOrElse { e ->
            handleLoadError(e)
            return
        }

        processLoadedData(allItems, allTemplates)
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun processLoadedData(allItems: List<Item>, allTemplates: List<WeeklyTemplate>) {
        try {
            val itemCount = allItems.size
            val templateCount = allTemplates.size
            
            val today =
                Clock.System
                    .now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date

            val templatesForToday = getTemplatesForDayUseCase(today.dayOfWeek.name).getOrElse { e ->
                handleLoadError(e)
                return
            }

            val itemIdsForToday = templatesForToday.flatMap { it.itemIds }.distinct()
            val scheduledItemCountToday = itemIdsForToday.size
            
            val checkStates = getCheckStatesForItemsUseCase(itemIdsForToday).getOrElse { e ->
                handleLoadError(e)
                return
            }

            processTodayData(allItems, itemCount, templateCount, today, itemIdsForToday, scheduledItemCountToday, checkStates)
        } catch (e: Exception) {
            handleLoadError(e)
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun processTodayData(
        allItems: List<Item>,
        itemCount: Int,
        templateCount: Int,
        today: LocalDate,
        itemIdsForToday: List<Int>,
        scheduledItemCountToday: Int,
        checkStates: List<ItemCheckState>
    ) {
        try {
            val checkedItemCountToday =
                checkStates.count { state ->
                    state.history.any { it.date == today && it.isChecked }
                }
            val completionRateToday =
                if (scheduledItemCountToday > 0) {
                    ((checkedItemCountToday.toDouble() / scheduledItemCountToday.toDouble()) * PERCENTAGE_MULTIPLIER).toInt()
                } else {
                    0
                }

            val itemsForToday = allItems.filter { itemIdsForToday.contains(it.id) }
            val checkStateMapByItemId = checkStates.associateBy { it.itemId }
            val uncheckedItems = itemsForToday.filter { item ->
                val state = checkStateMapByItemId[item.id]
                val record = state?.history?.find { it.date == today }
                record?.isChecked != true
            }

            val allItemIds = allItems.map { it.id }
            val allCheckStates = getCheckStatesForItemsUseCase(allItemIds).getOrElse { e ->
                handleLoadError(e)
                return
            }

            val totalRecordsCount = allCheckStates.sumOf { it.history.size }
            val totalCheckedRecordsCount = allCheckStates.sumOf { state -> state.history.count { record -> record.isChecked } }
            val historicalCompletionRate =
                if (totalRecordsCount > 0) {
                    ((totalCheckedRecordsCount.toDouble() / totalRecordsCount.toDouble()) * PERCENTAGE_MULTIPLIER).toInt()
                } else {
                    0
                }

            val tomorrow = today.plus(1, DateTimeUnit.DAY)
            val templatesForTomorrow = getTemplatesForDayUseCase(tomorrow.dayOfWeek.name).getOrElse { e ->
                handleLoadError(e)
                return
            }

            val itemIdsForTomorrow = templatesForTomorrow.flatMap { it.itemIds }.distinct()
            val itemsForTomorrow = allItems.filter { itemIdsForTomorrow.contains(it.id) }
            
            val checkStatesForTomorrow = getCheckStatesForItemsUseCase(itemIdsForTomorrow).getOrElse { e ->
                handleLoadError(e)
                return
            }

            val checkStateMapByItemIdTomorrow = checkStatesForTomorrow.associateBy { it.itemId }
            val uncheckedItemsTomorrow =
                itemsForTomorrow.filter { item ->
                    val state = checkStateMapByItemIdTomorrow[item.id]
                    val record = state?.history?.find { it.date == tomorrow }
                    record?.isChecked != true
                }
            
            updateFinalState(itemCount, templateCount, uncheckedItems, scheduledItemCountToday, checkedItemCountToday, completionRateToday, totalRecordsCount, totalCheckedRecordsCount, historicalCompletionRate, uncheckedItemsTomorrow)
        } catch (e: Exception) {
            handleLoadError(e)
        }
    }

    private fun updateFinalState(
        itemCount: Int,
        templateCount: Int,
        uncheckedItems: List<Item>,
        scheduledItemCountToday: Int,
        checkedItemCountToday: Int,
        completionRateToday: Int,
        totalRecordsCount: Int,
        totalCheckedRecordsCount: Int,
        historicalCompletionRate: Int,
        uncheckedItemsTomorrow: List<Item>
    ) {

        setState {
            copy(
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
    }
    
    private fun handleLoadError(e: Throwable) {
        val errorMessage = e.localizedMessage ?: "不明なエラーが発生しました"
        setState { copy(isLoading = false, error = errorMessage) }
    }
}
