package com.segnities007.dashboard.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressCardsRow(
    checkedItemCountToday: Int,
    scheduledItemCountToday: Int,
    totalCheckedRecordsCount: Int,
    totalRecordsCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        StatCardWithPercentage(
            title = "本日の完了率",
            value = "$checkedItemCountToday/$scheduledItemCountToday",
            progress = if (scheduledItemCountToday > 0) (checkedItemCountToday.toFloat() / scheduledItemCountToday) else 0f,
            modifier = Modifier.weight(1f),
        )
        StatCardWithPercentage(
            title = "累計完了率",
            value = "$totalCheckedRecordsCount/$totalRecordsCount",
            progress = if (totalRecordsCount > 0) (totalCheckedRecordsCount.toFloat() / totalRecordsCount) else 0f,
            modifier = Modifier.weight(1f),
        )
    }
}

