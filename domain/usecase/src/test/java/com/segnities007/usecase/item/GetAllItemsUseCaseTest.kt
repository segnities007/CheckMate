package com.segnities007.usecase.item

import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.repository.item.ItemRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllItemsUseCaseTest {

    private lateinit var repository: ItemRepository
    private lateinit var useCase: GetAllItemsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetAllItemsUseCase(repository)
    }

    @Test
    fun `Given リポジトリに5件のアイテム When getAllItems Then 5件すべてが返される`() = runTest {
        // Given (前提: リポジトリに5件のアイテムがある)
        val items = listOf(
            Item(id = 1, name = "Item 1", description = "", category = ItemCategory.STUDY_SUPPLIES, imagePath = "", barcodeInfo = null, productInfo = null),
            Item(id = 2, name = "Item 2", description = "", category = ItemCategory.PERSONAL_ITEMS, imagePath = "", barcodeInfo = null, productInfo = null),
            Item(id = 3, name = "Item 3", description = "", category = ItemCategory.STUDY_SUPPLIES, imagePath = "", barcodeInfo = null, productInfo = null),
            Item(id = 4, name = "Item 4", description = "", category = ItemCategory.SPORTSWEAR, imagePath = "", barcodeInfo = null, productInfo = null),
            Item(id = 5, name = "Item 5", description = "", category = ItemCategory.STUDY_SUPPLIES, imagePath = "", barcodeInfo = null, productInfo = null)
        )
        coEvery { repository.getAllItems() } returns flowOf(items)

        // When (操作: すべてのアイテムを取得)
        val result = useCase().first()

        // Then (結果: 5件すべてが返される)
        assertEquals(5, result.size)
        assertEquals(items, result)
    }

    @Test
    fun `Given リポジトリが空 When getAllItems Then 空のリストが返される`() = runTest {
        // Given (前提: リポジトリにアイテムがない)
        coEvery { repository.getAllItems() } returns flowOf(emptyList())

        // When (操作: すべてのアイテムを取得)
        val result = useCase().first()

        // Then (結果: 空のリストが返される)
        assertTrue(result.isEmpty())
    }
}
