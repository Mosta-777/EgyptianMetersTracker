package com.mostapps.egyptianmeterstracker


import android.app.Application
import androidx.annotation.StringRes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mostapps.egyptianmeterstracker.authentication.AuthenticationViewModel
import com.mostapps.egyptianmeterstracker.authentication.FirebaseAuthenticationManager
import com.mostapps.egyptianmeterstracker.data.local.MetersDataSource
import com.mostapps.egyptianmeterstracker.data.local.LocalDB
import com.mostapps.egyptianmeterstracker.data.local.MetersRepository
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseDatabaseInterface
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseDatabaseManager
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseStorageInterface
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseStorageManager
import com.mostapps.egyptianmeterstracker.screens.details.collectorarrived.CollectorArrivedViewModel
import com.mostapps.egyptianmeterstracker.screens.details.meterdetails.MeterDetailsViewModel
import com.mostapps.egyptianmeterstracker.screens.details.meterreadingscollectionslist.MeterReadingsCollectionsListViewModel
import com.mostapps.egyptianmeterstracker.screens.home.add.meter.reading.AddMeterReadingViewModel
import com.mostapps.egyptianmeterstracker.screens.home.createmeter.CreateMeterViewModel
import com.mostapps.egyptianmeterstracker.screens.home.meterslist.MetersListViewModel
import com.mostapps.egyptianmeterstracker.utils.SharedPreferencesUtils
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp : Application() {

    companion object {
        lateinit var instance: MyApp private set
    }

    object Strings {
        fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
            return MyApp.instance.getString(stringRes, *formatArgs)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val myModule = module {


            single { FirebaseAuth.getInstance() }
            single { FirebaseDatabase.getInstance() }
            single { FirebaseStorage.getInstance().reference }
            single { FirebaseAuthenticationManager(get()) }
            single { FirebaseDatabaseManager(get()) as FirebaseDatabaseInterface }
            single { FirebaseStorageManager(get()) as FirebaseStorageInterface }
            single { SharedPreferencesUtils(androidContext()) }
            single {
                MetersRepository(
                    get(),
                    get() as FirebaseDatabaseInterface
                ) as MetersDataSource
            }
            single { LocalDB.createMetersDao(this@MyApp) }


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

            viewModel {
                AddMeterReadingViewModel(
                    get(),
                    get() as MetersDataSource
                )
            }


            viewModel {
                MeterReadingsCollectionsListViewModel(
                    get(),
                    get() as MetersDataSource,
                    get() as FirebaseStorageInterface
                )
            }

            viewModel {
                MeterDetailsViewModel(
                    get(),
                    get() as MetersDataSource
                )
            }

            viewModel {
                CollectorArrivedViewModel(
                    get(),
                    get() as MetersDataSource
                )
            }


        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }

}