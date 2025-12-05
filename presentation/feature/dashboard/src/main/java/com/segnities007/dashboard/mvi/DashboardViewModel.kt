package com.segnities007.dashboard.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCheckState
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.checkstate.GetCheckStatesForItemsUseCase
import com.segnities007.usecase.item.GetAllItemsUseCase
import com.segnities007.usecase.template.GetAllTemplatesUseCase
import com.segnities007.usecase.template.GetTemplatesForDayUseCase
import kotlinx.coroutines.launch
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
        // Start collecting items Flow immediately
        @OptIn(ExperimentalTime::class)
        viewModelScope.launch {
            getAllItemsUseCase().collect { allItems ->
                runCatching {
                    val allTemplates = getAllTemplatesUseCase().getOrThrow()
                    
                    val itemCount = allItems.size
                    val templateCount = allTemplates.size
                    
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val templatesForToday = getTemplatesForDayUseCase(today.dayOfWeek.name).getOrThrow()
                    
                    val itemIdsForToday = templatesForToday.flatMap { it.itemIds }.distinct()
                    val scheduledItemCountToday = itemIdsForToday.size
                    
                    val checkStates = getCheckStatesForItemsUseCase(itemIdsForToday).getOrThrow()
                    
                    val checkedItemCountToday = checkStates.count { state ->
                        state.history.any { it.date == today && it.isChecked }
                    }
                    
                    val completionRateToday = if (scheduledItemCountToday > 0) {
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
                    val allCheckStates = getCheckStatesForItemsUseCase(allItemIds).getOrThrow()

                    val totalRecordsCount = allCheckStates.sumOf { it.history.size }
                    val totalCheckedRecordsCount = allCheckStates.sumOf { state -> state.history.count { record -> record.isChecked } }
                    val historicalCompletionRate = if (totalRecordsCount > 0) {
                        ((totalCheckedRecordsCount.toDouble() / totalRecordsCount.toDouble()) * PERCENTAGE_MULTIPLIER).toInt()
                    } else {
                        0
                    }

                    val tomorrow = today.plus(1, DateTimeUnit.DAY)
                    val templatesForTomorrow = getTemplatesForDayUseCase(tomorrow.dayOfWeek.name).getOrThrow()

                    val itemIdsForTomorrow = templatesForTomorrow.flatMap { it.itemIds }.distinct()
                    val itemsForTomorrow = allItems.filter { itemIdsForTomorrow.contains(it.id) }
                    
                    val checkStatesForTomorrow = getCheckStatesForItemsUseCase(itemIdsForTomorrow).getOrThrow()
                    val checkStateMapByItemIdTomorrow = checkStatesForTomorrow.associateBy { it.itemId }
                    val uncheckedItemsTomorrow = itemsForTomorrow.filter { item ->
                        val state = checkStateMapByItemIdTomorrow[item.id]
                        val record = state?.history?.find { it.date == tomorrow }
                        record?.isChecked != true
                    }

                    DashboardState(
                        itemCount = itemCount,
                        templateCount = templateCount,
                        uncheckedItemsToday = uncheckedItems,
                        uncheckedItemsTomorrow = uncheckedItemsTomorrow,
                        scheduledItemCountToday = scheduledItemCountToday,
                        checkedItemCountToday = checkedItemCountToday,
                        completionRateToday = completionRateToday,
                        totalRecordsCount = totalRecordsCount,
                        totalCheckedRecordsCount = totalCheckedRecordsCount,
                        historicalCompletionRate = historicalCompletionRate,
                    )
                }.onSuccess { newState ->
                    setState { newState }
                }.onFailure {
                    // Handle error if needed, e.g. show toast or error state
                }
            }
        }
    }

    override suspend fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadDashboardData -> {} // No-op, Flow is already collecting
        }
    }
}
