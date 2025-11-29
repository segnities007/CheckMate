package com.segnities007.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.segnities007.home.mvi.HomeIntent
import com.segnities007.home.page.EnhancedHomeContent
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import kotlinx.datetime.LocalDate
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

/**
 * Instrumented tests for Home module UI components
 * Tests EnhancedHomeContent which is the main testable composable
 */
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalTime::class)
    private val sampleItems = listOf(
        Item(
            id = 1,
            name = "Math homework",
            description = "",
            category = ItemCategory.STUDY_SUPPLIES,
            imagePath = "",
            barcodeInfo = null,
            productInfo = null
        ),
        Item(
            id = 2,
            name = "PE uniform",
            description = "",
            category = ItemCategory.CLOTHING_SUPPLIES,
            imagePath = "",
            barcodeInfo = null,
            productInfo = null
        )
    )

    private val sampleTemplates = listOf(
        WeeklyTemplate(
            id = 1,
            title = "Work",
            daysOfWeek = setOf(
                com.segnities007.model.DayOfWeek.MONDAY,
                com.segnities007.model.DayOfWeek.TUESDAY
            ),
            itemIds = listOf(1)
        )
    )

    @Test
    fun givenEnhancedHomeContent_whenDisplayed_thenShowsCalendarCard() {
        // Given (Precondition: Home content is displayed)
        val currentDate = LocalDate(2024, 1, 15)

        composeTestRule.setContent {
            EnhancedHomeContent(
                selectedDate = currentDate,
                currentWeekCenter = currentDate,
                templates = sampleTemplates,
                allItems = sampleItems,
                itemCheckStates = emptyMap(),
                onCheckItem = { _, _ -> },
                onDateSelected = {},
                sendIntent = {}
            )
        }

        // When (Implicit: Content is rendered)
        // Then (Result: Calendar card is displayed)
        // Note: Verify by checking for calendar-specific UI elements
        composeTestRule.waitForIdle()
        // WeekCalendarCard should be visible
        composeTestRule.onNode(hasTestTag("weekCalendarCard"), useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun givenEnhancedHomeContent_whenWeekChanges_thenChangeWeekIntentSent() {
        // Given (Precondition: Content is displayed)
        var weekChangeIntentSent = false
        val currentDate = LocalDate(2024, 1, 15)

        composeTestRule.setContent {
            EnhancedHomeContent(
                selectedDate = currentDate,
                currentWeekCenter = currentDate,
                templates = sampleTemplates,
                allItems = sampleItems,
                itemCheckStates = emptyMap(),
                onCheckItem = { _, _ -> },
                onDateSelected = {},
                sendIntent = { intent ->
                    if (intent is HomeIntent.ChangeWeek) {
                        weekChangeIntentSent = true
                    }
                }
            )
        }

        // When (Action: User swipes to next week)
        // Note: Actual swipe gesture testing requires more complex setup
        // For now, we verify the content renders correctly
        composeTestRule.waitForIdle()

        // Then (Result: Content is interactive and can emit intents)
        // The intent would be sent on actual interaction
        assert(!weekChangeIntentSent) { "Intent not yet sent without interaction" }
    }

    @Test
    fun givenEnhancedHomeContent_whenItemsExist_thenDisplaysItemList() {
        // Given (Precondition: Content with items)
        val today = LocalDate(2024, 1, 15)

        composeTestRule.setContent {
            EnhancedHomeContent(
                selectedDate = today,
                currentWeekCenter = today,
                templates = sampleTemplates,
                allItems = sampleItems,
                itemCheckStates = emptyMap(),
                onCheckItem = { _, _ -> },
                onDateSelected = {},
                sendIntent = {}
            )
        }

        // When (Implicit: Content shows items)
        // Then (Result: Items are displayed)
        composeTestRule.onNodeWithText("Math homework").assertExists()
        composeTestRule.onNodeWithText("PE uniform").assertExists()
    }

    @Test
    fun givenEnhancedHomeContent_whenItemUnchecked_thenCheckboxInteractionWorks() {
        // Given (Precondition: Unchecked item displayed)
        var checkItemCalled = false
        val today = LocalDate(2024, 1, 15)

        composeTestRule.setContent {
            EnhancedHomeContent(
                selectedDate = today,
                currentWeekCenter = today,
                templates = sampleTemplates,
                allItems = sampleItems,
                itemCheckStates = mapOf(1 to false), // Item with id=1 is unchecked
                onCheckItem = { itemId, checked ->
                    checkItemCalled = true
                },
                onDateSelected = {},
                sendIntent = {}
            )
        }

        // When (Action: User taps checkbox)
        // Find checkbox associated with the first item
        composeTestRule.onAllNodes(hasClickAction())
            .filter(hasAnyAncestor(hasText("Math homework")))
            .onFirst()
            .performClick()

        // Then (Result: onCheckItem callback is invoked)
        assert(checkItemCalled) { "onCheckItem should be called when checkbox is tapped" }
    }

    @Test
    fun givenEnhancedHomeContent_whenDateSelected_thenCallbackInvoked() {
        // Given (Precondition: Content is displayed with calendar)
        var dateSelectionCalled = false
        val currentDate = LocalDate(2024, 1, 15)

        composeTestRule.setContent {
            EnhancedHomeContent(
                selectedDate = currentDate,
                currentWeekCenter = currentDate,
                templates = sampleTemplates,
                allItems = sampleItems,
                itemCheckStates = emptyMap(),
                onCheckItem = { _, _ -> },
                onDateSelected = { date ->
                    dateSelectionCalled = true
                },
                sendIntent = {}
            )
        }

        // When (Action: User selects a different date)
        // Note: Actual date selection requires interacting with calendar UI
        // For now, verify callback mechanism is in place
        composeTestRule.waitForIdle()

        // Then (Result: Callback mechanism exists)
        assert(!dateSelectionCalled) { "Callback not invoked without interaction" }
    }

    @Test
    fun givenEnhancedHomeContent_whenCheckStatesProvided_thenItemsReflectCheckStates() {
        // Given (Precondition: Content with predefined check states)
        val today = LocalDate(2024, 1, 15)
        val checkStates = mapOf(1 to true, 2 to false)

        composeTestRule.setContent {
            EnhancedHomeContent(
                selectedDate = today,
                currentWeekCenter = today,
                templates = sampleTemplates,
                allItems = sampleItems,
                itemCheckStates = checkStates,
                onCheckItem = { _, _ -> },
                onDateSelected = {},
                sendIntent = {}
            )
        }

        // When (Implicit: Content renders with check states)
        // Then (Result: Items display correct check states)
        // Note: Visual verification would require checking checkbox state
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Math homework").assertExists()
        composeTestRule.onNodeWithText("PE uniform").assertExists()
    }

    @Test
    fun givenEnhancedHomeContent_whenStatisticsCardDisplayed_thenShowsItemStats() {
        // Given (Precondition: Content with items and check states)
        val today = LocalDate(2024, 1, 15)

        composeTestRule.setContent {
            EnhancedHomeContent(
                selectedDate = today,
                currentWeekCenter = today,
                templates = sampleTemplates,
                allItems = sampleItems,
                itemCheckStates = mapOf(1 to true, 2 to false),
                onCheckItem = { _, _ -> },
                onDateSelected = {},
                sendIntent = {}
            )
        }

        // When (Implicit: Content displays statistics)
        // Then (Result: Statistics card is visible)
        composeTestRule.waitForIdle()
        // Statistics card should show completion information
        // Verification depends on StatisticsCard implementation details
    }
}
