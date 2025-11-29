package com.segnities007.home.mvi

import com.segnities007.usecase.checkstate.CheckItemUseCase
import com.segnities007.usecase.checkstate.EnsureCheckHistoryForTodayUseCase
import com.segnities007.usecase.checkstate.GetCheckStatesForItemsUseCase
import com.segnities007.usecase.item.GetAllItemsUseCase
import com.segnities007.usecase.template.GetTemplatesForDayUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var getAllItemsUseCase: GetAllItemsUseCase
    private lateinit var getTemplatesForDayUseCase: GetTemplatesForDayUseCase
    private lateinit var getCheckStatesForItemsUseCase: GetCheckStatesForItemsUseCase
    private lateinit var ensureCheckHistoryForTodayUseCase: EnsureCheckHistoryForTodayUseCase
    private lateinit var checkItemUseCase: CheckItemUseCase
    
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        getAllItemsUseCase = mockk()
        getTemplatesForDayUseCase = mockk()
        getCheckStatesForItemsUseCase = mockk()
        ensureCheckHistoryForTodayUseCase = mockk()
        checkItemUseCase = mockk()
        
        // Return empty flow by default
        coEvery { getAllItemsUseCase() } returns flowOf(emptyList())
        coEvery { ensureCheckHistoryForTodayUseCase(any()) } returns Result.success(Unit)
    }

    @Test
    fun `Given viewing current week calendar When changing to next week Then currentWeekCenter is updated`() = runTest(testDispatcher) {
        // Given (Precondition: Viewing current week calendar)
        viewModel = HomeViewModel(
            getAllItemsUseCase,
            getTemplatesForDayUseCase,
            getCheckStatesForItemsUseCase,
            ensureCheckHistoryForTodayUseCase,
            checkItemUseCase
        )
        
        val initialCenter = viewModel.uiState.value.data.currentWeekCenter
        val nextWeek = LocalDate(2024, 1, 15)

        // When (Action: Change to next week)
        viewModel.sendIntent(HomeIntent.ChangeWeek(nextWeek))
        advanceUntilIdle()

        // Then (Result: currentWeekCenter is updated to next week)
        val state = viewModel.uiState.value
        assertEquals(nextWeek, state.data.currentWeekCenter)
        assertNotEquals(initialCenter, nextWeek)
    }

    @Test
    fun `Given viewing January 2024 When changing to December Then currentYear and currentMonth are updated`() = runTest(testDispatcher) {
        // Given (Precondition: Viewing January 2024 calendar)
        viewModel = HomeViewModel(
            getAllItemsUseCase,
            getTemplatesForDayUseCase,
            getCheckStatesForItemsUseCase,
            ensureCheckHistoryForTodayUseCase,
            checkItemUseCase
        )
        
        val newYear = 2024
        val newMonth = 12

        // When (Action: Change to December)
        viewModel.sendIntent(HomeIntent.ChangeMonth(newYear, newMonth))
        advanceUntilIdle()

        // Then (Result: Year and month are updated to December)
        val state = viewModel.uiState.value
        assertEquals(newYear, state.data.currentYear)
        assertEquals(newMonth, state.data.currentMonth)
    }

    @Test
    fun `Given calendar is displayed When selecting specific date Then selectedDate is updated`() = runTest(testDispatcher) {
        // Given (Precondition: Calendar is displayed)
        viewModel = HomeViewModel(
            getAllItemsUseCase,
            getTemplatesForDayUseCase,
            getCheckStatesForItemsUseCase,
            ensureCheckHistoryForTodayUseCase,
            checkItemUseCase
        )
        
        coEvery { getTemplatesForDayUseCase(any()) } returns Result.success(emptyList())
        coEvery { getCheckStatesForItemsUseCase(any()) } returns Result.success(emptyList())
        
        val selectedDate = LocalDate(2024, 1, 20)

        // When (Action: Select January 20th on calendar)
        viewModel.sendIntent(HomeIntent.SelectDate(selectedDate))
        advanceUntilIdle()

        // Then (Result: Selected date becomes January 20th)
        val state = viewModel.uiState.value
        assertEquals(selectedDate, state.data.selectedDate)
    }

    @Test
    fun `Given item check states not set When setting check states Then itemCheckStates is updated`() = runTest(testDispatcher) {
        // Given (Precondition: Item check states are not set)
        viewModel = HomeViewModel(
            getAllItemsUseCase,
            getTemplatesForDayUseCase,
            getCheckStatesForItemsUseCase,
            ensureCheckHistoryForTodayUseCase,
            checkItemUseCase
        )
        
        val testDate = LocalDate(2024, 1, 15)
        val checkStates = mapOf(1 to true, 2 to false, 3 to true)
        assertTrue(viewModel.uiState.value.data.itemCheckStates.isEmpty())

        // When (Action: Set check states for 3 items)
        viewModel.sendIntent(HomeIntent.SetItemCheckStates(testDate, checkStates))
        advanceUntilIdle()

        // Then (Result: Check states are set)
        val state = viewModel.uiState.value
        assertEquals(checkStates, state.data.itemCheckStates)
        assertEquals(3, state.data.itemCheckStates.size)
    }
}
