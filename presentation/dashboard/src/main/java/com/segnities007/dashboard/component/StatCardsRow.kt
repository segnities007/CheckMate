package com.segnities007.dashboard.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatCardsRow(
    itemCount: Int,
    templateCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        StatCard(
            title = "総アイテム数",
            value = itemCount.toString(),
            modifier = Modifier.weight(1f),
        )
        StatCard(
            title = "総テンプレート数",
            value = templateCount.toString(),
            modifier = Modifier.weight(1f),
        )
    }
}

