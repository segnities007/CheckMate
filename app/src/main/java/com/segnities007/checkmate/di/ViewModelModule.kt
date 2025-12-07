package com.segnities007.checkmate.di

import com.segnities007.dashboard.mvi.DashboardViewModel
import com.segnities007.home.mvi.HomeViewModel
import com.segnities007.items.mvi.ItemsViewModel
import com.segnities007.login.mvi.LoginViewModel
import com.segnities007.setting.mvi.SettingViewModel
import com.segnities007.splash.mvi.SplashViewModel
import com.segnities007.templates.mvi.TemplatesViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val viewModelModule =
    module {
        // reducers are created inside ViewModels; no DI required
        factoryOf(::LoginViewModel)
        factoryOf(::SplashViewModel)
        factoryOf(::SettingViewModel)
        factoryOf(::ItemsViewModel)
        factoryOf(::TemplatesViewModel)
        factoryOf(::HomeViewModel)
        factoryOf(::DashboardViewModel)
    }
