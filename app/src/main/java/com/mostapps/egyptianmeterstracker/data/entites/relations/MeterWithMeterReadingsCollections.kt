package com.mostapps.egyptianmeterstracker.data.entites.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mostapps.egyptianmeterstracker.data.entites.MeterDTO
import com.mostapps.egyptianmeterstracker.data.entites.MeterReadingsCollectionDTO

class MeterWithMeterReadingsCollections(
    @Embedded val meter: MeterDTO,
    @Relation(
        parentColumn = "meterId",
        entityColumn = "parentMeterId"
    )
    val meterCollections: List<MeterReadingsCollectionDTO>
)