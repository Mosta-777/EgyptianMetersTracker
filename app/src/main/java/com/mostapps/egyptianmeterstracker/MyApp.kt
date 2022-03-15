package com.mostapps.egyptianmeterstracker


import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module


class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val myModule = module {

        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }
}