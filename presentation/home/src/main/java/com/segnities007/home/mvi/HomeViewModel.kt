package com.segnities007.home.mvi

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.viewModelScope
import com.segnities007.model.item.ItemCheckRecord
import com.segnities007.model.item.ItemCheckState
import com.segnities007.repository.ItemCheckStateRepository
import com.segnities007.repository.ItemRepository
import com.segnities007.repository.WeeklyTemplateRepository
import com.segnities007.ui.mvi.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HomeViewModel(
    private val itemRepo: ItemRepository,
    private val templateRepo: WeeklyTemplateRepository,
    private val checkStateRepo: ItemCheckStateRepository,
) : BaseViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {

    private val itemCheckStatesByDate = mutableMapOf<LocalDate, MutableMap<Int, Boolean>>()

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadTodayData -> loadTodayData()
            is HomeIntent.SelectDate -> selectDate(intent.date)
            is HomeIntent.CheckItem -> checkItem(intent.itemId, intent.checked)
            HomeIntent.GetAllItem -> getAllItems()
        }
    }

    init {
        getAllItems()
        ensureCheckHistoryForToday()
        viewModelScope.launch { handleIntent(HomeIntent.LoadTodayData) }
    }

    @OptIn(ExperimentalTime::class)
    private fun ensureCheckHistoryForToday() {
        viewModelScope.launch {
            val today = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
            val templatesForToday = templateRepo.getTemplatesForDay(today.dayOfWeek.name)
            val itemIdsForToday = templatesForToday.flatMap { it.itemIds }.distinct()
            val allItems = itemRepo.getAllItems()
            val itemsScheduledForToday = allItems.filter { itemIdsForToday.contains(it.id) }

            for (item in itemsScheduledForToday) {
                val existingState = checkStateRepo.getCheckStateForItem(item.id)
                if (existingState == null) {
                    val newCheckState = ItemCheckState(
                        itemId = item.id,
                        history = mutableListOf(ItemCheckRecord(date = today, isChecked = false))
                    )
                    checkStateRepo.saveCheckState(newCheckState)
                } else {
                    val todayRecord = existingState.history.find { it.date == today }
                    if (todayRecord == null) {
                        val updatedHistory = existingState.history.toMutableList().apply {
                            add(ItemCheckRecord(date = today, isChecked = false))
                        }
                        val updatedCheckState = existingState.copy(history = updatedHistory)
                        checkStateRepo.saveCheckState(updatedCheckState)
                    }
                }
            }
        }
    }

    private fun getAllItems() {
        viewModelScope.launch {
            val allItems = itemRepo.getAllItems()
            setState { copy(allItem = allItems) }
        }
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
        setState { copy(selectedDate = date) }

        val templates = templateRepo.getTemplatesForDay(date.dayOfWeek.name)
        val itemIdsForToday = templates.flatMap { it.itemIds }.distinct()
        val itemsForToday = itemRepo.getAllItems().filter { itemIdsForToday.contains(it.id) }

        val checkStates =
            checkStateRepo
                .getCheckStatesForItems(itemIdsForToday)
                .associate { state ->
                    val recordForDate = state.history.find { it.date == date }
                    state.itemId to (recordForDate?.isChecked ?: false)
                }

        val stateMap = itemCheckStatesByDate.getOrPut(date) { mutableStateMapOf() }
        stateMap.clear()
        stateMap.putAll(checkStates)

        setState {
            copy(
                templatesForToday = templates,
                itemsForToday = itemsForToday,
                itemCheckStates = stateMap.toMap(),
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun checkItem(
        itemId: Int,
        checked: Boolean,
    ) {
        viewModelScope.launch {
            val currentDate = state.value.selectedDate

            val stateMap = itemCheckStatesByDate.getOrPut(currentDate) { mutableStateMapOf() }
            stateMap[itemId] = checked
            setState { copy(itemCheckStates = stateMap.toMap()) }

            val existingState = checkStateRepo.getCheckStateForItem(itemId)
            val newHistory = existingState?.history?.toMutableList() ?: mutableListOf()
            val index = newHistory.indexOfFirst { it.date == currentDate }
            if (index >= 0) {
                newHistory[index] = ItemCheckRecord(currentDate, checked)
            } else {
                newHistory.add(ItemCheckRecord(currentDate, checked))
            }

            checkStateRepo.saveCheckState(
                ItemCheckState(
                    itemId = itemId,
                    history = newHistory,
                    id = existingState?.id ?: 0,
                ),
            )
        }
    }
}
