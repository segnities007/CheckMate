package com.segnities007.checkmate.di

import com.segnities007.repository.BackupRepository
import com.segnities007.repository.BackupRepositoryImpl
import com.segnities007.repository.IcsTemplateRepository
import com.segnities007.repository.IcsTemplateRepositoryImpl
import com.segnities007.repository.ImageRepository
import com.segnities007.repository.ImageRepositoryImpl
import com.segnities007.repository.ItemCheckStateRepository
import com.segnities007.repository.ItemCheckStateRepositoryImpl
import com.segnities007.repository.ItemRepository
import com.segnities007.repository.ItemRepositoryImpl
import com.segnities007.repository.UserRepository
import com.segnities007.repository.UserRepositoryImpl
import com.segnities007.repository.WeeklyTemplateRepository
import com.segnities007.repository.WeeklyTemplateRepositoryImpl
import org.koin.dsl.module

val repositoryModule =
    module {
        single<UserRepository> { UserRepositoryImpl(get()) }
        single<ItemRepository> { ItemRepositoryImpl(get(), get(), get()) }
        single<ImageRepository> { ImageRepositoryImpl(get()) }
        single<WeeklyTemplateRepository> { WeeklyTemplateRepositoryImpl(get()) }
        single<ItemCheckStateRepository> { ItemCheckStateRepositoryImpl(get()) }
        single<BackupRepository> { BackupRepositoryImpl(get(), get(), get()) }
        single<IcsTemplateRepository> { IcsTemplateRepositoryImpl(get(), get(), get()) }
    }
