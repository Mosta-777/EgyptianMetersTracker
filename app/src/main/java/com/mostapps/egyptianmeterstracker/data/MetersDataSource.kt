package com.mostapps.egyptianmeterstracker.data

import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterDTO
import com.mostapps.egyptianmeterstracker.data.dto.entities.relations.MeterWithMeterReadings


interface MetersDataSource {

    suspend fun getMeters(): com.mostapps.egyptianmeterstracker.data.dto.Result<List<MeterDTO>>
    suspend fun saveMeter(meter: MeterDTO)
    suspend fun getMeter(id: String): com.mostapps.egyptianmeterstracker.data.dto.Result<MeterDTO>
    suspend fun getMeterWithMeterReadings(id: String): Result<MeterWithMeterReadings>
}