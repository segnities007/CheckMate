package com.segnities007.checkmate.mvi

import com.segnities007.navigation.NavKeys
import com.segnities007.usecase.user.GetUserStatusUseCase
import com.segnities007.usecase.user.IsAccountCreatedUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var isAccountCreatedUseCase: IsAccountCreatedUseCase
    private lateinit var getUserStatusUseCase: GetUserStatusUseCase
    private lateinit var viewModel: MainViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        isAccountCreatedUseCase = mockk()
        getUserStatusUseCase = mockk()

        coEvery { isAccountCreatedUseCase() } returns Result.success(true)
        coEvery { getUserStatusUseCase() } returns Result.success(com.segnities007.model.UserStatus())
    }

    @Test
    fun `Given initial state When navigating to Items Then hubBackStack contains Home and Items`() =
        runTest(testDispatcher) {
            viewModel = MainViewModel(isAccountCreatedUseCase, getUserStatusUseCase)
            advanceUntilIdle() // Handle initial checks

            // Initial state check
            assertEquals(listOf(NavKeys.SplashKey, NavKeys.Hub.HomeKey), viewModel.uiState.value.data.backStack)

            // Navigate to Items
            viewModel.sendIntent(MainIntent.Navigate(NavKeys.Hub.Items.ListKey))
            advanceUntilIdle()

            // Verify stack
            assertEquals(
                listOf(NavKeys.SplashKey, NavKeys.Hub.HomeKey, NavKeys.Hub.Items.ListKey),
                viewModel.uiState.value.data.backStack
            )
        }

    @Test
    fun `Given stack with Items When goBack Then backStack pops Items`() =
        runTest(testDispatcher) {
            viewModel = MainViewModel(isAccountCreatedUseCase, getUserStatusUseCase)
            advanceUntilIdle()

            // Navigate to Items
            viewModel.sendIntent(MainIntent.Navigate(NavKeys.Hub.Items.ListKey))
            advanceUntilIdle()
            assertEquals(
                listOf(NavKeys.SplashKey, NavKeys.Hub.HomeKey, NavKeys.Hub.Items.ListKey),
                viewModel.uiState.value.data.backStack
            )

            // Go Back
            viewModel.sendIntent(MainIntent.GoBack)
            advanceUntilIdle()

            // Verify stack
            assertEquals(listOf(NavKeys.SplashKey, NavKeys.Hub.HomeKey), viewModel.uiState.value.data.backStack)
        }

    @Test
    fun `Given stack with only Home When goBack Then stack remains Home`() =
        runTest(testDispatcher) {
            viewModel = MainViewModel(isAccountCreatedUseCase, getUserStatusUseCase)
            advanceUntilIdle()

            // Initial state is [Splash, Home] because checkAccount navigates to Home
            assertEquals(listOf(NavKeys.SplashKey, NavKeys.Hub.HomeKey), viewModel.uiState.value.data.backStack)

            // Go Back
            viewModel.sendIntent(MainIntent.GoBack)
            advanceUntilIdle()

            // Verify stack pops Home, returns to Splash
            assertEquals(listOf(NavKeys.SplashKey), viewModel.uiState.value.data.backStack)
        }

    @Test
    fun `Given stack with Home When navigating to Home again Then stack should not have duplicate Home`() =
        runTest(testDispatcher) {
            viewModel = MainViewModel(isAccountCreatedUseCase, getUserStatusUseCase)
            advanceUntilIdle()

            assertEquals(listOf(NavKeys.SplashKey, NavKeys.Hub.HomeKey), viewModel.uiState.value.data.backStack)

            // Navigate to Home again
            viewModel.sendIntent(MainIntent.Navigate(NavKeys.Hub.HomeKey))
            advanceUntilIdle()

            // Verify stack does NOT have duplicate
            assertEquals(listOf(NavKeys.SplashKey, NavKeys.Hub.HomeKey), viewModel.uiState.value.data.backStack)
        }
}
