package com.segnities007.common.module

import com.segnities007.remote.Auth
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val remoteModule = module {
    singleOf(::Auth)
}