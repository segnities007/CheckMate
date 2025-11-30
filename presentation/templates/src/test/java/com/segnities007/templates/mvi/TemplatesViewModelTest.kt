package com.segnities007.templates.mvi

import com.segnities007.navigation.NavKeys
import com.segnities007.usecase.template.AddWeeklyTemplateUseCase
import com.segnities007.usecase.template.DeleteWeeklyTemplateUseCase
import com.segnities007.usecase.template.GetAllWeeklyTemplatesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TemplatesViewModelTest {

    private lateinit var getAllWeeklyTemplatesUseCase: GetAllWeeklyTemplatesUseCase
    private lateinit var addWeeklyTemplateUseCase: AddWeeklyTemplateUseCase
    private lateinit var deleteWeeklyTemplateUseCase: DeleteWeeklyTemplateUseCase

    private lateinit var viewModel: TemplatesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getAllWeeklyTemplatesUseCase = mockk()
        addWeeklyTemplateUseCase = mockk()
        deleteWeeklyTemplateUseCase = mockk()

        // デフォルトで空のFlowを返す
        coEvery { getAllWeeklyTemplatesUseCase() } returns flowOf(emptyList())
    }

    @Test
    fun `Given 初期状態 When ShowBottomSheet Then isShowingBottomSheetがtrueになる`() =
        runTest(testDispatcher) {
            // Given
            viewModel = TemplatesViewModel(
                getAllWeeklyTemplatesUseCase,
                addWeeklyTemplateUseCase,
                deleteWeeklyTemplateUseCase
            )

            assertFalse(viewModel.uiState.value.data.isShowingBottomSheet)

            // When (FABボタンをクリックした時の動作)
            viewModel.sendIntent(TemplatesIntent.ShowBottomSheet)
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertTrue("BottomSheetが表示されるべき", state.data.isShowingBottomSheet)
        }

    @Test
    fun `Given BottomSheet表示中 When HideBottomSheet Then isShowingBottomSheetがfalseになる`() =
        runTest(testDispatcher) {
            // Given
            viewModel = TemplatesViewModel(
                getAllWeeklyTemplatesUseCase,
                addWeeklyTemplateUseCase,
                deleteWeeklyTemplateUseCase
            )

            // BottomSheetを表示状態にする
            viewModel.sendIntent(TemplatesIntent.ShowBottomSheet)
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.data.isShowingBottomSheet)

            // When
            viewModel.sendIntent(TemplatesIntent.HideBottomSheet)
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertFalse("BottomSheetが非表示になるべき", state.data.isShowingBottomSheet)
        }

    @Test
    fun `Given 初期状態 When NavigateToWeeklyTemplateList Then currentRouteが更新される`() =
        runTest(testDispatcher) {
            // Given
            viewModel = TemplatesViewModel(
                getAllWeeklyTemplatesUseCase,
                addWeeklyTemplateUseCase,
                deleteWeeklyTemplateUseCase
            )

            assertEquals(NavKeys.WeeklyTemplateList, viewModel.uiState.value.data.currentRoute)

            // When
            viewModel.sendIntent(TemplatesIntent.NavigateToWeeklyTemplateList)
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertEquals(NavKeys.WeeklyTemplateList, state.data.currentRoute)
        }

    @Test
    fun `Given 初期状態 When NavigateToWeeklyTemplateSelector Then currentRouteが更新される`() =
        runTest(testDispatcher) {
            // Given
            viewModel = TemplatesViewModel(
                getAllWeeklyTemplatesUseCase,
                addWeeklyTemplateUseCase,
                deleteWeeklyTemplateUseCase
            )

            // When
            viewModel.sendIntent(TemplatesIntent.NavigateToWeeklyTemplateSelector)
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertEquals(NavKeys.WeeklyTemplateSelector, state.data.currentRoute)
        }

    @Test
    fun `Given 初期状態 When UpdateTemplateSearchQuery Then searchQueryが更新される`() =
        runTest(testDispatcher) {
            // Given
            viewModel = TemplatesViewModel(
                getAllWeeklyTemplatesUseCase,
                addWeeklyTemplateUseCase,
                deleteWeeklyTemplateUseCase
            )

            assertEquals("", viewModel.uiState.value.data.searchQuery)

            // When
            val testQuery = "テスト"
            viewModel.sendIntent(TemplatesIntent.UpdateTemplateSearchQuery(testQuery))
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertEquals(testQuery, state.data.searchQuery)
        }
}
