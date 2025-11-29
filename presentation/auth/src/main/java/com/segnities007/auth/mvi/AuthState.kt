package com.segnities007.auth.mvi

import com.segnities007.ui.mvi.MviState

import com.segnities007.navigation.NavKey

data class AuthState(
    val currentRoute: NavKey = NavKey.Splash,
) : MviState
