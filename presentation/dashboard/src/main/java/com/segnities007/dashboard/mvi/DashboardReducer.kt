package com.segnities007.dashboard.mvi

import com.segnities007.ui.mvi.MviReducer

class DashboardReducer : MviReducer<DashboardState, DashboardIntent> {
    override fun reduce(
        currentState: DashboardState,
        intent: DashboardIntent,
    ): DashboardState =
        when (intent) {
            is DashboardIntent.LoadDashboardData -> currentState.copy(isLoading = true, error = null)
            // 他のIntentがあればここに追加
        }
}
