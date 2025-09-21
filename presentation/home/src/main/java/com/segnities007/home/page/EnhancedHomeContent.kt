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
import com.segnities007.ui.card.CenteredWeekCalendarCard
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import kotlinx.datetime.LocalDate

@Composable
fun EnhancedHomeContent(
    selectedDate: LocalDate,
    currentWeekCenter: LocalDate,
    templates: List<WeeklyTemplate>,
    allItems: List<Item>,
    itemCheckStates: Map<Int, Boolean>,
    onCheckItem: (itemId: Int, checked: Boolean) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    sendIntent: (HomeIntent) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        StatisticsCard(
            itemsForToday = allItems,
            itemCheckStates = itemCheckStates,
        )
        CenteredWeekCalendarCard(
            selectedDate = selectedDate,
            templates = templates,
            onDateSelected = onDateSelected,
            centerDate = currentWeekCenter,
            onCenterDateChanged = { sendIntent(HomeIntent.ChangeWeek(it)) },
        )
        CategoryBasedItemList(
            allItems = allItems,
            itemCheckStates = itemCheckStates,
            onCheckItem = onCheckItem,
        )
    }
}
