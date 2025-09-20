package com.segnities007.common.module

import com.segnities007.auth.mvi.AuthViewModel
import com.segnities007.dashboard.mvi.DashboardViewModel
import com.segnities007.home.mvi.HomeViewModel
import com.segnities007.hub.mvi.HubViewModel
import com.segnities007.items.mvi.ItemsViewModel
import com.segnities007.login.mvi.LoginViewModel
import com.segnities007.setting.mvi.SettingViewModel
import com.segnities007.templates.mvi.TemplatesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule =
    module {
        // reducers are created inside ViewModels; no DI required
        viewModelOf(::LoginViewModel)
        viewModelOf(::AuthViewModel)
        viewModelOf(::HubViewModel)
        viewModelOf(::SettingViewModel)
        viewModelOf(::ItemsViewModel)
        viewModelOf(::TemplatesViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::DashboardViewModel)
    }
