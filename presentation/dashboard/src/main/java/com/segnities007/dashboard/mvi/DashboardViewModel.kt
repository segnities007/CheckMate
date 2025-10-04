package com.segnities007.dashboard.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.checkstate.GetCheckStatesForItemsUseCase
import com.segnities007.usecase.item.GetAllItemsUseCase
import com.segnities007.usecase.template.GetAllTemplatesUseCase
import com.segnities007.usecase.template.GetTemplatesForDayUseCase
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
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
    private val reducer: DashboardReducer = DashboardReducer()

    init {
        // schedule load via BaseViewModel.sendIntent
        sendIntent(DashboardIntent.LoadDashboardData)
    }

    override suspend fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadDashboardData -> loadDashboardData()
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun loadDashboardData() {
        setState { reducer.reduce(this, DashboardIntent.LoadDashboardData) }
        try {
            // Use Caseを通じてデータ取得
            val allItems = getAllItemsUseCase()
            val itemCount = allItems.size
            val templateCount = getAllTemplatesUseCase().size
            
            val today =
                Clock.System
                    .now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
            
            // 今日のテンプレートとアイテムを取得
            val templatesForToday = getTemplatesForDayUseCase(today.dayOfWeek.name)
            val itemIdsForToday = templatesForToday.flatMap { it.itemIds }.distinct()
            val scheduledItemCountToday = itemIdsForToday.size
            
            // 今日のチェック状態を取得して完了率を計算
            val checkStates = getCheckStatesForItemsUseCase(itemIdsForToday)
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
            
            // 今日の未チェックアイテムを算出
            val itemsForToday = allItems.filter { itemIdsForToday.contains(it.id) }
            val checkStateMapByItemId = checkStates.associateBy { it.itemId }
            val uncheckedItems = itemsForToday.filter { item ->
                val state = checkStateMapByItemId[item.id]
                val record = state?.history?.find { it.date == today }
                record?.isChecked != true
            }

            // 全履歴からの完了率を計算
            val allItemIds = allItems.map { it.id }
            val allCheckStates = getCheckStatesForItemsUseCase(allItemIds)
            val totalRecordsCount = allCheckStates.sumOf { it.history.size }
            val totalCheckedRecordsCount = allCheckStates.sumOf { state -> state.history.count { record -> record.isChecked } }
            val historicalCompletionRate =
                if (totalRecordsCount > 0) {
                    ((totalCheckedRecordsCount.toDouble() / totalRecordsCount.toDouble()) * 100.0).toInt()
                } else {
                    0
                }

            // 明日の未チェックアイテムを算出
            val tomorrow = today.plus(1, DateTimeUnit.DAY)
            val templatesForTomorrow = getTemplatesForDayUseCase(tomorrow.dayOfWeek.name)
            val itemIdsForTomorrow = templatesForTomorrow.flatMap { it.itemIds }.distinct()
            val itemsForTomorrow = allItems.filter { itemIdsForTomorrow.contains(it.id) }
            val checkStatesForTomorrow = getCheckStatesForItemsUseCase(itemIdsForTomorrow)
            val checkStateMapByItemIdTomorrow = checkStatesForTomorrow.associateBy { it.itemId }
            val uncheckedItemsTomorrow =
                itemsForTomorrow.filter { item ->
                    val state = checkStateMapByItemIdTomorrow[item.id]
                    val record = state?.history?.find { it.date == tomorrow }
                    record?.isChecked != true
                }

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
        } catch (e: Exception) {
            val errorMessage = e.localizedMessage ?: "不明なエラーが発生しました"
            setState { copy(isLoading = false, error = errorMessage) }
//            sendEffect { DashboardEffect.ShowError("データの読み込みに失敗しました: $errorMessage") }
        }
    }
}
