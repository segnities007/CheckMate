package com.segnities007.home.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.segnities007.home.component.CategoryBasedItemList
import com.segnities007.ui.card.statistics.StatisticsCard
import com.segnities007.home.mvi.HomeIntent
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.ui.card.calendar.WeekCalendarCard
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime

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
        WeekCalendarCard(
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

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun EnhancedHomeContentPreview() {
    EnhancedHomeContent(
        selectedDate = LocalDate(2023, 10, 26),
        currentWeekCenter = LocalDate(2023, 10, 26),
        templates = listOf(WeeklyTemplate(
            id = 1,
            title = "Work",
            daysOfWeek = setOf(
                com.segnities007.model.DayOfWeek.MONDAY,
                com.segnities007.model.DayOfWeek.TUESDAY
            ),
            itemIds = listOf(1, 2)
        ), WeeklyTemplate(
            id = 2,
            title = "Personal",
            daysOfWeek = setOf(
                com.segnities007.model.DayOfWeek.WEDNESDAY,
                com.segnities007.model.DayOfWeek.THURSDAY
            ),
            itemIds = listOf(3, 4)
        )),
        allItems = listOf(
            Item(
                id = 1,
                name = "Task 1",
                description = "Description for Task 1",
                category = ItemCategory.STUDY_SUPPLIES,
                imagePath = "",
                barcodeInfo = null,
                productInfo = null
            ),
            Item(
                id = 2,
                name = "Task 2",
                description = "Description for Task 2",
                category = ItemCategory.STUDY_SUPPLIES,
                imagePath = "",
                barcodeInfo = null,
                productInfo = null
            ),
            Item(
                id = 3,
                name = "Task 3",
                description = "Description for Task 3",
                category = ItemCategory.STUDY_SUPPLIES,
                imagePath = "",
                barcodeInfo = null,
                productInfo = null
            ),
            Item(
                id = 4,
                name = "Task 4",
                description = "Description for Task 4",
                category = ItemCategory.STUDY_SUPPLIES,
                imagePath = "",
                barcodeInfo = null,
                productInfo = null
            ),
        ),
        itemCheckStates = mapOf(1 to true, 2 to false, 3 to true, 4 to false),
        onCheckItem = { _, _ -> },
        onDateSelected = {},
        sendIntent = {}
    )
}

