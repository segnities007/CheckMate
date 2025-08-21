package com.segnities007.items.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.items.mvi.ItemsState
import com.segnities007.model.item.Item

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemsList(
    state: ItemsState,
    onDeleteItem: (Item) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        state.items.forEach { item ->
            ItemCard(
                item = item,
                modifier = Modifier.fillMaxWidth(),
                onDeleteItem = onDeleteItem,
            )
        }
    }
}