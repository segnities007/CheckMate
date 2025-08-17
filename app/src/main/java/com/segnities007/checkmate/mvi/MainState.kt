package com.segnities007.checkmate.mvi

import com.segnities007.model.UserStatus
import com.segnities007.ui.mvi.MviState

data class MainState(
    val userStatus: UserStatus = UserStatus(),
) : MviState
