package com.mostapps.egyptianmeterstracker.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.models.User
import com.mostapps.egyptianmeterstracker.utils.Result


private const val USERS_KEY = "users"
private const val METERS_KEY = "meters"
private const val METER_COLLECTIONS_KEY = "meter_collections"
private const val METER_READINGS_KEY = "meter_readings"

class FirebaseDatabaseManager(private val database: FirebaseDatabase) : FirebaseDatabaseInterface {


    override fun saveUser(user: User, userId: String) {
        database.reference.child(USERS_KEY).child(userId).setValue(user)
    }

    override fun getMetersByUserId(
        userId: String,
        onResult: (Result<List<DatabaseMeter>>) -> Unit
    ) {
        database.reference.child(METERS_KEY).child(userId).get().addOnSuccessListener { data ->
            data?.run {
                val metersData: List<DatabaseMeter> =
                    children.mapNotNull { getValue(DatabaseMeter::class.java) }
                onResult(Result.Success(metersData))
            }
        }.addOnFailureListener {
            onResult(Result.Error(it.localizedMessage))
        }
    }

    override fun getMeterCollectionsByUserId(
        userId: String,
        onResult: (Result<List<DatabaseMeterReadingsCollection>>) -> Unit
    ) {
        database.reference.child(METER_COLLECTIONS_KEY).child(userId)
            .get().addOnSuccessListener { data ->
                data?.run {
                    val meterCollectionsData: List<DatabaseMeterReadingsCollection> =
                        children.mapNotNull { getValue(DatabaseMeterReadingsCollection::class.java) }
                    onResult(Result.Success(meterCollectionsData))
                }
            }.addOnFailureListener {
                onResult(Result.Error(it.localizedMessage))
            }
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