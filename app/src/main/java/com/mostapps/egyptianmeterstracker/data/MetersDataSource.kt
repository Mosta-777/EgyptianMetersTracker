package com.mostapps.egyptianmeterstracker.data

import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterDTO
import com.mostapps.egyptianmeterstracker.data.dto.entities.relations.MeterWithMeterReadings
import com.mostapps.egyptianmeterstracker.utils.Result
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterReadingDTO

interface MetersDataSource {

    suspend fun getMeters(): Result<List<MeterDTO>>
    suspend fun saveMeter(
        meter: MeterDTO, firstMeterReading: MeterReadingDTO,
        currentMeterReading: MeterReadingDTO
    )

    suspend fun getMeter(id: String): Result<MeterDTO>
    suspend fun getMeterWithMeterReadings(id: String): Result<MeterWithMeterReadings>
}