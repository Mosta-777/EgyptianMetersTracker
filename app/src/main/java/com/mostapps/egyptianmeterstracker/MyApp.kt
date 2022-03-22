package com.mostapps.egyptianmeterstracker


import android.app.Application
import com.mostapps.egyptianmeterstracker.data.MetersDataSource
import com.mostapps.egyptianmeterstracker.data.local.LocalDB
import com.mostapps.egyptianmeterstracker.data.local.MetersLocalRepository
import com.mostapps.egyptianmeterstracker.screens.home.meterslist.MetersListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module


class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val myModule = module {
            viewModel {
                MetersListViewModel(
                    get(),
                    get() as MetersDataSource
                )
            }
            single { MetersLocalRepository(get()) as MetersDataSource }
            single { LocalDB.createMetersDao(this@MyApp) }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }
}