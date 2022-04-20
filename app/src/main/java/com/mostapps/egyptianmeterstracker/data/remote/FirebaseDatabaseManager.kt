package com.mostapps.egyptianmeterstracker.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeter
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.remote.models.User
import com.mostapps.egyptianmeterstracker.utils.Result
import kotlinx.coroutines.tasks.await


private const val USERS_KEY = "users"
private const val METERS_KEY = "meters"
private const val METER_COLLECTIONS_KEY = "meter_collections"
private const val METER_READINGS_KEY = "meter_readings"

class FirebaseDatabaseManager(private val database: FirebaseDatabase) : FirebaseDatabaseInterface {


    override suspend fun saveUser(user: User, userId: String) {
        database.reference.child(USERS_KEY).child(userId).setValue(user).await()
    }

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

    override suspend fun uploadMetersOfUserId(userId: String, metersToUpload: List<RemoteMeter>) {
        database.reference.child(METERS_KEY).child(userId).setValue(metersToUpload).await()
    }


    override suspend fun downloadMeterCollectionsOfUser(userId: String): Result<List<RemoteMeterReadingsCollection>> {
        val data = database.reference.child(METER_COLLECTIONS_KEY).child(userId).get().await()
        if (data.exists() && data.hasChildren())
            return data.run {
                val meterReadingsCollections: List<RemoteMeterReadingsCollection> =
                    children.mapNotNull { remoteMeter ->
                        remoteMeter.getValue(
                            RemoteMeterReadingsCollection::class.java
                        )
                    }
                Result.Success(meterReadingsCollections)
            }
        return Result.Error("Errot")

    }

    override suspend fun getMeterCollectionsByUserId(
        userId: String
    ): Result<List<DatabaseMeterReadingsCollection>> {
        /*database.reference.child(METER_COLLECTIONS_KEY).child(userId)
            .get().addOnSuccessListener { data ->
                data?.run {
                    val meterCollectionsData: List<DatabaseMeterReadingsCollection> =
                        children.mapNotNull { getValue(DatabaseMeterReadingsCollection::class.java) }
                    onResult(Result.Success(meterCollectionsData))
                }
            }.addOnFailureListener {
                onResult(Result.Error(it.localizedMessage))
            }*/
        return Result.Error("")
    }

    override fun getMeterReadingsByUserIdAndMeterCollectionId(
        userId: String,
        meterCollectionId: String,
        onResult: (Result<List<DatabaseMeterReading>>) -> Unit
    ) {
        database.reference.child(METER_READINGS_KEY).child(userId).child(meterCollectionId)
            .get().addOnSuccessListener { data ->
                data?.run {
                    val databaseMeterReadingsData: List<DatabaseMeterReading> =
                        children.mapNotNull { getValue(DatabaseMeterReading::class.java) }
                    onResult(Result.Success(databaseMeterReadingsData))
                }
            }.addOnFailureListener {
                onResult(Result.Error(it.localizedMessage))
            }
    }


}