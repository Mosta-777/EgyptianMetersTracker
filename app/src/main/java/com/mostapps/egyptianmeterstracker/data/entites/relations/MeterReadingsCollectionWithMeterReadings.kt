package com.mostapps.egyptianmeterstracker.data.entites.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mostapps.egyptianmeterstracker.data.entites.MeterReadingDTO
import com.mostapps.egyptianmeterstracker.data.entites.MeterReadingsCollectionDTO

class MeterReadingsCollectionWithMeterReadings(
    @Embedded val meterCollection: MeterReadingsCollectionDTO,
    @Relation(
        parentColumn = "meterReadingsCollectionId",
        entityColumn = "parentMeterCollectionId"
    )
    val meterReadings: List<MeterReadingDTO>
)