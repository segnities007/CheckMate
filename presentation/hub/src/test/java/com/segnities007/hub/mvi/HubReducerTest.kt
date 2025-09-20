package com.segnities007.hub.mvi

import org.junit.Assert.assertEquals
import org.junit.Test

class HubReducerTest {
    @Test
    fun navigate_changesRoute() {
        val initial = HubUiState()
        val updated = HubReducer.reduce(initial, HubIntent.Navigate(com.segnities007.navigation.HubRoute.Items))
        assertEquals(com.segnities007.navigation.HubRoute.Items, updated.currentHubRoute)
    }

    @Test
    fun setBarsAndFab() {
        val initial = HubUiState()
        val updated1 = HubReducer.reduce(initial, HubIntent.SetBottomBar { })
        val updated2 = HubReducer.reduce(updated1, HubIntent.SetTopBar { })
        val updated3 = HubReducer.reduce(updated2, HubIntent.SetFab { })
        // Just ensure no state fields unexpectedly null; route unchanged
        assertEquals(initial.currentHubRoute, updated3.currentHubRoute)
    }
}
