package com.segnities007.dashboard.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.repository.ItemRepository
import com.segnities007.repository.WeeklyTemplateRepository
import com.segnities007.ui.mvi.BaseViewModel
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val itemRepository: ItemRepository,
    private val weeklyTemplateRepository: WeeklyTemplateRepository,
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

    private suspend fun loadDashboardData() {
        setState { copy(isLoading = true, error = null) }
        try {
            val itemCount = itemRepository.getAllItems().size
            val templateCount = weeklyTemplateRepository.getAllTemplates().size
            val uncheckedItems = itemRepository.getUncheckedItemsForToday()

            setState {
                copy(
                    isLoading = false,
                    itemCount = itemCount,
                    templateCount = templateCount,
                    uncheckedItemsToday = uncheckedItems,
                )
            }
        } catch (e: Exception) {
            val errorMessage = e.localizedMessage ?: "不明なエラーが発生しました"
            setState { copy(isLoading = false, error = errorMessage) }
//            sendEffect { DashboardEffect.ShowError("データの読み込みに失敗しました: $errorMessage") }
        }
    }
}
