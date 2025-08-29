package com.segnities007.dashboard.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.model.item.Item

@Composable
fun UncheckedItemsSection(
    uncheckedItemsToday: List<Item>,
    uncheckedItemsTomorrow: List<Item>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        UncheckedItemsCard(
            title = "本日の未チェックアイテム",
            items = uncheckedItemsToday,
        )
        UncheckedItemsCard(
            title = "明日の未チェックアイテム",
            items = uncheckedItemsTomorrow,
        )
    }
}
