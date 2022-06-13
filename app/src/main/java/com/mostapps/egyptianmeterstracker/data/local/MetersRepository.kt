package com.mostapps.egyptianmeterstracker.data.local

import com.google.firebase.auth.FirebaseUser
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import com.mostapps.egyptianmeterstracker.utils.Result
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.local.entites.asRemoteModel
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadings
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseDatabaseInterface
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeter
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeterReadingsCollection
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


    override suspend fun bulkInsertMeterReadingsCollections(vararg databaseMeterReadingsCollection: DatabaseMeterReadingsCollection) {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                metersDao.bulkInsertMeterReadingsCollections(meterReadingsCollections = databaseMeterReadingsCollection)
            }
        }
    }


    override suspend fun bulkInsertMeterReadings(vararg meterReadings: DatabaseMeterReading) {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                metersDao.bulkInsertMeterReading(databaseMeterReadings = meterReadings)
            }
        }
    }

    override suspend fun bulkInsertMetersData(vararg databaseMeter: DatabaseMeter) {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                metersDao.insertAllMeters(databaseMeters = databaseMeter)
            }
        }
    }


    override suspend fun getMeterReadingsCollections(): Result<List<DatabaseMeterReadingsCollection>> =
        withContext(ioDispatcher) {
            wrapEspressoIdlingResource {
                return@withContext try {
                    Result.Success(metersDao.getAllMeterReadingsCollections())
                } catch (ex: Exception) {
                    Result.Error(ex.localizedMessage)
                }
            }
        }


    override suspend fun getMeterWithMeterReadings(id: String): Result<MeterWithMeterReadings> {
        TODO("Not yet implemented")
    }


    override suspend fun syncAllData(uid: String) {

        //metersDao.deleteMeter("123456789")

        //1- Sync meters data separately by downloading data from
        //firebase, inserting new data into the database then
        //uploading the local meters data into firebase, meters data by itself is small enough
        //to do a complete upload, meter readings on the other hand are much larger in size
        //so a comparison between local and remote data is needed before downloading and uploading.

        syncMetersData(uid)

        //2- First we download all meter collections data from the server (not their corresponding
        //meter readings), we compare them with the local ones to decide which are we going to
        //download and upload their corresponding meter readings, we download and insert all meter
        //readings of finished meter collections which were not found locally and upload the ones
        //not found remotely, in case of the unfinished meter collections
        //we compare them to the local unfinished meter collection and decide the following:
        //          1- If both remote and local unfinished meter collections are the same ( have
        //          the same id), insert the readings of the remote into the local.
        //          2- If both are different discard the remote one ( we always give priority
        //          to the local data).
        //          3- If there is no unfinished local meter readings collection for a certain
        //          meter set the remote one to be the local one.
        //
        //
        //Note: There should be one unfinished meter readings collection per Meter

        val remoteMeterReadingsCollectionResult =
            firebaseDatabaseManager.downloadMeterCollectionsOfUser(uid)
        val remoteMeterReadingsCollection: List<RemoteMeterReadingsCollection> =
            if (remoteMeterReadingsCollectionResult is Result.Success<List<RemoteMeterReadingsCollection>>)
                remoteMeterReadingsCollectionResult.data else emptyList()

        val localMeterReadingsCollectionsResult = getMeterReadingsCollections()
        val localMeterReadingsCollections: List<DatabaseMeterReadingsCollection> =
            if (localMeterReadingsCollectionsResult is Result.Success<List<DatabaseMeterReadingsCollection>>)
                localMeterReadingsCollectionsResult.data else emptyList()

        val finishedRemoteCollections =
            remoteMeterReadingsCollection.filter { it.isFinished == true }
        val finishedLocalCollections =
            localMeterReadingsCollections.filter { it.isFinished == true }
        //Get finished collections in remote but not in local
        val distinctiveInRemote =
            finishedRemoteCollections.filter { it.meterReadingsCollectionId !in finishedLocalCollections.map { item -> item.meterReadingsCollectionId } }
        //Get finished collections in local but not in remote
        val distinctiveInLocal =
            finishedLocalCollections.filter { it.meterReadingsCollectionId !in finishedRemoteCollections.map { item -> item.meterReadingsCollectionId } }
        //Download and upload the distinctive finished ones with their corresponding readings


    }


    private suspend fun syncMetersData(uid: String) {
        val remoteMetersResult = firebaseDatabaseManager.downloadMetersOfUserId(userId = uid)
        val remoteMeters: List<RemoteMeter> =
            if (remoteMetersResult is Result.Success<List<RemoteMeter>>) remoteMetersResult.data else emptyList()
        bulkInsertMetersData(*(remoteMeters.asDatabaseMeter()).toTypedArray())
        val localMetersResult = getMeters()
        val localMeters: List<DatabaseMeter> =
            if (localMetersResult is Result.Success<List<DatabaseMeter>>)
                localMetersResult.data else emptyList()
        firebaseDatabaseManager.uploadMetersOfUserId(uid, localMeters.asRemoteModel())
    }


}
