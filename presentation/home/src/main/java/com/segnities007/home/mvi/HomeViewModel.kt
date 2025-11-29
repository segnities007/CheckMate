package com.segnities007.home.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.checkstate.CheckItemUseCase
import com.segnities007.usecase.checkstate.EnsureCheckHistoryForTodayUseCase
import com.segnities007.usecase.checkstate.GetCheckStatesForItemsUseCase
import com.segnities007.usecase.item.GetAllItemsUseCase
import com.segnities007.usecase.template.GetTemplatesForDayUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadTodayData -> loadTodayData()
            is HomeIntent.SelectDate -> selectDate(intent.date)
            is HomeIntent.ChangeWeek -> setState { copy(currentWeekCenter = intent.date) }
            is HomeIntent.CheckItem -> checkItem(intent.itemId, intent.checked)
            is HomeIntent.SetAllItems -> setState { copy(allItem = intent.allItems) }
            is HomeIntent.SetItemCheckStates -> setState { copy(itemCheckStates = intent.itemCheckStates) }
            HomeIntent.GetAllItem -> {} // No-op, Flow is already collecting
            HomeIntent.EnsureCheckHistory -> ensureCheckHistoryForToday()
            is HomeIntent.ChangeMonth -> changeMonth(intent.year, intent.month)
        }
    }

    init {
        val today = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        setState {
            copy(
                currentYear = today.year,
                currentMonth = today.month.number,
                currentWeekCenter = today,
            )
        }

        // Start collecting items Flow immediately
        viewModelScope.launch {
            getAllItemsUseCase().collect { items ->
                setState { copy(allItem = items) }
            }
        }

        sendIntent(HomeIntent.EnsureCheckHistory)
        sendIntent(HomeIntent.LoadTodayData)
    }

    private fun ensureCheckHistoryForToday() {
        val today = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        execute(
            action = { ensureCheckHistoryForTodayUseCase(today).getOrThrow() },
            reducer = { this }
        )
    }

    private fun loadTodayData() {
        val today = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        selectDate(today)
    }

    private fun selectDate(date: LocalDate) {
        execute(
            action = {
                val templates = getTemplatesForDayUseCase(date.dayOfWeek.name).getOrThrow()
                val itemIdsForToday = templates.flatMap { it.itemIds }.distinct()
                val allItems = getAllItemsUseCase().first()
                val itemsForToday = allItems.filter { itemIdsForToday.contains(it.id) }
                val checkStatesList = getCheckStatesForItemsUseCase(itemIdsForToday).getOrThrow()

                val checkStates = checkStatesList.associate { state ->
                    val recordForDate = state.history.find { it.date == date }
                    state.itemId to (recordForDate?.isChecked ?: false)
                }
                
                Triple(templates, itemsForToday, checkStates)
            },
            reducer = { (templates, itemsForToday, checkStates) ->
                copy(
                    selectedDate = date,
                    currentYear = date.year,
                    currentMonth = date.monthNumber,
                    templatesForToday = templates,
                    itemsForToday = itemsForToday,
                    itemCheckStates = checkStates
                )
            }
        )
    }

    private fun changeMonth(year: Int, month: Int) {
        setState {
            copy(
                currentYear = year,
                currentMonth = month,
            )
        }
    }

    private fun checkItem(itemId: Int, checked: Boolean) {
        val currentDate = currentState.selectedDate
        // Optimistic update
        setState {
            val newStates = itemCheckStates.toMutableMap().apply { put(itemId, checked) }
            copy(itemCheckStates = newStates)
        }

        execute(
            action = { checkItemUseCase(itemId, currentDate, checked).getOrThrow() },
            reducer = { this } // No state change on success
        )
    }
}
