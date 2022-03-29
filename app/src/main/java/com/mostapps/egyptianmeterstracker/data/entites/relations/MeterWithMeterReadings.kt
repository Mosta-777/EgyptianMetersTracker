package com.mostapps.egyptianmeterstracker.data.entites.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mostapps.egyptianmeterstracker.data.entites.MeterDTO
import com.mostapps.egyptianmeterstracker.data.entites.MeterReadingDTO


data class MeterWithMeterReadings(
    @Embedded val meter: MeterDTO,
    @Relation(
        parentColumn = "meterId",
        entityColumn = "parentMeterId"
    )
    val meterReadings: List<MeterReadingDTO>
)