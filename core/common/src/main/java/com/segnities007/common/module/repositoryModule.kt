package com.segnities007.common.module

import com.segnities007.repository.UserRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::UserRepositoryImpl)
}