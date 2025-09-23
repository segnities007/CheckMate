package com.segnities007.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.ui.card.EmptyStateCard
import com.segnities007.ui.card.EnhancedItemCard
import com.segnities007.ui.divider.HorizontalStatDivider
import kotlin.time.ExperimentalTime

@Composable
fun CategoryBasedItemList(
    allItems: List<Item>,
    itemCheckStates: Map<Int, Boolean>,
    onCheckItem: (itemId: Int, checked: Boolean) -> Unit,
) {
        when(allItems){
            listOf<Item>() -> EmptyStateCard()
            else -> CategoryBasedItemListUi(
                itemList = allItems,
                itemCheckStates = itemCheckStates,
                onCheckItem = onCheckItem,
            )
    }
}

@Composable
private fun CategoryBasedItemListUi(
    itemList: List<Item>,
    itemCheckStates: Map<Int, Boolean>,
    onCheckItem: (itemId: Int, checked: Boolean) -> Unit,
){
    val allItemIdsForSelectedDate = itemList.map { it.id }.distinct()
    val selectedItems = itemList.filter { allItemIdsForSelectedDate.contains(it.id) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
            HorizontalStatDivider(
                label = "今日の持ち物",
                itemCount = itemList.size,
                checkedCount = selectedItems.size,
            )

        itemList.forEach{ item ->
            val isChecked = itemCheckStates[item.id] ?: false
            EnhancedItemCard(
                item = item,
                checked = isChecked,
                onCheckedChange = { checked ->
                    onCheckItem(item.id, checked)
                },
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true)
@Composable
fun CategoryBasedItemListPreview() {
    val sampleItems = listOf(
        Item(id = 1, name = "教科書", category = ItemCategory.STUDY_SUPPLIES, imagePath = "textbook.png"),
        Item(id = 2, name = "ノート", category = ItemCategory.STUDY_SUPPLIES, imagePath = "notebook.png"),
        Item(id = 3, name = "歯ブラシ", category = ItemCategory.DAILY_SUPPLIES, imagePath = "toothbrush.png"),
        Item(id = 4, name = "タオル", category = ItemCategory.DAILY_SUPPLIES, imagePath = "towel.png")
    )
    val sampleItemCheckStates = mapOf(1 to true, 3 to false)

    CategoryBasedItemList(
        allItems = sampleItems,
        itemCheckStates = sampleItemCheckStates,
        onCheckItem = { _, _ -> }
    )
}

@Preview(showBackground = true)
@Composable
fun CategoryBasedItemListEmptyPreview() {
    CategoryBasedItemList(
        allItems = emptyList(),
        itemCheckStates = emptyMap(),
        onCheckItem = { _, _ -> }
    )
}

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true)
@Composable
fun CategoryBasedItemListAllCheckedPreview() {
    val sampleItems = listOf(
        Item(id = 1, name = "Tシャツ", category = ItemCategory.CLOTHING_SUPPLIES, imagePath = "tshirt.png"),
        Item(id = 2, name = "靴下", category = ItemCategory.CLOTHING_SUPPLIES, imagePath = "socks.png")
    )
    val sampleItemCheckStates = mapOf(1 to true, 2 to true)

    CategoryBasedItemList(
        allItems = sampleItems,
        itemCheckStates = sampleItemCheckStates,
        onCheckItem = { _, _ -> }
    )
}
