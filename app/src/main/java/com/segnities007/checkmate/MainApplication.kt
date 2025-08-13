package com.segnities007.checkmate

import android.app.Application
import com.segnities007.common.module.remoteModule
import com.segnities007.common.module.repositoryModule
import com.segnities007.common.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@MainApplication)
            // Load modules
            modules(remoteModule, repositoryModule, viewModelModule)
        }
    }
}