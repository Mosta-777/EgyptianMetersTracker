package com.mostapps.egyptianmeterstracker


import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mostapps.egyptianmeterstracker.authentication.FirebaseAuthenticationManager
import com.mostapps.egyptianmeterstracker.data.local.MetersLocalDataSource
import com.mostapps.egyptianmeterstracker.data.local.LocalDB
import com.mostapps.egyptianmeterstracker.data.local.MetersLocalRepository
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseDatabaseManager
import com.mostapps.egyptianmeterstracker.screens.home.createmeter.CreateMeterViewModel
import com.mostapps.egyptianmeterstracker.screens.home.meterslist.MetersListViewModel
import com.mostapps.egyptianmeterstracker.utils.SharedPreferencesUtils
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
                    get() as MetersLocalDataSource
                )
            }
            viewModel {
                CreateMeterViewModel(
                    get(),
                    get() as MetersLocalDataSource
                )
            }

            single { FirebaseAuth.getInstance() }
            single { FirebaseDatabase.getInstance() }
            single { FirebaseAuthenticationManager(get()) }
            single { FirebaseDatabaseManager(get()) }
            single { SharedPreferencesUtils(androidContext()) }
            single { MetersLocalRepository(get()) as MetersLocalDataSource }
            single { LocalDB.createMetersDao(this@MyApp) }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }

}