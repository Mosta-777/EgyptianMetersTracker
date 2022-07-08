package com.mostapps.egyptianmeterstracker.data.local

import com.google.firebase.auth.FirebaseUser
import com.mostapps.egyptianmeterstracker.data.local.entites.*
import com.mostapps.egyptianmeterstracker.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import com.mostapps.egyptianmeterstracker.utils.Result
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterReadingsCollectionWithMeterReadings
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadings
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadingsCollections
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseDatabaseInterface
import com.mostapps.egyptianmeterstracker.data.remote.models.*


class MetersRepository(
    private val metersDao: MetersDao,
    private val firebaseDatabaseManager: FirebaseDatabaseInterface,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MetersDataSource {


    private var commonLocalMeters = emptyList<DatabaseMeter>()
    private var commonRemoteMeters = emptyList<RemoteMeter>()


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

    override suspend fun getMeterReadingsOfMeterReadingsCollection(meterReadingsCollectionId: String): Result<MeterReadingsCollectionWithMeterReadings> =
        withContext(ioDispatcher) {
            wrapEspressoIdlingResource {
                return@withContext try {
                    Result.Success(
                        metersDao.getMeterReadingsCollectionWithMeterReadings(
                            meterReadingsCollectionId
                        )
                    )
                } catch (ex: Exception) {
                    Result.Error(ex.localizedMessage)
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

    override suspend fun saveMeterReading(meterReading: DatabaseMeterReading) {
        withContext(ioDispatcher) {
            wrapEspressoIdlingResource {
                return@withContext try {
                    Result.Success(metersDao.insertMeterReading(meterReading))
                } catch (ex: Exception) {
                    Result.Error(ex.localizedMessage)
                }
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


    override suspend fun getMeterReadingsOfMeter(id: String): Result<MeterWithMeterReadings> =
        withContext(ioDispatcher) {
            wrapEspressoIdlingResource {
                return@withContext try {
                    Result.Success(metersDao.getMeterReadingsOfMeter(id))
                } catch (ex: Exception) {
                    Result.Error(ex.localizedMessage)
                }
            }
        }


    override suspend fun getMeterReadingsCollectionsOfMeter(meterId: String): Result<MeterWithMeterReadingsCollections> =
        withContext(ioDispatcher) {
            wrapEspressoIdlingResource {
                return@withContext try {
                    Result.Success(metersDao.getMeterWithMeterReadingsCollections(meterId))
                } catch (ex: Exception) {
                    Result.Error(ex.localizedMessage)
                }
            }
        }


    override suspend fun syncNonConflictedData(uid: String): Int {


        //Get all local and remote meters data

        val remoteMetersResult = firebaseDatabaseManager.downloadMetersOfUserId(userId = uid)
        val remoteMeters: List<RemoteMeter> =
            if (remoteMetersResult is Result.Success<List<RemoteMeter>>) remoteMetersResult.data else emptyList()


        //firebaseDatabaseManager.uploadMetersOfUserId(uid, localMeters.asRemoteModel())

        val localMetersResult = getMeters()
        val localMeters: List<DatabaseMeter> =
            if (localMetersResult is Result.Success<List<DatabaseMeter>>)
                localMetersResult.data else emptyList()


        //Get fully distinctive meters in the local and the remote meters
        //those and their corresponding meter collections and meter readings
        //are going to be uploaded and downloaded respectively

        //Fully Download these
        val distinctiveMetersInRemote =
            remoteMeters.filter { it.meterId !in localMeters.map { item -> item.meterId } }

        //Fully Upload these
        val distinctiveLocalMeters =
            localMeters.filter { it.meterId !in remoteMeters.map { item -> item.meterId } }


        if (distinctiveMetersInRemote.isNotEmpty())
            startFullyDownloadingMeters(distinctiveMetersInRemote, uid)
        if (distinctiveLocalMeters.isNotEmpty())
            startFullyUploadingMeters(distinctiveLocalMeters, uid)


        commonLocalMeters =
            localMeters.filter { it.meterId in remoteMeters.map { item -> item.meterId } }

        commonRemoteMeters =
            remoteMeters.filter { it.meterId in localMeters.map { item -> item.meterId } }



        return commonLocalMeters.size

    }

    override suspend fun syncConflictedData(uid: String, keepLocalData: Boolean) {


        if (keepLocalData) {
            //Upload the meters data of local data to replace the remote ones
            startFullyUploadingMeters(commonLocalMeters, uid)
        } else {
            startFullyDownloadingMeters(commonRemoteMeters, uid)
        }


    }

    private suspend fun startFullyDownloadingMeters(
        distinctiveRemoteMeters: List<RemoteMeter>,
        uid: String
    ) {
        //Insert the meters data in database

        bulkInsertMetersData(*(distinctiveRemoteMeters.asDatabaseMeterCollection()).toTypedArray())

        //Download corresponding meter collections and meter readings for each meter

        val downloadedCollectionsToInsert = mutableListOf<RemoteMeterReadingsCollection>()
        val downloadedReadingsToInsert = mutableListOf<RemoteMeterReading>()
        for (meter in distinctiveRemoteMeters) {
            val downloadedCollections =
                firebaseDatabaseManager.downloadMeterCollectionsOfUserIdAndMeterId(
                    userId = uid,
                    meterId = meter.meterId!!
                )
            downloadedCollectionsToInsert.addAll(
                if (downloadedCollections is Result.Success<List<RemoteMeterReadingsCollection>>)
                    downloadedCollections.data else emptyList()
            )

            val downloadedReadings =
                firebaseDatabaseManager.downloadMeterReadingsOfUserIdAndMeterId(
                    userId = uid,
                    meterId = meter.meterId
                )


            downloadedReadingsToInsert.addAll(
                if (downloadedReadings is Result.Success<List<RemoteMeterReading>>)
                    downloadedReadings.data else emptyList()
            )

        }

        //Insert all the downloaded data, on inserting, data is replaced removing the ol
        //stored data in the database

        bulkInsertMeterReadingsCollections(
            *(downloadedCollectionsToInsert.asDatabaseMeterCollection()).toTypedArray()
        )
        bulkInsertMeterReadings(
            *(downloadedReadingsToInsert.asDatabaseMeterReading()).toTypedArray()
        )

    }


    private suspend fun startFullyUploadingMeters(
        distinctiveMetersInLocal: List<DatabaseMeter>,
        uid: String
    ) {


        //Convert the meters list to a map to be uploaded
        val distinctiveMetersInLocalMap: Map<String, RemoteMeter> =
            distinctiveMetersInLocal.asRemoteModel().associateBy {
                it.meterId!!
            }


        //Upload meters data,
        firebaseDatabaseManager.uploadMetersOfUserId(uid, distinctiveMetersInLocalMap)


        for (meter in distinctiveMetersInLocal) {

            //Get corresponding meter collections and upload them
            getMeterReadingsCollectionsOfMeter(meter.meterId).run {
                firebaseDatabaseManager.uploadMeterCollectionsOfUserIdAndMeterId(
                    userId = uid,
                    meterId = meter.meterId,
                    meterReadingCollectionsToUpload =
                    if (this is Result.Success<MeterWithMeterReadingsCollections>)
                        data.meterCollections.asRemoteMeterReadingsCollection().associateBy {
                            it.meterReadingsCollectionId!!
                        }.toMap() else emptyMap()
                )
            }
            //Get corresponding meter readings and upload them
            getMeterReadingsOfMeter(meter.meterId).run {
                firebaseDatabaseManager.uploadMeterReadingsOfUserIdAndMeterId(
                    userId = uid,
                    meterId = meter.meterId,
                    meterReadingsToUpload =
                    if (this is Result.Success<MeterWithMeterReadings>)
                        data.databaseMeterReadings.asRemoteMeterReading().associateBy {
                            it.meterReadingId!!
                        } else emptyMap()
                )
            }
        }
    }


}
