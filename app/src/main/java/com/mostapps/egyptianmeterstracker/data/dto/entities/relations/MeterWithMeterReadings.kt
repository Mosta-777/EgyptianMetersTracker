package com.mostapps.egyptianmeterstracker.data.dto.entities.relations

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterDTO
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterReadingDTO


data class MeterWithMeterReadings(
    @Embedded val meter: MeterDTO,
    @Relation(
        parentColumn = "meterId",
        entityColumn = "parentMeterId"
    )
    val meterReadings: List<MeterReadingDTO>
)