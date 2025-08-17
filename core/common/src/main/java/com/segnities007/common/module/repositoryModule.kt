package com.segnities007.common.module

import com.segnities007.repository.ImageRepository
import com.segnities007.repository.ImageRepositoryImpl
import com.segnities007.repository.ItemRepository
import com.segnities007.repository.ItemRepositoryImpl
import com.segnities007.repository.UserRepository
import com.segnities007.repository.UserRepositoryImpl
import org.koin.dsl.module

val repositoryModule =
    module {
        single<UserRepository> { UserRepositoryImpl(get()) }
        single<ItemRepository> { ItemRepositoryImpl(get()) }
        single<ImageRepository> { ImageRepositoryImpl(get()) }
    }
