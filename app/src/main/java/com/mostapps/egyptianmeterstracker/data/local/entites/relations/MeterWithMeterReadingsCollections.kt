package com.mostapps.egyptianmeterstracker.data.local.entites.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection

class MeterWithMeterReadingsCollections(
    @Embedded val databaseMeter: DatabaseMeter,
    @Relation(
        parentColumn = "meterId",
        entityColumn = "parentMeterId"
    )
    val meterCollections: List<DatabaseMeterReadingsCollection>
)