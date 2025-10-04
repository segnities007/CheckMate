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

    companion object {
        /**
         * パーセンテージ変換用の定数（0.0～1.0 → 0～100）
         */
        private const val PERCENTAGE_MULTIPLIER = 100.0
    }

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
        
        // Use Caseを通じてデータ取得（Result型で統一的なエラーハンドリング）
        val allItemsResult = getAllItemsUseCase()
        val allTemplatesResult = getAllTemplatesUseCase()
        
        allItemsResult.fold(
            onSuccess = { allItems ->
                allTemplatesResult.fold(
                    onSuccess = { allTemplates ->
                        processLoadedData(allItems, allTemplates)
                    },
                    onFailure = { e ->
                        handleLoadError(e)
                    }
                )
            },
            onFailure = { e ->
                handleLoadError(e)
            }
        )
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun processLoadedData(allItems: List<com.segnities007.model.item.Item>, allTemplates: List<com.segnities007.model.WeeklyTemplate>) {
        try {
            val itemCount = allItems.size
            val templateCount = allTemplates.size
            
            val today =
                Clock.System
                    .now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
            
            // 今日のテンプレートとアイテムを取得（Result型）
            val templatesForTodayResult = getTemplatesForDayUseCase(today.dayOfWeek.name)
            templatesForTodayResult.fold(
                onSuccess = { templatesForToday ->
                    val itemIdsForToday = templatesForToday.flatMap { it.itemIds }.distinct()
                    val scheduledItemCountToday = itemIdsForToday.size
                    
                    // 今日のチェック状態を取得して完了率を計算（Result型）
                    val checkStatesResult = getCheckStatesForItemsUseCase(itemIdsForToday)
                    checkStatesResult.fold(
                        onSuccess = { checkStates ->
                            processTodayData(allItems, itemCount, templateCount, today, templatesForToday, itemIdsForToday, scheduledItemCountToday, checkStates)
                        },
                        onFailure = { e ->
                            handleLoadError(e)
                        }
                    )
                },
                onFailure = { e ->
                    handleLoadError(e)
                }
            )
        } catch (e: Exception) {
            handleLoadError(e)
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun processTodayData(
        allItems: List<com.segnities007.model.item.Item>,
        itemCount: Int,
        templateCount: Int,
        today: kotlinx.datetime.LocalDate,
        templatesForToday: List<com.segnities007.model.WeeklyTemplate>,
        itemIdsForToday: List<Int>,
        scheduledItemCountToday: Int,
        checkStates: List<com.segnities007.model.item.ItemCheckState>
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
            
            // 今日の未チェックアイテムを算出
            val itemsForToday = allItems.filter { itemIdsForToday.contains(it.id) }
            val checkStateMapByItemId = checkStates.associateBy { it.itemId }
            val uncheckedItems = itemsForToday.filter { item ->
                val state = checkStateMapByItemId[item.id]
                val record = state?.history?.find { it.date == today }
                record?.isChecked != true
            }

            // 全履歴からの完了率を計算（Result型）
            val allItemIds = allItems.map { it.id }
            val allCheckStatesResult = getCheckStatesForItemsUseCase(allItemIds)
            allCheckStatesResult.fold(
                onSuccess = { allCheckStates ->
                    val totalRecordsCount = allCheckStates.sumOf { it.history.size }
                    val totalCheckedRecordsCount = allCheckStates.sumOf { state -> state.history.count { record -> record.isChecked } }
                    val historicalCompletionRate =
                        if (totalRecordsCount > 0) {
                            ((totalCheckedRecordsCount.toDouble() / totalRecordsCount.toDouble()) * PERCENTAGE_MULTIPLIER).toInt()
                        } else {
                            0
                        }

                    // 明日の未チェックアイテムを算出（Result型）
                    val tomorrow = today.plus(1, DateTimeUnit.DAY)
                    val templatesForTomorrowResult = getTemplatesForDayUseCase(tomorrow.dayOfWeek.name)
                    templatesForTomorrowResult.fold(
                        onSuccess = { templatesForTomorrow ->
                            val itemIdsForTomorrow = templatesForTomorrow.flatMap { it.itemIds }.distinct()
                            val itemsForTomorrow = allItems.filter { itemIdsForTomorrow.contains(it.id) }
                            val checkStatesForTomorrowResult = getCheckStatesForItemsUseCase(itemIdsForTomorrow)
                            checkStatesForTomorrowResult.fold(
                                onSuccess = { checkStatesForTomorrow ->
                                    val checkStateMapByItemIdTomorrow = checkStatesForTomorrow.associateBy { it.itemId }
                                    val uncheckedItemsTomorrow =
                                        itemsForTomorrow.filter { item ->
                                            val state = checkStateMapByItemIdTomorrow[item.id]
                                            val record = state?.history?.find { it.date == tomorrow }
                                            record?.isChecked != true
                                        }
                                    
                                    updateFinalState(itemCount, templateCount, uncheckedItems, scheduledItemCountToday, checkedItemCountToday, completionRateToday, totalRecordsCount, totalCheckedRecordsCount, historicalCompletionRate, uncheckedItemsTomorrow)
                                },
                                onFailure = { e ->
                                    handleLoadError(e)
                                }
                            )
                        },
                        onFailure = { e ->
                            handleLoadError(e)
                        }
                    )
                },
                onFailure = { e ->
                    handleLoadError(e)
                }
            )
        } catch (e: Exception) {
            handleLoadError(e)
        }
    }

    private suspend fun updateFinalState(
        itemCount: Int,
        templateCount: Int,
        uncheckedItems: List<com.segnities007.model.item.Item>,
        scheduledItemCountToday: Int,
        checkedItemCountToday: Int,
        completionRateToday: Int,
        totalRecordsCount: Int,
        totalCheckedRecordsCount: Int,
        historicalCompletionRate: Int,
        uncheckedItemsTomorrow: List<com.segnities007.model.item.Item>
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
    
    private suspend fun handleLoadError(e: Throwable) {
        val errorMessage = e.localizedMessage ?: "不明なエラーが発生しました"
        setState { copy(isLoading = false, error = errorMessage) }
//      sendEffect { DashboardEffect.ShowError("データの読み込みに失敗しました: $errorMessage") }
    }
}
