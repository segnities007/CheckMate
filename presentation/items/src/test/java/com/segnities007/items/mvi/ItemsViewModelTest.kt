package com.segnities007.items.mvi

import com.segnities007.ui.mvi.UiState
import com.segnities007.usecase.image.DeleteImageUseCase
import com.segnities007.usecase.image.SaveImageUseCase
import com.segnities007.usecase.item.AddItemUseCase
import com.segnities007.usecase.item.DeleteItemUseCase
import com.segnities007.usecase.item.GetAllItemsUseCase
import com.segnities007.usecase.item.GetItemByIdUseCase
import com.segnities007.usecase.item.GetProductInfoByBarcodeUseCase
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
class ItemsViewModelTest {

    private lateinit var getAllItemsUseCase: GetAllItemsUseCase
    private lateinit var getItemByIdUseCase: GetItemByIdUseCase
    private lateinit var addItemUseCase: AddItemUseCase
    private lateinit var deleteItemUseCase: DeleteItemUseCase
    private lateinit var getProductInfoByBarcodeUseCase: GetProductInfoByBarcodeUseCase
    private lateinit var saveImageUseCase: SaveImageUseCase
    private lateinit var deleteImageUseCase: DeleteImageUseCase
    
    private lateinit var viewModel: ItemsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        getAllItemsUseCase = mockk()
        getItemByIdUseCase = mockk()
        addItemUseCase = mockk()
        deleteItemUseCase = mockk()
        getProductInfoByBarcodeUseCase = mockk()
        saveImageUseCase = mockk()
        deleteImageUseCase = mockk()
        
        // Return empty flow by default
        coEvery { getAllItemsUseCase() } returns flowOf(emptyList())
    }

    @Test
    fun `Given BottomSheet is hidden When user clicks FAB Then BottomSheet is displayed`() = runTest(testDispatcher) {
        // Given (Precondition: BottomSheet is hidden)
        viewModel = ItemsViewModel(
            getAllItemsUseCase,
            getItemByIdUseCase,
            addItemUseCase,
            deleteItemUseCase,
            getProductInfoByBarcodeUseCase,
            saveImageUseCase,
            deleteImageUseCase
        )
        
        assertFalse(viewModel.uiState.value.data.isShowBottomSheet)

        // When (Action: User clicks FAB to show BottomSheet)
        viewModel.sendIntent(ItemsIntent.UpdateIsShowBottomSheet(true))
        advanceUntilIdle()

        // Then (Result: BottomSheet is displayed)
        val state = viewModel.uiState.value
        assertTrue("BottomSheet should be displayed", state.data.isShowBottomSheet)
    }

    @Test
    fun `Given BottomSheet is displayed When user taps close button Then BottomSheet is hidden`() = runTest(testDispatcher) {
        // Given (Precondition: BottomSheet is displayed)
        viewModel = ItemsViewModel(
            getAllItemsUseCase,
            getItemByIdUseCase,
            addItemUseCase,
            deleteItemUseCase,
            getProductInfoByBarcodeUseCase,
            saveImageUseCase,
            deleteImageUseCase
        )
        
        viewModel.sendIntent(ItemsIntent.UpdateIsShowBottomSheet(true))
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.data.isShowBottomSheet)

        // When (Action: User taps close button)
        viewModel.sendIntent(ItemsIntent.UpdateIsShowBottomSheet(false))
        advanceUntilIdle()

        // Then (Result: BottomSheet is hidden)
        val state = viewModel.uiState.value
        assertFalse("BottomSheet should be hidden", state.data.isShowBottomSheet)
    }

    @Test
    fun `Given image path is not set When user captures photo Then image path is set`() = runTest(testDispatcher) {
        // Given (Precondition: Image path is not set)
        viewModel = ItemsViewModel(
            getAllItemsUseCase,
            getItemByIdUseCase,
            addItemUseCase,
            deleteItemUseCase,
            getProductInfoByBarcodeUseCase,
            saveImageUseCase,
            deleteImageUseCase
        )
        
        assertEquals("", viewModel.uiState.value.data.capturedTempPathForViewModel)

        // When (Action: User captures photo and sets image path)
        val testPath = "/tmp/captured_image.jpg"
        viewModel.sendIntent(ItemsIntent.UpdateCapturedTempPathForViewModel(testPath))
        advanceUntilIdle()

        // Then (Result: Image path is set)
        val state = viewModel.uiState.value
        assertEquals(testPath, state.data.capturedTempPathForViewModel)
    }
    
    @Test
    fun `Given user is on any screen When navigating to ItemsList Then currentRoute becomes ItemsList`() = runTest(testDispatcher) {
        // Given (Precondition: User is on camera screen)
        viewModel = ItemsViewModel(
            getAllItemsUseCase,
            getItemByIdUseCase,
            addItemUseCase,
            deleteItemUseCase,
            getProductInfoByBarcodeUseCase,
            saveImageUseCase,
            deleteImageUseCase
        )

        // When (Action: Navigate back to list screen)
        viewModel.sendIntent(ItemsIntent.NavigateToItemsList)
        advanceUntilIdle()

        // Then (Result: currentRoute becomes ItemsList)
        val state = viewModel.uiState.value
        assertEquals(com.segnities007.navigation.NavKey.ItemsList, state.data.currentRoute)
    }
    
    @Test
    fun `Given user is on ItemsList screen When tapping camera button Then navigates to camera screen`() = runTest(testDispatcher) {
        // Given (Precondition: User is on ItemsList screen)
        viewModel = ItemsViewModel(
            getAllItemsUseCase,
            getItemByIdUseCase,
            addItemUseCase,
            deleteItemUseCase,
            getProductInfoByBarcodeUseCase,
            saveImageUseCase,
            deleteImageUseCase
        )

        // When (Action: User taps camera button)
        viewModel.sendIntent(ItemsIntent.NavigateToCameraCapture)
        advanceUntilIdle()

        // Then (Result: Navigates to camera screen)
        val state = viewModel.uiState.value
        assertEquals(com.segnities007.navigation.NavKey.CameraCapture, state.data.currentRoute)
    }
}
