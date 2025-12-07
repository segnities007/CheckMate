package com.segnities007.checkmate

import android.app.Application
import com.segnities007.checkmate.di.databaseModule
import com.segnities007.checkmate.di.remoteModule
import com.segnities007.checkmate.di.repositoryModule
import com.segnities007.checkmate.di.useCaseModule
import com.segnities007.checkmate.di.viewModelModule
import com.segnities007.navigation.mvi.MainViewModel
import com.segnities007.checkmate.notification.NotificationHelper
import com.segnities007.checkmate.notification.NotificationScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val mainModule =
            module {
                factoryOf(::MainViewModel)
            }

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                remoteModule,
                repositoryModule,
                useCaseModule,
                viewModelModule,
                mainModule,
                databaseModule
            )
        }

        NotificationHelper.createNotificationChannel(this)
        // 毎日7:30の通知をスケジュール
        NotificationScheduler.scheduleDailyReminder(this)
    }
}
