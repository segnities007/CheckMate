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
import kotlinx.datetime.number
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

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadTodayData -> loadTodayData()
            is HomeIntent.SelectDate -> selectDate(intent.date)
            is HomeIntent.ChangeWeek -> setState { copy(currentWeekCenter = intent.date) }
            is HomeIntent.CheckItem -> checkItem(intent.itemId, intent.checked)
            is HomeIntent.SetAllItems -> setState { reduce(intent) }
            is HomeIntent.SetItemCheckStates -> setState { reduce(intent) }
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
                currentMonth = today.month.number,
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
        ensureCheckHistoryForTodayUseCase(today).fold(
            onSuccess = { },
            onFailure = { }
        )
    }

    private suspend fun getAllItems() {
        getAllItemsUseCase().fold(
            onSuccess = { allItems ->
                setState { reduce(HomeIntent.SetAllItems(allItems)) }
            },
            onFailure = { e ->
                sendEffect { HomeEffect.ShowError("アイテムの読み込みに失敗しました") }
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

        // 早期リターンパターンでネストを削減
        val templates = getTemplatesForDayUseCase(date.dayOfWeek.name).getOrElse { e ->
            sendEffect { HomeEffect.ShowError("テンプレートの読み込みに失敗しました") }
            return
        }

        val itemIdsForToday = templates.flatMap { it.itemIds }.distinct()

        val allItems = getAllItemsUseCase().getOrElse { e ->
            sendEffect { HomeEffect.ShowError("アイテムの読み込みに失敗しました") }
            return
        }

        val itemsForToday = allItems.filter { itemIdsForToday.contains(it.id) }

        val checkStatesList = getCheckStatesForItemsUseCase(itemIdsForToday).getOrElse { e ->
            sendEffect { HomeEffect.ShowError("チェック状態の読み込みに失敗しました") }
            return
        }

        // チェック状態のマップを構築
        val checkStates = checkStatesList.associate { state ->
            val recordForDate = state.history.find { it.date == date }
            state.itemId to (recordForDate?.isChecked ?: false)
        }

        val stateMap = itemCheckStatesByDate.getOrPut(date) { mutableStateMapOf() }
        stateMap.clear()
        stateMap.putAll(checkStates)

        setState { reduce(HomeIntent.SetItemCheckStates(date, stateMap.toMap())) }
        // templatesForToday and itemsForToday are still set directly because they are derived lists
        setState {
            copy(
                templatesForToday = templates,
                itemsForToday = itemsForToday,
            )
        }
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

        checkItemUseCase(itemId, currentDate, checked).fold(
            onSuccess = { },
            onFailure = { e ->
                stateMap[itemId] = !checked
                setState { copy(itemCheckStates = stateMap.toMap()) }
            }
        )
    }
}

// =============================================================================
// Reducer Function
// =============================================================================

private fun HomeState.reduce(intent: HomeIntent): HomeState {
    return when (intent) {
        is HomeIntent.SetAllItems -> copy(allItem = intent.allItems)
        is HomeIntent.SetItemCheckStates -> copy(itemCheckStates = intent.itemCheckStates)
        
        // 他のIntentはViewModelで処理（非同期処理など）
        else -> this
    }
}
