package com.segnities007.home.mvi

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.viewModelScope
import com.segnities007.model.item.ItemCheckRecord
import com.segnities007.model.item.ItemCheckState
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.checkstate.CheckItemUseCase
import com.segnities007.usecase.checkstate.EnsureCheckHistoryForTodayUseCase
import com.segnities007.usecase.checkstate.GetCheckStatesForItemsUseCase
import com.segnities007.usecase.item.GetAllItemsUseCase
import com.segnities007.usecase.template.GetTemplatesForDayUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class HomeViewModel(
    private val getAllItemsUseCase: GetAllItemsUseCase,
    private val getTemplatesForDayUseCase: GetTemplatesForDayUseCase,
    private val getCheckStatesForItemsUseCase: GetCheckStatesForItemsUseCase,
    private val ensureCheckHistoryForTodayUseCase: EnsureCheckHistoryForTodayUseCase,
    private val checkItemUseCase: CheckItemUseCase,
) : BaseViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {
    private val itemCheckStatesByDate = mutableMapOf<LocalDate, MutableMap<Int, Boolean>>()
    private val reducer: HomeReducer = HomeReducer()

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadTodayData -> loadTodayData()
            is HomeIntent.SelectDate -> selectDate(intent.date)
            is HomeIntent.ChangeWeek -> setState { copy(currentWeekCenter = intent.date) }
            is HomeIntent.CheckItem -> checkItem(intent.itemId, intent.checked)
            is HomeIntent.SetAllItems -> setState { reducer.reduce(this, intent) }
            is HomeIntent.SetItemCheckStates -> setState { reducer.reduce(this, intent) }
            HomeIntent.GetAllItem -> getAllItems()
            HomeIntent.EnsureCheckHistory -> ensureCheckHistoryForToday()
            is HomeIntent.ChangeMonth -> changeMonth(intent.year, intent.month)
        }
    }

    init {
        // 現在の年月を設定
        val today =
            Clock.System
                .now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                .date
        setState {
            copy(
                currentYear = today.year,
                currentMonth = today.monthNumber,
                currentWeekCenter = today,
            )
        }

        sendIntent(HomeIntent.GetAllItem)
        sendIntent(HomeIntent.EnsureCheckHistory)
        sendIntent(HomeIntent.LoadTodayData)
    }

    private suspend fun ensureCheckHistoryForToday() {
        val today =
            Clock.System
                .now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                .date
        ensureCheckHistoryForTodayUseCase(today)
    }

    private suspend fun getAllItems() {
        getAllItemsUseCase().fold(
            onSuccess = { allItems ->
                // set directly via reducer since we're in the coroutine started by sendIntent
                setState { reducer.reduce(this, HomeIntent.SetAllItems(allItems)) }
            },
            onFailure = { e ->
                // エラーハンドリング: ログ出力またはEffect発行
            }
        )
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun loadTodayData() {
        val today =
            Clock.System
                .now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                .date
        selectDate(today)
    }

    private suspend fun selectDate(date: LocalDate) {
        setState {
            copy(
                selectedDate = date,
                currentYear = date.year,
                currentMonth = date.monthNumber,
            )
        }

        getTemplatesForDayUseCase(date.dayOfWeek.name).fold(
            onSuccess = { templates ->
                val itemIdsForToday = templates.flatMap { it.itemIds }.distinct()
                getAllItemsUseCase().fold(
                    onSuccess = { allItems ->
                        val itemsForToday = allItems.filter { itemIdsForToday.contains(it.id) }

                        getCheckStatesForItemsUseCase(itemIdsForToday).fold(
                            onSuccess = { checkStatesList ->
                                val checkStates = checkStatesList.associate { state ->
                                    val recordForDate = state.history.find { it.date == date }
                                    state.itemId to (recordForDate?.isChecked ?: false)
                                }

                                val stateMap = itemCheckStatesByDate.getOrPut(date) { mutableStateMapOf() }
                                stateMap.clear()
                                stateMap.putAll(checkStates)

                                // update state via reducer directly
                                setState { reducer.reduce(this, HomeIntent.SetItemCheckStates(date, stateMap.toMap())) }
                                // templatesForToday and itemsForToday are still set directly because they are derived lists
                                setState {
                                    copy(
                                        templatesForToday = templates,
                                        itemsForToday = itemsForToday,
                                    )
                                }
                            },
                            onFailure = { e ->
                                // エラーハンドリング
                            }
                        )
                    },
                    onFailure = { e ->
                        // エラーハンドリング
                    }
                )
            },
            onFailure = { e ->
                // エラーハンドリング
            }
        )
    }

    private fun changeMonth(
        year: Int,
        month: Int,
    ) {
        setState {
            copy(
                currentYear = year,
                currentMonth = month,
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun checkItem(
        itemId: Int,
        checked: Boolean,
    ) {
        val currentDate = state.value.selectedDate

        val stateMap = itemCheckStatesByDate.getOrPut(currentDate) { mutableStateMapOf() }
        stateMap[itemId] = checked
        setState { copy(itemCheckStates = stateMap.toMap()) }

        checkItemUseCase(itemId, currentDate, checked)
    }
}
