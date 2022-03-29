package com.mostapps.egyptianmeterstracker.data

import com.mostapps.egyptianmeterstracker.data.entites.MeterDTO
import com.mostapps.egyptianmeterstracker.data.entites.MeterReadingDTO
import com.mostapps.egyptianmeterstracker.data.entites.MeterReadingsCollectionDTO
import com.mostapps.egyptianmeterstracker.data.entites.relations.MeterWithMeterReadings
import com.mostapps.egyptianmeterstracker.utils.Result

interface MetersDataSource {

    suspend fun getMeters(): Result<List<MeterDTO>>
    suspend fun saveMeter(
        meter: MeterDTO,
        meterReadingsCollection: MeterReadingsCollectionDTO,
        firstMeterReading: MeterReadingDTO,
        currentMeterReading: MeterReadingDTO
    )

    suspend fun getMeter(id: String): Result<MeterDTO>
    suspend fun getMeterWithMeterReadings(id: String): Result<MeterWithMeterReadings>
}