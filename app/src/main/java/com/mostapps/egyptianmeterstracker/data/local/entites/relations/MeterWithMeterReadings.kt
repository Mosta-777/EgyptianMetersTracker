package com.mostapps.egyptianmeterstracker.data.local.entites.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingDTO


data class MeterWithMeterReadings(
    @Embedded val meter: MeterDTO,
    @Relation(
        parentColumn = "meterId",
        entityColumn = "parentMeterId"
    )
    val meterReadings: List<MeterReadingDTO>
)