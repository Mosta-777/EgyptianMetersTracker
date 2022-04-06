package com.mostapps.egyptianmeterstracker.data.local.entites.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingsCollectionDTO

class MeterWithMeterReadingsCollections(
    @Embedded val meter: MeterDTO,
    @Relation(
        parentColumn = "meterId",
        entityColumn = "parentMeterId"
    )
    val meterCollections: List<MeterReadingsCollectionDTO>
)