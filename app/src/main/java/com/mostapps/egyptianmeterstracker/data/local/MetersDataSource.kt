package com.mostapps.egyptianmeterstracker.data.local

import com.google.firebase.auth.FirebaseUser
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterReadingsCollectionWithMeterReadings
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadings
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadingsCollections
import com.mostapps.egyptianmeterstracker.utils.Result

interface MetersDataSource {


    //User specific functions
    suspend fun storeUserData(user: FirebaseUser)


    //Meter specific functions
    suspend fun getMeter(id: String): Result<DatabaseMeter>
    suspend fun getMeters(): Result<List<DatabaseMeter>>
    suspend fun saveMeter(
        databaseMeter: DatabaseMeter,
        meterReadingsCollection: DatabaseMeterReadingsCollection,
        firstDatabaseMeterReading: DatabaseMeterReading,
        currentDatabaseMeterReading: DatabaseMeterReading
    )
    suspend fun syncNonConflictedData(uid: String): Int
    suspend fun syncConflictedData(uid: String, keepLocalData: Boolean)

    suspend fun bulkInsertMetersData(vararg databaseMeter: DatabaseMeter)


    //Meter Readings Collections specific functions
    suspend fun bulkInsertMeterReadingsCollections(vararg databaseMeterReadingsCollection: DatabaseMeterReadingsCollection)
    suspend fun getMeterReadingsCollections(): Result<List<DatabaseMeterReadingsCollection>>


    //Meter Readings specific functions
    suspend fun saveMeterReading(meterReading: DatabaseMeterReading)
    suspend fun bulkInsertMeterReadings(vararg meterReadings: DatabaseMeterReading)


    //Relational functions
    suspend fun getMeterReadingsOfMeterReadingsCollection(meterReadingsCollectionId: String): Result<MeterReadingsCollectionWithMeterReadings>
    suspend fun getMeterReadingsCollectionsOfMeter(meterId: String): Result<MeterWithMeterReadingsCollections>
    suspend fun getMeterReadingsOfMeter(id: String): Result<MeterWithMeterReadings>


}