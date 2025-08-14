package com.segnities007.common.module

import com.segnities007.auth.mvi.AuthViewModel
import com.segnities007.hub.mvi.HubViewModel
import com.segnities007.login.mvi.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModelOf(::LoginViewModel)
        viewModelOf(::AuthViewModel)
        viewModelOf(::HubViewModel)
    }
