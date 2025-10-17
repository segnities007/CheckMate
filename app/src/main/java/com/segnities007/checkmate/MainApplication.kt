package com.segnities007.checkmate

import android.app.Application
import android.util.Log
import com.segnities007.checkmate.mvi.MainViewModel
import com.segnities007.checkmate.notification.NotificationHelper
import com.segnities007.checkmate.notification.NotificationScheduler
import com.segnities007.common.module.databaseModule
import com.segnities007.common.module.remoteModule
import com.segnities007.common.module.repositoryModule
import com.segnities007.common.module.useCaseModule
import com.segnities007.common.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        Log.d(TAG, "MainApplication onCreate started")

        val mainModule =
            module {
                viewModelOf(::MainViewModel)
            }

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@MainApplication)
            // Load modules
            modules(remoteModule, repositoryModule, useCaseModule, viewModelModule, mainModule, databaseModule)
        }

        // 通知チャンネルを作成
        Log.d(TAG, "Creating notification channel")
        NotificationHelper.createNotificationChannel(this)

        // 毎日7:30の通知をスケジュール
        Log.d(TAG, "Scheduling daily reminder")
        NotificationScheduler.scheduleDailyReminder(this)
        
        Log.d(TAG, "MainApplication onCreate completed")
    }
    
    companion object {
        private const val TAG = "MainApplication"
    }
}
