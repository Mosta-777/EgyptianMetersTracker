package com.mostapps.egyptianmeterstracker.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeter
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeterReading
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.remote.models.User
import com.mostapps.egyptianmeterstracker.utils.Result
import kotlinx.coroutines.tasks.await


private const val USERS_KEY = "users"
private const val METERS_KEY = "meters"
private const val METER_COLLECTIONS_KEY = "meter_collections"
private const val METER_READINGS_KEY = "meter_readings"

class FirebaseDatabaseManager(private val database: FirebaseDatabase) : FirebaseDatabaseInterface {


    //User related functions

    override suspend fun saveUser(user: User, userId: String) {
        database.reference.child(USERS_KEY).child(userId).setValue(user).await()
    }


    //Meter data related functions
    override suspend fun downloadMetersOfUserId(
        userId: String
    ): Result<List<RemoteMeter>> {
        val data = database.reference.child(METERS_KEY).child(userId).get().await()
        if (data.exists() && data.hasChildren())
            return data.run {
                val metersData: List<RemoteMeter> =
                    children.mapNotNull { remoteMeter -> remoteMeter.getValue(RemoteMeter::class.java) }
                Result.Success(metersData)
            }
        return Result.Error("Error")

    }

    override suspend fun uploadMetersOfUserId(
        userId: String,
        metersToUpload: Map<String, RemoteMeter>
    ) {
        database.reference.child(METERS_KEY).child(userId).updateChildren(metersToUpload).await()
    }


    //Meter Collections related function

    override suspend fun downloadMeterCollectionsOfUserIdAndMeterId(
        userId: String,
        meterId: String
    ): Result<List<RemoteMeterReadingsCollection>> {
        val data =
            database.reference.child(METER_COLLECTIONS_KEY).child(userId).child(meterId).get()
                .await()
        if (data.exists() && data.hasChildren())
            return data.run {
                val collectionsData: List<RemoteMeterReadingsCollection> =
                    children.mapNotNull { remoteCollection ->
                        remoteCollection.getValue(
                            RemoteMeterReadingsCollection::class.java
                        )
                    }
                Result.Success(collectionsData)
            }
        return Result.Error("Error")
    }

    override suspend fun uploadMeterCollectionsOfUserIdAndMeterId(
        userId: String,
        meterId: String,
        meterReadingCollectionsToUpload: Map<String, RemoteMeterReadingsCollection>
    ) {
        database.reference.child(METER_COLLECTIONS_KEY).child(userId).child(meterId)
            .setValue(meterReadingCollectionsToUpload).await()
    }


    //Meter readings related functions
    override suspend fun downloadMeterReadingsOfUserIdAndMeterId(
        userId: String,
        meterId: String
    ): Result<List<RemoteMeterReading>> {
        val data =
            database.reference.child(METER_READINGS_KEY).child(userId).child(meterId).get()
                .await()
        if (data.exists() && data.hasChildren())
            return data.run {
                val collectionsData: List<RemoteMeterReading> =
                    children.mapNotNull { remoteReading ->
                        remoteReading.getValue(
                            RemoteMeterReading::class.java
                        )
                    }
                Result.Success(collectionsData)
            }
        return Result.Error("Error")
    }

    override suspend fun uploadMeterReadingsOfUserIdAndMeterId(
        userId: String, meterId: String, meterReadingsToUpload: Map<String, RemoteMeterReading>
    ) {
        database.reference.child(METER_READINGS_KEY).child(userId).child(meterId)
            .setValue(meterReadingsToUpload).await()
    }


}