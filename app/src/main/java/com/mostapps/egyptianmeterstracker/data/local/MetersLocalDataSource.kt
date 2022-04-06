package com.mostapps.egyptianmeterstracker.data.local

import com.mostapps.egyptianmeterstracker.data.local.entites.MeterDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingsCollectionDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadings
import com.mostapps.egyptianmeterstracker.utils.Result

interface MetersLocalDataSource {

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