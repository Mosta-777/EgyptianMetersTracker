package com.mostapps.egyptianmeterstracker.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingsCollectionDTO
import com.mostapps.egyptianmeterstracker.utils.Result


private const val METERS_KEY = "meters"
private const val METER_COLLECTIONS_KEY = "meter_collections"
private const val METER_READINGS_KEY = "meter_readings"

class FirebaseDatabaseManager(private val database: FirebaseDatabase) : FirebaseDatabaseInterface {
    override fun getMetersByUserId(userId: String, onResult: (Result<List<MeterDTO>>) -> Unit) {
        database.reference.child(METERS_KEY).child(userId).get().addOnSuccessListener { data ->
            data?.run {
                val metersData: List<MeterDTO> =
                    children.mapNotNull { getValue(MeterDTO::class.java) }
                onResult(Result.Success(metersData))
            }
        }.addOnFailureListener {
            onResult(Result.Error(it.localizedMessage))
        }
    }

    override fun getMeterCollectionsByUserId(
        userId: String,
        onResult: (Result<List<MeterReadingsCollectionDTO>>) -> Unit
    ) {
        database.reference.child(METER_COLLECTIONS_KEY).child(userId)
            .get().addOnSuccessListener { data ->
                data?.run {
                    val meterCollectionsData: List<MeterReadingsCollectionDTO> =
                        children.mapNotNull { getValue(MeterReadingsCollectionDTO::class.java) }
                    onResult(Result.Success(meterCollectionsData))
                }
            }.addOnFailureListener {
                onResult(Result.Error(it.localizedMessage))
            }
    }

    override fun getMeterReadingsByUserIdAndMeterCollectionId(
        userId: String,
        meterCollectionId: String,
        onResult: (Result<List<MeterReadingDTO>>) -> Unit
    ) {
        database.reference.child(METER_READINGS_KEY).child(userId).child(meterCollectionId)
            .get().addOnSuccessListener { data ->
                data?.run {
                    val meterReadingsData: List<MeterReadingDTO> =
                        children.mapNotNull { getValue(MeterReadingDTO::class.java) }
                    onResult(Result.Success(meterReadingsData))
                }
            }.addOnFailureListener {
                onResult(Result.Error(it.localizedMessage))
            }
    }


}