package com.mostapps.egyptianmeterstracker.data.local

import com.google.firebase.auth.FirebaseUser
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import com.mostapps.egyptianmeterstracker.utils.Result
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadings
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseDatabaseInterface
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseDatabaseManager
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeter
import com.mostapps.egyptianmeterstracker.data.remote.models.User
import com.mostapps.egyptianmeterstracker.data.remote.models.asDatabaseMeter


class MetersRepository(
    private val metersDao: MetersDao,
    private val firebaseDatabaseManager: FirebaseDatabaseInterface,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MetersDataSource {


    override suspend fun storeUserData(user: FirebaseUser) {
        firebaseDatabaseManager.saveUser(
            user.run { User(username = displayName, email = email) },
            user.uid
        )
    }

    override suspend fun getMeters(): Result<List<DatabaseMeter>> = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            return@withContext try {
                Result.Success(metersDao.getAllMeters())
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)
            }
        }
    }

    override suspend fun getMeter(id: String): Result<DatabaseMeter> =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                try {
                    val meter = metersDao.getMeterById(id)
                    if (meter != null) {
                        return@withContext Result.Success(meter)
                    } else {
                        return@withContext Result.Error("Meter not found!")
                    }
                } catch (e: Exception) {
                    return@withContext Result.Error(e.localizedMessage)
                }
            }
        }

    override suspend fun saveMeter(
        databaseMeter: DatabaseMeter,
        meterReadingsCollection: DatabaseMeterReadingsCollection,
        firstDatabaseMeterReading: DatabaseMeterReading,
        currentDatabaseMeterReading: DatabaseMeterReading
    ) =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                metersDao.saveMeter(
                    databaseMeter,
                    meterReadingsCollection,
                    firstDatabaseMeterReading,
                    currentDatabaseMeterReading
                )
            }
        }

    override suspend fun bulkInsertMetersData(vararg databaseMeter: DatabaseMeter) {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                metersDao.insertAllMeters(databaseMeters = databaseMeter)
            }
        }
    }

    override suspend fun getMeterWithMeterReadings(id: String): Result<MeterWithMeterReadings> {
        TODO("Not yet implemented")
    }


    override suspend fun syncAllData(uid: String) {
        var remoteMeters: List<RemoteMeter> = emptyList()
        val remoteResult = firebaseDatabaseManager.getMetersByUserId(userId = uid)
        if (remoteResult is Result.Success<List<RemoteMeter>>) remoteMeters = remoteResult.data
        val databaseMeters = remoteMeters.asDatabaseMeter()
        bulkInsertMetersData(*(databaseMeters).toTypedArray())
        print("Inserted Meters")
    }


}
