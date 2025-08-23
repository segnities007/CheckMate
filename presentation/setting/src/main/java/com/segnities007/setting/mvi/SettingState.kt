package com.segnities007.setting.mvi

import com.segnities007.model.UserStatus
import com.segnities007.ui.mvi.MviState

data class SettingState(
    val showDeleteAllDataDialog: Boolean = false,
    val userStatus: UserStatus = UserStatus(),
) : MviState
