package com.segnities007.items.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.segnities007.items.mvi.ItemsState
import com.segnities007.model.item.Item
import com.segnities007.ui.card.ItemCard
import com.segnities007.ui.card.ItemsEmptyStateCard
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemsList(
    state: ItemsState,
    onCreateClick: (() -> Unit)? = null,
) {
    when{
        state.items.isEmpty() -> ItemsEmptyStateCard(onCreateClick = onCreateClick)
        else -> ItemsListUi(state)
    }
}

@Composable
private fun ItemsListUi(state: ItemsState){
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ){
        state.items.forEach { item ->
            ItemCard(
                item = item,
                modifier = Modifier.fillMaxWidth(),
            ){
                //TODO
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true)
@Composable
fun ItemsListPreview() {
    ItemsList(
        state = ItemsState(
            items = listOf(
                Item(id = 1, name = "Item 1", description = "Description 1"),
                Item(id = 2, name = "Item 2", description = "Description 2"),
            )
        )
    )
}
