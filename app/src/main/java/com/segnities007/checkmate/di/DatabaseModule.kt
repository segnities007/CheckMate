package com.segnities007.checkmate.di

import androidx.room.Room
import com.segnities007.local.db.AppDatabase
import com.segnities007.local.db.MIGRATION_2_3
import org.koin.dsl.module

val databaseModule =
    module {

        single {
            Room
                .databaseBuilder(get(), AppDatabase::class.java, "app_database")
                .addMigrations(MIGRATION_2_3)
                .build()
        }

        single { get<AppDatabase>().itemDao() }
        single { get<AppDatabase>().weeklyTemplateDao() }
        single { get<AppDatabase>().itemCheckStateDao() }
    }
