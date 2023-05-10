package com.kotlineering.stocksapp.android

import android.app.Application
import com.kotlineering.stocksapp.android.koin.viewModuleModule
import com.kotlineering.stocksapp.koin.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(viewModuleModule)
        }
    }
}
