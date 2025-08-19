package com.segnities007.common.module

import androidx.room.Room
import com.segnities007.local.db.AppDatabase
import org.koin.dsl.module

val databaseModule =
    module {

        single {
            Room
                .databaseBuilder(get(), AppDatabase::class.java, "app_database")
                .fallbackToDestructiveMigration(true)
                .build()
        }

        single { get<AppDatabase>().itemDao() }
        single { get<AppDatabase>().weeklyTemplateDao() }
        single { get<AppDatabase>().itemCheckStateDao() }
    }
