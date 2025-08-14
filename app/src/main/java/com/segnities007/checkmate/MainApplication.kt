package com.segnities007.checkmate

import android.app.Application
import com.segnities007.checkmate.mvi.MainViewModel
import com.segnities007.common.module.remoteModule
import com.segnities007.common.module.repositoryModule
import com.segnities007.common.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

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
            modules(remoteModule, repositoryModule, viewModelModule, mainModule)
        }
    }
}
