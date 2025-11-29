package com.segnities007.usecase.item

import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.repository.item.ItemRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ItemUseCaseTest {

    private lateinit var repository: ItemRepository

    @Before
    fun setup() {
        repository = mockk()
    }

    // GetAllItemsUseCase Tests
    @Test
    fun `Given repository has 5 items When getAllItems Then returns all 5 items`() = runTest {
        // Given (Precondition: Repository has 5 items)
        val items = (1..5).map { id ->
            Item(
                id = id,
                name = "Item $id",
                description = "Description $id",
                category = ItemCategory.STUDY_SUPPLIES,
                imagePath = "",
                barcodeInfo = null,
                productInfo = null
            )
        }
        coEvery { repository.getAllItems() } returns flowOf(items)
        val useCase = GetAllItemsUseCase(repository)

        // When (Action: Get all items)
        val result = useCase().first()

        // Then (Result: All 5 items are returned)
        assertEquals(5, result.size)
        assertEquals(items, result)
        coVerify { repository.getAllItems() }
    }

    @Test
    fun `Given repository is empty When getAllItems Then returns empty list`() = runTest {
        // Given (Precondition: Repository has no items)
        coEvery { repository.getAllItems() } returns flowOf(emptyList())
        val useCase = GetAllItemsUseCase(repository)

        // When (Action: Get all items)
        val result = useCase().first()

        // Then (Result: Empty list is returned)
        assertTrue(result.isEmpty())
        coVerify { repository.getAllItems() }
    }

    // AddItemUseCase Tests
    @Test
    fun `Given valid item When addItem Then item is added to repository`() = runTest {
        // Given (Precondition: Valid item to add)
        val newItem = Item(
            id = 0,
            name = "New Item",
            description = "New Description",
            category = ItemCategory.PERSONAL_ITEMS,
            imagePath = "",
            barcodeInfo = null,
            productInfo = null
        )
        coEvery { repository.insertItem(any()) } returns Result.success(Unit)
        val useCase = AddItemUseCase(repository)

        // When (Action: Add item)
        val result = useCase(newItem)

        // Then (Result: Item is successfully added)
        assertTrue(result.isSuccess)
        coVerify { repository.insertItem(newItem) }
    }

    @Test
    fun `Given repository error When addItem Then returns failure`() = runTest {
        // Given (Precondition: Repository will fail)
        val item = Item(
            id = 1,
            name = "Test",
            description = "",
            category = ItemCategory.STUDY_SUPPLIES,
            imagePath = "",
            barcodeInfo = null,
            productInfo = null
        )
        val errorMessage = "Database error"
        coEvery { repository.insertItem(any()) } returns Result.failure(Exception(errorMessage))
        val useCase = AddItemUseCase(repository)

        // When (Action: Add item)
        val result = useCase(item)

        // Then (Result: Failure is returned)
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    // DeleteItemUseCase Tests
    @Test
    fun `Given existing item When deleteItem Then item is removed from repository`() = runTest {
        // Given (Precondition: Item exists in repository)
        val itemId = 1
        coEvery { repository.deleteItem(itemId) } returns Result.success(Unit)
        val useCase = DeleteItemUseCase(repository)

        // When (Action: Delete item)
        val result = useCase(itemId)

        // Then (Result: Item is successfully deleted)
        assertTrue(result.isSuccess)
        coVerify { repository.deleteItem(itemId) }
    }

    // GetItemByIdUseCase Tests
    @Test
    fun `Given item exists When getItemById Then returns the item`() = runTest {
        // Given (Precondition: Item exists in repository)
        val itemId = 1
        val expectedItem = Item(
            id = itemId,
            name = "Test Item",
            description = "Test Description",
            category = ItemCategory.SPORTSWEAR,
            imagePath = "",
            barcodeInfo = null,
            productInfo = null
        )
        coEvery { repository.getItemById(itemId) } returns Result.success(expectedItem)
        val useCase = GetItemByIdUseCase(repository)

        // When (Action: Get item by ID)
        val result = useCase(itemId)

        // Then (Result: Item is returned)
        assertTrue(result.isSuccess)
        assertEquals(expectedItem, result.getOrNull())
        coVerify { repository.getItemById(itemId) }
    }

    @Test
    fun `Given item does not exist When getItemById Then returns failure`() = runTest {
        // Given (Precondition: Item does not exist)
        val itemId = 999
        coEvery { repository.getItemById(itemId) } returns Result.failure(Exception("Item not found"))
        val useCase = GetItemByIdUseCase(repository)

        // When (Action: Get item by ID)
        val result = useCase(itemId)

        // Then (Result: Failure is returned)
        assertTrue(result.isFailure)
        coVerify { repository.getItemById(itemId) }
    }
}
