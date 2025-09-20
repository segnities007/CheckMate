package com.segnities007.home.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.segnities007.home.component.CategoryBasedItemList
import com.segnities007.ui.card.EnhancedCalendarCard
import com.segnities007.ui.card.StatisticsCard
import com.segnities007.home.mvi.HomeIntent
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import kotlinx.datetime.LocalDate

@Composable
fun EnhancedHomeContent(
    selectedDate: LocalDate,
    currentYear: Int,
    currentMonth: Int,
    templates: List<WeeklyTemplate>,
    allItems: List<Item>,
    itemCheckStates: Map<Int, Boolean>,
    onCheckItem: (itemId: Int, checked: Boolean) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (Int, Int) -> Unit,
    sendIntent: (HomeIntent) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // カレンダー
        EnhancedCalendarCard(
            year = currentYear,
            month = currentMonth,
            selectedDate = selectedDate,
            templates = templates,
            onDateSelected = onDateSelected,
            onMonthChanged = { year, month ->
                onMonthChanged(year, month)
                sendIntent(HomeIntent.ChangeMonth(year, month))
            },
        )

        // 今日の進捗セクション
        HorizontalDividerWithLabel("今日の進捗")
        StatisticsCard(
            itemsForToday = allItems,
            itemCheckStates = itemCheckStates,
        )

        // カテゴリ別アイテムリスト
        CategoryBasedItemList(
            allItems = allItems,
            itemCheckStates = itemCheckStates,
            onCheckItem = onCheckItem,
        )
    }
}
