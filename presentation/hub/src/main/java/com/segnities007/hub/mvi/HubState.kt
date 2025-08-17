package com.segnities007.hub.mvi

import com.segnities007.model.UserStatus
import com.segnities007.ui.mvi.MviState

data class HubState(
    val userStatus: UserStatus = UserStatus(),
    val isShowNavigationBar: Boolean = true,
) : MviState
