package com.segnities007.checkmate.di

import com.segnities007.remote.Auth
import com.segnities007.remote.ProductApiService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val remoteModule =
    module {
        singleOf(::Auth)
        singleOf(::ProductApiService)
    }
