package com.kotlineering.interview.android

import android.app.Application
import com.kotlineering.interview.android.koin.viewModuleModule
import com.kotlineering.interview.koin.initKoin
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
