package com.mostapps.egyptianmeterstracker


import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mostapps.egyptianmeterstracker.authentication.AuthenticationViewModel
import com.mostapps.egyptianmeterstracker.authentication.FirebaseAuthenticationManager
import com.mostapps.egyptianmeterstracker.data.local.MetersDataSource
import com.mostapps.egyptianmeterstracker.data.local.LocalDB
import com.mostapps.egyptianmeterstracker.data.local.MetersRepository
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseDatabaseInterface
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
                AuthenticationViewModel(
                    get(),
                    get() as MetersDataSource
                )
            }

            viewModel {
                MetersListViewModel(
                    get(),
                    get() as MetersDataSource
                )
            }
            viewModel {
                CreateMeterViewModel(
                    get(),
                    get() as MetersDataSource
                )
            }

            single { FirebaseAuth.getInstance() }
            single { FirebaseDatabase.getInstance() }
            single { FirebaseAuthenticationManager(get()) }
            single { FirebaseDatabaseManager(get()) as FirebaseDatabaseInterface }
            single { SharedPreferencesUtils(androidContext()) }
            single {
                MetersRepository(
                    get(),
                    get() as FirebaseDatabaseInterface
                ) as MetersDataSource
            }
            single { LocalDB.createMetersDao(this@MyApp) }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }

}