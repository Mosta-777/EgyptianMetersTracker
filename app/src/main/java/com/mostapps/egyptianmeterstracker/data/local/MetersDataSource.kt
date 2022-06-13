package com.mostapps.egyptianmeterstracker.data.local

import com.google.firebase.auth.FirebaseUser
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadings
import com.mostapps.egyptianmeterstracker.utils.Result

interface MetersDataSource {


    suspend fun storeUserData(user: FirebaseUser)
    suspend fun syncAllData(uid: String)
    suspend fun getMeters(): Result<List<DatabaseMeter>>
    suspend fun saveMeter(
        databaseMeter: DatabaseMeter,
        meterReadingsCollection: DatabaseMeterReadingsCollection,
        firstDatabaseMeterReading: DatabaseMeterReading,
        currentDatabaseMeterReading: DatabaseMeterReading
    )

    suspend fun bulkInsertMetersData(vararg databaseMeter: DatabaseMeter)
    suspend fun bulkInsertMeterReadings(vararg meterReadings: DatabaseMeterReading)
    suspend fun bulkInsertMeterReadingsCollections(vararg databaseMeterReadingsCollection: DatabaseMeterReadingsCollection)
    suspend fun getMeter(id: String): Result<DatabaseMeter>
    suspend fun getMeterReadingsCollections(): Result<List<DatabaseMeterReadingsCollection>>
    suspend fun getMeterWithMeterReadings(id: String): Result<MeterWithMeterReadings>
}