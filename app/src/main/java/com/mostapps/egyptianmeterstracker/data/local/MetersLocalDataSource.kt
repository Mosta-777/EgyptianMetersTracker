package com.mostapps.egyptianmeterstracker.data.local

import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadings
import com.mostapps.egyptianmeterstracker.utils.Result

interface MetersLocalDataSource {

    suspend fun getMeters(): Result<List<DatabaseMeter>>
    suspend fun saveMeter(
        databaseMeter: DatabaseMeter,
        meterReadingsCollection: DatabaseMeterReadingsCollection,
        firstDatabaseMeterReading: DatabaseMeterReading,
        currentDatabaseMeterReading: DatabaseMeterReading
    )

    suspend fun getMeter(id: String): Result<DatabaseMeter>
    suspend fun getMeterWithMeterReadings(id: String): Result<MeterWithMeterReadings>
}