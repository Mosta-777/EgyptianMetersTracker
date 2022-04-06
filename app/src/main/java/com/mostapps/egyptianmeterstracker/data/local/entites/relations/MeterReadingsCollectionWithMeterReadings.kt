package com.mostapps.egyptianmeterstracker.data.local.entites.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingsCollectionDTO

class MeterReadingsCollectionWithMeterReadings(
    @Embedded val meterCollection: MeterReadingsCollectionDTO,
    @Relation(
        parentColumn = "meterReadingsCollectionId",
        entityColumn = "parentMeterCollectionId"
    )
    val meterReadings: List<MeterReadingDTO>
)