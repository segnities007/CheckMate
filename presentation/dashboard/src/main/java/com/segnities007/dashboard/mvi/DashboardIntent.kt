package com.segnities007.dashboard.mvi

import com.segnities007.ui.mvi.MviIntent

sealed interface DashboardIntent: MviIntent {
    object LoadDashboardData : DashboardIntent
}
